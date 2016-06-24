<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@ page import="java.io.*"%>
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
<script type="text/javascript">
//alert("plugin setting이 완료 되었습니다.");
</script>
</head>
<body>
<%
		//String configSelect = (String)request.getAttribute("configSelect");
		request.setCharacterEncoding("UTF-8");
		String configSelect = request.getParameter("configSelect");
		FileWriter fw = null;
	
		BufferedWriter bw = null;
		
		try {
			Runtime rt = Runtime.getRuntime(); 
            Process proc = rt.exec("./baramStop.sh &"); //시스템 명령어
            System.out.print("./baramStop.sh &");
           /*  InputStream is = proc.getInputStream(); 
            InputStreamReader isr = new InputStreamReader(is); 
            BufferedReader br = new BufferedReader(isr);
 
            String line;
            while((line=br.readLine())!= null){
                System.out.println(line);
                System.out.flush();} */
	
		} catch (Exception e) {

			e.printStackTrace();

		}
	%>


	<div class='wrap'>
		<div class='header'>
			<h1>
				Welcome to BARAM
			</h1>
		</div>
		<div class="jq_tabonoff comm_tab1">
			<ul class="jq_tab tab_menu">
				<li class='tabOn'><img src='img/tab/start_b.png' class='imgblue' /><a class="onTit">Start</a></li>
				<li class='liba'><img src='img/tab/settings_w.png' class='imgw' /><a class="tit" href='modify.jsp' >Settings</a></li>
				<li class='liba'><img src='img/tab/monitor_w.png' class='imgw' /><a class="tit">Monitor</a></li>
			</ul>
			<div class="jq_cont tab_cont">
				<div class="cont">
					<p class='firstTabTop'>BARAM Stop <%=configSelect %></p>
					<div class="jq_tabonoff comm_tab2">
					<div style='font:bold 13px/25px Malgun Gothic; margin-left:15px;'>BARAM이 정지 되었습니다.<br/><br/></div>
					<form action='baramStop.jsp' method="post">
					<input type='hidden' value='<%=configSelect%>' name='configSelect' />
						<input type='submit' value='Start' class='btn'/>
						</form>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>