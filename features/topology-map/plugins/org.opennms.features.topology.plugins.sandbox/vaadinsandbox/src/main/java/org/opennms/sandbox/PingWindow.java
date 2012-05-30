package org.opennms.sandbox;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout.MarginInfo;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.Window;

public class PingWindow extends Window{

   
    private final double sizePercentage = 0.80; // Window size proportionate to main window
    private boolean numOutput = false;
    private NativeSelect ipDropdown = null;
    private NativeSelect packetSizeDropdown = null;
    private Node testNode = null;
    private Label nodeLabel = null;
    public PingWindow (float width, float height){
        
        //Test Data
        testNode = new Node("172.0.1.234","Test Node");
        //End Test Data
        String nodeName = "<div style=\"text-align: center; font-size: 18pt; font-weight:bold;\">" + testNode.getName() + "</div>";
        nodeLabel = new Label(nodeName);
        nodeLabel.setContentMode(Label.CONTENT_XHTML);
        int windowWidth = (int)(sizePercentage * width), windowHeight = (int)(sizePercentage * height);

        setCaption("Ping");
        setImmediate(true);
        setResizable(false);
        setWidth("" + windowWidth + "px");
        setHeight("" + windowHeight + "px");
        setPositionX((int)((1.0 - windowWidth/width)/2.0 * width));
        setPositionY((int)((1.0 - windowHeight/height)/2.0 * height));
        
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSizeFull();
        VerticalSplitPanel vSplit = new VerticalSplitPanel();
        VerticalLayout topLayout = new VerticalLayout();
        VerticalLayout bottomLayout = new VerticalLayout();
        VerticalLayout form = new VerticalLayout();
        GridLayout grid = new GridLayout(2,4);
        grid.setWidth("420");
        grid.setHeight("120");
        ipDropdown = new NativeSelect();
        packetSizeDropdown = new NativeSelect();
        CheckBox numericalDataCheckBox = new CheckBox("Use Numerical Node Names");
        numericalDataCheckBox.setImmediate(true);
        packetSizeDropdown.addItem("16");
        packetSizeDropdown.addItem("32");
        packetSizeDropdown.addItem("64");
        packetSizeDropdown.addItem("128");
        packetSizeDropdown.addItem("256");
        packetSizeDropdown.addItem("512");
        packetSizeDropdown.addItem("1024");
        
        //Test Data
        ipDropdown.addItem(testNode.getDisplayedName());
        ipDropdown.select(testNode.getDisplayedName());
        //End Test Data
        Label ipLabel = new Label("IP Address: ");
        Label requestsLabel = new Label("Number of Requests: ");
        Label timeoutLabel = new Label("Time-Out (seconds): ");
        Label packetLabel = new Label("Packet Size: ");
        TextField requestsField = new TextField();
        TextField timeoutField = new TextField();
     
        grid.addComponent(ipLabel);
        grid.setComponentAlignment(ipLabel, Alignment.MIDDLE_LEFT);
        grid.addComponent(ipDropdown);
        grid.setComponentAlignment(ipDropdown, Alignment.MIDDLE_LEFT);
        grid.addComponent(requestsLabel);
        grid.setComponentAlignment(requestsLabel, Alignment.MIDDLE_LEFT);
        grid.addComponent(requestsField);
        grid.setComponentAlignment(requestsField, Alignment.MIDDLE_LEFT);
        grid.addComponent(timeoutLabel);
        grid.setComponentAlignment(timeoutLabel, Alignment.MIDDLE_LEFT);
        grid.addComponent(timeoutField);
        grid.setComponentAlignment(timeoutField, Alignment.MIDDLE_LEFT);
        grid.addComponent(packetLabel);
        grid.setComponentAlignment(packetLabel, Alignment.MIDDLE_LEFT);
        grid.addComponent(packetSizeDropdown);
        grid.setComponentAlignment(packetSizeDropdown, Alignment.MIDDLE_LEFT);
        packetSizeDropdown.select("16");
        
        numericalDataCheckBox.setValue(false);
        
        numericalDataCheckBox.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				switchNumOutput();
			}
        	
        });
       
        final Button pingButton = new Button("Ping"); 
        pingButton.addListener(new Button.ClickListener() {

            public void buttonClick(ClickEvent event) {
                if(event.getButton() == pingButton){
                  getWindow().showNotification("I HAS NOTIFICATZ UR DISLAI!!!111");
                }
            }
        }); 
        form.addComponent(grid);
        form.addComponent(numericalDataCheckBox);
        form.addComponent(pingButton);
        form.setWidth("100%");
        form.setSpacing(true);
        topLayout.addComponent(nodeLabel);
        topLayout.setComponentAlignment(nodeLabel, Alignment.MIDDLE_CENTER);
        topLayout.addComponent(form);
        bottomLayout.addComponent(new Label("RESULTS GO HERE"));
        topLayout.setSizeFull();
        topLayout.setMargin(true, true, false, true);
        bottomLayout.setSizeFull();
        bottomLayout.setMargin(true);
        vSplit.setFirstComponent(topLayout);
        vSplit.setSecondComponent(bottomLayout);
        vSplit.setSplitPosition(33, UNITS_PERCENTAGE);
        vSplit.setLocked(true);
        mainLayout.addComponent(vSplit);
        mainLayout.setExpandRatio(vSplit, 1);
        setContent(mainLayout);
    }
    
	private void switchNumOutput() {
		ipDropdown.removeAllItems();
		if (numOutput == false){
			testNode.setDisplayedName(testNode.ip);
	        ipDropdown.addItem(testNode.getDisplayedName());
	        numOutput = true;
		} else {
			testNode.setDisplayedName(testNode.name);
            ipDropdown.addItem(testNode.getDisplayedName());
            numOutput = false;
		}
		ipDropdown.select(testNode.getDisplayedName());
		ipDropdown.requestRepaint();
	}
}
