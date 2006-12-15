package org.opennms.web.performance;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.opennms.core.utils.LazySet;
import org.opennms.netmgt.collectd.StorageStrategy;
import org.opennms.netmgt.utils.RrdFileConstants;
import org.opennms.web.graph.GraphResourceDao;
import org.opennms.web.graph.PrefabGraph;
import org.springframework.orm.ObjectRetrievalFailureException;

public class GenericIndexGraphResourceType implements GraphResourceType {

    private String m_name;
    private String m_label;
    private PerformanceModel m_performanceModel;
    private StorageStrategy m_storageStrategy;

    public GenericIndexGraphResourceType(PerformanceModel performanceModel, String name, String label, StorageStrategy storageStrategy) {
        m_performanceModel = performanceModel;
        m_name = name;
        m_label = label;
        m_storageStrategy = storageStrategy;
    }
    
    public String getName() {
        return m_name;
    }
    
    public String getLabel() {
        return m_label;
    }
    
    public StorageStrategy getStorageStrategy() {
        return m_storageStrategy;
    }
    
    public boolean isResourceTypeOnNode(int nodeId) {
        /*
         *  XXX this should be based on the code in
         *  PerformanceModel.getQueryableNodes().  For now, we just return true.
         */
      return getResourceTypeDirectory(nodeId, false).isDirectory();
//    File resourceDirectory = new File(m_performanceModel.getNodeDirectory(nodeId, false), getName());
//        return resourceDirectory.isDirectory();
//        return false;
    }
    
    private File getResourceTypeDirectory(int nodeId, boolean verify) {
        File snmp = new File(m_performanceModel.getRrdDirectory(verify), PerformanceModel.SNMP_DIRECTORY);
        
        File node = new File(snmp, Integer.toString(nodeId));
        if (verify && !node.isDirectory()) {
            throw new ObjectRetrievalFailureException(File.class, "No node directory exists for node " + nodeId + ": " + node);
        }

        File generic = new File(node, getName());
        if (verify && !generic.isDirectory()) {
            throw new ObjectRetrievalFailureException(File.class, "No node directory exists for generic index " + getName() + ": " + generic);
        }

        return generic;
    }
    
    private File getResourceDirectory(int nodeId, String index) {
        return getResourceDirectory(nodeId, index, false);
    }
    
    private File getResourceDirectory(int nodeId, String index, boolean verify) {
        return new File(getResourceTypeDirectory(nodeId, verify), index);

    }
    
    
    public List<GraphResource> getResourcesForNode(int nodeId) {
        ArrayList<DefaultGraphResource> graphResources =
            new ArrayList<DefaultGraphResource>();

        List<String> indexes = getQueryableIndexesForNode(nodeId);
        for (String index : indexes) {
            graphResources.add(getResourceByNodeAndIndex(nodeId, index));
        }
        return DefaultGraphResource.sortIntoGraphResourceList(graphResources);
    }
    
    public List<String> getQueryableIndexesForNode(int nodeId) {
        File nodeDir = getResourceTypeDirectory(nodeId, true);
        
        List<String> indexes = new LinkedList<String>();
        
        File[] indexDirs =
            nodeDir.listFiles(RrdFileConstants.INTERFACE_DIRECTORY_FILTER);

        if (indexDirs == null) {
            return indexes;
        }
        
        for (File indexDir : indexDirs) {
            indexes.add(indexDir.getName());
        }
        
        return indexes;
    }

    
    public DefaultGraphResource getResourceByNodeAndIndex(int nodeId,
            String index) {
        
        String label = index;
//            label = m_performanceModel.getHumanReadableNameForIfLabel(nodeId, intf);


        Set<GraphAttribute> set =
            new LazySet<GraphAttribute>(new AttributeLoader(nodeId, index));
        return new DefaultGraphResource(index, label, set);
    }


    public class AttributeLoader implements LazySet.Loader<GraphAttribute> {
    
        private int m_nodeId;
        private String m_index;

        public AttributeLoader(int nodeId, String index) {
            m_nodeId = nodeId;
            m_index = index;
        }

        public Set<GraphAttribute> load() {
            File resourceDirectory = getResourceDirectory(m_nodeId, m_index); 
            List<String> dataSources =
                m_performanceModel.getDataSourcesInDirectory(resourceDirectory);

            Set<GraphAttribute> attributes =
                new HashSet<GraphAttribute>(dataSources.size());
        
            for (String dataSource : dataSources) {
                attributes.add(new RrdGraphAttribute(dataSource));
            }

            return attributes;
        }

    }

    public String getRelativePathForAttribute(String resourceParent, String resource, String attribute) {
        return m_storageStrategy.getRelativePathForAttribute(PerformanceModel.SNMP_DIRECTORY + File.separator + resourceParent, resource, attribute);
    }
    
    /**
     * This resource type is never available for domains.
     * Only the interface resource type is available for domains.
     */
    public boolean isResourceTypeOnDomain(String domain) {
        return false;
    }
    

    @SuppressWarnings("unchecked")
    public List<GraphResource> getResourcesForDomain(String domain) {
        return Collections.EMPTY_LIST;
    }

    public List<PrefabGraph> getAvailablePrefabGraphs(Set<GraphAttribute> attributes) {
        PrefabGraph[] graphs =
            m_performanceModel.getQueriesByResourceTypeAttributes(getName(), attributes);
        return Arrays.asList(graphs);
    }
    
    public GraphResourceDao getModel() {
        return m_performanceModel;
    }
    
    public PrefabGraph getPrefabGraph(String name) {
        return m_performanceModel.getQuery(name);
    }

    public String getGraphType() {
        return "performance";
    }
}
