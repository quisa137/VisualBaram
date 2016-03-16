package baram.web;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * 필터 관련 세팅은 여기서 한다.
 * @author SGcom
 *
 */
@WebListener
public class EntryListener implements ServletContextListener {

    public EntryListener() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // TODO Auto-generated method stub
        ServletContext sc = (ServletContext) sce.getServletContext();
//        ServletRegistration.Dynamic dynamicServlet = sc.addServlet("ManagerServlet", ManagerServlet.class);
//        dynamicServlet.addMapping("/manager");
//        dynamicServlet.setServletSecurity(new ServletSecurityElement());

        FilterRegistration fr = sc.addFilter("SetEncodingFilter", org.apache.catalina.filters.SetCharacterEncodingFilter.class);
        fr.setInitParameter("encoding", "UTF-8");
        fr.addMappingForUrlPatterns(null, false, "/*");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // TODO Auto-generated method stub
        
    }

}
