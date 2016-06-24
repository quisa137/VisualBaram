package baram.view.analysis;

import java.sql.Timestamp;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.logging.Logger;

import baram.view.Database;

public class Metadata {
	
	/*
	create database streamingviewer;
	grant all on streamingviewer.* to 'hive'@'localhost' identified by 'hadoop' with grant option;
	grant all on streamingviewer.* to 'hive'@'%' identified by 'hadoop' with grant option;
	flush privileges;
	mysql -hlocalhost -uhive -phadoop -Dstreamingviewer < streamingviewer.sql
	*/
	
	public static HashMap<String, String> ctypes = new HashMap<String, String>();
	public static HashMap<String, String> costTypes = new HashMap<String, String>();
	public static HashMap<String, String> isMobiles = new HashMap<String, String>();
	public static TreeSet<String> categories = new TreeSet<String>();
	
	public static HashMap<String, String> zones = new HashMap<String, String>();
	public static HashMap<String, String> zoneCostTypes = new HashMap<String, String>();
	public static HashMap<String, String> zoneIsMobiles = new HashMap<String, String>();
	public static HashMap<String, String> clients = new HashMap<String, String>();
	public static HashMap<String, String> campaigns = new HashMap<String, String>();
	public static HashMap<String, String> banners = new HashMap<String, String>();
	public static HashMap<String, String> clientCategories = new HashMap<String, String>();
	public static HashMap<String, StringBuilder> zctFilters = new HashMap<String, StringBuilder>();
	public static HashMap<String, StringBuilder> ismobileFilters = new HashMap<String, StringBuilder>();
	
	static {
		try {
			if(!Metadata.initialized()) {
				Metadata.main(null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String args[]) throws Exception {
		
		Logger logger = Logger.getLogger(Metadata.class.getName());
		
		ctypes.put("-1", "Passback");
		ctypes.put("103", "Mobile MMO");
		ctypes.put("200", "Mobile Casual");
		ctypes.put("201", "PC MMO");
		ctypes.put("202", "PC Casual");
		
		costTypes.put("1", "유료");
		costTypes.put("5", "무료");
		
		isMobiles.put("0", "PC");
		isMobiles.put("1", "Mobile");
		
		Class.forName("com.mysql.jdbc.Driver");
		
		Connection conn = Database.getConnection(logger);
		conn.setReadOnly(true);
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("select * from clients");
		while(rs.next()) {
			String id = rs.getString(1);
			String pname = rs.getString(2);
			String cname = rs.getString(3);
			if(null!=id&&null!=pname) {
				clients.put(id, pname);
				if(null!=cname) {
					clientCategories.put(id, cname);
					if(!categories.contains(cname)) {
						categories.add(cname);
					}
				}
			}
		}
		rs.close();
		clients.put("-1", "null");
		
		rs = stmt.executeQuery("select * from zones");
		while(rs.next()) {
			String id = rs.getString(1);
			String name = rs.getString(2);
			String costType = rs.getString(3);
			String isMobile = rs.getString(4);
			
			if(null!=id&&null!=name) {
				
				zones.put(id, name);
				if(null!=costType) {
					zoneCostTypes.put(id, costType);
				}
				if(null!=isMobile) {
					zoneIsMobiles.put(id, isMobile);
				}
				
				for(String k : zctFilters.keySet()) {
					zctFilters.get(k).setLength(0);
				}
				
				if(null!=costType) {
					StringBuilder filter = zctFilters.get(costType);
					if(null==filter) {
						filter = new StringBuilder();
						filter.append(id);
						zctFilters.put(costType, filter);
					} else {
						filter.append(","+id);
					}
				}
				
				for(String k : ismobileFilters.keySet()) {
					ismobileFilters.get(k).setLength(0);
				}
				
				if(null!=isMobile) {
					StringBuilder filter = ismobileFilters.get(isMobile);
					if(null==filter) {
						filter = new StringBuilder();
						filter.append(id);
						ismobileFilters.put(isMobile, filter);
					} else {
						filter.append(","+id);
					}
				}
			}
		}
		rs.close();
		
		rs = stmt.executeQuery("select * from campaigns");
		while(rs.next()) {
			String id = rs.getString(1);
			//campaignname, ctype, lid
			String name = rs.getString(2).replace(", ", "___") + ", " + rs.getString(3) + ", " + rs.getString(4);
			if(null!=id&&null!=name) {
				campaigns.put(id, name);
			}
		}
		rs.close();
		
		banners.put("0","-1");
		campaigns.put("-1", "null, -1, -1");
		
		stmt.close();
		conn.close();
		
	}
	
	public static Timestamp getMaxTime(int type, Logger logger) throws Exception {
		Connection conn = Database.getConnection(logger);
		conn.setReadOnly(true);
		Statement stmt = conn.createStatement();
		Timestamp time = null;
		ResultSet rs = null;
		switch(type) {
		   case 0:
			    rs = stmt.executeQuery("select max(time) time from v_five_minutely");
				rs.next();
				time = new Timestamp(rs.getTimestamp(1).getTime()+300000L);
				break;
		   case 1:
			    rs = stmt.executeQuery("select max(time) time from v_hourly");
				rs.next();
				time = new Timestamp(rs.getTimestamp(1).getTime()+3600000L);
				break;
		   case 2:
			    rs = stmt.executeQuery("select max(time) time from v_daily");
				rs.next();
				time = rs.getTimestamp(1);
				break;
		   default:
			    rs = stmt.executeQuery("select max(time) time from v_monthly");
				rs.next();
				time = rs.getTimestamp(1);
		}
		rs.close();
		stmt.close();
		conn.close();
		return time;
	}
	
	public static String getCampaignInfo(String id) {
		return campaigns.get(id);
	}
	
	public static String getCampaignName(String id) {
		return campaigns.get(id).split(", ")[0];
	}
	
	public static String getCampaignId(String id) {
		return banners.get(id);
	}
	
	public static String getZoneName(String id) {
		return zones.get(id);
	}
	
	public static String getClientName(String id) {
		return clients.get(id);
	}
	
	public static String getCampaignClient(String id) {
		return campaigns.get(id).split("___")[2];
	}
	
	public static String getCampaignClientName(String id) {
		return campaigns.get(id).split("___")[2];
	}
	
	public static String getCategoryName(String id) {
		return clientCategories.get(id);
	}
	
	public static String getCtypeName(String id) {
		return ctypes.get(id);
	}
	
	public static boolean initialized() {
		return ctypes.size()>0;
	}
	
}//The end of the class
