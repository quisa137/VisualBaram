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
		//String configSelect = (String)request.getAttribute("configSelect");
		request.setCharacterEncoding("UTF-8");
		String[] ParametersName = request
				.getParameterValues("ParametersName");
		String[] ParametersValue = request
				.getParameterValues("ParametersValue");

		String configSelect = request.getParameter("configSelect");
		FileWriter fw = null;
		FileReader fr = null;
		BufferedWriter bw = null;
		BufferedReader br = null;
		try {
		//	fw = new FileWriter("E:\\stsWorkspace\\project-baram\\conf-demo\\run\\conf.properties", false);
			fw = new FileWriter("/home/hadoop/project-baram/conf-demo/run/conf.properties", false);
			bw = new BufferedWriter(fw);

			for (int i = 0; i < ParametersName.length; i++) {
				System.out.print(ParametersName[i] + " = " + ParametersValue[i]);
				bw.write(ParametersName[i] + " = " + ParametersValue[i]);
				bw.newLine();
			}

			bw.close();

		} catch (Exception e) {

			e.printStackTrace();

		}
	%>

	


	<div class='wrap'>
		<div class='header'>
			<h1>Welcome to BARAM</h1>
		</div>
		<div class="comm_tab1">
			<ul class="jq_tab tab_menu">
				<li class='tabOn'><img src='img/tab/start_b.png'
					class='imgblue' /><a class="onTit">Start</a></li>
				<li class='liba'><img src='img/tab/settings_w.png' class='imgw'
					href='modify.jsp' /><a class="tit">Settings</a></li>
				<li class='liba'><img src='img/tab/monitor_w.png' class='imgw' /><a
					class="tit" href='monitor.jsp'>Monitor</a></li>
			</ul>
			<div class="jq_cont tab_cont">

				<div class="cont">
<p class='firstTabTop'>
									Time Settings & Start Select Config :
									<%=configSelect%>
								</p>
					

							<div class="cont">
								
								<ul class='ParametersWrap'>
								<div style='font:bold 13px/25px Malgun Gothic; '>	plugin setting이 완료 되었습니다.<br/><br/></div>
								<form action='baramStart.jsp' method="post" >
								<%
								try {
										//fr = new FileReader("E:\\stsWorkspace\\project-baram\\conf-demo\\run\\loader.run.properties");
								fr = new FileReader("/home/hadoop/project-baram/conf-demo/run/loader.run.properties");
											br = new BufferedReader(fr);
											String s = null;
											while ((s = br.readLine()) != null) {
												String[] ss = s.split(" = ");
												out.println("<li class='ParametersNameCurrent'><input name='currentName' value='"
														+ ss[0] + "'/><a class='sms'> : </a></li>");
												out.println("<li class='ParametersCurrentInput'><input name='currentValue' value='"
														+ ss[1] + "'/></li>");
											}
											br.close();
								} catch (Exception e) {
									e.printStackTrace();
								}
	%>
	<input type='hidden' value='<%=configSelect%>'
											name='configSelect' />
	<li><input type='submit' value='저장' class='btn' style='float:right;margin:10px 0;'/></li>
									</form>
								</ul>


								<br />
								<br />
							
					</div>
				</div>

			</div>
		</div>
	</div>
</body>
</html>