<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
baram.manager.monitor.JMXClient client = new baram.manager.monitor.JMXClient();
/*!!!개발용 실사용 시 삭제!!!
response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
response.setHeader("Access-Control-Max-Age", "3600");
response.setHeader("Access-Control-Allow-Headers", "x-requested-with");
response.setHeader("Access-Control-Allow-Origin", "*");
!!!개발용 실사용 시 삭제 끝!!!*/
%>
<%=client.getMonitoringData()%>
