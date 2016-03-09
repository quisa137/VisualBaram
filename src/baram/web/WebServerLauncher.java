package baram.web;

import java.io.File;


import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;

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
		// TODO Auto-generated method stub
		String webappDirLocation = "web";
        String webPort = System.getenv("PORT");
        if(webPort == null || webPort.isEmpty()) {
            webPort = "8080";
        }
        
        Tomcat tomcat = new Tomcat();
        //포트 지정
        tomcat.setPort(Integer.valueOf(webPort));
        //톰캣 임시 폴더
        tomcat.setBaseDir("web/WEB-INF/");
        
        //Context 생성(설정을 담는 그릇 같은 거)
        StandardContext ctx = (StandardContext) tomcat.addWebapp("", new File(webappDirLocation).getAbsolutePath());
        
        //Servlet 설정 어노테이션으로 하는 방법도 있으나 실행 시점이 불명확함
        Tomcat.addServlet(ctx,"EntryServlet",new EntryServlet());
        ctx.addServletMapping("/*", "EntryServlet");

//        // Declare an alternative location for your "WEB-INF/classes" dir
//        // Servlet 3.0 annotation will work
        File additionWebInfClasses = new File("build").getAbsoluteFile();
        WebResourceRoot resources = new StandardRoot(ctx);
        DirResourceSet dirSet = new DirResourceSet(resources, "/WEB-INF/classes", additionWebInfClasses.getAbsolutePath(), "/");
        resources.addPreResources(dirSet);
        
        ctx.setResources(resources);
        
        tomcat.start();
        tomcat.getServer().await();
	}
}
