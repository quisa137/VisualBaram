package baram.view;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;

import java.util.logging.Logger;
import java.util.logging.Level;

public class Database {
	
	public static String jdbcDriver = MainServlet.jdbcDriver;
	public static String jdbcUrl = MainServlet.jdbcUrl;
	public static String jdbcUser = MainServlet.jdbcUser;
	public static String jdbcPassword = MainServlet.jdbcPassword;
	
	public static String monitorTable = MainServlet.monitorTable;
	public static String analysisTable = MainServlet.monitorTable;
	
	public static Connection getConnection(Logger logger) {
		Connection conn = null;
		try {
			if(null==jdbcUrl && null==jdbcUser && null==jdbcPassword) {
				conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/visdb", "sys", "manager");
			} else {
				conn = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPassword);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			if(null!=logger) {
				logger.log(Level.SEVERE, "Connection failed!", e);
			}
		}
		return conn;
	}
}
