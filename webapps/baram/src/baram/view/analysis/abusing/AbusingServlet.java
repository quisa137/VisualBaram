package baram.view.analysis.abusing;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import baram.view.User;
import baram.view.CommonHtml;

public class AbusingServlet extends HttpServlet {

	private static final long serialVersionUID = -201511082233L;
	Logger logger = Logger.getLogger(AbusingServlet.class.getName());
	
	private static String appName = "abusing";
	
	protected void service(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("text/html;charset=UTF-8");

		PrintWriter out = response.getWriter();

		HttpSession session = request.getSession(true);
		if (!User.checkSession(request, logger,
				this.getClass().getName()+".service", this)) {
			session.setAttribute("ref", "./Abusing");
			out.println("<script>");
			out.println("alert('잘못된 접근입니다. 로그인 후 이용해 주세요.');");
			out.println("location.href='./LoginPage'");
			out.println("</script>");
			out.close();
			return;
		}

		User user = (User) (session.getAttribute("user"));

		out.println(CommonHtml.getHtml("head", new String[] {
				"<title>Analysis Demo</title>",
				"<title>Abusing</title>"}));
				
		out.println(CommonHtml.getHtml("top", new String[]{"##username##", user.getName()}));
		
		out.println("<div class='Container'>");
		
		out.println(CommonHtml.getHtml("side", new String[]{"<div class='sideNoClickAction' id='side12'></div>", "<div class='sideClickAction'></div>", "img/side/s12.png", "img/side/s12_a.png"}));
		
		out.println("<div class='Contents'>");
		out.println("<p class='ContentsTop'>");
		out.println("<a class='ContentsTitle'>Demo Title 10</a>");
		out.println("</p>");

		out.println("<div class='ContentsBottom'>");
		out.println("<div class='settingsWrap'>");
		out.println("<div class='settingsText settingsTextStyle_red'>* 참고용으로 네트워크 분석을 통해 여러 현상을 파악할 수 있도록 개발 진행 중, 라이센스 문제로 자체  시각화 개발 진행 중</div>");
		out.println("</div>");
		
		
		out.print("<iframe id='charting-frame' src='");
		out.print(appName+"?dataType=html");
		out.print("' width='100%' height='768px' frameBorder='0' scrolling='no'></iframe>\n");
		
		out.println("</div>");
		out.println("</div>");
		out.println("</div>");

		out.println(CommonHtml.getHtml("foot", null));

		out.close();
	}

}