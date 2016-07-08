<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="baram.manager.monitor.TailLogfile"%>
<%
String seek = request.getParameter("seek");
TailLogfile tailLog = new TailLogfile();
response.addHeader("Content-Type", "application/json; charset=utf-8");
out.write(tailLog.getTailLines("/home/hadoop/project-baram/conf-demo/jdbc-hdfs/logs/loader.log", Integer.parseInt(seek)));
//out.write(tailLog.getTailLines("/home/hadoop/project-baram/conf-demo/run/logs/loader.log", Integer.parseInt(line)));
%>