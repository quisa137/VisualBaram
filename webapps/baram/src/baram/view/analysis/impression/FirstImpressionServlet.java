package baram.view.analysis.impression;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import baram.dataset.stringkey.KeyedValues;
import baram.view.CommonHtml;
import baram.view.Database;
import baram.view.MainServlet;
import baram.view.User;
import baram.view.analysis.install.InstallEnvServlet;

public class FirstImpressionServlet extends HttpServlet {

	private static final long serialVersionUID = -201510211130L;

	Logger logger = Logger.getLogger(InstallEnvServlet.class.getName());

	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private synchronized static long getTime(String s) throws Exception {
		return sdf.parse(s).getTime();
	}

	private synchronized static String getTimeString(long t) throws Exception {
		return sdf.format(t);
	}

	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html;charset=UTF-8");

		PrintWriter out = response.getWriter();

		HttpSession session = request.getSession(true);
		if (!User.checkSession(request, logger, "GetAllMemberInfoServlet.service", this)) {
			session.setAttribute("ref", "./Impression");
			out.println("<script>");
			out.println("alert('잘못된 접근입니다. 로그인 후 이용해 주세요.');");
			out.println("location.href='./LoginPage'");
			out.println("</script>");
			out.close();
			return;
		}

		User user = (User) (session.getAttribute("user"));

		out.println(CommonHtml.getHtml("head",
				new String[] { "<title>Analysis Demo</title>", "<title>Pie Chart</title>" }));

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

		out.println(CommonHtml.getHtml("top", new String[] { "##username##", user.getName() }));

		out.println("<div class='Container'>");

		out.println(CommonHtml.getHtml("side", new String[] { "<div class='sideNoClickAction' id='side7'></div>",
				"<div class='sideClickAction'></div>", "img/side/s7.png", "img/side/s7_a.png" }));

		KeyedValues logtypes = new KeyedValues();
		StringBuilder sb = new StringBuilder();
		try {
			conn = Database.getConnection(logger);
			stmt = conn.createStatement();
			if (startDate == null) {
				rs = stmt.executeQuery(
						"select min(time) mintime, max(time) maxtime FROM " + MainServlet.UnstructuredDatTable);
				if (rs.next()) {
					startTimeLong = rs.getLong("mintime");
					endTimeLong = rs.getLong("maxtime");
					startTimeLong = Math.max(startTimeLong, endTimeLong - 3600000L);
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
			rs = stmt
					.executeQuery("select c6, c12, count(*), substring(c4, 2, 20) from "+MainServlet.dbmsOutputTable+" group by c6,c12;");
			boolean isFirst = true;
			while (rs.next()) {
				// BigInteger time =
				// BigInteger.valueOf(Long.parseLong(rs.getString("time")));
				String time = rs.getString("substring(c4, 2, 20)");
				SimpleDateFormat original_format = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss", Locale.ENGLISH);
				SimpleDateFormat new_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date original_date = original_format.parse(time);
				// 날짜 형식을 원하는 타입으로 변경한다.
				String new_date = new_format.format(original_date);
				// 결과를 출력한다.
			//	System.out.println(new_date);
				int count = rs.getInt("count(*)");
				String errCode = rs.getString("c6");
				String logIP = rs.getString("c12");
				

				if (isFirst) {
					isFirst = false;
				} else {
					sb.append(",\n");
				}
				sb.append("{time : '" + new_date + "', loc : '" + logIP + "', category : '"
						+ errCode + "', cnt : " + count + "}");

			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.log(Level.SEVERE, "Error in " + this.getClass().getName() + ".service()", e);
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
		typesArray.append("var logtypes = new Array(" + logtypes.getItemCount() + ");");
		for (int i = 0; i < logtypes.getItemCount(); i++) {
			typesArray.append("logtypes[" + i + "] = '" + logtypes.getKey(i) + "';");
		}
		out.println("<div class='Contents'>");

		out.println("<p class='ContentsTop'>");
		out.println("<a class='ContentsTitle'>Pie Chart</a>");
		// out.println("<a class='popupBtn' id='btnClick'>Quickstart
		// Guide</a>");
		out.println("</p>");

		out.println("<div class='ContentsBottom'>");

		out.println("<ul class='settingsWrap' id='timeFixed'>");

		out.println("<li class='TimeInputWrap'>");

		out.println("</li>");

		out.println("<li class='monitorSettingsBtnWrap'>");

		out.println("</li>");

		out.println("</ul>");

		out.println("<script type='text/JavaScript'>");
		out.println("$('#timeFixed .time').timepicker({'showDuration': true, 'timeFormat': 'H:i'});");
		out.println("$('#timeFixed .date').datepicker({'format': 'yyyy-mm-dd', 'autoclose': true});");
		out.println("var timeFixedEl = document.getElementById('timeFixed');");

		out.println("var temp =[");
		out.println(sb.toString());

		out.println("];");
		out.println(typesArray.toString());
		out.println("</script>");

		out.println(
				"<style>.dc-chart g.row text {fill: #000;}.dc-chart path.area{fill-opacity: 0.5;}.dc-chart .pie-slice {opacity:0.8; fill:#000;}.dc-chart .node{font-size:12px; fill:#000;}</style>");

		out.println("<div class='ContentsBottomBottom'>");

		out.println("<div id='pie-chart' style='margin-top:50px; margin-left:200px'></div>");

		out.println("</div>");

		out.println("<script type='text/javascript' src='js/Impression.js'></script>");
		out.println("</div>");
		out.println("</div>");
		out.println("</div>");
		out.println(CommonHtml.getHtml("foot", null));

		out.close();
	}
}
