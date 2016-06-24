package baram.view.analysis.abusing;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.util.logging.Level;
import java.util.logging.Logger;

import baram.view.User;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.OutputStreamWriter;

public class Abusing extends HttpServlet {

	private static final long serialVersionUID = 201511070840L;
	private static String lineSeparator = System.getProperty("line.separator","\r\n");
	private static String appName = "abusing";
	private static StringBuilder template = new StringBuilder();
	static {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(Abusing.class.getResourceAsStream("chart-template.html")));
			String line = null;
			while(null!=(line=br.readLine())) {
				template.append(line);
				template.append(lineSeparator);
			}
			br.close();
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	private Logger logger = Logger.getLogger(Abusing.class.getName());
	
	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		if (!User.checkSession(request, logger, this.getClass().getName()+".service", this)) {
			PrintWriter out = response.getWriter();
			response.setContentType("text/html;charset=UTF-8");
			out.println("<script>");
			out.println("alert('잘못된 접근입니다. 로그인 후 이용해 주세요.');");
			out.println("</script>");
			out.close();
			return;
		}
		
		//String encCheck = request.getParameter("enc_check");
		//boolean needTransform = false;
		//if(null!=encCheck&&!encCheck.equals("확")) {
			//needTransform = true;
		//}
		String dataType = request.getParameter("dataType");
		boolean isCsv = false;
		if(null==dataType||dataType.equals("csv")) {
			isCsv = true;
			response.setContentType("Content-Type: application/json;");
			response.setHeader("Content-Description", "Streaming Viewer Data Service");
			response.setHeader("Content-Disposition", "attachment; filename=result.json");
		} else {
			response.setCharacterEncoding("UTF-8");
			response.setContentType("text/html;charset=utf-8");
		}
		OutputStreamWriter osw = null;
		try {
			if(isCsv) {
				osw = new OutputStreamWriter(response.getOutputStream(), "UTF-8");
				BufferedReader br = new BufferedReader(new InputStreamReader(Abusing.class.getResourceAsStream("data.json")));
				String line = null;
				while(null!=(line=br.readLine())) {
					osw.append(line);
					osw.append(lineSeparator);
				}
				br.close();
			} else {
				osw = new OutputStreamWriter(response.getOutputStream(), "UTF-8");
				osw.append(template.toString().replace("##dataUrl##", appName+"?dataType=csv&forChart=true"));
			}
			osw.close();
			osw = null;
		} catch(Exception e) {
			e.printStackTrace();
			logger.log(Level.SEVERE, "Error!", e);
		} finally {
			try {
				if (null != osw) {
					osw.close();
				}
			} catch (IOException e) {}
		}
	}	
}
