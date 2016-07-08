<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.io.*"%>
<%@ page import="java.util.*"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="description" content="">
<meta name="author" content="">
<title>BARAM Management</title>
<link href="css/bootstrap.min.css" rel="stylesheet">
<link href="css/jumbotron-narrow.css" rel="stylesheet">
<link href="css/jquery-ui.css" rel="stylesheet">
<script src="js/jquery.min.js"></script>
<script src="js/jquery-ui.js"></script>
<link href="css/style.css" rel="stylesheet">
<script>
	$(function() {
		$("#tabs").tabs();
	});
</script>
</head>
<body style="margin: 0; padding: 0">
	<header><img src='img/logo.png' style='display:block; width:114px;height:50px;'/></header>
	<div class="container">
		<div class="header clearfix">
			<nav>
				<ul class="nav nav-pills pull-right">
					<li><a href="index.jsp">Settings</a></li>
					<li><a id="startBtn">Start</a></li>
					<li class="active"><a href="monitor.jsp">Monitor</a></li>
				</ul>
			</nav>
			<h3 class="text-muted">BARAM Management</h3>
		</div>
		<div class="jumbotron tabBg" style=''>
			<h4 class='tabTitle'>
				title
			</h4>
			<div id="tabs" class='tabWrap'>
				<ul class='tabControllBtn'>
					<li><a href="#tabs-1">tabs-1</a></li>
					<li><a href="#tabs-2">tabs-2</a></li>
					<li><a href="#tabs-3">tabs-3</a></li>
					<li><a href="#tabs-4">tabs-4</a></li>
					<li><a href="#tabs-5">tabs-5</a></li>
					<li><a href="#tabs-6">tabs-6</a></li>
					<li><a href="#tabs-7">tabs-7</a></li>
					<li><a href="#tabs-8">tabs-8</a></li>
				</ul>
				<div id='tabs-1' style='text-align: left;'>tabs-1</div>
				<div id='tabs-2' style='text-align: left;'>tabs-2</div>
				<div id='tabs-3' style='text-align: left;'>tabs-3</div>
				<div id='tabs-4' style='text-align: left;'>tabs-4</div>
				<div id='tabs-5' style='text-align: left;'>tabs-5</div>
				<div id='tabs-6' style='text-align: left;'>tabs-6</div>
				<div id='tabs-7' style='text-align: left;'>tabs-7</div>
				<div id='tabs-8' style='text-align: left;'>tabs-8</div>
			</div>
		</div>
		<footer class="footer">
			<p>Copyright JinData Company. All Right Reserved.</p>
		</footer>
	</div>
	<!-- /container -->
</body>
</html>
