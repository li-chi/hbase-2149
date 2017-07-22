package org.apache.hadoop.hbase.generated.master;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import java.util.*;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.util.StringUtils;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.JvmVersion;
import org.apache.hadoop.hbase.util.FSUtils;
import org.apache.hadoop.hbase.master.HMaster;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.HServerInfo;
import org.apache.hadoop.hbase.HServerAddress;
import org.apache.hadoop.hbase.HTableDescriptor;

public final class master_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

  private static final JspFactory _jspxFactory = JspFactory.getDefaultFactory();

  private static java.util.Vector _jspx_dependants;

  private org.apache.jasper.runtime.ResourceInjector _jspx_resourceInjector;

  public Object getDependants() {
    return _jspx_dependants;
  }

  public void _jspService(HttpServletRequest request, HttpServletResponse response)
        throws java.io.IOException, ServletException {

    PageContext pageContext = null;
    HttpSession session = null;
    ServletContext application = null;
    ServletConfig config = null;
    JspWriter out = null;
    Object page = this;
    JspWriter _jspx_out = null;
    PageContext _jspx_page_context = null;

    try {
      response.setContentType("text/html;charset=UTF-8");
      pageContext = _jspxFactory.getPageContext(this, request, response,
      			null, true, 8192, true);
      _jspx_page_context = pageContext;
      application = pageContext.getServletContext();
      config = pageContext.getServletConfig();
      session = pageContext.getSession();
      out = pageContext.getOut();
      _jspx_out = out;
      _jspx_resourceInjector = (org.apache.jasper.runtime.ResourceInjector) application.getAttribute("com.sun.appserv.jsp.resource.injector");


  HMaster master = (HMaster)getServletContext().getAttribute(HMaster.MASTER);
  Configuration conf = master.getConfiguration();
  HServerAddress rootLocation = master.getCatalogTracker().getRootLocation();
  boolean metaOnline = master.getCatalogTracker().getMetaLocation() != null;
  Map<String, HServerInfo> serverToServerInfos =
    master.getServerManager().getOnlineServers();
  int interval = conf.getInt("hbase.regionserver.msginterval", 1000)/1000;
  if (interval == 0) {
      interval = 1;
  }
  boolean showFragmentation = conf.getBoolean("hbase.master.ui.fragmentation.enabled", false);
  Map<String, Integer> frags = null;
  if (showFragmentation) {
      frags = FSUtils.getTableFragmentation(master);
  }

      out.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \n  \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\"> \n<html xmlns=\"http://www.w3.org/1999/xhtml\">\n<head><meta http-equiv=\"Content-Type\" content=\"text/html;charset=UTF-8\"/>\n<title>HBase Master: ");
      out.print( master.getMasterAddress().getHostname());
      out.write(':');
      out.print( master.getMasterAddress().getPort() );
      out.write("</title>\n<link rel=\"stylesheet\" type=\"text/css\" href=\"/static/hbase.css\" />\n</head>\n<body>\n<a id=\"logo\" href=\"http://wiki.apache.org/lucene-hadoop/Hbase\"><img src=\"/static/hbase_logo_med.gif\" alt=\"HBase Logo\" title=\"HBase Logo\" /></a>\n<h1 id=\"page_title\">Master: ");
      out.print(master.getMasterAddress().getHostname());
      out.write(':');
      out.print(master.getMasterAddress().getPort());
      out.write("</h1>\n<p id=\"links_menu\"><a href=\"/logs/\">Local logs</a>, <a href=\"/stacks\">Thread Dump</a>, <a href=\"/logLevel\">Log Level</a></p>\n\n<!-- Various warnings that cluster admins should be aware of -->\n");
 if (JvmVersion.isBadJvmVersion()) { 
      out.write("\n  <div class=\"warning\">\n  Your current JVM version ");
      out.print( System.getProperty("java.version") );
      out.write(" is known to be\n  unstable with HBase. Please see the\n  <a href=\"http://wiki.apache.org/hadoop/Hbase/Troubleshooting#A18\">HBase wiki</a>\n  for details.\n  </div>\n");
 } 
      out.write('\n');
 if (!FSUtils.isAppendSupported(conf) && FSUtils.isHDFS(conf)) { 
      out.write("\n  <div class=\"warning\">\n  You are currently running the HMaster without HDFS append support enabled.\n  This may result in data loss.\n  Please see the <a href=\"http://wiki.apache.org/hadoop/Hbase/HdfsSyncSupport\">HBase wiki</a>\n  for details.\n  </div>\n");
 } 
      out.write("\n\n<hr id=\"head_rule\" />\n\n<h2>Master Attributes</h2>\n<table>\n<tr><th>Attribute Name</th><th>Value</th><th>Description</th></tr>\n<tr><td>HBase Version</td><td>");
      out.print( org.apache.hadoop.hbase.util.VersionInfo.getVersion() );
      out.write(", r");
      out.print( org.apache.hadoop.hbase.util.VersionInfo.getRevision() );
      out.write("</td><td>HBase version and svn revision</td></tr>\n<tr><td>HBase Compiled</td><td>");
      out.print( org.apache.hadoop.hbase.util.VersionInfo.getDate() );
      out.write(',');
      out.write(' ');
      out.print( org.apache.hadoop.hbase.util.VersionInfo.getUser() );
      out.write("</td><td>When HBase version was compiled and by whom</td></tr>\n<tr><td>Hadoop Version</td><td>");
      out.print( org.apache.hadoop.util.VersionInfo.getVersion() );
      out.write(", r");
      out.print( org.apache.hadoop.util.VersionInfo.getRevision() );
      out.write("</td><td>Hadoop version and svn revision</td></tr>\n<tr><td>Hadoop Compiled</td><td>");
      out.print( org.apache.hadoop.util.VersionInfo.getDate() );
      out.write(',');
      out.write(' ');
      out.print( org.apache.hadoop.util.VersionInfo.getUser() );
      out.write("</td><td>When Hadoop version was compiled and by whom</td></tr>\n<tr><td>HBase Root Directory</td><td>");
      out.print( FSUtils.getRootDir(master.getConfiguration()).toString() );
      out.write("</td><td>Location of HBase home directory</td></tr>\n<tr><td>Load average</td><td>");
      out.print( StringUtils.limitDecimalTo2(master.getServerManager().getAverageLoad()) );
      out.write("</td><td>Average number of regions per regionserver. Naive computation.</td></tr>\n");
  if (showFragmentation) { 
      out.write("\n        <tr><td>Fragmentation</td><td>");
      out.print( frags.get("-TOTAL-") != null ? frags.get("-TOTAL-").intValue() + "%" : "n/a" );
      out.write("</td><td>Overall fragmentation of all tables, including .META. and -ROOT-.</td></tr>\n");
  } 
      out.write("\n<tr><td>Zookeeper Quorum</td><td>");
      out.print( master.getZooKeeperWatcher().getQuorum() );
      out.write("</td><td>Addresses of all registered ZK servers. For more, see <a href=\"/zk.jsp\">zk dump</a>.</td></tr>\n</table>\n\n<h2>Catalog Tables</h2>\n");
 
  if (rootLocation != null) { 
      out.write("\n<table>\n<tr>\n    <th>Table</th>\n");
  if (showFragmentation) { 
      out.write("\n        <th title=\"Fragmentation - Will be 0% after a major compaction and fluctuate during normal usage.\">Frag.</th>\n");
  } 
      out.write("\n    <th>Description</th>\n</tr>\n<tr>\n    <td><a href=\"table.jsp?name=");
      out.print( Bytes.toString(HConstants.ROOT_TABLE_NAME) );
      out.write('"');
      out.write('>');
      out.print( Bytes.toString(HConstants.ROOT_TABLE_NAME) );
      out.write("</a></td>\n");
  if (showFragmentation) { 
      out.write("\n        <td align=\"center\">");
      out.print( frags.get("-ROOT-") != null ? frags.get("-ROOT-").intValue() + "%" : "n/a" );
      out.write("</td>\n");
  } 
      out.write("\n    <td>The -ROOT- table holds references to all .META. regions.</td>\n</tr>\n");

    if (metaOnline) { 
      out.write("\n<tr>\n    <td><a href=\"table.jsp?name=");
      out.print( Bytes.toString(HConstants.META_TABLE_NAME) );
      out.write('"');
      out.write('>');
      out.print( Bytes.toString(HConstants.META_TABLE_NAME) );
      out.write("</a></td>\n");
  if (showFragmentation) { 
      out.write("\n        <td align=\"center\">");
      out.print( frags.get(".META.") != null ? frags.get(".META.").intValue() + "%" : "n/a" );
      out.write("</td>\n");
  } 
      out.write("\n    <td>The .META. table holds references to all User Table regions</td>\n</tr>\n  \n");
  } 
      out.write("\n</table>\n");
} 
      out.write("\n\n<h2>User Tables</h2>\n");
 HTableDescriptor[] tables = new HBaseAdmin(conf).listTables(); 
   if(tables != null && tables.length > 0) { 
      out.write("\n<table>\n<tr>\n    <th>Table</th>\n");
  if (showFragmentation) { 
      out.write("\n        <th title=\"Fragmentation - Will be 0% after a major compaction and fluctuate during normal usage.\">Frag.</th>\n");
  } 
      out.write("\n    <th>Description</th>\n</tr>\n");
   for(HTableDescriptor htDesc : tables ) { 
      out.write("\n<tr>\n    <td><a href=table.jsp?name=");
      out.print( htDesc.getNameAsString() );
      out.write('>');
      out.print( htDesc.getNameAsString() );
      out.write("</a> </td>\n");
  if (showFragmentation) { 
      out.write("\n        <td align=\"center\">");
      out.print( frags.get(htDesc.getNameAsString()) != null ? frags.get(htDesc.getNameAsString()).intValue() + "%" : "n/a" );
      out.write("</td>\n");
  } 
      out.write("\n    <td>");
      out.print( htDesc.toString() );
      out.write("</td>\n</tr>\n");
   }  
      out.write("\n\n<p> ");
      out.print( tables.length );
      out.write(" table(s) in set.</p>\n</table>\n");
 } 
      out.write("\n\n<h2>Region Servers</h2>\n");
 if (serverToServerInfos != null && serverToServerInfos.size() > 0) { 
      out.write('\n');
   int totalRegions = 0;
     int totalRequests = 0; 

      out.write("\n\n<table>\n<tr><th rowspan=\"");
      out.print( serverToServerInfos.size() + 1);
      out.write("\"></th><th>Address</th><th>Start Code</th><th>Load</th></tr>\n");
   String[] serverNames = serverToServerInfos.keySet().toArray(new String[serverToServerInfos.size()]);
     Arrays.sort(serverNames);
     for (String serverName: serverNames) {
       HServerInfo hsi = serverToServerInfos.get(serverName);
       String hostname = hsi.getServerAddress().getHostname() + ":" + hsi.getInfoPort();
       String url = "http://" + hostname + "/";
       totalRegions += hsi.getLoad().getNumberOfRegions();
       totalRequests += hsi.getLoad().getNumberOfRequests() / interval;
       long startCode = hsi.getStartCode();

      out.write("\n<tr><td><a href=\"");
      out.print( url );
      out.write('"');
      out.write('>');
      out.print( hostname );
      out.write("</a></td><td>");
      out.print( startCode );
      out.write("</td><td>");
      out.print( hsi.getLoad().toString(interval) );
      out.write("</td></tr>\n");
   } 
      out.write("\n<tr><th>Total: </th><td>servers: ");
      out.print( serverToServerInfos.size() );
      out.write("</td><td>&nbsp;</td><td>requests=");
      out.print( totalRequests );
      out.write(", regions=");
      out.print( totalRegions );
      out.write("</td></tr>\n</table>\n\n<p>Load is requests per second and count of regions loaded</p>\n");
 } 
      out.write("\n</body>\n</html>\n");
    } catch (Throwable t) {
      if (!(t instanceof SkipPageException)){
        out = _jspx_out;
        if (out != null && out.getBufferSize() != 0)
          out.clearBuffer();
        if (_jspx_page_context != null) _jspx_page_context.handlePageException(t);
      }
    } finally {
      _jspxFactory.releasePageContext(_jspx_page_context);
    }
  }
}
