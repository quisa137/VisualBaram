package baram.view;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.logging.Logger;
import java.util.logging.Level;

import baram.view.Config;

public class MainServlet extends HttpServlet {

	private static final long serialVersionUID = -20151091607L;

	

	public static boolean first = true;

	public static boolean isInteralServer;
	public static boolean showLogin;
	public static boolean showAnalysis;
	public static boolean showNetwork;
	public static boolean showRowList;
	public static String jdbcDriver;
	public static String jdbcUrl;
	public static String jdbcUser;
	public static String jdbcPassword;

	public static String monitorTable;
	public static String analysisTable;
	public static String UnstructuredDatTable;
	public static String LocDangerSourceTable;

	public static String networkNodeTable;
	public static String networkEdgeTable;
	public static String dbmsOutputTable;

	Logger logger = Logger.getLogger(MainServlet.class.getName());

	public void init() {
		try {
			if (first) {
				initialize(this, logger);
			}
			logger.info("init() was called.");
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error in MainServlet.init.", e);
		}
	}

	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();

		if (User.checkSession(request, logger, this.getClass().getName()+".service", this)) {
			out.println("<script>");
			out.println("location.href='./Monitoring'");
			out.println("</script>");
		} else {
			out.println("<script>");
			out.println("location.href='./LoginPage'");
			out.println("</script>");
		}
		out.close();
	}
	
	public static void initialize(HttpServlet servlet, Logger logger) {
		
		isInteralServer = Config.isInteralServer;
		
		if (isInteralServer) {
			showLogin = Config.showLogin;
			showAnalysis = Config.showAnalysis;
			showNetwork = Config.showNetwork;
			showRowList = Config.showRowList;

			jdbcDriver = Config.jdbcDriver;
			jdbcUrl = Config.jdbcUrl;
			jdbcUser = Config.jdbcUser;
			jdbcPassword = Config.jdbcPassword;

			monitorTable = Config.monitorTable;
			analysisTable = Config.analysisTable;
			networkNodeTable=Config.networkNodeTable;
					networkEdgeTable=Config.networkEdgeTable;
					networkEdgeTable=Config.networkEdgeTable;
					UnstructuredDatTable=Config.UnstructuredDatTable;
					LocDangerSourceTable=Config.LocDangerSourceTable;
					dbmsOutputTable=Config.dbmsOutputTable;
		} else if(null!=servlet) {
			ServletContext context = servlet.getServletContext();
			String loginView = context.getInitParameter("server.view.login");
			String analysisView = context.getInitParameter("server.view.analysis");
			String networkView = context.getInitParameter("server.view.network");
			String rowlistView = context.getInitParameter("server.view.rowlist");
			showLogin = (null != loginView) && loginView.trim().equalsIgnoreCase("true");
			showAnalysis = (null != analysisView) && analysisView.trim().equalsIgnoreCase("true");
			showNetwork = (null != networkView) && networkView.trim().equalsIgnoreCase("true");
			showRowList = (null != rowlistView) && rowlistView.trim().equalsIgnoreCase("true");

			jdbcDriver = context.getInitParameter("jdbc.driver");
			jdbcUrl = context.getInitParameter("jdbc.url");
			jdbcUser = context.getInitParameter("jdbc.user");
			System.err.println(jdbcUser);
			jdbcPassword = context.getInitParameter("jdbc.password");

			monitorTable = context.getInitParameter("monitor.table");
			analysisTable = context.getInitParameter("analysis.table");
			networkNodeTable = context.getInitParameter("networkNodeTable.table");
			networkEdgeTable = context.getInitParameter("networkEdgeTable.table");
			UnstructuredDatTable = context.getInitParameter("UnstructuredDatTable.table");
			LocDangerSourceTable = context.getInitParameter("LocDangerSourceTable.table");
			dbmsOutputTable = context.getInitParameter("dbmsOutput.table");
		}

		try {
			Class.forName(jdbcDriver);
		} catch (ClassNotFoundException e) {
			logger.log(Level.FINE,
					"Error! There is no jdbc driver library for the class '" + jdbcDriver + "'.", e);
		}
		
		first = false;
	}
}
