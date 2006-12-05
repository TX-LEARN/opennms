<%--

//
// This file is part of the OpenNMS(R) Application.
//
// OpenNMS(R) is Copyright (C) 2002-2003 The OpenNMS Group, Inc.  All rights reserved.
// OpenNMS(R) is a derivative work, containing both original code, included code and modified
// code that was published under the GNU General Public License. Copyrights for modified 
// and included code are below.
//
// OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
//
// Copyright (C) 1999-2001 Oculan Corp.  All rights reserved.
//
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
//
// For more information contact:
//      OpenNMS Licensing       <license@opennms.org>
//      http://www.opennms.org/
//      http://www.opennms.com///

--%>

<%@page language="java"
	contentType="text/html"
	session="true"
	import="java.util.*,
		java.text.DecimalFormat,
		org.opennms.web.element.NetworkElementFactory,
		org.opennms.web.event.*
		"
%>
<%@ taglib tagdir="/WEB-INF/tags/form" prefix="form" %>
<%!
    public static final DecimalFormat MINUTE_FORMAT = new DecimalFormat( "00" );
%>
<%
    //get the service names, in alpha order
    Map<String, Integer> serviceNameMap = new TreeMap<String, Integer>(NetworkElementFactory.getServiceNameToIdMap());
    Set<String> serviceNameSet = serviceNameMap.keySet();
    Iterator serviceNameIterator = serviceNameSet.iterator();

    //get the severity names, in severity order
    List severities = EventUtil.getSeverityList();
    Iterator severityIterator = severities.iterator();

    //get the current time values
    Calendar now = Calendar.getInstance();
    int nowHour = now.get(Calendar.HOUR); //gets the hour as a value between 1-12
    int nowMinute = now.get(Calendar.MINUTE);
    int nowAmPm = now.get(Calendar.AM_PM);
%>

<form action="event/query" method="get">
  <table width="100%" border="0" cellpadding="2" cellspacing="0">
    <tr>
      <td valign="top">
        <table width="100%" border="0" cellpadding="2" cellspacing="0" >
          <tr>
            <td>Event Text Contains:</td>
            <td>TCP/IP Address Like:</td>
          </tr>

          <tr>
            <td><input type="text" name="msgsub" /></td>
            <td><input type="text" name="iplike" value="*.*.*.*" /></td>
          </tr>

          <tr>
            <td>Node Label Contains:</td>
            <td>Severity:</td>
          </tr>

          <tr>
            <td><input type="text" name="nodenamelike" /></td>
            <td>
              <select name="severity" size="1">
                <option selected="selected"><%=EventUtil.ANY_SEVERITIES_OPTION%></option>

                <% while( severityIterator.hasNext() ) { %>
                  <% int severity = ((Integer)severityIterator.next()).intValue(); %>
                  <option value="<%=severity%>">
                    <%=EventUtil.getSeverityLabel(severity)%>
                  </option>
                <% } %>
              </select>
            </td>
          </tr>

          <tr>
            <td colspan="2">Service:</td>
          </tr>
          <tr>
            <td colspan="2">
              <select name="service" size="1">
                <option selected><%=EventUtil.ANY_SERVICES_OPTION%></option>

                <% while( serviceNameIterator.hasNext() ) { %>
                  <% String name = (String)serviceNameIterator.next(); %>
                  <option value="<%=serviceNameMap.get(name)%>"><%=name%></option>
                <% } %>
              </select>
            </td>
          </tr>

          <tr><td colspan="2"><hr width=100% /></td></tr>

          <tr>
            <td valign="top">
              <input type="checkbox" name="useaftertime" value="1" />Events After:
            </td>
            <td valign="top">
              <input type="checkbox" name="usebeforetime" value="1"/>Events Before:
            </td>
          </tr>
          <tr>
            <td>
              <select name="afterhour" size="1">
                <% for( int i = 1; i < 13; i++ ) { %>
                  <form:option value="<%=i%>" selected="<%= nowHour==i %>">
                    <%= i %>
                  </form:option>
                <% } %>
              </select>

              <input type="text" name="afterminute" size="4" maxlength="2" value="<%=MINUTE_FORMAT.format(nowMinute)%>" />

              <select name="afterampm" size="1">
                <form:option value="am" selected="<%=(nowAmPm == Calendar.AM && nowHour != 12)%>">AM</form:option>
                <form:option value="pm" selected="<%=(nowAmPm == Calendar.PM && nowHour == 12)%>">Noon</form:option>
                <form:option value="pm" selected="<%=(nowAmPm == Calendar.PM && nowHour != 12)%>">PM</form:option>
                <form:option value="am" selected="<%=(nowAmPm == Calendar.AM && nowHour == 12)%>">Midnight</form:option>
              </select>
            </td>
            <td>
              <select name="beforehour" size="1">
                <% for( int i = 1; i < 13; i++ ) { %>
                  <form:option value="<%=i%>" selected="<%=(nowHour==i)%>">
                    <%=i%>
                  </form:option>
                <% } %>
              </select>

              <input type="text" name="beforeminute" size="4" maxlength="2" value="<%=MINUTE_FORMAT.format(nowMinute)%>" />

              <select name="beforeampm" size="1">
                <form:option value="am" selected="<%=(nowAmPm == Calendar.AM && nowHour != 12) %>">AM</form:option>
                <form:option value="pm" selected="<%=(nowAmPm == Calendar.PM && nowHour == 12)%>">Noon</form:option>
                <form:option value="pm" selected="<%=(nowAmPm == Calendar.PM && nowHour != 12)%>">PM</form:option>
                <form:option value="am" selected="<%=(nowAmPm == Calendar.AM && nowHour == 12)%>">Midnight</form:option>
              </select>
            </td>
          </tr>
          <tr>
            <td>
              <select name="aftermonth" size="1">
                <% for( int i = 0; i < 12; i++ ) { %>
                  <form:option value="<%=i%>" selected="<%=(now.get(Calendar.MONTH)==i)%>">
                    <%=months[i]%>
                  </form:option>
                <% } %>
              </select>

              <input type="text" name="afterdate" size="4" maxlength="2" value="<%=now.get(Calendar.DATE)%>" />
              <input type="text" name="afteryear" size="6" maxlength="4" value="<%=now.get(Calendar.YEAR)%>" />
            </td>
            <td>
              <select name="beforemonth" size="1">
                <% for( int i = 0; i < 12; i++ ) { %>
                  <form:option value="<%=i%>" selected="<%=(now.get(Calendar.MONTH)==i)%>">
                    <%=months[i]%>
                  </form:option>
                <% } %>
              </select>

              <input type="text" name="beforedate" size="4" maxlength="2" value="<%=now.get(Calendar.DATE)%>" />
              <input type="text" name="beforeyear" size="6" maxlength="4" value="<%=now.get(Calendar.YEAR)%>" />
            </td>
          </tr>

          <tr><td colspan="2"><hr width=100% /></td></tr>

          <tr>
            <td>Sort By:</td>
            <td>Number of Events Per Page:</td>
          </tr>
          <tr>
            <td>
              <select name="sortby" size="1">
                <option value="id"           >Event ID  (Descending)</option>
                <option value="rev_id"       >Event ID  (Ascending) </option>
                <option value="severity"     >Severity  (Descending)</option>
                <option value="rev_severity" >Severity  (Ascending) </option>
                <option value="time"         >Time      (Descending)</option>
                <option value="rev_time"     >Time      (Ascending) </option>
                <option value="node"         >Node      (Ascending) </option>
                <option value="rev_node"     >Node      (Descending)</option>
                <option value="interface"    >Interface (Ascending) </option>
                <option value="rev_interface">Interface (Descending)</option>
                <option value="service"      >Service   (Ascending) </option>
                <option value="rev_service"  >Service   (Descending)</option>
              </select>
            </td>
            <td>
              <select name="limit" size="1">
                <option value="10">10 events</option>
                <option value="20">20 events</option>
                <option value="30">30 events</option>
                <option value="50">50 events</option>
                <option value="100">100 events</option>
              </select>
            </td>
          </tr>

          <tr><td colspan="2"><hr width=100% /></td></tr>

        </table>
      </td>
    </tr>
    <tr>
      <td>
        <input type="submit" value="Search" />
      </td>
    </tr>
  </table>
</form>


<%!
    public static String getAmPm( int hour ) {
        switch(hour) {
            case 24:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
                return "AM";
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
                return "PM";
            default:
                throw new IllegalArgumentException("Can only take hours 1-24, " + hour + " is illegal");
        }
    }

    //note these run from 0-11, this is because of java.util.Calendar!
    public static String[] months = new String[] {
        "Jan",
        "Feb",
        "Mar",
        "Apr",
        "May",
        "Jun",
        "Jul",
        "Aug",
        "Sep",
        "Oct",
        "Nov",
        "Dec"
    };
%>


