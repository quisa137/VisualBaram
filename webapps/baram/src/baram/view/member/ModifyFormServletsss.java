package baram.view.member;
/*package baram.view.member;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;



import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;









public class ModifyFormServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public ModifyFormServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
		ServletContext context = getServletContext();
		PrintWriter out = response.getWriter();
		
		out.println("<!DOCTYPE html PUBLIC '-//W3C//DTD HTML 4.01 Transitional//EN' 'http://www.w3.org/TR/html4/loose.dtd'>");
		out.println("<html>");
		out.println("<head>");
		out.println("<meta http-equiv='Content-Type' content='text/html; charset=utf-8'>");
		out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=no' />");
		out.println("<title>Baram Analysis</title>");
		out.println("<link rel='stylesheet' type='text/css' href='css/reset.css' />");
		out.println("<link rel='stylesheet' type='text/css' href='css/notebookHeader.css' />");
		out.println("<link rel='stylesheet' type='text/css' href='css/notebookContainer.css' />");
		out.println("<link rel='stylesheet' type='text/css'	href='css/sumoselect.css' />");
		out.println("<script type='text/JavaScript' src='js/jquery-2.1.3.js'></script>");
		out.println("<script type='text/JavaScript' src='js/jquery.sumoselect.js'></script>");
		out.println("<script type='text/JavaScript' src='js/jquery_ui.js'></script>");
		out.println("<script type='text/JavaScript' src='js/select.js'></script>");
		out.println("<script type='text/javascript' src='js/HeaderGnb.js'></script>");
	
		
		out.println("</head>");
		out.println("<body>");
		String monitorView = getServletContext().getInitParameter("baram.monitoring.view");
		String notebookView = getServletContext().getInitParameter("baram.notebook.view");
		String analysisView = getServletContext().getInitParameter("baram.analysis.view");
		String loginView = getServletContext().getInitParameter("baram.view.login");
		System.out.println(monitorView+"  "+notebookView+"  "+analysisView+"  "+loginView);
		
		
		
		HttpSession session = request.getSession();
		String memId = (String) session.getAttribute("memId");
		String memPw = (String) session.getAttribute("memPw");
		String memName = (String) session.getAttribute("memName");
		String memRegNum = (String) session.getAttribute("memRegNum");
		String memGroupEvel = (String) session.getAttribute("memGroupEvel");
		 MemberDTO mem = (MemberDTO) session.getAttribute("login_session");
		 session.setAttribute("memId", memId);
			session.setAttribute("memPw", memPw);
			session.setAttribute("memName", memName);
			session.setAttribute("memRegNum", memRegNum);
			session.setAttribute("memGroupEvel", memGroupEvel);
		
		session.setAttribute("login_session", new MemberDTO(mem.getId(), mem.getPassword(), mem.getName(),mem.getEmail(), mem.getGroupEvel()));
	
		
		out.println("<div class='noteBookHeader'>");
		out.println("<h1 class='logo'><img src='img/logo.png' /></h1>");
		out.println("<ul class='noteBookGnb'>");
			
			out.println("<li class='gnbLi1'>");
				out.println("<p class='gnbLi1Top'>");
					out.println("<a class='gnbLi1TopMesage'>TRIAL DAYS LEFT</a>");
				out.println("</p>");
				out.println("<dl class='gnbLi1Bottom'>");
					out.println("<dd class='gnbLi1BottomLeft'>");
						out.println("<img class='gnbLi1BottomLeftTop' src='img/test/top/g1.png'/>");
					out.println("</dd>");
					out.println("<dd class='gnbLi1BottomRight'>");
						out.println("<div class='gnbLi1BottomRightTop'>");
							out.println("<div class='gnbLi1BottomRightTopLeft'></div>");
							out.println("<div class='gnbLi1BottomRightTopRight'></div>");
						out.println("</div>");
						out.println("<div class='gnbLi1BottomRightBottom'>");
							out.println("<div class='gnbLi1BottomRightBottomLeft'></div>");
							out.println("<div class='gnbLi1BottomRightBottomRight'>");
							out.println("<div class='gnbLi1BottomRightBottomRightLeft'>");
							out.println("<a class='gnbLi1BottomRightBottomRightLeftRight'>0</a>");
							out.println("<a class='gnbLi1BottomRightBottomRightLeftLeft'>3</a>");
								out.println("</div>");
								out.println("<a class='gnbLi1BottomRightBottomRightRight'>Upgrade</a>");
							out.println("</div>");
						out.println("</div>");
					out.println("</dd>");
				out.println("</dl>");

			out.println("</li>");
			out.println("<a class='gnbBar'></a>");
			out.println("<li class='gnbLi2'>");
				out.println("<img src='img/test/top/g2.png' class='gnbLi2Img'/>");
				out.println("<a class='gnbLi2Text'>Notifications</a>");
			out.println("</li>");
			out.println("<dl class='noteBookNotificationsSub'>");
				out.println("<dd class='noteBookNotificationsSubTop'></dd>");
				out.println("<dd class='noteBookNotificationsSubDd'></dd>");
				out.println("<dd class='noteBookNotificationsSubDd'></dd>");
				out.println("<dd class='noteBookNotificationsSubDd'></dd>");

			out.println("</dl>");
			out.println("<a class='gnbBar'></a>");
			out.println("<li class='gnbLi3'>");
				out.println("<img src='img/test/top/g3.png' class='gnbLi3Img'/>");
				out.println("<a class='gnbLi3Text'>Feedback</a>");
			out.println("</li>");
			out.println("<a class='gnbBar'></a>");
			out.println("<li class='gnbLi4'>");
				out.println("<img src='img/test/top/g4.png' class='gnbLi4Img'/>");
				out.println("<a class='gnbLi4Text'>Help</a>");
				out.println("<img src='img/test/top/g45ClickImg.png'class='g45ClickImg'/>");
			out.println("</li>");
			out.println("<dl class='noteBookHelpSub'>");
			out.println("<dd class='noteBookHelpSubTop'></dd>");
		//	out.println("<dd class='noteBookHelpSubDd'><img src='img/test/top/gs4h1.png' /><a>Tutorial</a>");
			out.println("<dd class='noteBookHelpSubDd'><a></a>");
			out.println("</dd>");
		//	out.println("<dd class='noteBookHelpSubDd'><img src='img/test/top/gs4h2.png' /><a> My Support Cases</a>");
			out.println("<dd class='noteBookHelpSubDd'><a></a>");
			out.println("</dd>");
		//	out.println("<dd class='noteBookHelpSubDd'><img src='img/test/top/gs4h3.png' /><a>Support Chat</a>");
			out.println("<dd class='noteBookHelpSubDd'><a></a>");
			out.println("</dd>");
			//out.println("<dd class='noteBookHelpSubDd'><img src='img/test/top/gs4h4.png' /><a>Documentation</a>");
			out.println("<dd class='noteBookHelpSubDd'><a></a>");
			out.println("</dd>");
		//	out.println("<dd class='noteBookHelpSubDd'><img src='img/test/top/gs4h5.png' /><a>Educational Videos</a>");
			out.println("<dd class='noteBookHelpSubDd'><a></a>");
			out.println("</dd>");
		//	out.println("<dd class='noteBookHelpSubDd'><img src='img/test/top/gs4h6.png' /><a>Release Notes</a>");
			out.println("<dd class='noteBookHelpSubDd'><a></a>");
			out.println("</dd>");
			//out.println("<dd class='noteBookHelpSubDd'><img src='img/test/top/gs4h7.png' /><a>Service Status</a>");
			out.println("<dd class='noteBookHelpSubDd'><a></a>");
			out.println("</dd>");
		//	out.println("<dd class='noteBookHelpSubDd'><img src='img/test/top/gs4h8.png' /><a>Create Supprt Case</a>");
			out.println("<dd class='noteBookHelpSubDd'><a></a>");
			out.println("</dd>");
		out.println("</dl>");
			out.println("<a class='gnbBar'></a>");
			
			
			if(loginView.equalsIgnoreCase("True")){
				
				System.out.println(mem.getName());
			out.println("<li class='gnbLi5'>");
				out.println("<img src='img/test/top/g5.png' class='gnbLi5Img'/>");
				out.println("<a class='gnbLi5Text'>"+mem.getName()+"</a>");
				out.println("<img src='img/test/top/g45ClickImg.png'class='g45ClickImg'/>");
			out.println("</li>");
			out.println("<dl class='noteBookUserSub'>");
				out.println("<dd class='noteBookUserSubTop'></dd>");
				out.println("<dd class='noteBookUserSubDd'><img src='img/test/top/gs5h1.png' /><a href='./ModifyForm'>My profile</a>");
				out.println("</dd>");
				out.println("<dd class='noteBookUserSubDd'><img src='img/test/top/gs5h2.png' /><a href='./Logout'>Logout</a>");
				out.println("</dd>");
			out.println("</dl>");
			}else if(loginView.equalsIgnoreCase("False")){}
			out.println("</ul>");
	out.println("</div>");//noteBookHeader
		
		
	
	out.println("<div class='noteBookContainer'>");
	out.println("<div class='notebookSideBar'>");
	out.println("<ul class='notebookNav'>");
		out.println("<li class='s1Wrap'>");
			out.println("<p class='s1'>");
				out.println("<div class='notebookNoClickActionS1'></div>");
				out.println("<a class='notebookNoClickActionAS1' href='collectData.html'><img src='img/test/side/s1.png' class='notebookNavIconS1' /></a>");
			out.println("</p>");
			out.println("<div class='s1Hover'><img src='img/test/side/hoverImg.png' class='s1HoverImg'/><a class='s1HoverText'>Collect Data</a>");
			out.println("</div>");
		out.println("</li>");

	out.println("	<li  class='s2Wrap'>");
			out.println("<p class='s2'>");
				out.println("<div class='notebookNoClickActionS2'></div>");
				out.println("<a class='notebookNoClickActionAS2' href='jobs.html'><img src='img/test/side/s2.png'  class='notebookNavIconS2'/></a>");
			out.println("</p>");
			out.println("<div class='s2Hover'><img src='img/test/side/hoverImg.png' class='s2HoverImg'/><a class='s2HoverText'>Jobs</a>");
			out.println("</div>");
		out.println("</li>");

		out.println("<li  class='s3Wrap'>");
			out.println("<p class='s3'>");
				out.println("<div class='notebookNoClickActionS3'></div>");
				out.println("<a class='notebookNoClickActionAS3' href='./DataBasesServlet'><img src='img/test/side/s3.png'  class='notebookNavIconS3'/></a>");
			out.println("</p>");
			out.println("<div class='s3Hover'><img src='img/test/side/hoverImg.png' class='s3HoverImg'/><a class='s3HoverText'>Databases</a>");
			out.println("</div>");
		out.println("</li>");

		out.println("<li  class='s4Wrap'>");
			out.println("<p class='s4'>");
				out.println("<div class='notebookNoClickActionS4'></div>");
				out.println("<a class='notebookNoClickActionAS4' href='newQuery.html'><img src='img/test/side/s4.png'  class='notebookNavIconS4'/></a>");
			out.println("</p>");
			out.println("<div class='s4Hover'><img src='img/test/side/hoverImg.png' class='s4HoverImg'/><a class='s4HoverText'>New Query</a>");
			out.println("</div>");
		out.println("</li>");

		out.println("<li  class='s5Wrap'>");
			out.println("<p class='s5'>");
				out.println("<div class='notebookNoClickActionS5'></div>");
				out.println("<a class='notebookNoClickActionAS5' href='queries.html'><img src='img/test/side/s5.png'  class='notebookNavIconS5'/></a>");
			out.println("</p>");
			out.println("<div class='s5Hover'><img src='img/test/side/hoverImg.png' class='s5HoverImg'/><a class='s5HoverText'>Queries</a>");
			out.println("</div>");
		out.println("</li>");

		out.println("<li  class='s6Wrap'>");
			out.println("<p class='s6'>");
				out.println("<div class='notebookNoClickActionS6'></div>");
				out.println("<a class='notebookNoClickActionAS6' href='./AnalysisServlet'><img src='img/test/side/s6.png'  class='notebookNavIconS6'/></a>");
			out.println("</p>");
			out.println("<div class='s6Hover'><img src='img/test/side/hoverImg.png' class='s6HoverImg'/><a class='s6HoverText'>Analysis</a>");
			out.println("</div>");
		out.println("</li>");
		if(mem.getGroupEvel().equals("1")){
		out.println("<li  class='s7Wrap'>");
			out.println("<p class='s7'>");
				out.println("<div class='notebookClickActionS7'></div>");
				out.println("<a class='notebookNoClickActionAS7' href='./GetAllMemberInfo'><img src='img/test/side/s7_a.png'  class='notebookNavIconS7'/></a>");
			out.println("</p>");
			out.println("<div class='s7Hover'><img src='img/test/side/hoverImg.png' class='s7HoverImg'/><a class='s7HoverText'>Team</a>");
			out.println("</div>");
		out.println("</li>");
		}else{
			
			out.println("<li  class='s7Wrap'>");
			out.println("<p class='s7'>");
				out.println("<div class='notebookClickActionS7'></div>");
				out.println("<a class='notebookNoClickActionAS7' href='./ModifyForm'><img src='img/test/side/s7_a.png'  class='notebookNavIconS7'/></a>");
			out.println("</p>");
			out.println("<div class='s7Hover'><img src='img/test/side/hoverImg.png' class='s7HoverImg'/><a class='s7HoverText'>Team</a>");
			out.println("</div>");
		out.println("</li>");
		}
		out.println("<li  class='s8Wrap'>");
			out.println("<p class='s8'>");
				out.println("<div class='notebookNoClickActionS8'></div>");
				out.println("<a class='notebookNoClickActionAS8'  href='./MonitorServlet'><img src='img/test/side/s8.png'  class='notebookNavIconS8'/></a>");
			out.println("</p>");
			out.println("<div class='s8Hover'><img src='img/test/side/hoverImg.png' class='s8HoverImg'/><a class='s8HoverText'>Monitoring</a>");
			out.println("</div>");
		out.println("</li>");

	out.println("</ul>");
out.println("</div>");
out.println("<div class='modifyFormContents'>");





out.println("<div class='modifyFormContentsTop'>");
out.println("<img src='img/moicon.png' class='modifyFormContentsTitleImg'/>");

out.println("<ul class='modifyFormContentsTitleTextWrap'>");
out.println("<li class='modifyFormContentsTitleTextName'>");
out.println(mem.getName());
out.println("</li>");
out.println("<li class='modifyFormContentsTitleTextMail'>");
out.println(mem.getEmail());
out.println("</li>");

out.println("</ul>");
out.println("</div>");
out.println("<div class='modifyFormContentsBottom'>");



String sql = "select * from member where id='"+mem.getId()+"';";
PreparedStatement preparedStatement = null;
Connection connection = null;
ResultSet resultSet = null;


try {
	connection = DriverManager.getConnection(
			context.getInitParameter("baram.view.jdbc.member.url"),
			context.getInitParameter("baram.view.jdbc.userName"),
			context.getInitParameter("baram.view.jdbc.password"));

	// preparedStatement
	preparedStatement = connection.prepareStatement(sql);

	resultSet = preparedStatement.executeQuery();

	String memIdMo = null;
	String memPwMo = null;
	String memNameMo = null;
	String memRegNumMo = null;
	String memGroupEvelMo =null;
	
	if (resultSet.next()) {
		memIdMo = resultSet.getString("Id");
		memPwMo = resultSet.getString("password");
		memNameMo = resultSet.getString("name");
		memRegNumMo = resultSet.getString("email");
		memGroupEvelMo = resultSet.getString("groupEvel");
		}
	System.out.println(memIdMo+memPwMo+memNameMo+memRegNumMo+memGroupEvelMo);
	
	if (session.getAttribute("memName") != null) {
		
		out.println("<div class='modifyFormWrap'>");
		out.println("<p class='modifyFormTitle'>");
		out.println("Profile");
		out.println("</p>");
		
		out.println("<form action='./ModifyMember' method='POST'>");
		out.println("<ul class='modifyFormListUl'>");
		out.println("<li class='modifyFormList'>");
		out.println("<a class='modifyFormListText'>ID");
		out.println("</a>");
		out.println("<input type='text' name='id' value='" + memIdMo+ "' disabled='disabled' class='modifyFormListInput' />");
		out.println("</li>");
		out.println("<li class='modifyFormList'>");
		out.println("<a class='modifyFormListText'>Your current password");
		out.println("</a>");
		out.println("<input type='password' name='curr_password'  class='modifyFormListInput' />");
		out.println("</li>");
		out.println("<li class='modifyFormList'>");
		out.println("<a class='modifyFormListText'>New password");
		out.println("</a>");
		out.println("<input type='password' name='password'  class='modifyFormListInput' />");
		out.println("</li>");
		out.println("<li class='modifyFormList'>");
		out.println("<a class='modifyFormListText'>Confirm new password");
		out.println("</a>");
		out.println("<input type='password' name='confirm_password'  class='modifyFormListInput' />");
		out.println("</li>");
		
		
		
		out.println("<li class='modifyFormList'>");
		out.println("<a class='modifyFormListText'>Name");
		out.println("</a>");
		out.println("<input type='text' name='name' disabled='disabled' value='"
				+ memNameMo + "' class='modifyFormListInput' />");
		out.println("</li>");
		
		
		out.println("<li class='modifyFormList'>");
		out.println("<a class='modifyFormListText'>E-mail");
		out.println("</a>");
		out.println("<input type='text' name='regNum1'  value='"
				+ memRegNumMo + "' class='modifyFormListInput'/>");
		out.println("</li>");
		
		out.println("<li class='modifyFormList'>");
		out.println("<a class='modifyFormListText'>GroupEvel");
		out.println("</a>");
		out.println("<input type='text' name='groupEvel' disabled='disabled' value='"
				+ memGroupEvelMo + "' class='modifyFormListInput'/>");
		out.println("</li>");

		
		out.println("<li class='modifyFormListBtnWrap'>");
		out.println("<input type='submit' value='정보수정' />");
		out.println("<input type='reset' value='Reset' />");
		out.println("</li>");
		
		
		out.println("</ul>");
		out.println(" </form>");
		out.println("</div>");
	} else {
		 out.println("<script>");
        	out.println("alert('정보를 수정할 수 없습니다. 로그인을 먼저 해주세요.');");
  		  	out.println("location.href='servletMemberIndex.html'");
  		  	out.println("</script>");
}
	
	
	
	
	
} catch (SQLException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();

} finally {
	if (resultSet != null) {
		try {
			resultSet.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	if (preparedStatement != null) {
		try {
			preparedStatement.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	if (connection != null) {
		try {
			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}




out.println("</div>");





out.println("</div>");
	out.println("</div>");
		

		
	
	out.println("<div class='feedBackpopupWrap'>");
	out.println("<div class='feedBackpopupbg'>");
	out.println("</div>");
	out.println("<div class='feedBackpopup'></div>");
out.println("</div>");

out.println("<div class='upgradepopupWrap'>");
	out.println("<div class='upgradepopupbg'>");
	out.println("</div>");
	out.println("<div class='upgradepopup'></div>");
out.println("</div>");
		
		





		
		
		
		
		
		
		
	
		
		

		
		out.println("</body>");
		out.println("	</html>");

	}
}
*/