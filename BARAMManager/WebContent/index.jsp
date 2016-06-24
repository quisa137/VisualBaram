<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
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
	<div class='wrap'>
		<div class='header'>
			<h1>
				Welcome to BARAM
			</h1>
		</div>
		<div class="jq_tabonoff comm_tab1">
			<ul class="jq_tab tab_menu">
				<li class='liba'><img src='img/tab/start_w.png' class='imgw' /> <a class="tit">Start</a></li>
				<li class='tabOn'><img src='img/tab/settings_b.png' class='imgblue' /><a class="onTit">Settings</a></li>
				<li class='liba'><img src='img/tab/monitor_w.png' class='imgw' /> <a href='monitor.jsp' class="tit">Monitor</a></li>
			</ul>
			<div class="jq_cont tab_cont">
				<div class="cont">
					<p class='firstTabTop'>Config Select</p>
					<div class="jq_tabonoff comm_tab2">
					<form action='settings.jsp' method="post" class="formMargin">
					<label>config 파일을 선택하세요.</label><br><br>
						<select name='configSelect' class='selectBox'>
						<option value=''>  ---------- 수집 적재 방식에 따라 선택하세요 ----------</option>
						<option value='lambda'>lambda</option>
						<option value='dbms'>dbms</option>
						<option value='ssh'>ssh</option>
						<option value='type'>type</option>
						
						</select>
						<br><br>
						<div class='btnWrap'>
					<input type="submit" value="선택완료" class="btn" />
					<input type="reset" value="reset" class="btn1" />
					</div>
					</form>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>