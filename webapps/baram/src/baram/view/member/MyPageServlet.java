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
import baram.view.MainServlet;
import baram.view.User;
import baram.view.CommonHtml;

public class MyPageServlet extends HttpServlet {

	private static final long serialVersionUID = -201510211342L;

	Logger logger = Logger.getLogger(MyPageServlet.class.getName());

	protected void service(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("text/html;charset=UTF-8");

		PrintWriter out = response.getWriter();

		HttpSession session = request.getSession(true);
		if (!User.checkSession(request, logger,
				"GetAllMemberInfoServlet.service", this)) {
			session.setAttribute("ref", "./GetAllMemberInfo");
			out.println("<script>");
			out.println("alert('잘못된 접근입니다. 로그인 후 이용해 주세요.');");
			out.println("location.href='./LoginPage'");
			out.println("</script>");
			out.close();
			return;
		}
		User user = (User) (session.getAttribute("user"));

		out.println(CommonHtml.getHtml("head", new String[] {
				"<title>Analysis Demo</title>", "<title>MemberList</title>" }));
		out.println(CommonHtml.getHtml("top", new String[] { "##username##",
				user.getName() }));

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			conn = Database.getConnection(logger);
			stmt = conn.createStatement();
			rs = stmt.executeQuery("select * from member where id='"+user.getId()+"'");

			out.println("<div class='Container'>");

			out.println(CommonHtml.getHtml("side", null));

			out.println("<div class='Contents'>");

			out.println("<div class='ContentsTop'>");
			out.println("<a class='ContentsTitle'>Team</a>");

			

			out.println("</div>");

			out.println("<div class='ContentsBottom'>");
			out.println("<div class='memberSettingsWrap'>");
			out.println("<div class='userAddWrapBox'>");
			out.println("<input />");
			out.println("<a class='quickstartBtnTeam'>Search Member</a>");
			out.println("</div>");
			out.println("</div>");
			
			
			out.println("<ul class='memberListTitle'>");
			out.println("<li class='memberId'>ID</li>");
			// out.println("<li class='memberPwd'>Password</li>");
			out.println("<li class='memberName'>NAME</li>");
			out.println("<li class='memberMail'>E-mail</li>");
			out.println("<li class='memberGroupEvel'>Group Evel</li>");
			out.println("<li class='memberModi'>정보 수정</li>");
			out.println("<li class='memberDelet'>탈퇴</li>");
			out.println("</ul>");

			out.println("<ul class='ContentsBottomBottom'>");

			while (rs.next()) {
				out.println("<ul  class='memberList'><li class='memberId'>"
						+ rs.getString("id")
						+ "</li><li class='memberName'>"
						+ rs.getString("name")
						+ "</li><li class='memberMail'>"
						+ rs.getString("email")
						+ "</li><li class='memberGroupEvel'>"
						+ rs.getString("group")
						+ "</li><li class='memberModi'><a href='./Modify'>정보 수정</a></li><li class='memberDelet'><a href=''>탈퇴</a></li></ul>");
			}

			out.println("</ul>");

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