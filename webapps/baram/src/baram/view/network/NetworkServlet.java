package baram.view.network;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

public class NetworkServlet extends HttpServlet {

	private static final long serialVersionUID = -201510211130L;

	Logger logger = Logger.getLogger(NetworkServlet.class.getName());

	protected void service(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("text/html;charset=UTF-8");

		PrintWriter out = response.getWriter();

		HttpSession session = request.getSession(true);
		if (!User.checkSession(request, logger,
				"GetAllMemberInfoServlet.service", this)) {
			session.setAttribute("ref", "./Network");
			out.println("<script>");
			out.println("alert('잘못된 접근입니다. 로그인 후 이용해 주세요.');");
			out.println("location.href='./LoginPage'");
			out.println("</script>");
			out.close();
			return;
		}

		User user = (User) (session.getAttribute("user"));

		out.println(CommonHtml.getHtml("head", new String[] {
				"<title>Analysis Demo</title>", "<title>Network</title>" }));

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		ResultSet rs2 = null;

		out.println(CommonHtml.getHtml("top", new String[] { "##username##",
				user.getName() }));

		out.println("<div class='Container'>");

		out.println(CommonHtml.getHtml("side", new String[] {
				"<div class='sideNoClickAction' id='side12'></div>",
				"<div class='sideClickAction'></div>", "img/side/s12.png",
				"img/side/s12_a.png" }));

		StringBuilder sb = new StringBuilder();
		try {
			conn = Database.getConnection(logger);
			stmt = conn.createStatement();

			// rs = stmt.executeQuery("select * FROM "+
			// MainServlet.UnstructuredDatTable);
			rs = stmt.executeQuery("select count(*),c6 from "+MainServlet.dbmsOutputTable+" group by c6;");

			boolean isFirst = true;
			while (rs.next()) {
				String c6 = rs.getString("c6");
				int nodeSize = rs.getInt("count(*)");
				if (isFirst) {
					isFirst = false;
				} else {
					sb.append(",\n");
				}
				sb.append("{'id' : '" + c6 + "', 'label' : '" + c6 + "', 'size' : " + nodeSize + "}");
				
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
		
		StringBuilder sb2 = new StringBuilder();
		try {
			conn = Database.getConnection(logger);
			stmt = conn.createStatement();

			// rs = stmt.executeQuery("select * FROM "+
			// MainServlet.UnstructuredDatTable);
			rs2 = stmt.executeQuery("select count(*),c12 from "+MainServlet.dbmsOutputTable+" group by c12;");

			boolean isFirst = true;
			while (rs2.next()) {
				String c12 = rs2.getString("c12");
				int nodeSize = rs2.getInt("count(*)");
				if (isFirst) {
					isFirst = false;
				} else {
					sb2.append(",\n");
				}
				sb2.append("{'id' : '" + c12 + "', 'label' : '" + c12 + "', 'size' : " + nodeSize + "}");

			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.log(Level.SEVERE, "Error in " + this.getClass().getName()
					+ ".service()", e);
		} finally {
			try {
				if (null != rs) {
					rs2.close();
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
		
		StringBuilder sb1 = new StringBuilder();
		try {
			conn = Database.getConnection(logger);
			stmt = conn.createStatement();

			rs1 = stmt
					.executeQuery("select c4,c6,c12,count(*) from "+MainServlet.dbmsOutputTable+" group by c6,c12;");

			boolean isFirst = true;
			while (rs1.next()) {
				String edgeId = rs1.getString("c4");
				String source = rs1.getString("c12");
				String target = rs1.getString("c6");
				int edgeSize = rs1.getInt("count(*)");
				if (isFirst) {
					isFirst = false;
				} else {
					sb1.append(",\n");
				}
				sb1.append("{'id' : '" + edgeId + "', 'source' : '" + source
						+ "','target':'" + target + "', 'size' : " + edgeSize
						+ "}");
			
				
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.log(Level.SEVERE, "Error in " + this.getClass().getName()
					+ ".service()", e);
		} finally {
			try {
				if (null != rs1) {
					rs1.close();
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

		out.println("<div class='Contents'>");

		out.println("<p class='ContentsTop'>");
		out.println("<a class='ContentsTitle'>Network</a>");
//		out.println("<a class='popupBtn' id='btnClick'>Quickstart Guide</a>");
		out.println("</p>");

		out.println("<div class='ContentsBottom'>");

		out.println("<div class='settingsWrap'>");

		out.println("</div>");
		out.println("<script src='sigmaSrc/sigma.core.js'></script>");
		out.println("<script src='sigmaSrc/conrad.js'></script>");
		out.println("<script src='sigmaSrc/utils/sigma.utils.js'></script>");
		out.println("<script src='sigmaSrc/utils/sigma.polyfills.js'></script>");
		out.println("<script src='sigmaSrc/sigma.settings.js'></script>");
		out.println("<script src='sigmaSrc/classes/sigma.classes.dispatcher.js'></script>");
		out.println("<script src='sigmaSrc/classes/sigma.classes.configurable.js'></script>");
		out.println("<script src='sigmaSrc/classes/sigma.classes.graph.js'></script>");
		out.println("<script src='sigmaSrc/classes/sigma.classes.camera.js'></script>");
		out.println("<script src='sigmaSrc/classes/sigma.classes.quad.js'></script>");
		out.println("<script src='sigmaSrc/captors/sigma.captors.mouse.js'></script>");
		out.println("<script src='sigmaSrc/captors/sigma.captors.touch.js'></script>");
		out.println("<script src='sigmaSrc/renderers/sigma.renderers.canvas.js'></script>");
		out.println("<script src='sigmaSrc/renderers/sigma.renderers.webgl.js'></script>");
		out.println("<script src='sigmaSrc/renderers/sigma.renderers.svg.js'></script>");
		out.println("<script src='sigmaSrc/renderers/sigma.renderers.def.js'></script>");
		out.println("<script src='sigmaSrc/renderers/webgl/sigma.webgl.nodes.def.js'></script>");
		out.println("<script src='sigmaSrc/renderers/webgl/sigma.webgl.nodes.fast.js'></script>");
		out.println("<script src='sigmaSrc/renderers/webgl/sigma.webgl.edges.def.js'></script>");
		out.println("<script src='sigmaSrc/renderers/webgl/sigma.webgl.edges.fast.js'></script>");
		out.println("<script src='sigmaSrc/renderers/webgl/sigma.webgl.edges.arrow.js'></script>");
		out.println("<script src='sigmaSrc/renderers/canvas/sigma.canvas.labels.def.js'></script>");
		out.println("<script src='sigmaSrc/renderers/canvas/sigma.canvas.hovers.def.js'></script>");
		out.println("<script src='sigmaSrc/renderers/canvas/sigma.canvas.nodes.def.js'></script>");
		out.println("<script src='sigmaSrc/renderers/canvas/sigma.canvas.edges.def.js'></script>");
		out.println("<script src='sigmaSrc/renderers/canvas/sigma.canvas.edges.curve.js'></script>");
		out.println("<script src='sigmaSrc/renderers/canvas/sigma.canvas.edges.arrow.js'></script>");
		out.println("<script src='sigmaSrc/renderers/canvas/sigma.canvas.edges.curvedArrow.js'></script>");
		out.println("<script src='sigmaSrc/renderers/canvas/sigma.canvas.edgehovers.def.js'></script>");
		out.println("<script src='sigmaSrc/renderers/canvas/sigma.canvas.edgehovers.curve.js'></script>");
		out.println("<script src='sigmaSrc/renderers/canvas/sigma.canvas.edgehovers.arrow.js'></script>");
		out.println("<script src='sigmaSrc/renderers/canvas/sigma.canvas.edgehovers.curvedArrow.js'></script>");
		out.println("<script src='sigmaSrc/renderers/canvas/sigma.canvas.extremities.def.js'></script>");
		out.println("<script src='sigmaSrc/renderers/svg/sigma.svg.utils.js'></script>");
		out.println("<script src='sigmaSrc/renderers/svg/sigma.svg.nodes.def.js'></script>");
		out.println("<script src='sigmaSrc/renderers/svg/sigma.svg.edges.def.js'></script>");
		out.println("<script src='sigmaSrc/renderers/svg/sigma.svg.edges.curve.js'></script>");
		out.println("<script src='sigmaSrc/renderers/svg/sigma.svg.edges.curvedArrow.js'></script>");
		out.println("<script src='sigmaSrc/renderers/svg/sigma.svg.labels.def.js'></script>");
		out.println("<script src='sigmaSrc/renderers/svg/sigma.svg.hovers.def.js'></script>");
		out.println("<script src='sigmaSrc/middlewares/sigma.middlewares.rescale.js'></script>");
		out.println("<script src='sigmaSrc/middlewares/sigma.middlewares.copy.js'></script>");
		out.println("<script src='sigmaSrc/misc/sigma.misc.animation.js'></script>");
		out.println("<script src='sigmaSrc/misc/sigma.misc.bindEvents.js'></script>");
		out.println("<script src='sigmaSrc/misc/sigma.misc.bindDOMEvents.js'></script>");
		out.println("<script src='sigmaSrc/misc/sigma.misc.drawHovers.js'></script>");
		out.println("<script src='plugins/sigma.renderers.customShapes/shape-library.js'></script>");
		out.println("<script src='plugins/sigma.renderers.customShapes/sigma.renderers.customShapes.js'></script>");
		out.println("<script type='text/JavaScript'>");
//	
	
		out.println("var data ={nodes:[");
		out.println(sb.toString());
		out.println(", "+sb2.toString());
		out.println("],");
		out.println("edges:[");
		out.println(sb1.toString());
		out.println("]};");
		out.println("console.log(data);");
		// out.println("console.log(data4);");
		//out.println("alert('준비중입니다')");

		out.println("</script>");
	
		out.println("<style>#graph-container {min-height: 600px;width: 100%;position: relative;}	</style>");
		out.println("<div class='ContentsBottomBottom'>");
		out.println("<div id='graph-container'></div>");
		out.println("</div>");
		out.println("<script type='text/javascript' src='js/network.js'></script>");
		out.println("</div>");
		out.println("</div>");
		out.println("</div>");
		out.println(CommonHtml.getHtml("foot", null));

		out.close();
	}

}