package baram.view;

import java.util.HashMap;

public class CommonHtml {

	private static HashMap<String, StringBuilder> htmlMap = new HashMap<String, StringBuilder>();
    
	static {
		
		// head
		StringBuilder head = new StringBuilder();
		head.append("<!DOCTYPE html PUBLIC “-//W3C//DTD XHTML 1.0 Transitional//EN” “http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd”>");
		head.append("<html>");
		head.append("<head>");
		head.append("<meta http-equiv='Content-Type' content='text/html; charset=utf-8'>");
		head.append("<meta name='viewport' content='width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=no' />");
		head.append("<title>Analysis Demo</title>");
		head.append("<link rel='shortcut icon' type='image/x-icon' href='baramicon.bmp' />");
		head.append("<link rel='stylesheet' type='text/css' href='css/jquery-ui.css' />");
		head.append("<link rel='stylesheet' type='text/css' href='css/reset.css' />");
		head.append("<link rel='stylesheet' type='text/css' href='css/style.css' />");
		head.append("<link rel='stylesheet' type='text/css' href='css/jquery.timepicker.css' />");
		head.append("<link rel='stylesheet' type='text/css' href='css/bootstrap-datepicker.css' />");
		head.append("<link rel='stylesheet' type='text/css' href='css/dc.css' >");
		head.append("<link rel='stylesheet' type='text/css' href='css/join.css' >");
		head.append("<script type='text/javascript' src='js/jquery.js'></script>");
		head.append("<script type='text/JavaScript' src='js/jquery-2.1.3.js'></script>");
		head.append("<script type='text/JavaScript' src='js/jquery_ui.js'></script>");
		head.append("<script type='text/JavaScript' src='js/select.js'></script>");
		head.append("<script type='text/javascript' src='js/HeaderGnb.js'></script>");
		head.append("<script type='text/JavaScript' src='js/jquery.timepicker.js'></script>");
		head.append("<script type='text/JavaScript' src='js/bootstrap-datepicker.js'></script>");
		head.append("<script type='text/JavaScript' src='js/datepair.js'></script>");
		head.append("<script type='text/JavaScript' src='js/csvDownload.js'></script>");
		head.append("<script type='text/javascript' src='js/d3.js'></script>");
		head.append("<script type='text/javascript' src='js/crossfilter.js'></script>");
		head.append("<script type='text/javascript' src='js/dc.js'></script>");
		

		head.append("</head>");
		head.append("<body>");
		htmlMap.put("head", head);
		
		StringBuilder top = new StringBuilder();
		top.append("<div class='Header'>");
		top.append("<h1 class='logo'><a hrdf='./Monitoring'><img src='img/logo.png' /></a></h1>");
		top.append("<ul class='gnbWrap'>");
		
		top.append("<a class='gnbBar'></a>");
		top.append("<li class='gnb' id='NoticeClick'>");
		top.append("<img src='img/top/g2.png' class='gnbImg'/>");
		top.append("<a class='gnbTextShortNotice gnbText'>Notice</a>");
		top.append("<img src='img/top/g45ClickImg.png'class='gnbClickImg'/>");
		top.append("</li>");
		top.append("<dl class='NoticeClickSub'>");
		top.append("<dd class='NoticeClickSubTop'></dd>");
		top.append("<dd class='NoticeClickSubBottom'></dd>");
		top.append("<dd class='NoticeClickSubBottom'></dd>");
		top.append("<dd class='NoticeClickSubBottom'></dd>");
		top.append("</dl>");
		top.append("<a class='gnbBar'></a>");
		top.append("<li class='gnb' id='FeedbackClick'>");
		top.append("<img src='img/top/g3.png' class='gnbImg'/>");
		top.append("<a class='gnbText'>Feedback</a>");
		top.append("</li>");
		top.append("<a class='gnbBar'></a>");
		top.append("<li class='gnb' id='HelpClick'>");
		top.append("<img src='img/top/g4.png' class='gnbImg'/>");
		top.append("<a class='gnbTextShortHelp gnbText'>Help</a>");
		top.append("<img src='img/top/g45ClickImg.png'class='gnbClickImg'/>");
		top.append("</li>");
		top.append("<dl class='HelpClickSub'>");
		top.append("<dd class='HelpClickSubTop'></dd>");
		top.append("<dd class='HelpClickSubDd'><a></a>");
		top.append("</dd>");
		top.append("<dd class='HelpClickSubDd'><a></a>");
		top.append("</dd>");
		top.append("<dd class='HelpClickSubDd'><a></a>");
		top.append("</dd>");
		top.append("<dd class='HelpClickSubDd'><a></a>");
		top.append("</dd>");
		top.append("<dd class='HelpClickSubDd'><a></a>");
		top.append("</dd>");
		top.append("<dd class='HelpClickSubDd'><a></a>");
		top.append("</dd>");
		top.append("<dd class='HelpClickSubDd'><a></a>");
		top.append("</dd>");
		top.append("<dd class='HelpClickSubDd'><a></a>");
		top.append("</dd>");
		top.append("</dl>");
		top.append("<a class='gnbBar'></a>");

		if (MainServlet.showLogin) {
			top.append("<li class='gnb' id='UserClick'>");
			top.append("<img src='img/top/g5.png' class='gnbImg'/>");
			top.append("<a class='gnbTextShortUser gnbText'>##username##</a>");
			top.append("<img src='img/top/g45ClickImg.png'class='gnbClickImg'/>");
			top.append("</li>");
			top.append("<dl class='UserClickSub'>");
			top.append("<dd class='UserClickSubTop'></dd>");
			top.append("<dd class='UserClickSubDd'><a href='./MyPage'>My profile</a></dd>");
			top.append("<dd class='UserClickSubDd'><a href='./Logout'>Logout</a></dd>");
			top.append("</dl>");
		}
		top.append("</ul>");
		top.append("</div>");
		htmlMap.put("top", top);
		

		// sideBar
		StringBuilder sideBar = new StringBuilder();
		sideBar.append("<div class='SideBar'>");
		sideBar.append("<ul class='sideNav'>");
		
		sideBar.append("<li class='sideBtnWrap'>");
		sideBar.append("<p class='sideBtnViewWrap'>");
		sideBar.append("<div class='sideNoClickAction' id='side1'></div>");
		sideBar.append("<a class='sideNoClickActionA' href='./Monitoring'><img src='img/side/s1.png' class='sideNavIcon' /></a>");
		sideBar.append("</p>");
		sideBar.append("<div class='sideNavIHoverWrap'><img src='img/side/hoverImg.png' class='sideNavHoverImg'/><a class='sideNavHoverText'>Monitoring</a>");
		sideBar.append("</div>");
		sideBar.append("</li>");

		sideBar.append("<li class='sideBtnWrap'>");
		sideBar.append("<p class='sideBtnViewWrap'>");
		sideBar.append("<div class='sideNoClickAction' id='side2'></div>");
		sideBar.append("<a class='sideNoClickActionA' href='./Streaming'><img src='img/side/s2.png'  class='sideNavIcon'/></a>");
		sideBar.append("</p>");
		sideBar.append("<div class='sideNavIHoverWrap'><img src='img/side/hoverImg.png' class='sideNavHoverImg'/><a class='sideNavHoverText'>Demo title01</a>");
		sideBar.append("</div>");
		sideBar.append("</li>");

		sideBar.append("<li class='sideBtnWrap'>");
		sideBar.append("<p class='sideBtnViewWrap'>");
		sideBar.append("<div class='sideNoClickAction' id='side3'></div>");
		sideBar.append("<a class='sideNoClickActionA' href='Interest'><img src='img/side/s3.png'  class='sideNavIcon'/></a>");
		sideBar.append("</p>");
		sideBar.append("<div class='sideNavIHoverWrap'><img src='img/side/hoverImg.png' class='sideNavHoverImg'/><a class='sideNavHoverText'>Demo title02</a>");
		sideBar.append("</div>");
		sideBar.append("</li>");

		sideBar.append("<li class='sideBtnWrap'>");
		sideBar.append("<p class='sideBtnViewWrap'>");
		sideBar.append("<div class='sideNoClickAction' id='side4'></div>");
		sideBar.append("<a class='sideNoClickActionA' href='./Advertising'><img src='img/side/s4.png'  class='sideNavIcon'/></a>");
		sideBar.append("</p>");
		sideBar.append("<div class='sideNavIHoverWrap'><img src='img/side/hoverImg.png' class='sideNavHoverImg'/><a class='sideNavHoverText'>Demo title03</a>");
		sideBar.append("</div>");
		sideBar.append("</li>");

		sideBar.append("<li class='sideBtnWrap'>");
		sideBar.append("<p class='sideBtnViewWrap'>");
		sideBar.append("<div class='sideNoClickAction' id='side5'></div>");
		sideBar.append("<a class='sideNoClickActionA' href='InstallEnv'><img src='img/side/s5.png'  class='sideNavIcon'/></a>");
		sideBar.append("</p>");
		sideBar.append("<div class='sideNavIHoverWrap'><img src='img/side/hoverImg.png' class='sideNavHoverImg'/><a class='sideNavHoverText'>Demo title04</a>");
		sideBar.append("</div>");
		sideBar.append("</li>");

		sideBar.append("<li class='sideBtnWrap'>");
		sideBar.append("<p class='sideBtnViewWrap'>");
		sideBar.append("<div class='sideNoClickAction' id='side6'></div>");
		sideBar.append("<a class='sideNoClickActionA' href='./Population'><img src='img/side/s6.png'  class='sideNavIcon'/></a>");
		sideBar.append("</p>");
		sideBar.append("<div class='sideNavIHoverWrap'><img src='img/side/hoverImg.png' class='sideNavHoverImg'/><a class='sideNavHoverText'>Demo title05</a>");
		sideBar.append("</div>");
		sideBar.append("</li>");
		
		sideBar.append("<li class='sideBtnWrap'>");
		sideBar.append("<p class='sideBtnViewWrap'>");
		sideBar.append("<div class='sideNoClickAction' id='side7'></div>");
		sideBar.append("<a class='sideNoClickActionA' href='./FirstImpression'><img src='img/side/s7.png'  class='sideNavIcon'/></a>");
		sideBar.append("</p>");
		sideBar.append("<div class='sideNavIHoverWrap'><img src='img/side/hoverImg.png' class='sideNavHoverImg'/><a class='sideNavHoverText'>Demo title06</a>");
		sideBar.append("</div>");
		sideBar.append("</li>");
		
		/*sideBar.append("<li class='sideBtnWrap'>");
		sideBar.append("<p class='sideBtnViewWrap'>");
		sideBar.append("<div class='sideNoClickAction' id='side8'></div>");
		sideBar.append("<a class='sideNoClickActionA' href='./Hurdle'><img src='img/side/s8.png'  class='sideNavIcon'/></a>");
		sideBar.append("</p>");
		sideBar.append("<div class='sideNavIHoverWrap'><img src='img/side/hoverImg.png' class='sideNavHoverImg'/><a class='sideNavHoverText'>Demo Title 07</a>");
		sideBar.append("</div>");
		sideBar.append("</li>");*/
		
		sideBar.append("<li class='sideBtnWrap'>");
		sideBar.append("<p class='sideBtnViewWrap'>");
		sideBar.append("<div class='sideNoClickAction' id='side9'></div>");
		sideBar.append("<a class='sideNoClickActionA' href='./Classification'><img src='img/side/s9.png'  class='sideNavIcon'/></a>");
		sideBar.append("</p>");
		sideBar.append("<div class='sideNavIHoverWrap'><img src='img/side/hoverImg.png' class='sideNavHoverImg'/><a class='sideNavHoverText'>Demo Title 07</a>");
		sideBar.append("</div>");
		sideBar.append("</li>");
		
		sideBar.append("<li class='sideBtnWrap'>");
		sideBar.append("<p class='sideBtnViewWrap'>");
		sideBar.append("<div class='sideNoClickAction' id='side10'></div>");
		sideBar.append("<a class='sideNoClickActionA'  href='Economy'><img src='img/side/s10.png'  class='sideNavIcon'/></a>");
		sideBar.append("</p>");
		sideBar.append("<div class='sideNavIHoverWrap'><img src='img/side/hoverImg.png' class='sideNavHoverImg'/><a class='sideNavHoverText'>Demo Title 08</a>");
		sideBar.append("</div>");
		sideBar.append("</li>");
		
		
		
		sideBar.append("<li class='sideBtnWrap'>");
		sideBar.append("<p class='sideBtnViewWrap'>");
		sideBar.append("<div class='sideNoClickAction' id='side12'></div>");
		sideBar.append("<a class='sideNoClickActionA'  href='./Network'><img src='img/side/s12.png'  class='sideNavIcon'/></a>");
		sideBar.append("</p>");
		sideBar.append("<div class='sideNavIHoverWrap'><img src='img/side/hoverImg.png' class='sideNavHoverImg'/><a class='sideNavHoverText'>Demo Title 9</a>");
		sideBar.append("</div>");
		sideBar.append("</li>");
		
		
		
		sideBar.append("<li class='sideBtnWrap'>");
		sideBar.append("<p class='sideBtnViewWrap'>");
		sideBar.append("<div class='sideNoClickAction' id='side11'></div>");
		sideBar.append("<a class='sideNoClickActionA'  href='http://192.168.0.140:8080/'><img src='img/side/s11.png'  class='sideNavIcon'/></a>");
		sideBar.append("</p>");
		sideBar.append("<div class='sideNavIHoverWrap'><img src='img/side/hoverImg.png' class='sideNavHoverImg'/><a class='sideNavHoverText'>Demo Title 10</a>");
		sideBar.append("</div>");
		sideBar.append("</li>");
		
		sideBar.append("</ul>");
		sideBar.append("</div>");
		htmlMap.put("side", sideBar);
		
		// foot
		StringBuilder foot = new StringBuilder();
		foot.append("<div class='popupWrap' id='FeedbackPopupWrap'>");
		foot.append("<div class='popupbg' id='FeedbackPopupbg'>");
		foot.append("</div>");
		foot.append("<div class='popup'></div>");
		foot.append("</div>");
		
		foot.append("<div class='popupWrap' id='btnPopupWrap'>");
		foot.append("<div class='popupbg' id='btnPopupbg'>");
		foot.append("</div>");
		foot.append("<div class='popup'></div>");
		foot.append("</div>");

	

		foot.append("</body>");
		foot.append("</html>");
		htmlMap.put("foot", foot);
	}

	public static String getHtml(String k, String arr[])  {
		StringBuilder sb = htmlMap.get(k);
		String string = sb.toString();
		if(null!=arr && arr.length>0) {
			for(int i=0; i<arr.length; i++) {
				string = string.replace(arr[i], arr[++i]);
			}
		}
		return string;
	}
}
