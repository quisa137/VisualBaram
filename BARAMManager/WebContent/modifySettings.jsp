<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.io.*"%>
<%@ page import="java.util.Properties"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
<title>Manager</title>
<meta name="description" content="">
<meta name="author" content="eodo">
<meta name="viewport" content="width=device-width; initial-scale=1.0">
<link rel="shortcut icon" href="/favicon.ico">
<link rel="apple-touch-icon" href="/apple-touch-icon.png">
<link rel="stylesheet" href="css/reset.css">
<link rel="stylesheet" href="css/Manager.css">
<link rel="stylesheet" href="css/style.css">
<script src="js/jquery.min.js"></script>
</head>
<body>
	<%
		request.setCharacterEncoding("UTF-8");
		String configSelect = request.getParameter("configSelect");
		System.out.print(configSelect);
		FileReader fr = null;
		
		BufferedReader br = null;
		
		try {
	//fr = new FileReader("E:\\stsWorkspace\\project-baram\\conf-demo\\run\\conf.properties");
fr = new FileReader("/home/hadoop/project-baram/conf-demo/run/conf.properties");
			br = new BufferedReader(fr);
			String s = null;
	%>
	<div class='wrap'>
		<div class='header'>
			<h1>Welcome to BARAM</h1>
		</div>
		<div class="comm_tab1">
			<ul class="jq_tab tab_menu">
				<li class='liba'><img src='img/tab/start_w.png' class='imgw' />
					<a href="start.jsp" class="tit">Start</a></li>
				<li class='tabOn'><img src='img/tab/settings_b.png'
					class='imgblue' /> <a href="settings.jsp" class="onTit">Settings</a>
				</li>
				<li class='liba'><img src='img/tab/monitor_w.png' class='imgw' />
					<a href="monitor.jsp" class="tit">Monitor</a></li>
			</ul>
			<div class="jq_cont tab_cont">
				<div class="cont">
				<p class='firstTabTop'>	Select Config : <%=configSelect%></p>
					<div class="jq_tabonoff comm_tab2">
						
								<ul class='ParametersWrap'>
									<form action='modifyTimeSettings.jsp' method="post">
										<%
											while ((s = br.readLine()) != null) {
													String[] ss = s.split(" = ");
													out.println("<li class='ParametersName'><input name='ParametersName' value='"
															+ ss[0] + "'/><a class='sms'> : </a></li>");
													out.println("<li class='ParametersInput'><input name='ParametersValue' value='"
															+ ss[1] + "'/></li>");
												}
												br.close();
										%>
										<input type='hidden' value='<%=configSelect%>'
											name='configSelect' />
										<li><li><input type='submit' value='저장' class='btn' style='float:right;margin:10px 0;'/> <br /><br />
											<br /> <br /></li>
									</form>
								</ul>
								<%
									} catch (Exception e) {
										e.printStackTrace();
									}
								%>
							
					</div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>