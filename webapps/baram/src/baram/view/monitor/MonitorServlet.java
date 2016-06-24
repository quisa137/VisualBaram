package baram.view.monitor;

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

public class MonitorServlet extends HttpServlet {

	private static final long serialVersionUID = -201510211130L;

	Logger logger = Logger.getLogger(MonitorServlet.class.getName());

	private static SimpleDateFormat sdf = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss.SSS");

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
			session.setAttribute("ref", "./Monitoring");
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
				"<title>Monitoring</title>"}));

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
				"<div class='sideNoClickAction' id='side1'></div>",
				"<div class='sideClickAction'></div>", "img/side/s1.png",
				"img/side/s1_a.png" }));

		KeyedValues logtypes = new KeyedValues();
		StringBuilder sb = new StringBuilder();
		try {
			conn = Database.getConnection(logger);
			stmt = conn.createStatement();
			if (startDate == null) {
				rs = stmt
						.executeQuery("select min(time) mintime, max(time) maxtime FROM "
								+ MainServlet.monitorTable);
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
				}
				rs.close();
			} else {
				startTimeString = startDate + " " + startTime + ":00.0";
				startTimeLong = getTime(startTimeString);
				endTimeString = endDate + " " + endTime + ":00.0";
				endTimeLong = getTime(endTimeString);
			}
			rs = stmt.executeQuery("select * from " + MainServlet.monitorTable
					+ " where time>" + startTimeLong + " and time<="
					+ endTimeLong + " order by time desc");
			boolean isFirst = true;
			while (rs.next()) {
				long time = rs.getLong("time");
				String logtype = rs.getString("logtype");
				logtypes.incrementValue(logtype, 1);
				String loader_ip = rs.getString("loader_ip");
				String loader_conf_dir = rs.getString("loader_conf_dir");
				String source_path = rs.getString("source_path");
				String dest_path = rs.getString("dest_path");
				long source_size = rs.getLong("source_size");
				long dest_size = rs.getLong("dest_size");
				int linecount = rs.getInt("linecount");
				int invalid = rs.getInt("invalid");
				long loader_memory = rs.getLong("loader_memory");
				long hdfs_use = rs.getLong("hdfs_use");
				String dirs = rs.getString("dirs");
				String files = rs.getString("files");
				int corrupted = rs.getInt("corrupted");
				String elapsed = rs.getString("elapsed");
				long utime = rs.getLong("utime");
				if (isFirst) {
					isFirst = false;
				} else {
					sb.append(",\n");
				}
				sb.append("{time : '" + getTimeString(time).substring(0, 19)
						+ "', logtype : '" + logtype + "', loader_ip : '"
						+ loader_ip + "',loader_conf_dir : '" + loader_conf_dir
						+ "',source_path : '" + source_path + "',dest_path : '"
						+ dest_path + "',source_size : " + source_size
						+ ",dest_size : " + dest_size + ",linecount : "
						+ linecount + ",invalid : " + invalid
						+ ",loader_memory : " + loader_memory + ",hdfs_use : "
						+ hdfs_use + ",dirs : '" + dirs + "',files : '" + files
						+ "',corrupted : " + corrupted + ",elapsed : '"
						+ elapsed + "', utime : '"
						+ getTimeString(utime).substring(0, 19) + "'}");

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
		out.println("<a class='ContentsTitle'>Monitoring</a>");
//		out.println("<a class='popupBtn' id='btnClick'>Quickstart Guide</a>");
		out.println("</p>");

		out.println("<div class='ContentsBottom'>");

		out.println("<ul class='settingsWrap' id='timeFixed'>");
		out.println("<form action='./Monitoring' method='post'>");

		out.println("<li class='TimeInputWrap'>");
		out.println("<div class='StartTimeWrap'>");
		out.println("<a class='StartTimeTitle'>조회기간 : </a>");
		out.println("<input type='text' value='" + startDate
				+ "' name='startDate'  class='date start InputBoxStyle'>");
		out.println("<input type='text' value='" + startTime
				+ "' name='startTime'  class='time start InputBoxStyle'>");
		out.println("</div>");
		out.println("<div class='EndTimeWrap'>");
		out.println("<a class='EndTimeTitle'>~</a>");
		out.println("<input type='text' value='" + endDate
				+ "' name='endDate'  class='date end InputBoxStyle'>");
		out.println("<input type='text' value='" + endTime
				+ "' name='endTime'  class='time end InputBoxStyle'>");
		out.println("</div>");
		out.println("</li>");

		out.println("<li class='monitorSettingsBtnWrap'>");
		out.println("<div class='monitorSettingsBtn'>");
		out.println("<input type='image' src='img/refresh.png'/>");
		out.println("</div>");
		out.println("</li>");
		out.println("</form>");
		out.println("</ul>");

		out.println("<script type='text/JavaScript'>");
		out.println("$('#timeFixed .time').timepicker({'showDuration': true, 'timeFormat': 'H:i'});");
		out.println("$('#timeFixed .date').datepicker({'format': 'yyyy-mm-dd', 'autoclose': true});");
		out.println("var timeFixedEl = document.getElementById('timeFixed');");
		out.println("var datepair = new Datepair(timeFixedEl);");
		out.println("var data =[");
		out.println(sb.toString());
		out.println("];");
		out.println(typesArray.toString());
		out.println("</script>");

		out.println("<style>.dc-chart g.row text {fill: #000;}.dc-chart path.area{fill-opacity: 0.5;}.dc-chart .pie-slice {opacity:0.8; fill:#000;}.dc-chart .node{font-size:12px; fill:#000;}</style>");

		out.println("<div class='ContentsBottomBottom'>");

		out.println("<div class='rowWrap monitorRowWrapStyle'>");

		out.println("<div id='bubble-wrap' class='graphHeight monitorChartWrapStyle'>");
		out.println("<img class='monitorChartTitleImg monitorBubbleChartImgStyle' src='img/bubble.png' />");
		out.println("<a class='monitorChartTitle'>Source & HDFS Volume Size (radius: Event Count)</a>");
		out.println("<div id='monitoring-bubble-chart' class='dc-chart monitorChartStyle'></div>");
		out.println("</div>");

		/*out.println("<div id='quarter-wrap' class='graphHeight monitorChartWrapStyle'>");
		out.println("<img class='monitorChartTitleImg' src='img/pie1.png' />");
		out.println("<a class='monitorChartTitle'>Used HDFS Size (total)</a>");
		out.println("<div id='quarter-chart' class='dc-chart monitorChartStyle'></div>");
		out.println("</div>");*/

		out.println("<div id='memory-wrap' class='graphHeight monitorChartWrapStyle'>");
		out.println("<img class='monitorChartTitleImg' src='img/barchart.png' />");
		out.println("<a class='monitorChartTitle'>Baram Memory Size</a>");
		out.println("<div id='memory-chart' class='dc-chart monitorChartStyle'></div>");
		out.println("</div>");

		out.println("</div>");

		out.println("<div class='rowWrap monitorRowWrapStyle'>");
		out.println("<div id='stack-wrap' class='graphHeight monitorChartWrapStyle'>");
		out.println("<img class='monitorChartTitleImg' src='img/linechart.png' />");
		out.println("<a class='monitorChartTitle'>Event Count by Type</a>");
		out.println("<div id='stack-chart' class='dc-chart monitorChartStyle'></div>");
		out.println("</div>");
		out.println("</div>");

		out.println("<div class='rowWrap monitorRowWrapStyle'>");
		out.println("<div id='line-wrap' class='graphHeight monitorChartWrapStyle'>");
		out.println("<img class='monitorChartTitleImg' src='img/linechart.png' />");
		out.println("<a class='monitorChartTitle'>Total Event Count</a>");
		out.println("<div id='line-chart' class='dc-chart monitorChartStyle'></div>");
		out.println("</div>");
		out.println("</div>");

		out.println("</div>");
		out.println("<script type='text/javascript' src='js/monitoring.js'></script>");
		out.println("</div>");
		out.println("</div>");
		out.println("</div>");
		out.println(CommonHtml.getHtml("foot", null));

		out.close();
	}

}