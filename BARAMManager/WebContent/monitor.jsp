<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <meta name="viewport" content="width=device-width,initial-scale=1.0,minimum-scale=1.0">
  <meta name="description" content="">
  <meta name="author" content="">
  <!-- link rel="shortcut icon" href="/favicon.ico">
  <link rel="apple-touch-icon" href="/apple-touch-icon.png" -->
  <title>BARAM Management</title>
  <link rel="stylesheet" type="text/css" href="css/bootstrap.min.css">
  <link href="css/jumbotron-narrow.css" rel="stylesheet">
  <link href="css/jquery-ui.css" rel="stylesheet">
  <link rel="stylesheet" href="css/style.css">
  <link rel="stylesheet" type="text/css" href="css/monitor.css">
  <script src="js/jquery.min.2.1.4.js"></script>
  <script src="js/moment.min.js"></script>
  <script src="js/bootstrap.min.js"></script>
  <script src="js/d3.v3.min.js" charset="utf-8"></script>
  <script type="text/javascript" src="js/monitor.js"></script>
  <script type="text/javascript" src="js/monitor_chart.js"></script>
</head>
<body style="margin: 0; padding: 0">
  <header><img src='img/logo.png' style='display:block; width:114px;height:50px;'/></header>
  <div class="container">
    <div class="header clearfix">
      <nav>
        <ul class="nav nav-pills pull-right">
          <li><a href="index.jsp">Settings</a></li>
          <li><a href="settings.jsp" id="startBtn">Start</a></li>
          <li class="active"><a href="monitor.jsp">Monitor</a></li>
        </ul>
      </nav>
      <h3 class="text-muted">BARAM Management</h3>
    </div>
    <h4 class='tabTitle'>Monitor</h4>
    <div>
      <div class="timeTxt timeField"></div>
      <ul class="nav nav-tabs" role="tablist">
        <li class="active" role="tab" data-toggle="tab">
          <a href="#Stats">Stats</a>
        </li>
        <li role="tab" data-toggle="tab">
          <a href="#Syslog">Syslog</a>
        </li>
      </ul>
      <div class="tab-content">
        <div role="tabpanel" class="tab-pane active" id="Stats">
          <table class="table monitoring">
          <tr>
            <td class="lbl">CPU(%)</td>
            <td class="values">
              <span class="CpuLoadNum stat"></span>
              <span class="CpuDetail stat_detail">
                <div class="CpuTotalLoadNum"></div>
              </span>
            </td>
            <td><div id="cpuChart"></div></td>
          </tr>
          <tr>
            <td class="lbl">Memory(%)</td>
            <td class="values">
              <span class="HeapNum stat"></span>
              <span class="HeapDetail stat_detail">
                <div class="used"></div>
                <div class="max"></div>
              </span>
            </td>
            <td><div id="memChart"></div></td>
          </tr>
          <tr>
            <td class="lbl">Disk(%)</td>
            <td class="values">
              <span class="DiskNum stat"></span>
              <span class="DiskDetail stat_detail">
                <div class="free"></div>
                <div class="total"></div>
              </span>
            </td>
            <td><div id="diskChart"></div></td>
          </tr>
          </table>
        </div>
        <div role="tabpanel" class="tab-pane" id="Syslog">
          <div class="syslog">
              <form class="form-inline watch-btns">
                <button class="btn btn-primary subscribe">지켜보기</button>
                <button class="btn btn-default clearsubscribe">해제</button>
              </form>
              <textarea name="" id="" cols="30" rows="100" class=" logOutput form-control" readonly="true"></textarea>
          </div>
        </div>
      </div>
    </div>    <footer class="footer">
      <p>Copyright JinData Company. All Right Reserved.</p>
    </footer>
  </div>
  <!-- /container -->
</body>
</html>
