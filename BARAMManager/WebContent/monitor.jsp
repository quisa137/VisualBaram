<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
  <title>Manager</title>
  <meta name="description" content="">
  <meta name="author" content="eodo">
  <meta name="viewport" content="width=device-width,initial-scale=1.0,minimum-scale=1.0">
  <link rel="shortcut icon" href="/favicon.ico">
  <link rel="apple-touch-icon" href="/apple-touch-icon.png">
  <link rel="stylesheet" href="css/Manager.css">
  <link rel="stylesheet" href="css/reset.css">
  <link rel="stylesheet" href="css/style.css">
  <link rel="stylesheet" type="text/css" href="css/bootstrap.min.css">
  <link rel="stylesheet" type="text/css" href="css/monitor.css">
  <script src="js/jquery.min.2.1.4.js"></script>
  <script src="js/moment.min.js"></script>
  <script src="js/bootstrap.min.js"></script>
  <script src="js/d3.v3.min.js" charset="utf-8"></script>
  <script type="text/javascript" src="js/monitor.js"></script>
  <script type="text/javascript" src="js/monitor_chart.js"></script>
</head>
<body>
  <div class='wrap'>
    <div class='header'>
      <h1>
        <!--img src="img/logo.png" /-->
        Welcome to BARAM
      </h1>
    </div>
    <div class="jq_tabonoff comm_tab1">
      <%@include file="include/menu.jsp"%>
      <div class="jq_cont tab_cont">
        <div class="cont">
          <p class='firstTabTop'>Monitor</p>
          <div class="jq_tabonoff comm_tab2">
            <table class="table timelbl">
              <tr>
                <td colspan="3" class="timeField"><span class="timeTxt"></span></td>
              </tr>
            </table>
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
                <table class="table syslog">
                <tr>
                  <td colspan="3" class="logField">
                    <form class="form-inline">
                      <div class="form-group">
                        <label for="">라인수</label>
                        <input type="text" name="lineLength" class="form-control lineLength" value="100">
                      </div>
                      <button class="btn btn-primary subscribe">읽기</button>
                      <button class="btn btn-default clearsubscribe">해제</button>
                    </form>
                  </td>
                </tr>
                <tr>
                  <td colspan="3">
                    <textarea name="" id="" cols="30" rows="120" class=" logOutput form-control" readonly="true"></textarea>
                  </td>
                </tr>
                </table>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</body>
</html>