package baram.view.analysis.install;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

import baram.dataset.stringkey.KeyedValues;

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

public class InstallEnvServlet extends HttpServlet {

	private static final long serialVersionUID = -201510211130L;

	Logger logger = Logger.getLogger(InstallEnvServlet.class.getName());

	private static SimpleDateFormat sdf = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	private synchronized static long getTime(String s) throws Exception {
		return sdf.parse(s).getTime();
	}

	private synchronized static String getTimeString(long t) throws Exception {
		return sdf.format(t);
	}

	protected void service(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("text/html;charset=UTF-8");

		PrintWriter out = response.getWriter();

		HttpSession session = request.getSession(true);
		if (!User.checkSession(request, logger,
				"GetAllMemberInfoServlet.service", this)) {
			session.setAttribute("ref", "./InstallEnv");
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
				"<title>Pie and RowBar and Bar Chart</title>"}));

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		String startDate = request.getParameter("startDate");
		String startTime = request.getParameter("startTime");
		String endDate = request.getParameter("endDate");
		String endTime = request.getParameter("endTime");

		String startTimeString = null;
		long startTimeLong = 0L;
		String endTimeString = null;
		long endTimeLong = 0L;

		out.println(CommonHtml.getHtml("top", new String[] { "##username##",
				user.getName() }));

		out.println("<div class='Container'>");

		out.println(CommonHtml.getHtml("side", new String[] {
				"<div class='sideNoClickAction' id='side5'></div>",
				"<div class='sideClickAction'></div>", "img/side/s5.png",
				"img/side/s5_a.png" }));

		KeyedValues logtypes = new KeyedValues();
		StringBuilder sb = new StringBuilder();
		try {
			conn = Database.getConnection(logger);
			stmt = conn.createStatement();
			if (startDate == null) {
				rs = stmt
						.executeQuery("select min(time) mintime, max(time) maxtime FROM "+ MainServlet.UnstructuredDatTable);
				if (rs.next()) {
					startTimeLong = rs.getLong("mintime");
					endTimeLong = rs.getLong("maxtime");
					startTimeLong = Math.max(startTimeLong,
							endTimeLong - 3600000L);
					startTimeString = getTimeString(startTimeLong);
					endTimeString = getTimeString(endTimeLong);
					startDate = startTimeString.substring(0, 10);
					endDate = endTimeString.substring(0, 10);
					startTime = startTimeString.substring(11, 16);
					endTime = endTimeString.substring(11, 16);
					System.out.println(startTime);
					System.out.println(endTime);
					
				}
				rs.close();
			} else {
				startTimeString = startDate + " " + startTime + ":00.0";
				startTimeLong = getTime(startTimeString);
				endTimeString = endDate + " " + endTime + ":00.0";
				endTimeLong = getTime(endTimeString);
			}
			rs = stmt.executeQuery("select c6, c12, count(*),substring(c4, 2, 20) from "+MainServlet.dbmsOutputTable+" group by c12,c6;");
			boolean isFirst = true;
			while (rs.next()) {
				int count=rs.getInt("count(*)");
				String errCode = rs.getString("c6");
				String logIP = rs.getString("c12");
				
				
				if (isFirst) {
					isFirst = false;
				} else {
					sb.append(",\n");
				}
				sb.append("{game : '"+  logIP  + "', os : '" +errCode+"', install : "+count+ "}");

			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.log(Level.SEVERE, "Error in " + this.getClass().getName()
					+ ".service()", e);
		} finally {
			try {
				if (null != rs) {
					rs.close();
				}
			} catch (SQLException e) {
			}
			try {
				if (null != stmt) {
					stmt.close();
				}
			} catch (SQLException e) {
			}
			try {
				if (null != conn) {
					conn.close();
				}
			} catch (SQLException e) {
			}
		}
		StringBuilder typesArray = new StringBuilder();
		typesArray.append("var logtypes = new Array(" + logtypes.getItemCount()
				+ ");");
		for (int i = 0; i < logtypes.getItemCount(); i++) {
			typesArray.append("logtypes[" + i + "] = '" + logtypes.getKey(i)
					+ "';");
		}
		out.println("<div class='Contents'>");

		out.println("<p class='ContentsTop'>");
		out.println("<a class='ContentsTitle'>Pie and RowBar and Bar Chart</a>");
//		out.println("<a class='popupBtn' id='btnClick'>Quickstart Guide</a>");
		out.println("</p>");

		out.println("<div class='ContentsBottom'>");

		out.println("<ul class='settingsWrap' id='timeFixed'>");
	//	out.println("<form action='./Interest' method='post'>");

		out.println("<li class='TimeInputWrap'>");
	//	out.println("<div class='StartTimeWrap'>");
	//	out.println("<a class='StartTimeTitle'>조회기간 : </a>");
	//	out.println("<input type='text' value='" + startDate
	//			+ "' name='startDate'  class='date start InputBoxStyle'>");
	//	out.println("<input type='text' value='" + startTime
	//			+ "' name='startTime'  class='time start InputBoxStyle'>");
	//	out.println("</div>");
	//	out.println("<div class='EndTimeWrap'>");
	//	out.println("<a class='EndTimeTitle'>~</a>");
	//	out.println("<input type='text' value='" + endDate
	//			+ "' name='endDate'  class='date end InputBoxStyle'>");
	//	out.println("<input type='text' value='" + endTime
	//			+ "' name='endTime'  class='time end InputBoxStyle'>");
	//	out.println("</div>");
		out.println("</li>");

		out.println("<li class='monitorSettingsBtnWrap'>");
		//out.println("<div class='monitorSettingsBtn'>");
	//	out.println("<input type='image' src='img/refresh.png'/>");
	//	out.println("</div>");
		out.println("</li>");
	//	out.println("</form>");
		out.println("</ul>");

		out.println("<script type='text/JavaScript'>");
		out.println("$('#timeFixed .time').timepicker({'showDuration': true, 'timeFormat': 'H:i'});");
		out.println("$('#timeFixed .date').datepicker({'format': 'yyyy-mm-dd', 'autoclose': true});");
		out.println("var timeFixedEl = document.getElementById('timeFixed');");
		
		out.println("var data =[");
	//	out.println("{'game': '강남구', 'install': 51352, 'os': '침수'}, ");
	//	out.println("{'game': '강남구', 'install': 3215, 'os': '화재'},");
	//	out.println("{'game': '서초구', 'install': 99613, 'os': '침수'}, ");
	//	out.println("{'game': '서초구', 'install': 5125, 'os': '화재'},");
	//	out.println("{'game': '강북구', 'install': 12563, 'os': '침수'},");
	//	out.println("{'game': '강북구', 'install': 1254, 'os': '화재'},");
	//	out.println("{'game': '은평구', 'install': 12356, 'os': '침수'}, ");
	//	out.println("{'game': '은평구', 'install': 8124, 'os': '화재'}	");
	out.println(sb.toString());
		out.println("];");
		out.println(typesArray.toString());
		out.println("</script>");

		out.println("<style>.dc-chart g.row text {fill: #000;}.dc-chart path.area{fill-opacity: 0.5;}.dc-chart .pie-slice {opacity:0.8; fill:#000;}.dc-chart .node{font-size:12px; fill:#000;}.x>.tick>text{text-anchor: end !important;transform : rotate(-45deg);}</style>");
		out.println("<div class='ContentsBottomBottom' style='border-bottom: 1px solid #bac2c3;'>");
		out.println("<div id='chart-ring-year' style='margin:50px 0;'></div><div id='chart-row-spenders' style='margin:50px 0'></div><div id='chart-hist-spend' style='margin:50px 0;'></div>");
		out.println("</div>");
		
		out.println("<script type='text/javascript' src='js/Install.js'></script>");
		out.println("</div>");
		out.println("</div>");
		out.println("</div>");
		out.println(CommonHtml.getHtml("foot", null));

		out.close();
	}

}