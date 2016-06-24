package baram.view;
 
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import baram.view.User;
 
public class LoginPage extends HttpServlet {
    
	private static final long serialVersionUID = 201510211023L;
	private static StringBuilder pageHtml = new StringBuilder();
	static {
		pageHtml.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">");
		pageHtml.append("<html>");
		pageHtml.append("<head>");
		pageHtml.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");
		pageHtml.append("<title>Login</title>");
		pageHtml.append("<link rel='shortcut icon' type='image/x-icon' href='baramicon.bmp'>");
		pageHtml.append("<link rel='stylesheet' type='text/css' href='css/reset.css' />");
		pageHtml.append("<link rel='stylesheet' type='text/css' href='css/login.css' />");
		pageHtml.append("</head>");
		pageHtml.append("<body>");
		pageHtml.append("<div class='LoginHeader'>");
		pageHtml.append("			<h1 class='loginLogo'><a href='./Main'><img src='img/logo.png' /></a></h1>");
		pageHtml.append("		</div>");
		pageHtml.append("		<div class='LoginContainer'>");
		pageHtml.append("			<div class=\"loginWrap\">");
		pageHtml.append("				<div class=\"loginTop\">");
		pageHtml.append("					<a>LOGIN</a>");
		pageHtml.append("				</div>");
		pageHtml.append("				<div class=\"loginBottom\">");
		pageHtml.append("					<div class=\"loginBottomTop\">");
		pageHtml.append("						<form name=\"loginform\" action=\"./Login\" method=\"post\">");
		pageHtml.append("							<div class=\"loginIdInPutWrap\">");
		pageHtml.append("								<input class=\"loginIdInPut\" type=\"text\" name=\"id\" />");
		pageHtml.append("								<img");
		pageHtml.append("								src=\"img/loginID.png\" class=\"loginIdInPutImg\" />");
		pageHtml.append("							</div>");
		pageHtml.append("							<div class=\"loginPasswdInPutWrap\">");
		pageHtml.append("								<input class=\"loginPasswdInPut\" type=\"password\" name=\"password\"/>");
		pageHtml.append("								<img");
		pageHtml.append("								src=\"img/loginPasswd.png\" class=\"loginPasswdInPutImg\" />");
		pageHtml.append("							</div>");
		pageHtml.append("							<input onclick=\"javascript:loginform.submit()\" class=\"loginBtn\" type=\"submit\" value=\"Login\" />");
		pageHtml.append("						</form>");
		pageHtml.append("					</div>");
		pageHtml.append("					<div class=\"loginBottomBottom\">");
		pageHtml.append("						<a href=\"./JoinPage\" class=\"createuser\">Create an account</a>");
		pageHtml.append("					</div>");
		pageHtml.append("				</div>");
		pageHtml.append("			</div>");
		pageHtml.append("		</div>");
		pageHtml.append("		<div class='feedBackpopupWrap'>");
		pageHtml.append("			<div class='feedBackpopupbg'>");
		pageHtml.append("			</div>");
		pageHtml.append("			<div class='feedBackpopup'></div>");
		pageHtml.append("		</div>");
		pageHtml.append("		<div class='upgradepopupWrap'>");
		pageHtml.append("			<div class='upgradepopupbg'>");
		pageHtml.append("			</div>");
		pageHtml.append("			<div class='upgradepopup'></div>");
		pageHtml.append("		</div>");
		pageHtml.append("</body>");
		pageHtml.append("</html>");
	}
	
    public LoginPage() {
        super();
    }
    
    Logger logger =  Logger.getLogger(LoginPage.class.getName());
    
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        if(User.checkSession(request, logger, this.getClass().getName()+".service", this)) {
        	Object refObj = request.getSession().getAttribute("ref");
            String ref = null;
            if(null==refObj) {
            	ref = "./Main";
            } else {
            	ref = (String)refObj;
            }
			out.println("<script>");
        	out.println("alert('이미 로그인이 되어 있는 사용자입니다.');");
        	out.println("location.href='"+ref+"'");
  		  	out.println("</script>");
			out.close();
			return;
		} else {
			out.println(pageHtml.toString());
		}
        out.close();
    }
}