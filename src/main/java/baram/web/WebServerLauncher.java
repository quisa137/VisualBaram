package baram.web;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Scanner;

import org.apache.catalina.Host;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardEngine;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.startup.ContextConfig;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Baram 톰캣 로딩 하는 역할을 한다. 
 * 바람에 포함된 
 * lib-hive/hive-jdbc-1.2.1-standalone.jar,
 * lib-spark/spark-assembly-1.4.0-hadoop2.6.0.jar
 * lib-spark/spark-examples-1.4.0-hadoop2.6.0.jar 들이 
 * tomcat-embed.jar보다 먼저 BuildPath에 들어가면 충돌하여 제대로 동작되지 않는다.
 * 
 * 현재 baram library의 jar 파일이 너무 많아서 시간이 걸린다. 필요한 jar파일만 로딩할 방법이 필요하다.
 * Run configure에서 필요한 파일만 로딩하도록 classpath를 변경함
 * @author SGcom
 *
 */
public class WebServerLauncher {
    
    public static void main(String[] args) throws Exception {
        Enumeration e = NetworkInterface.getNetworkInterfaces();
        while(e.hasMoreElements())
        {
            NetworkInterface n = (NetworkInterface) e.nextElement();
            Enumeration ee = n.getInetAddresses();
            while (ee.hasMoreElements())
            {
                InetAddress i = (InetAddress) ee.nextElement();
                System.out.println(i.getHostAddress());
            }
        }
        
		// TODO Auto-generated method stub
		String webappDirLocation = "web";
		Config c = Config.getInstance();
		JSONArray configs = c.getConfigArray("config");
		Iterator itConfigs = configs.iterator();
		
		while(itConfigs.hasNext()) {
		    JSONObject obj = (JSONObject)itConfigs.next();
		    String baseFolder = obj.getString("basePath");
		    File docBase = new File(baseFolder.split("/")[0]);
		    JSONArray classesFolders = obj.getJSONArray("classesFolder");
		  //Baram Visualization
	        Tomcat tomcat = new Tomcat();
	        //포트 지정
	        tomcat.setPort(obj.getInt("port"));
            tomcat.getConnector().setURIEncoding("UTF-8");
	        
	        StandardHost host = (StandardHost)tomcat.getHost();
	        //host.setName(InetAddress.getByName("eno16777736").getHostAddress());
	        host.setAutoDeploy(true);
	        host.setUnpackWARs(true);
	        
	        if(baseFolder.startsWith("webapps")){
	            host.setAppBase(docBase.getAbsolutePath());
	        }else{
	            //톰캣 임시 폴더
	            tomcat.setBaseDir(baseFolder);
	        }
            //Context 생성(설정을 담는 그릇 같은 거)
            StandardContext ctx = (StandardContext) tomcat.addWebapp(host,"", new File(baseFolder).getAbsolutePath());
            ctx.setCrossContext(true);
            ctx.setReloadable(true);
	        
	        //Servlet 설정 어노테이션으로 하는 방법도 있으나 실행 시점이 불명확함
	        /*
	        Tomcat.addServlet(ctx,"EntryServlet",new EntryServlet());
	        ctx.addServletMapping("/api/*", "EntryServlet");
	        ctx.addServletMapping("/*", "EntryServlet");
	        */
	        // Declare an alternative location for your "WEB-INF/classes" dir
	        // Servlet 3.0 annotation will work
	        Iterator itClassesFolders = classesFolders.iterator();
            
	        WebResourceRoot resources = new StandardRoot(ctx);
            
	        while(itClassesFolders.hasNext()) {
	            String classesFolder = (String) itClassesFolders.next();
	            File additionWebInfClasses = new File(classesFolder).getAbsoluteFile();
	            resources.addPreResources(new DirResourceSet(resources, "/WEB-INF/classes", additionWebInfClasses.getAbsolutePath(), "/"));
	        }
	        ctx.setResources(resources);
	        tomcat.start();
	        
	        if(!itConfigs.hasNext()) {
	            tomcat.getServer().await();
	        }
		}
	}
}
