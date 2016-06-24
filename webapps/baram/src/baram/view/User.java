package baram.view;

import java.io.PrintWriter;
import java.util.logging.Logger;
import java.util.logging.Level;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class User {
	
	private String id;
	private String name;
	private String sessionId;
	private String email;
	private String group;
	private long created;
	
	public User(String id, String name, String sessionId, String email, String group, long created) {
		this.id = id;
		this.name = name;
		this.sessionId = sessionId;
		this.email = email;
		this.group = group;
		this.created = created;
	}
	
	public boolean check(String sessionId, long created) {
		return this.created==created && null!=this.sessionId && this.sessionId.equals(sessionId);
	}
	
	public String getId() {
		return this.id;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getSessionId() {
		return this.sessionId;
	}
	
	public String getEmail() {
		return this.email;
	}
	
	public String getGroup() {
		return this.group;
	}
	
	public long getCreatedTime() {
		return this.created;
	}
	
	public static boolean login(HttpServletRequest req, Logger logger, String message, HttpServlet servlet) {
		boolean pass = false;
		if(!checkSession(req, logger, message, servlet)) {
			HttpSession session = req.getSession(false);
			Connection conn = null;
	        Statement stmt = null;
	        ResultSet rs = null;
	        try {
				String sessionId = session.getId();
				String id = req.getParameter("id");
		        String pw = req.getParameter("password");
		        conn = Database.getConnection(logger);
		        stmt = conn.createStatement();
		        rs = stmt.executeQuery("select password, name, email, `group` from member where id = '" + id + "'");
		        if(rs.next()) {
		        	if(Sha256.encrypt(pw, 15, logger).equals(rs.getString("password")))  {
		        		session.setAttribute("user", new User(id, rs.getString("name"), sessionId, rs.getString("email"), rs.getString("group"), session.getCreationTime()));
			        	pass = true;
			        	session.setAttribute("status", "logined");
		        	} else {
		        		session.setAttribute("status", "invalid password");
		        	}
		        } else {
		        	session.setAttribute("status", "no id");
		        }
		        
			} catch (SQLException e) {
				session.setAttribute("status", "internal error");
				e.printStackTrace();
				logger.log(Level.SEVERE,"Error in User.login.", e);
			} finally {
				try {
					rs.close();
				} catch (SQLException e) {}
		        try {
					stmt.close();
				} catch (SQLException e) {}
				try {
					conn.close();
				} catch (SQLException e) {}
			}
		}
		return pass;
	}
	/*public static boolean join(HttpServletRequest req, Logger logger, String message, HttpServlet servlet) {
		boolean pass = false;
		if(!checkSession(req, logger, message, servlet)) {
			HttpSession session = req.getSession(false);
			Connection conn = null;
			Statement stmt = null;
			ResultSet rs = null;
			try {
				String sessionId = session.getId();
				String id = req.getParameter("id");
				String pw = req.getParameter("password");
				conn = Database.getConnection(logger);
				stmt = conn.createStatement();
				rs = stmt.executeQuery("select password, name, email, `group` from member where id = '" + id + "'");
				if(rs.next()) {
					if(Sha256.encrypt(pw, 15, logger).equals(rs.getString("password")))  {
						session.setAttribute("user", new User(id, rs.getString("name"), sessionId, rs.getString("email"), rs.getString("group"), session.getCreationTime()));
						pass = true;
						session.setAttribute("status", "logined");
					} else {
						session.setAttribute("status", "invalid password");
					}
				} else {
					session.setAttribute("status", "no id");
				}
				
			} catch (SQLException e) {
				session.setAttribute("status", "internal error");
				e.printStackTrace();
				logger.log(Level.SEVERE,"Error in User.login.", e);
			} finally {
				try {
					rs.close();
				} catch (SQLException e) {}
				try {
					stmt.close();
				} catch (SQLException e) {}
				try {
					conn.close();
				} catch (SQLException e) {}
			}
		}
		return pass;
	}*/
	
	public static boolean checkSession(HttpServletRequest req, Logger logger, String message, HttpServlet servlet) {
		if(MainServlet.first) {
			MainServlet.initialize(servlet, logger);
		}
		HttpSession session = req.getSession(true);
		String sessionId = session.getId();
		long created = session.getCreationTime();
		Object userObj = session.getAttribute("user");
		boolean check = null!=userObj && ((User)userObj).check(sessionId, created);
		if(check) {
			User user = ((User)userObj);
			logger.info("checked,"+user.getId()+","+user.getCreatedTime()+","+user.getSessionId()+","+message);
		} else {
			if(MainServlet.showLogin) {
				logger.info("refused,unknown,"+created+","+sessionId+","+message);
			} else {
				check = true;
				session.setAttribute("user", new User("guest", "guest", sessionId, "guest@a.b.c", "2", session.getCreationTime()));
				session.setAttribute("status", "logined");
				logger.info("guest,guest,"+created+","+sessionId+","+message);
			}
		}
		return check;
	}
	
	public static void logoutSession(HttpServletRequest req, Logger logger, String message, PrintWriter out) {
		HttpSession session = req.getSession(true);
		Object userObj = session.getAttribute("user");
		session.invalidate();
		if(null!=userObj) {
			User user = (User)userObj;
			logger.info("logout,"+user.getId()+","+user.getCreatedTime()+","+user.getSessionId()+","+message);
		}
	}
}
