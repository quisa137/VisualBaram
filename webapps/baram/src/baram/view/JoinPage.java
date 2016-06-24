package baram.view;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import baram.view.User;

public class JoinPage extends HttpServlet {

	private static final long serialVersionUID = 201510211023L;
	private static StringBuilder pageHtml = new StringBuilder();
	static {
		pageHtml.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">");
		pageHtml.append("<html>");
		pageHtml.append("<head>");
		pageHtml.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");
		pageHtml.append("<title>Join</title>");
		pageHtml.append("<link rel='shortcut icon' type='image/x-icon' href='baramicon.bmp'>");
		pageHtml.append("<link rel='stylesheet' type='text/css' href='css/reset.css' />");
		pageHtml.append("<link rel='stylesheet' type='text/css' href='css/jquery-ui.css' />");
		pageHtml.append("<link rel='stylesheet' type='text/css' href='css/style.css' />");
		pageHtml.append("<link rel='stylesheet' type='text/css' href='css/join.css' />");
		pageHtml.append("<script type=\"text/javascript\" src=\"js/jquery.js\"></script>");
		pageHtml.append("<script type=\"text/javascript\" src=\"js/jquery_ui.js\"></script>");
		pageHtml.append("<script type=\"text/javascript\" src=\"js/select.js\"></script>");
		pageHtml.append("</head>");
		pageHtml.append("<body>");
		pageHtml.append("<div class='Header'>");
		pageHtml.append("<h1 class='joinLogo'><a href='./Main'><img src='img/logo.png' /></a></h1>");
		pageHtml.append("</div>");
		pageHtml.append("<div class='joinContainer'>");
		pageHtml.append("<div class='joinFormWrap'>");
		pageHtml.append("<div class='joinFormHeader'>");
		pageHtml.append("<img alt='' src='img/joinicon.png' class='joinicon'>");
		pageHtml.append("	<a class='joinTitle'>BARAM 사용자 등록");
		pageHtml.append("</a>");

		pageHtml.append("	</div>");
		pageHtml.append("<div class='joinFormContent'>");
		pageHtml.append("	<div class='joinFormContentTop'>");
		pageHtml.append("		사용자 정보 입력");
		pageHtml.append("	</div>");
		pageHtml.append("	<form name='' action='./Join' method='post'>");
		pageHtml.append("	<div class='joinFormContentMiddle'>");
		pageHtml.append("		<div class='joinFormContentMiddleLeft'>");
		pageHtml.append("			<p class='joinFormContentMiddleLeftTop'>회원가입을 위한 정보를 입력해 주세요</p>");
		pageHtml.append("			<div class='joinInputWrap'>");
		pageHtml.append("			<p class='joinInputText'>아이디</p>");
		pageHtml.append("			<input class='joinInputbox' type='text' name='id' />");
		pageHtml.append("			</div>");
		pageHtml.append("		<div class='joinInputWrap'>");
		pageHtml.append("		<p class='joinInputText'>비밀번호</p>");
		pageHtml.append("		<input class='joinInputbox' type='password' name='password' />");
		pageHtml.append("		</div>");
		pageHtml.append("		<div class='joinInputWrap'>");
		pageHtml.append("			<p class='joinInputText'>이름</p>");
		pageHtml.append("			<input class='joinInputbox' type='text' name='name' />");
		pageHtml.append("		</div>");

		pageHtml.append("		<div class='joinInputWrap'>");
		pageHtml.append("			<p class='joinInputText'>Group</p>");
		pageHtml.append("<div class='joinSelectboxWrap'>");
		pageHtml.append("			<select name='group' class='SlectBox joinSelectBoxStyle'>");
		pageHtml.append("					<option  value=''>그룹을 선택하세요</option>");
		pageHtml.append("					<option  value='1'>admin</option>");
		pageHtml.append("					<option  value='2'>user1</option>");
		pageHtml.append("				<option  value='3'>user2</option>");
		pageHtml.append("				<option value='4'>guest</option>");
		pageHtml.append("		</select>");
		pageHtml.append("</div>");

		pageHtml.append("	</div>");
		pageHtml.append("		<div class='joinInputWrap'>");
		pageHtml.append("			<p class='joinInputText'>이메일</p>");
		pageHtml.append("			<input class='joinInputbox' type='text' name='email' />");
		pageHtml.append("		</div>");
		pageHtml.append("	</div>");
		pageHtml.append("		<div class='joinContentMiddleRight'>");
		pageHtml.append("			<div class='joinContentMiddleRightTop'>");
		pageHtml.append("				<p class='joinContentMiddleRightTopTitle'>BARAM</p>");
		pageHtml.append("			<p class='joinContentMiddleRightTopSubTitle'>Big data Analysis Real time Activator & Monitor</p>");
		pageHtml.append("		</div>");
		pageHtml.append("		<div class='joinContentMiddleRightBottom'></div>");

		pageHtml.append("	</div>");
		pageHtml.append("</div>");
		pageHtml.append("<div class='joinContentBottom'>");

		pageHtml.append("<div class='joinContentBottombutton'>");
		pageHtml.append("<input type='submit' value='정보등록' class='loginBtn' />");
		pageHtml.append("");
		pageHtml.append("<input type='reset' value='다시작성' class='loginBtn' />");
		pageHtml.append("	</div>");

		pageHtml.append("	</div>");
		pageHtml.append("</form>");
		pageHtml.append("</div>");
		pageHtml.append("</div>");
		pageHtml.append("</div>");
		pageHtml.append("</body>");
		pageHtml.append("</html>");
	}

	public JoinPage() {
		super();
	}

	Logger logger = Logger.getLogger(JoinPage.class.getName());

	protected void service(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();
		if (User.checkSession(request, logger, this.getClass().getName()
				+ ".service", this)) {
			Object refObj = request.getSession().getAttribute("ref");
			String ref = null;
			if (null == refObj) {
				ref = "./Main";
			} else {
				ref = (String) refObj;
			}
			out.println("<script>");
			out.println("alert('이미 로그인이 되어 있는 사용자입니다.');");
			out.println("location.href='" + ref + "'");
			out.println("</script>");
			out.close();
			return;
		} else {
			out.println(pageHtml.toString());
		}
		out.close();
	}
}