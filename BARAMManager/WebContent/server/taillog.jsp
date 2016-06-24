<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="baram.manager.monitor.TailLogfile" %>
<%
  String line = request.getParameter("line");
  TailLogfile tailLog = new TailLogfile();

  out.write(tailLog.getTailLines("/home/hadoop/project-baram/conf-demo/run/logs/loader.log", Integer.parseInt(line)));
%>