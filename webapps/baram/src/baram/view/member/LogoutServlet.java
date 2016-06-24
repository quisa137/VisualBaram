package baram.view.member;
 
import java.io.IOException;
 
import java.io.PrintWriter;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
 
import baram.view.User;

public class LogoutServlet extends HttpServlet {
	
    private static final long serialVersionUID = -201510211346L;
    
    Logger logger = Logger.getLogger(LogoutServlet.class.getName());
    
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        User.logoutSession(request, logger, "LogoutServlet.service", out);
        
        out.println("<script>");
    	out.println("alert('로그아웃 되었습니다.');");
		out.println("location.href='./Main'");
		out.println("</script>");
    }
}