package baram.view.member;
 
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import baram.view.User;
 
public class LoginServlet extends HttpServlet {
    
	private static final long serialVersionUID = 201510201543L;
 
    public LoginServlet() {
        super();
    }
    
    Logger logger =  Logger.getLogger(LoginServlet.class.getName());
    
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        boolean pass = User.login(request, logger, "LoginServlet", this);
        HttpSession session = request.getSession();
        if(pass) {
        	User user = (User)(session.getAttribute("user")); 
        	Object refObj = request.getSession().getAttribute("ref");
            if(null==refObj) {
            	if(user.getGroup().equals("1")) {
            		out.println("<script>");
          		  	out.println("location.href='./GetAllMemberInfo'");
          		  	out.println("</script>");
            	} else {
            		out.println("<script>");
          		  	out.println("location.href='./Monitoring'");
          		  	out.println("</script>");
            	}
            } else {
            	out.println("<script>");
      		  	out.println("location.href='"+(String)refObj+"'");
      		  	out.println("</script>");
            }
        	
        } else {
        	Object statusObj = session.getAttribute("status");
        	if (statusObj==null || ((String)statusObj).equals("internal error")) {
        		out.println("<script>");
            	out.println("alert('로그인하는 동안 에러가 발생하였습니다. 관리자에게 문의해 주세요.');");
      		  	out.println("location.href='./LoginPage'");
      		  	out.println("</script>");
            } else if(((String)statusObj).equals("invalid password")) {
            	out.println("<script>");
            	out.println("alert('존재하지 않는 아이디 입니다. 회원가입 후 이용해 주세요');");
      		  	out.println("location.href='./LoginPage'");
      		  	out.println("</script>");
            } else {
            	out.println("<script>");
            	out.println("alert('잘못된 패스워드입니다. 패스워드를 확인해 주세요.');");
      		  	out.println("location.href='./LoginPage'");
      		  	out.println("</script>");
            }
        }
    }
}