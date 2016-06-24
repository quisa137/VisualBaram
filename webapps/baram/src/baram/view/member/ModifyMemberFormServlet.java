package baram.view.member;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import baram.view.Database;
import baram.view.User;
import baram.view.CommonHtml;

public class ModifyMemberFormServlet extends HttpServlet {

	private static final long serialVersionUID = -201510211342L;

	Logger logger = Logger.getLogger(ModifyMemberFormServlet.class.getName());

	protected void service(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("text/html;charset=UTF-8");

		PrintWriter out = response.getWriter();

		HttpSession session = request.getSession(true);
		if (!User.checkSession(request, logger, "MyPage.service", this)) {
			session.setAttribute("ref", "./MyPage");
			out.println("<script>");
			out.println("alert('잘못된 접근입니다. 로그인 후 이용해 주세요.');");
			out.println("location.href='./LoginPage'");
			out.println("</script>");
			out.close();
			return;
		}
		User user = (User) (session.getAttribute("user"));

		out.println(CommonHtml.getHtml("head", new String[] {
				"<title>Analysis Demo</title>", "<title>내정보 수정</title>" }));
		out.println(CommonHtml.getHtml("top", new String[] { "##username##",
				user.getName() }));

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			conn = Database.getConnection(logger);
			stmt = conn.createStatement();
			rs = stmt.executeQuery("select * from member where id='"
					+ user.getId() + "'");

			out.println("<div class='Container'>");

			out.println(CommonHtml.getHtml("side", null));

			out.println("<div class='Contents'>");

			out.println("<div class='ContentsTop'>");
			out.println("<a class='ContentsTitle'>사용자 정보 수정</a>");
			out.println("</div>");

			out.println("<div class='ContentsBottom'>");
			out.println("<div class='settingsWrap'>");
			out.println( user.getName()+ " 님의 사용자 정보입니다. 사용자정보는 개인정보취급방침에 따라 안전하게 보호되며, 회원님의 명백한 동의 없이 공개 또는 제 3자에게 제공되지 않습니다.");
			out.println("</div>");

			out.println("<div class='ContentsBottomBottom'>");
			out.println("<div class='modifyFormWrap'>");
			out.println("<ul class='modifyFormLeftWrap'>");
			out.println("<li class='modifyFormLeftLi'>아이디 : ");
			
			out.println("</li>");
			out.println("<li class='modifyFormLeftLi'>비밀번호 : ");
			out.println("</li>");
			out.println("<li class='modifyFormLeftLi'>이름 : ");
			out.println("</li>");
			out.println("<li class='modifyFormLeftLi'>Group : ");
			out.println("</li>");
			out.println("<li class='modifyFormLeftLi'>이메일 : ");
			out.println("</li>");
			out.println("</ul>");
			
			
			
			out.println("	<form name='' action='' method='post'>");
			while (rs.next()) {
			out.println("<ul class='modifyFormRightWrap'>");
			out.println("<li class='modifyFormRightLi'><a>");
			out.println(rs.getString("id"));
			out.println("</a><br><a>아이디는 변경이 불가능 합니다.</a></li>");
			out.println("<li class='modifyFormRightLi'>");
			out.println("<input type='password' name='password' placeholder=''/>");
			out.println("<br><a>변경할 비밀번호를 넣어주세요</a></li>");
			out.println("<li class='modifyFormRightLi'><a>");
			out.println(rs.getString("name"));
			out.println("</a><br><a>개명으로 이름이 변경된 경우에 한하여 변경이 가능합니다. (관리자에게 문의하세요.)</a></li>");
			out.println("<li class='modifyFormRightLi'><a>");
			out.println(rs.getString("group"));
			out.println("</a><br></li>");
			out.println("<li class='modifyFormRightLi'><a>");
			out.println("	<input type='text' name='email' value='"+ rs.getString("email") + "' />");
			out.println("</a></li>");
			out.println("</ul>");
			out.println("<input type='submit' value='정보 수정' />");
			}
			out.println("</div>");
			
			

			
			
				
			
				
				
				
			out.println("</form>");

			out.println("</div>");

			out.println("</div>");

			out.println("</div>");
			out.println("</div>");

			out.println(CommonHtml.getHtml("foot", null));
		} catch (SQLException e) {
			e.printStackTrace();
			logger.log(Level.SEVERE,
					"Error in GetAllMemberInfoServlet.service.", e);
		} finally {
			try {
				rs.close();
			} catch (SQLException e) {
			}
			try {
				stmt.close();
			} catch (SQLException e) {
			}
			try {
				conn.close();
			} catch (SQLException e) {
			}
			try {
				out.close();
			} catch (Exception e) {
			}
		}

	}

}