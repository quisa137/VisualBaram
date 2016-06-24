package baram.view.datalod.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DataLoadDAO {
	private static final String URL = "jdbc:mysql://localhost:3306/mysql?user=root&password=ysC072566";

	private Connection mConnection;
	
	public DataLoadDAO() throws SQLException, ClassNotFoundException {
		Class.forName("com.mysql.jdbc.Driver");
		mConnection = DriverManager.getConnection(URL);
		
	}
	
	public List<DataLoadBean> getBoardByPage(int page) throws SQLException {
		String sql = "select * from baram_monitor;";
		
		PreparedStatement pstmt = mConnection.prepareStatement(sql);

		
		ResultSet rs = pstmt.executeQuery();
		List<DataLoadBean> dataLoadBeans = new ArrayList<DataLoadBean>();
		
		while(rs.next()) {
			DataLoadBean dataLoadBean = new DataLoadBean();
			dataLoadBean.setMergetype(rs.getInt("mergetype"));
			dataLoadBean.setYyyymmdd(rs.getInt("yyyymmdd"));
			dataLoadBean.setTime(rs.getLong("time"));
			dataLoadBean.setLogtype(rs.getString("logtype"));
			dataLoadBean.setLoader_ip(rs.getString("loader_ip"));
			dataLoadBean.setLoader_conf_dir(rs.getString("loader_conf_dir"));
			dataLoadBean.setSource_path(rs.getString("source_path"));
			dataLoadBean.setDest_path(rs.getString("dest_path"));
			dataLoadBean.setSource_size(rs.getLong("source_size"));
			dataLoadBean.setDest_size(rs.getLong("dest_size"));
			dataLoadBean.setLinecount(rs.getInt("linecount"));
			dataLoadBean.setInvalid(rs.getInt("invalid"));
			dataLoadBean.setLoader_memory(rs.getLong("loader_memory"));
			dataLoadBean.setHdfs_use(rs.getLong("hdfs_use"));
			dataLoadBean.setDirs(rs.getString("dirs"));
			dataLoadBean.setFiles(rs.getString("files"));
			dataLoadBean.setCorrupted(rs.getInt("corrupted"));
			dataLoadBean.setElapsed(rs.getString("elapsed"));
			dataLoadBean.setUtime(rs.getLong("utime"));
			
			dataLoadBeans.add(dataLoadBean);
		}
		
		rs.close();
		
		return dataLoadBeans;
	}
	public void close() throws SQLException {
		mConnection.close();
	}
	
	
	}
