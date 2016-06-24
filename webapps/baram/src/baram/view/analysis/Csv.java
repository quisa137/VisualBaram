package baram.view.analysis;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import java.util.logging.Level;
import java.util.logging.Logger;

import baram.dataset.stringkey.Dataset;
import baram.dataset.stringkey.KeyedValues;
import baram.dataset.stringkey.S;
import baram.view.Database;
import baram.view.User;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class Csv extends HttpServlet {

	private static final long serialVersionUID = 201511031609L;
	private static String lineSeparator = System.getProperty("line.separator","\r\n");
	private static long oneHour = 3600000L;
	private static long oneDay = 24L*oneHour;
	private static long oneYear = 365L*oneDay;

	private static StringBuilder template = new StringBuilder();
	static {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(Csv.class.getResourceAsStream("streaming-viewer.html"), "UTF-8"));
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
	
	private static void createHtml(OutputStreamWriter osw, Dataset dataset, String title, String subtitle, String ytitle, long pointInterval, long pointStart, long pointEnd, String orderby, String topn, String basis) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.0");
		StringBuilder sb = new StringBuilder();
		int orgRowCount = dataset.getRowCount();
		int rowCount = orgRowCount;
		KeyedValues keyedValues = null;
		int topN = 15;
		if(null!=topn) {
			topN = Integer.parseInt(topn);
		}
		if(rowCount>topN) {
			keyedValues = new KeyedValues();
			for(int i=0; i<rowCount; i++) {
				String k = (String)dataset.getRowKey(i);
				if(k.indexOf("(-1)")>0||k.indexOf("Passback")>0) {
					continue;
				}
				Number n1 = dataset.getValue(k, String.valueOf(pointEnd));
				double v1 = 0;
				if(null!=n1) {
					v1 = n1.doubleValue();
				}
				if(basis!=null&&basis.equals("Weekly Variation")) {
					Number n2 = dataset.getValue(k, String.valueOf(pointEnd-oneDay*7L));
					double v2 = 0;
					if(null!=n2) {
						v2 = n2.doubleValue();
					}
					keyedValues.setValue(k, Math.abs(v2-v1));
				} else if(basis!=null&&basis.equals("Volume")) {
					keyedValues.setValue(k, v1);
				} else {
					Number n2 = dataset.getValue(k, String.valueOf(pointEnd-oneDay));
					double v2 = 0;
					if(null!=n2) {
						v2 = n2.doubleValue();
					}
					keyedValues.setValue(k, Math.abs(v2-v1));
				}
			}
			if(null!=orderby&&orderby.equals("Bottom")) {
				keyedValues.sortByValues(S.ASCENDING);
			} else {
				keyedValues.sortByValues(S.DESCENDING);
			}
			rowCount = Math.min(keyedValues.getItemCount(), topN);
		}
		
		boolean isFirst = true;
		for(int i=0; i<rowCount; i++) {
			String k = (String)dataset.getRowKey(i);
			if(null==keyedValues) {
				k = (String)dataset.getRowKey(i);
				if(k.indexOf("(-1)")>0||k.indexOf("Passback")>0) {
					continue;
				}
			} else {
				k = (String)keyedValues.getKey(i);
			}
			int columnCount = dataset.getColumnCount();
			for(int j=0;j<columnCount;j++) {
				String timeString = dataset.getColumnKey(j);
				Number n = dataset.getValue(k, timeString);
				if(null==n) {
					continue;
				}
				if(isFirst) {
					isFirst = false; 
				} else {
					sb.append(',');
				}
				sb.append("{time:'"+sdf.format(Long.valueOf(timeString)).substring(0, 19)+"', k:'"+k+"', v:"+n.doubleValue()+"}");
			}
		}
	   
	    if(null!=keyedValues) {
	    	subtitle += " - top " + topN + "/" + orgRowCount + " " + basis.toLowerCase();
	    }		
	    String html = template.toString().replace("##title##", title)
		          .replace("##subtitle##", subtitle)
		          .replace("##ytitle##", ytitle)
		          .replace("##data##",sb.toString());
		osw.append(html);
		
		dataset.clear();
		sb.setLength(0);
	}
	
	public static void main(String args[]) throws Exception {
		if(args.length<1) {
			System.out.println("Invalid arguments.");
			return;
		}		
		if(args[0].equals("refreshMetadata")) {
			Metadata.main(null);
		} else {
			System.out.println("Invalid command.");
		}
	}
	
	private Logger logger = Logger.getLogger(Csv.class.getName());
	
	public void init() {
	    logger.info("Csv Servlet started.");
	}
	
	public void getMetaJson(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=utf-8");
		PrintWriter pw = response.getWriter();
		String term = null;
		if(null!=(term=request.getParameter("term"))&&term.length()>2) {
			if(term.startsWith("확")) {
				term = term.substring(1).toLowerCase();
			} else {
				term = new String(term.getBytes("ISO-8859-1"),"UTF-8").substring(1).toLowerCase();
			}
			StringBuilder sb = new StringBuilder();
			sb.append("[");
			int count = 0;
			for(String k:Metadata.campaigns.keySet()) {
				String v = Metadata.getCampaignInfo(k);
				if(null!=v&&v.toLowerCase().indexOf(term)>=0) {
					String arr[] = v.split(", ");
					if(arr[0].indexOf(term)>=0) {
						String value = arr[0]+" ("+arr[2]+","+k+")";
						sb.append("{\"id\":\"");
						sb.append("\",\"label\":\"");
						sb.append(value);
						sb.append("\",\"value\":\"");
						sb.append(value);
						sb.append("\"}");
						if(++count>35) {
							break;
						}
					}
				}
		    }
			sb.append("]");
			pw.write(sb.toString().replace("}{","},{"));
		}
		pw.flush();
		pw.close();
	}
	
	public static String[] getMetricQuery(String metric, String groupby, StringBuilder filter, StringBuilder likeFilter) {
		String title = null;
		String subtitle = null;
		if(filter.length()>0) {
			subtitle = filter.toString().trim().replace("\t",",");
			if(likeFilter.length()>0) {
				subtitle += ",like filter"; 
			}
		} else if(likeFilter.length()>0) {
			subtitle = "with the like filter";
		} else {
			subtitle = "all the zones & all the clients";
		}
		String ytitle = null;
		String query = null;
		if(metric.equals("click")) {
			title = "Sum of Clicks group by " + groupby;
			ytitle = "N";
			query = "sum(clicks) value"; 
		} else if(metric.equals("ctr")) {
			title = "CTR group by " + groupby;
			ytitle = "%";
			query = "round(100000*sum(clicks)/sum(impressions))/1000.0 value"; 
		} else if(metric.equals("request")) {
			title = "Sum of Requests group by " + groupby;
			ytitle = "N";
			query = "sum(requests) value"; 
		} else if(metric.equals("impression")) {
			title = "Sum of Impressions group by " + groupby;
			ytitle = "N";
			query = "sum(impressions) value"; 
		} else if(metric.equals("fill-rate")) {
			title = "Fill rate group by " + groupby;
			ytitle = "%";
			query = "round(100000*sum(impressions)/sum(requests))/1000.0 value"; 
		} else if(metric.equals("conversion")) {
			title = "Sum of Conversions group by " + groupby;
			ytitle = "N";
			query = "sum(conversions1) value"; 
		} else if(metric.equals("one-day-based-conversion")) {
			title = "Sum of One-day based Conversions group by " + groupby;
			ytitle = "N";
			query = "sum(conversions2) value"; 
		} else if(metric.equals("conversion-ratio")) {
			title = "Conversion Ratio group by " + groupby;
			ytitle = "%";
			query = "round(100000*sum(conversions1)/sum(clicks))/1000.0 value"; 
		} else if(metric.equals("one-day-based-conversion-ratio")) {
			title = "One-day based Conversion Ratio group by " + groupby;
			ytitle = "%";
			query = "round(100000*sum(conversions2)/sum(clicks))/1000.0 value"; 
		} else if(metric.equals("sales")) {
			title = "Sum of Sales group by " + groupby;
			ytitle = "Won";
			query = "sum(sales1) value"; 
		} else if(metric.equals("one-day-based-sales")) {
			title = "One-day based Sales group by " + groupby;
			ytitle = "Won";
			query = "sum(sales2) value"; 
		} else if(metric.equals("roas")) {
			title = "ROAS group by " + groupby;
			ytitle = "%";
			query = "round(100000*sum(sales1)/sum(revenue))/1000.0 value"; 
		} else if(metric.equals("one-day-based-roas")) {
			title = "One-day based ROAS group by " + groupby;
			ytitle = "%";
			query = "round(100000*sum(sales2)/sum(revenue))/1000.0 value"; 
		} else if(metric.equals("revenue")) {
			title = "Sum of Revenue group by " + groupby;
			ytitle = "Won";
			query = "sum(revenue) value"; 
		} else if(metric.equals("zone-cost")) {
			title = "Sum of Zone Cost group by " + groupby;
			ytitle = "Won";
			query = "sum(cost1) value"; 
		} else if(metric.equals("zone-cost-ratio")) {
			title = "Zone Cost Ratio group by " + groupby;
			ytitle = "%";
			query = "round(100000*sum(cost1)/sum(revenue))/1000.0 value"; 
		} else if(metric.equals("net-zone-cost")) {
			title = "Sum of Zone Net Cost group by " + groupby;
			ytitle = "Won";
			query = "sum(cost2) value"; 
		} else if(metric.equals("net-zone-cost-ratio")) {
			title = "Zone Net Cost Ratio group by " + groupby;
			ytitle = "%";
			query = "round(100000*sum(cost2)/sum(revenue))/1000.0 value"; 
		} else if(metric.equals("profit")) {
			title = "Sum of Profit group by " + groupby;
			ytitle = "Won";
			query = "(sum(revenue)-sum(cost1)) value"; 
		} else if(metric.equals("net-profit")) {
			title = "Sum of Net Profit group by " + groupby;
			ytitle = "Won";
			query = "(sum(revenue)-sum(cost2)) value"; 
		} else if(metric.equals("count-of-zones")) {
			title = "Count of Zones group by " + groupby;
			ytitle = "N";
			query = "count(distinct zoneid) value"; 
		} else if(metric.equals("count-of-clients")) {
			title = "Count of Clients group by " + groupby;
			ytitle = "N";
			query = "count(distinct clientid) value"; 
		} else if(metric.equals("count-of-campaigns")) {
			title = "Count of Campaigns group by " + groupby;
			ytitle = "N";
			query = "count(distinct campaignid) value"; 
		}
		return new String[]{title,subtitle,ytitle,query};
	}
	
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
		
		String command = request.getParameter("command");
		if(null!=command) {
			PrintWriter pw = response.getWriter();
			try {
				main(new String[]{command});
				pw.append("pass\n");
			} catch(Exception e) {
				System.out.println(e.toString());
				e.printStackTrace();
				e.printStackTrace(pw);
				pw.append("fail\n");
			}
			pw.close();
			return;
		}
		
		//logger.info("Addr, User, Agent : " + request.getRemoteAddr() + ", " + request.getRemoteUser() + ", " + request.getHeader("User-Agent"));
		
		String encCheck = request.getParameter("enc_check");
		boolean needTransform = false;
		if(null!=encCheck&&!encCheck.equals("확")) {
			needTransform = true;
		}
		String dataType = request.getParameter("dataType");
		String metric = request.getParameter("metric");
		if(null==dataType) {
			metric = null;
		} else if(dataType.equals("meta")) {
			getMetaJson(request, response);
			return;
		}
		if(null==metric) {
			response.setContentType("text/csv;");
			response.setHeader("Content-Description", "Streaming Viewer Data Service");
			response.setHeader("Content-Disposition", "attachment; filename=result.csv");
		} else {
			response.setCharacterEncoding("UTF-8");
			response.setContentType("text/html;charset=utf-8");
		}
		
		//viewtype
		String viewType = request.getParameter("viewtype");
		
		//filter
		int zoneFilter = 0;
		int costTypeFilter = 0;
		int isMobileFilter = 0;
		int clientFilter = 0;
		int ctypeFilter = 0;
		int campaignFilter = 0;
		StringBuilder filter = new StringBuilder();
		
		String zone = request.getParameter("zone");
		if(null!=zone&&!zone.startsWith("All")) {
			filter.append(" \t zoneid="+zone);
			zoneFilter = 1;
		}
		String client = request.getParameter("client");
		if(null!=client&&!client.startsWith("All")) {
			filter.append(" \t clientid="+client);
			clientFilter = 1;
		}
		String ctype = request.getParameter("ctype");
		if(null!=ctype&&!ctype.startsWith("All")) {
			filter.append(" \t ctype="+ctype);
			ctypeFilter = 1;
		}
		
		StringBuilder likeFilter = new StringBuilder();
		String like = request.getParameter("like");
		String zonelike = request.getParameter("zonelike");
		if(null!=zonelike) {
			if(needTransform) {
				zonelike = new String(zonelike.getBytes("ISO-8859-1"),"UTF-8");
			}
			if(zonelike.length()>0) {
				String zonelikes[] = zonelike.replace(",","").toLowerCase().split(" ");
				for(int i=0;i<zonelikes.length;i++) {
					zonelikes[i] = zonelikes[i].toLowerCase().trim();
				}
				StringBuilder sb = new StringBuilder();
				for(String k : Metadata.zones.keySet()) {
					String v = Metadata.zones.get(k);
					if(null==v) {
						continue;
					} else {
						v = v.toLowerCase();
					}
					boolean include = false;
					for(String s : zonelikes) {
						if(v.indexOf(s)>=0) {
							include = true;
						}
					}
					if(include) {
						sb.append(k);
						sb.append(' ');
					}
				}
				if(sb.length()>0) {
					likeFilter.append(" \t zoneid " + like + " ("+sb.toString().trim().replace(' ',',')+")");
					zoneFilter = 1;
				}
			}
		}
		
		String categorylike = request.getParameter("categorylike");
		if(null!=categorylike) {
			if(needTransform) {
				categorylike = new String(categorylike.getBytes("ISO-8859-1"),"UTF-8");
			}
			if(categorylike.length()>0) {
				String categorylikes[] = categorylike.replace(",","").toLowerCase().split(" ");
				for(int i=0;i<categorylikes.length;i++) {
					categorylikes[i] = categorylikes[i].toLowerCase().trim();
				}
				StringBuilder sb = new StringBuilder();
				for(String k : Metadata.clientCategories.keySet()) {
					String v = Metadata.clientCategories.get(k);
					if(null==v) {
						continue;
					} else {
						v = v.toLowerCase();
					}
					boolean include = false;
					for(String s : categorylikes) {
						if(v.indexOf(s)>=0) {
							include = true;
						}
					}
					if(include) {
						sb.append(k);
						sb.append(' ');
					}
				}
				if(sb.length()>0) {
					likeFilter.append(" \t clientid " + like + " ("+sb.toString().trim().replace(' ',',')+")");
					clientFilter = 1;
				}
			}
		}
		
		String clientlike = request.getParameter("clientlike");
		if(null!=clientlike) {
			if(needTransform) {
				clientlike = new String(clientlike.getBytes("ISO-8859-1"),"UTF-8");
			}
			if(clientlike.length()>0) {
				String clientlikes[] = clientlike.replace(",","").toLowerCase().split(" ");
				for(int i=0;i<clientlikes.length;i++) {
					clientlikes[i] = clientlikes[i].toLowerCase().trim();
				}
				StringBuilder sb = new StringBuilder();
				for(String k : Metadata.clients.keySet()) {
					String v = Metadata.clients.get(k);
					if(null==v) {
						continue;
					} else {
						v = v.toLowerCase();
					}
					boolean include = false;
					for(String s : clientlikes) {
						if(v.indexOf(s)>=0) {
							include = true;
						}
					}
					if(include) {
						sb.append(k);
						sb.append(' ');
					}
				}
				if(sb.length()>0) {
					likeFilter.append(" \t clientid " + like + " ("+sb.toString().trim().replace(' ',',')+")");
					clientFilter = 1;
				}
			}
		}
		
		String campaignlike = request.getParameter("campaignlike");
		if(null!=campaignlike) {
			if(needTransform) {
				campaignlike = new String(campaignlike.getBytes("ISO-8859-1"),"UTF-8");
			}
			if(campaignlike.length()>0) {
				String campaignlikes[] = campaignlike.replace(",","").toLowerCase().split(" ");
				for(int i=0;i<campaignlikes.length;i++) {
					campaignlikes[i] = campaignlikes[i].toLowerCase().trim();
				}
				StringBuilder sb = new StringBuilder();
				for(String k : Metadata.campaigns.keySet()) {
					String v = Metadata.campaigns.get(k);
					if(null==v) {
						continue;
					} else {
						v = v.toLowerCase();
					}
					boolean include = false;
					for(String s : campaignlikes) {
						if(v.indexOf(s)>=0) {
							include = true;
						}
					}
					if(include) {
						sb.append(k);
						sb.append(' ');
					}
				}
				if(sb.length()>0) {
					likeFilter.append(" \t campaignid " + like + " ("+sb.toString().trim().replace(' ',',')+")");
					campaignFilter = 1;
				}
			}
		}
		
		String campaign = request.getParameter("campaign");
		int idx1 = 0;
		int idx2 = 0;
		if(null!=campaign&&(idx1=campaign.lastIndexOf("("))>0&&(idx2=campaign.lastIndexOf(")"))>0&&idx2>idx1) {
			filter.append(" \t clientid="+campaign.substring(idx1+1,idx2).replace(","," \t campaignid="));
			campaignFilter = 1;
		}
		
		String ismobile = request.getParameter("ismobile");
		if(null!=ismobile&&!ismobile.startsWith("All")) {
			filter.append(" \t is_mobile="+ismobile);
			isMobileFilter = 1;
		}
		
		String costType = request.getParameter("costtype");
		if(null!=costType&&!costType.startsWith("All")) {
			filter.append(" \t cost_type="+costType);
			costTypeFilter = 1;
		}
		
		Dataset dataset = null;
		if(null!=metric) {
			 dataset = new Dataset();
		}
		
		String orderby = request.getParameter("orderby");
		String topn = request.getParameter("topn");
		String basis = request.getParameter("basis");
		
		//group by
		String groupby = request.getParameter("groupby");
		try {

			Connection conn = Database.getConnection(logger);
			Statement stmt = conn.createStatement();
			
			OutputStreamWriter osw = null;
			if(null==metric) {
				osw = new OutputStreamWriter(response.getOutputStream(), "MS949");
				osw.append(groupby);
				osw.append(", Time, Requests, Impressions, Clicks, Conversions1, Conversions2, Revenue, Cost1, Cost2, Sales1, Sales2");
				osw.append(lineSeparator);
			} else {
				osw = new OutputStreamWriter(response.getOutputStream(), "UTF-8");
			}
			
			if(viewType.equals("Five-Minutely")) {
				Timestamp time = Metadata.getMaxTime(0, logger);
				long mt = time.getTime();
				if(groupby.equals("Time-Period")) {
					String title = null;
					String subtitle = null;
					String ytitle = null;
					Timestamp time1 = new Timestamp(mt-oneHour);
					Timestamp oneDayBefore1 = new Timestamp(mt-oneDay-oneHour);
					String query = null;
					if(null==metric) {
						query = "select time, sum(requests) requests, sum(impressions) impressions, sum(clicks) clicks, sum(conversions1) conversions1, sum(conversions2) conversions2, sum(revenue) revenue, sum(cost1) cost1, sum(cost2) cost2, sum(sales1) sales1, sum(sales2) sales2 from v_five_minutely";
					} else {
						String arr[] = getMetricQuery(metric, groupby, filter, likeFilter);
						title = arr[0];
						subtitle = arr[1];
						ytitle = arr[2];
						query = "select time, " + arr[3] + " from v_five_minutely";
					}
					if(filter.length()>0) {
						query += " where " + filter.toString().trim().replace("\t","and");
					}
					if(likeFilter.length()>0) {
						if(query.indexOf(" where ")>0) {
							query += " and " + likeFilter.toString().trim().replace("\t","and");
						} else {
							query += " where " + likeFilter.toString().trim().replace("\t","and");
						}
					}
					query += " group by time";
					logger.info("Query : " + query);
					long stime = System.currentTimeMillis();
					ResultSet rs = stmt.executeQuery(query);
					logger.info("Elapsed time for executing query : " + (System.currentTimeMillis()-stime)/1000.0 + "s");
					long pointStart = 0L;
					long pointEnd = 0L;
					while(rs.next()) {
						Timestamp t = rs.getTimestamp("time");
						long tLong = t.getTime();
						String k1 = null;
						Timestamp k2 = null;
						if(t.equals(time1)||t.after(time1)) {
							k1 = "latest one hour";
							k2 = t;
						} else if(t.equals(oneDayBefore1)||t.after(oneDayBefore1)) {
							k1 = "yesterday";
							k2 = new Timestamp(tLong+oneDay);
						} else {
							k1 = "last week";
							k2 = new Timestamp(tLong+7L*oneDay);
						}
						tLong = k2.getTime();
						if(pointStart==0L) {
							pointStart = tLong;
						}
						if(pointEnd<tLong) {
							pointEnd = tLong;
						}
						if(null==metric) {
							String rowKey = k1+", "+k2.toString();
							osw.append(rowKey);
							osw.append(", ");
							osw.append(rs.getString("requests"));
							osw.append(", ");
							osw.append(rs.getString("impressions"));
							osw.append(", ");
							osw.append(rs.getString("clicks"));
							osw.append(", ");
							osw.append(rs.getString("conversions1"));
							osw.append(", ");
							osw.append(rs.getString("conversions2"));
							osw.append(", ");
							osw.append(rs.getString("revenue"));
							osw.append(", ");
							osw.append(rs.getString("cost1"));
							osw.append(", ");
							osw.append(rs.getString("cost2"));
							osw.append(", ");
							osw.append(rs.getString("sales1"));
							osw.append(", ");
							osw.append(rs.getString("sales2"));
							osw.append(lineSeparator);
						} else {
							dataset.setValue(rs.getDouble("value"), k1, String.valueOf(tLong));
						}
					}
					rs.close();
					if(null!=dataset) {
						createHtml(osw, dataset, title, subtitle, ytitle, 300000L, pointStart, pointEnd, orderby, topn, basis);
					}
				} else if(groupby.equals("Cost-Type")) {
					String title = null;
					String subtitle = null;
					String ytitle = null;
					Timestamp time1 = new Timestamp(mt-oneHour);
					Timestamp oneDayBefore1 = new Timestamp(mt-oneDay-oneHour);
					String query = null;
					if(null==metric) {
						query = "select time, cost_type, sum(requests) requests, sum(impressions) impressions, sum(clicks) clicks, sum(conversions1) conversions1, sum(conversions2) conversions2, sum(revenue) revenue, sum(cost1) cost1, sum(cost2) cost2, sum(sales1) sales1, sum(sales2) sales2 from v_five_minutely";
					} else {
						String arr[] = getMetricQuery(metric, groupby, filter, likeFilter);
						title = arr[0];
						subtitle = arr[1];
						ytitle = arr[2];
						query = "select time, cost_type," + arr[3] + " from v_five_minutely";
					}	
					if(filter.length()>0) {
						query += " where " + filter.toString().trim().replace("\t","and");
					}
					if(likeFilter.length()>0) {
						if(query.indexOf(" where ")>0) {
							query += " and " + likeFilter.toString().trim().replace("\t","and");
						} else {
							query += " where " + likeFilter.toString().trim().replace("\t","and");
						}
					}
					query += " group by time, cost_type";
					logger.info("Query : " + query);
					long stime = System.currentTimeMillis();
					ResultSet rs = stmt.executeQuery(query);
					logger.info("Elapsed time for executing query : " + (System.currentTimeMillis()-stime)/1000.0 + "s");
					long pointStart = 0L;
					long pointEnd = 0L;
					while(rs.next()) {
						Timestamp t = rs.getTimestamp("time");
						long tLong = t.getTime();
						String c1 = Metadata.costTypes.get(rs.getString("cost_type"));
						if(null==c1) {
							continue;
						}
						c1 += " (" + rs.getString("cost_type") + ")";
						String k1 = c1 + ".";
						Timestamp k2 = null;
						if(t.equals(time1)||t.after(time1)) {
							k1 += "latest one hour";
							k2 = t;
						} else if(t.equals(oneDayBefore1)||t.after(oneDayBefore1)) {
							k1 += "yesterday";
							k2 = new Timestamp(tLong+oneDay);
						} else {
							k1 += "last week";
							k2 = new Timestamp(tLong+7L*oneDay);
						}
						tLong = k2.getTime();
						if(pointStart==0L) {
							pointStart = tLong;
						}
						if(pointEnd<tLong) {
							pointEnd = tLong;
						}
						if(null==metric) {
							String rowKey = k1+", "+k2.toString();
							osw.append(rowKey);
							osw.append(", ");
							osw.append(rs.getString("requests"));
							osw.append(", ");
							osw.append(rs.getString("impressions"));
							osw.append(", ");
							osw.append(rs.getString("clicks"));
							osw.append(", ");
							osw.append(rs.getString("conversions1"));
							osw.append(", ");
							osw.append(rs.getString("conversions2"));
							osw.append(", ");
							osw.append(rs.getString("revenue"));
							osw.append(", ");
							osw.append(rs.getString("cost1"));
							osw.append(", ");
							osw.append(rs.getString("cost2"));
							osw.append(", ");
							osw.append(rs.getString("sales1"));
							osw.append(", ");
							osw.append(rs.getString("sales2"));
							osw.append(lineSeparator);
						} else {
							dataset.setValue(rs.getDouble("value"), k1, String.valueOf(tLong));
						}
					}
					rs.close();
					if(null!=dataset) {
						createHtml(osw, dataset, title, subtitle, ytitle, 300000L, pointStart, pointEnd, orderby, topn, basis);
					}
				} else if(groupby.equals("Is-Mobile")) {
					String title = null;
					String subtitle = null;
					String ytitle = null;
					Timestamp time1 = new Timestamp(mt-oneHour);
					Timestamp oneDayBefore1 = new Timestamp(mt-oneDay-oneHour);
					String query = null;
					if(null==metric) {
						query = "select time, is_mobile, sum(requests) requests, sum(impressions) impressions, sum(clicks) clicks, sum(conversions1) conversions1, sum(conversions2) conversions2, sum(revenue) revenue, sum(cost1) cost1, sum(cost2) cost2, sum(sales1) sales1, sum(sales2) sales2 from v_five_minutely";
					} else {
						String arr[] = getMetricQuery(metric, groupby, filter, likeFilter);
						title = arr[0];
						subtitle = arr[1];
						ytitle = arr[2];
						query = "select time, is_mobile, " + arr[3] + " from v_five_minutely";
					}	
					if(filter.length()>0) {
						query += " where " + filter.toString().trim().replace("\t","and");
					}
					if(likeFilter.length()>0) {
						if(query.indexOf(" where ")>0) {
							query += " and " + likeFilter.toString().trim().replace("\t","and");
						} else {
							query += " where " + likeFilter.toString().trim().replace("\t","and");
						}
					}
					query += " group by time, is_mobile";
					logger.info("Query : " + query);
					long stime = System.currentTimeMillis();
					ResultSet rs = stmt.executeQuery(query);
					logger.info("Elapsed time for executing query : " + (System.currentTimeMillis()-stime)/1000.0 + "s");
					long pointStart = 0L;
					long pointEnd = 0L;
					while(rs.next()) {
						Timestamp t = rs.getTimestamp("time");
						long tLong = t.getTime();
						String c1 = Metadata.isMobiles.get(rs.getString("is_mobile"));
						if(null==c1) {
							continue;
						}
						c1 += " (" + rs.getString("is_mobile") + ")";
						String k1 = c1 + ".";
						Timestamp k2 = null;
						if(t.equals(time1)||t.after(time1)) {
							k1 += "latest one hour";
							k2 = t;
						} else if(t.equals(oneDayBefore1)||t.after(oneDayBefore1)) {
							k1 += "yesterday";
							k2 = new Timestamp(tLong+oneDay);
						} else {
							k1 += "last week";
							k2 = new Timestamp(tLong+7L*oneDay);
						}
						tLong = k2.getTime();
						if(pointStart==0L) {
							pointStart = tLong;
						}
						if(pointEnd<tLong) {
							pointEnd = tLong;
						}
						if(null==metric) {
							String rowKey = k1+", "+k2.toString();
							osw.append(rowKey);
							osw.append(", ");
							osw.append(rs.getString("requests"));
							osw.append(", ");
							osw.append(rs.getString("impressions"));
							osw.append(", ");
							osw.append(rs.getString("clicks"));
							osw.append(", ");
							osw.append(rs.getString("conversions1"));
							osw.append(", ");
							osw.append(rs.getString("conversions2"));
							osw.append(", ");
							osw.append(rs.getString("revenue"));
							osw.append(", ");
							osw.append(rs.getString("cost1"));
							osw.append(", ");
							osw.append(rs.getString("cost2"));
							osw.append(", ");
							osw.append(rs.getString("sales1"));
							osw.append(", ");
							osw.append(rs.getString("sales2"));
							osw.append(lineSeparator);
						} else {
							dataset.setValue(rs.getDouble("value"), k1, String.valueOf(tLong));
						}
					}
					rs.close();
					if(null!=dataset) {
						createHtml(osw, dataset, title, subtitle, ytitle, 300000L, pointStart, pointEnd, orderby, topn, basis);
					}
				} else if(groupby.equals("Campaign-Type")) {
					String title = null;
					String subtitle = null;
					String ytitle = null;
					Timestamp time1 = new Timestamp(mt-oneHour);
					Timestamp oneDayBefore1 = new Timestamp(mt-oneDay-oneHour);
					String query = null;
					if(null==metric) {
						query = "select time, ctype, sum(requests) requests, sum(impressions) impressions, sum(clicks) clicks, sum(conversions1) conversions1, sum(conversions2) conversions2, sum(revenue) revenue, sum(cost1) cost1, sum(cost2) cost2, sum(sales1) sales1, sum(sales2) sales2 from v_five_minutely";
					} else {
						String arr[] = getMetricQuery(metric, groupby, filter, likeFilter);
						title = arr[0];
						subtitle = arr[1];
						ytitle = arr[2];
						query = "select time, ctype, " + arr[3] + " from v_five_minutely";
					}	
					if(filter.length()>0) {
						query += " where " + filter.toString().trim().replace("\t","and");
					}
					if(likeFilter.length()>0) {
						if(query.indexOf(" where ")>0) {
							query += " and " + likeFilter.toString().trim().replace("\t","and");
						} else {
							query += " where " + likeFilter.toString().trim().replace("\t","and");
						}
					}
					query += " group by time, ctype";
					logger.info("Query : " + query);
					long stime = System.currentTimeMillis();
					ResultSet rs = stmt.executeQuery(query);
					logger.info("Elapsed time for executing query : " + (System.currentTimeMillis()-stime)/1000.0 + "s");
					long pointStart = 0L;
					long pointEnd = 0L;
					while(rs.next()) {
						Timestamp t = rs.getTimestamp("time");
						long tLong = t.getTime();
						String c1 = Metadata.ctypes.get(rs.getString("ctype"));
						if(null==c1) {
							continue;
						}
						c1 += " (" + rs.getString("ctype") + ")";
						String k1 = c1 + ".";
						Timestamp k2 = null;
						if(t.equals(time1)||t.after(time1)) {
							k1 += "latest one hour";
							k2 = t;
						} else if(t.equals(oneDayBefore1)||t.after(oneDayBefore1)) {
							k1 += "yesterday";
							k2 = new Timestamp(tLong+oneDay);
						} else {
							k1 += "last week";
							k2 = new Timestamp(tLong+7L*oneDay);
						}
						tLong = k2.getTime();
						if(pointStart==0L) {
							pointStart = tLong;
						}
						if(pointEnd<tLong) {
							pointEnd = tLong;
						}
						if(null==metric) {
							String rowKey = k1+", "+k2.toString();
							osw.append(rowKey);
							osw.append(", ");
							osw.append(rs.getString("requests"));
							osw.append(", ");
							osw.append(rs.getString("impressions"));
							osw.append(", ");
							osw.append(rs.getString("clicks"));
							osw.append(", ");
							osw.append(rs.getString("conversions1"));
							osw.append(", ");
							osw.append(rs.getString("conversions2"));
							osw.append(", ");
							osw.append(rs.getString("revenue"));
							osw.append(", ");
							osw.append(rs.getString("cost1"));
							osw.append(", ");
							osw.append(rs.getString("cost2"));
							osw.append(", ");
							osw.append(rs.getString("sales1"));
							osw.append(", ");
							osw.append(rs.getString("sales2"));
							osw.append(lineSeparator);
						} else {
							dataset.setValue(rs.getDouble("value"), k1, String.valueOf(tLong));
						}
					}
					rs.close();
					if(null!=dataset) {
						createHtml(osw, dataset, title, subtitle, ytitle, 300000L, pointStart, pointEnd, orderby, topn, basis);
					}
				} else if(groupby.equals("Zone")) {
					String title = null;
					String subtitle = null;
					String ytitle = null;
					Timestamp time1 = new Timestamp(mt-oneHour);
					Timestamp oneDayBefore1 = new Timestamp(mt-oneDay-oneHour);
					String query = null;
					if(null==metric) {
						query = "select time, zoneid, sum(requests) requests, sum(impressions) impressions, sum(clicks) clicks, sum(conversions1) conversions1, sum(conversions2) conversions2, sum(revenue) revenue, sum(cost1) cost1, sum(cost2) cost2, sum(sales1) sales1, sum(sales2) sales2 from v_five_minutely";
					} else {
						String arr[] = getMetricQuery(metric, groupby, filter, likeFilter);
						title = arr[0];
						subtitle = arr[1];
						ytitle = arr[2];
						query = "select time, zoneid, " + arr[3] + " from v_five_minutely";
					}	
					if(filter.length()>0) {
						query += " where " + filter.toString().trim().replace("\t","and");
					}
					if(likeFilter.length()>0) {
						if(query.indexOf(" where ")>0) {
							query += " and " + likeFilter.toString().trim().replace("\t","and");
						} else {
							query += " where " + likeFilter.toString().trim().replace("\t","and");
						}
					}
					query += " group by time, zoneid";
					logger.info("Query : " + query);
					long stime = System.currentTimeMillis();
					ResultSet rs = stmt.executeQuery(query);
					logger.info("Elapsed time for executing query : " + (System.currentTimeMillis()-stime)/1000.0 + "s");
					long pointStart = 0L;
					long pointEnd = 0L;
					while(rs.next()) {
						Timestamp t = rs.getTimestamp("time");
						long tLong = t.getTime();
						String c1 = Metadata.zones.get(rs.getString("zoneid"));
						if(null==c1) {
							continue;
						}
						c1 += " (" + rs.getString("zoneid") + ")";
						String k1 = c1 + ".";
						Timestamp k2 = null;
						if(t.equals(time1)||t.after(time1)) {
							k1 += "latest one hour";
							k2 = t;
						} else if(t.equals(oneDayBefore1)||t.after(oneDayBefore1)) {
							k1 += "yesterday";
							k2 = new Timestamp(tLong+oneDay);
						} else {
							k1 += "last week";
							k2 = new Timestamp(tLong+7L*oneDay);
						}
						tLong = k2.getTime();
						if(pointStart==0L) {
							pointStart = tLong;
						}
						if(pointEnd<tLong) {
							pointEnd = tLong;
						}
						if(null==metric) {
							String rowKey = k1+", "+k2.toString();
							osw.append(rowKey);
							osw.append(", ");
							osw.append(rs.getString("requests"));
							osw.append(", ");
							osw.append(rs.getString("impressions"));
							osw.append(", ");
							osw.append(rs.getString("clicks"));
							osw.append(", ");
							osw.append(rs.getString("conversions1"));
							osw.append(", ");
							osw.append(rs.getString("conversions2"));
							osw.append(", ");
							osw.append(rs.getString("revenue"));
							osw.append(", ");
							osw.append(rs.getString("cost1"));
							osw.append(", ");
							osw.append(rs.getString("cost2"));
							osw.append(", ");
							osw.append(rs.getString("sales1"));
							osw.append(", ");
							osw.append(rs.getString("sales2"));
							osw.append(lineSeparator);
						} else {
							dataset.setValue(rs.getDouble("value"), k1, String.valueOf(tLong));
						}
					}
					rs.close();
					if(null!=dataset) {
						createHtml(osw, dataset, title, subtitle, ytitle, 300000L, pointStart, pointEnd, orderby, topn, basis);
					}
				} else if(groupby.equals("Client")) {
					String title = null;
					String subtitle = null;
					String ytitle = null;
					Timestamp time1 = new Timestamp(mt-oneHour);
					Timestamp oneDayBefore1 = new Timestamp(mt-oneDay-oneHour);
					String query = null;
					if(null==metric) {
						query = "select time, clientid, sum(requests) requests, sum(impressions) impressions, sum(clicks) clicks, sum(conversions1) conversions1, sum(conversions2) conversions2, sum(revenue) revenue, sum(cost1) cost1, sum(cost2) cost2, sum(sales1) sales1, sum(sales2) sales2 from v_five_minutely";
					} else {
						String arr[] = getMetricQuery(metric, groupby, filter, likeFilter);
						title = arr[0];
						subtitle = arr[1];
						ytitle = arr[2];
						query = "select time, clientid, " + arr[3] + " from v_five_minutely";
					}	
					if(filter.length()>0) {
						query += " where " + filter.toString().trim().replace("\t","and");
					}
					if(likeFilter.length()>0) {
						if(query.indexOf(" where ")>0) {
							query += " and " + likeFilter.toString().trim().replace("\t","and");
						} else {
							query += " where " + likeFilter.toString().trim().replace("\t","and");
						}
					}
					query += " group by time, clientid";
					logger.info("Query : " + query);
					long stime = System.currentTimeMillis();
					ResultSet rs = stmt.executeQuery(query);
					logger.info("Elapsed time for executing query : " + (System.currentTimeMillis()-stime)/1000.0 + "s");
					long pointStart = 0L;
					long pointEnd = 0L;
					while(rs.next()) {
						Timestamp t = rs.getTimestamp("time");
						long tLong = t.getTime();
						String c1 = Metadata.clients.get(rs.getString("clientid"));
						if(null==c1) {
							continue;
						}
						c1 += " (" + rs.getString("clientid") + ")";
						String k1 = c1 + ".";
						Timestamp k2 = null;
						if(t.equals(time1)||t.after(time1)) {
							k1 += "latest one hour";
							k2 = t;
						} else if(t.equals(oneDayBefore1)||t.after(oneDayBefore1)) {
							k1 += "yesterday";
							k2 = new Timestamp(tLong+oneDay);
						} else {
							k1 += "last week";
							k2 = new Timestamp(tLong+7L*oneDay);
						}
						tLong = k2.getTime();
						if(pointStart==0L) {
							pointStart = tLong;
						}
						if(pointEnd<tLong) {
							pointEnd = tLong;
						}
						if(null==metric) {
							String rowKey = k1+", "+k2.toString();
							osw.append(rowKey);
							osw.append(", ");
							osw.append(rs.getString("requests"));
							osw.append(", ");
							osw.append(rs.getString("impressions"));
							osw.append(", ");
							osw.append(rs.getString("clicks"));
							osw.append(", ");
							osw.append(rs.getString("conversions1"));
							osw.append(", ");
							osw.append(rs.getString("conversions2"));
							osw.append(", ");
							osw.append(rs.getString("revenue"));
							osw.append(", ");
							osw.append(rs.getString("cost1"));
							osw.append(", ");
							osw.append(rs.getString("cost2"));
							osw.append(", ");
							osw.append(rs.getString("sales1"));
							osw.append(", ");
							osw.append(rs.getString("sales2"));
							osw.append(lineSeparator);
						} else {
							dataset.setValue(rs.getDouble("value"), k1, String.valueOf(tLong));
						}
					}
					rs.close();
					if(null!=dataset) {
						createHtml(osw, dataset, title, subtitle, ytitle, 300000L, pointStart, pointEnd, orderby, topn, basis);
					}
				} else if(groupby.equals("Client-Campaign")) {
					String title = null;
					String subtitle = null;
					String ytitle = null;
					Timestamp time1 = new Timestamp(mt-oneHour);
					Timestamp oneDayBefore1 = new Timestamp(mt-oneDay-oneHour);
					String query = null;
					if(null==metric) {
						query = "select time, clientid, campaignid, sum(requests) requests, sum(impressions) impressions, sum(clicks) clicks, sum(conversions1) conversions1, sum(conversions2) conversions2, sum(revenue) revenue, sum(cost1) cost1, sum(cost2) cost2, sum(sales1) sales1, sum(sales2) sales2 from v_five_minutely";
					} else {
						String arr[] = getMetricQuery(metric, groupby, filter, likeFilter);
						title = arr[0];
						subtitle = arr[1];
						ytitle = arr[2];
						query = "select time, clientid, campaignid, " + arr[3] + " from v_five_minutely";
					}	
					if(filter.length()>0) {
						query += " where " + filter.toString().trim().replace("\t","and");
					}
					if(likeFilter.length()>0) {
						if(query.indexOf(" where ")>0) {
							query += " and " + likeFilter.toString().trim().replace("\t","and");
						} else {
							query += " where " + likeFilter.toString().trim().replace("\t","and");
						}
					}
					query += " group by time, clientid, campaignid";
					logger.info("Query : " + query);
					long stime = System.currentTimeMillis();
					ResultSet rs = stmt.executeQuery(query);
					logger.info("Elapsed time for executing query : " + (System.currentTimeMillis()-stime)/1000.0 + "s");
					long pointStart = 0L;
					long pointEnd = 0L;
					while(rs.next()) {
						Timestamp t = rs.getTimestamp("time");
						long tLong = t.getTime();
						String c1 = Metadata.clients.get(rs.getString("clientid"));
						if(null==c1) {
							continue;
						}
						String c2 = Metadata.campaigns.get(rs.getString("campaignid"));
						if(null==c2) {
							continue;
						}
						c1 += " (" + rs.getString("clientid") + ")";
						c2 += " (" + rs.getString("campaignid") + ")";
						String k1 = c1 + " - " + c2 + ".";
						Timestamp k2 = null;
						if(t.equals(time1)||t.after(time1)) {
							k1 += "latest one hour";
							k2 = t;
						} else if(t.equals(oneDayBefore1)||t.after(oneDayBefore1)) {
							k1 += "yesterday";
							k2 = new Timestamp(tLong+oneDay);
						} else {
							k1 += "last week";
							k2 = new Timestamp(tLong+7L*oneDay);
						}
						tLong = k2.getTime();
						if(pointStart==0L) {
							pointStart = tLong;
						}
						if(pointEnd<tLong) {
							pointEnd = tLong;
						}
						if(null==metric) {
							String rowKey = k1+", "+k2.toString();
							osw.append(rowKey);
							osw.append(", ");
							osw.append(rs.getString("requests"));
							osw.append(", ");
							osw.append(rs.getString("impressions"));
							osw.append(", ");
							osw.append(rs.getString("clicks"));
							osw.append(", ");
							osw.append(rs.getString("conversions1"));
							osw.append(", ");
							osw.append(rs.getString("conversions2"));
							osw.append(", ");
							osw.append(rs.getString("revenue"));
							osw.append(", ");
							osw.append(rs.getString("cost1"));
							osw.append(", ");
							osw.append(rs.getString("cost2"));
							osw.append(", ");
							osw.append(rs.getString("sales1"));
							osw.append(", ");
							osw.append(rs.getString("sales2"));
							osw.append(lineSeparator);
						} else {
							dataset.setValue(rs.getDouble("value"), k1, String.valueOf(tLong));
						}
					}
					rs.close();
					if(null!=dataset) {
						createHtml(osw, dataset, title, subtitle, ytitle, 300000L, pointStart, pointEnd, orderby, topn, basis);
					}
				} else if(groupby.equals("Client-Category")) {
					String title = null;
					String subtitle = null;
					String ytitle = null;
					Timestamp time1 = new Timestamp(mt-oneHour);
					Timestamp oneDayBefore1 = new Timestamp(mt-oneDay-oneHour);
					String query = null;
					if(null==metric) {
						query = "select time, category, sum(requests) requests, sum(impressions) impressions, sum(clicks) clicks, sum(conversions1) conversions1, sum(conversions2) conversions2, sum(revenue) revenue, sum(cost1) cost1, sum(cost2) cost2, sum(sales1) sales1, sum(sales2) sales2 from v_five_minutely a join clients b on (a.clientid=b.client)";
					} else {
						String arr[] = getMetricQuery(metric, groupby, filter, likeFilter);
						title = arr[0];
						subtitle = arr[1];
						ytitle = arr[2];
						query = "select time, category, " + arr[3] + " from v_five_minutely a join clients b on (a.clientid=b.client)";
					}	
					if(filter.length()>0) {
						query += " where " + filter.toString().trim().replace("\t","and");
					}
					if(likeFilter.length()>0) {
						if(query.indexOf(" where ")>0) {
							query += " and " + likeFilter.toString().trim().replace("\t","and");
						} else {
							query += " where " + likeFilter.toString().trim().replace("\t","and");
						}
					}
					query += " group by time, category";
					logger.info("Query : " + query);
					long stime = System.currentTimeMillis();
					ResultSet rs = stmt.executeQuery(query);
					logger.info("Elapsed time for executing query : " + (System.currentTimeMillis()-stime)/1000.0 + "s");
					long pointStart = 0L;
					long pointEnd = 0L;
					while(rs.next()) {
						Timestamp t = rs.getTimestamp("time");
						long tLong = t.getTime();
						String k1 = rs.getString("category");
						if(k1==null) {
							k1 = "None";
						}
						k1 = k1 + ".";
						Timestamp k2 = null;
						if(t.equals(time1)||t.after(time1)) {
							k1 += "latest one hour";
							k2 = t;
						} else if(t.equals(oneDayBefore1)||t.after(oneDayBefore1)) {
							k1 += "yesterday";
							k2 = new Timestamp(tLong+oneDay);
						} else {
							k1 += "last week";
							k2 = new Timestamp(tLong+7L*oneDay);
						}
						tLong = k2.getTime();
						if(pointStart==0L) {
							pointStart = tLong;
						}
						if(pointEnd<tLong) {
							pointEnd = tLong;
						}
						if(null==metric) {
							String rowKey = k1+", "+k2.toString();
							osw.append(rowKey);
							osw.append(", ");
							osw.append(rs.getString("requests"));
							osw.append(", ");
							osw.append(rs.getString("impressions"));
							osw.append(", ");
							osw.append(rs.getString("clicks"));
							osw.append(", ");
							osw.append(rs.getString("conversions1"));
							osw.append(", ");
							osw.append(rs.getString("conversions2"));
							osw.append(", ");
							osw.append(rs.getString("revenue"));
							osw.append(", ");
							osw.append(rs.getString("cost1"));
							osw.append(", ");
							osw.append(rs.getString("cost2"));
							osw.append(", ");
							osw.append(rs.getString("sales1"));
							osw.append(", ");
							osw.append(rs.getString("sales2"));
							osw.append(lineSeparator);
						} else {
							dataset.setValue(rs.getDouble("value"), k1, String.valueOf(tLong));
						}
					}
					rs.close();
					if(null!=dataset) {
						createHtml(osw, dataset, title, subtitle, ytitle, 300000L, pointStart, pointEnd, orderby, topn, basis);
					}
				} else {
					osw.append("Invalid condition!");
					osw.append(lineSeparator);
				}
			} else if(viewType.equals("Hourly")) {
				Timestamp time = Metadata.getMaxTime(1, logger);
				long mt = time.getTime();
				if(groupby.equals("Time-Period")) {
					String title = null;
					String subtitle = null;
					String ytitle = null;
					Timestamp oneDayBefore = new Timestamp(mt-oneDay);
					Timestamp twoDaysBefore = new Timestamp(mt-2L*oneDay);
					String query = null; 
					if(null==metric) {
						query = "select time, sum(requests) requests, sum(impressions) impressions, sum(clicks) clicks, sum(conversions1) conversions1, sum(conversions2) conversions2, sum(revenue) revenue, sum(cost1) cost1, sum(cost2) cost2, sum(sales1) sales1, sum(sales2) sales2 from v_hourly";
					} else {
						String arr[] = getMetricQuery(metric, groupby, filter, likeFilter);
						title = arr[0];
						subtitle = arr[1];
						ytitle = arr[2];
						query = "select time, " + arr[3] + " from v_hourly";
					}
					if(filter.length()>0) {
						query += " where " + filter.toString().trim().replace("\t","and");
					}
					if(likeFilter.length()>0) {
						if(query.indexOf(" where ")>0) {
							query += " and " + likeFilter.toString().trim().replace("\t","and");
						} else {
							query += " where " + likeFilter.toString().trim().replace("\t","and");
						}
					}
					query += " group by time";
					logger.info("Query : " + query);
					long stime = System.currentTimeMillis();
					ResultSet rs = stmt.executeQuery(query);
					logger.info("Elapsed time for executing query : " + (System.currentTimeMillis()-stime)/1000.0 + "s");
					long pointStart = 0L;
					long pointEnd = 0L;
					while(rs.next()) {
						Timestamp t = rs.getTimestamp("time");
						long tLong = t.getTime();
						String k1 = null;
						Timestamp k2 = null;
						if(t.equals(oneDayBefore)||t.after(oneDayBefore)) {
							k1 = "latest 24 hours";
							k2 = t;
						} else if(t.equals(twoDaysBefore)||t.after(twoDaysBefore)) {
							k1 = "yesterday";
							k2 = new Timestamp(tLong+oneDay);
						} else {
							k1 = "last week";
							k2 = new Timestamp(tLong+7L*oneDay);
						}
						tLong = k2.getTime();
						if(pointStart==0L) {
							pointStart = tLong;
						}
						if(pointEnd<tLong) {
							pointEnd = tLong;
						}
						if(null==metric) {
							String rowKey = k1+", "+k2.toString();
							osw.append(rowKey);
							osw.append(", ");
							osw.append(rs.getString("requests"));
							osw.append(", ");
							osw.append(rs.getString("impressions"));
							osw.append(", ");
							osw.append(rs.getString("clicks"));
							osw.append(", ");
							osw.append(rs.getString("conversions1"));
							osw.append(", ");
							osw.append(rs.getString("conversions2"));
							osw.append(", ");
							osw.append(rs.getString("revenue"));
							osw.append(", ");
							osw.append(rs.getString("cost1"));
							osw.append(", ");
							osw.append(rs.getString("cost2"));
							osw.append(", ");
							osw.append(rs.getString("sales1"));
							osw.append(", ");
							osw.append(rs.getString("sales2"));
							osw.append(lineSeparator);
						} else {
							dataset.setValue(rs.getDouble("value"), k1, String.valueOf(tLong));
						}
					}
					rs.close();
					if(null!=dataset) {
						createHtml(osw, dataset, title, subtitle, ytitle, 3600000L, pointStart, pointEnd, orderby, topn, basis);
					}
				} else if(groupby.equals("Cost-Type")) {
					String title = null;
					String subtitle = null;
					String ytitle = null;
					Timestamp oneDayBefore = new Timestamp(mt-oneDay);
					Timestamp twoDaysBefore = new Timestamp(mt-2L*oneDay);
					String query = null; 
					if(null==metric) {
						query = "select time, cost_type, sum(requests) requests, sum(impressions) impressions, sum(clicks) clicks, sum(conversions1) conversions1, sum(conversions2) conversions2, sum(revenue) revenue, sum(cost1) cost1, sum(cost2) cost2, sum(sales1) sales1, sum(sales2) sales2 from v_hourly";
					} else {
						String arr[] = getMetricQuery(metric, groupby, filter, likeFilter);
						title = arr[0];
						subtitle = arr[1];
						ytitle = arr[2];
						query = "select time, cost_type, " + arr[3] + " from v_hourly";
					}
					if(filter.length()>0) {
						query += " where " + filter.toString().trim().replace("\t","and");
					}
					if(likeFilter.length()>0) {
						if(query.indexOf(" where ")>0) {
							query += " and " + likeFilter.toString().trim().replace("\t","and");
						} else {
							query += " where " + likeFilter.toString().trim().replace("\t","and");
						}
					}
					query += " group by time, cost_type";
					logger.info("Query : " + query);
					long stime = System.currentTimeMillis();
					ResultSet rs = stmt.executeQuery(query);
					logger.info("Elapsed time for executing query : " + (System.currentTimeMillis()-stime)/1000.0 + "s");
					long pointStart = 0L;
					long pointEnd = 0L;
					while(rs.next()) {
						Timestamp t = rs.getTimestamp("time");
						long tLong = t.getTime();
						String c1 = Metadata.costTypes.get(rs.getString("cost_type"));
						if(null==c1) {
							continue;
						}
						c1 += " (" + rs.getString("cost_type") + ")";
						String k1 = c1 + ".";
						Timestamp k2 = null;
						if(t.equals(oneDayBefore)||t.after(oneDayBefore)) {
							k1 += "latest 24 hours";
							k2 = t;
						} else if(t.equals(twoDaysBefore)||t.after(twoDaysBefore)) {
							k1 += "yesterday";
							k2 = new Timestamp(tLong+oneDay);
						} else {
							k1 += "last week";
							k2 = new Timestamp(tLong+7L*oneDay);
						}
						tLong = k2.getTime();
						if(pointStart==0L) {
							pointStart = tLong;
						}
						if(pointEnd<tLong) {
							pointEnd = tLong;
						}
						if(null==metric) {
							String rowKey = k1+", "+k2.toString();
							osw.append(rowKey);
							osw.append(", ");
							osw.append(rs.getString("requests"));
							osw.append(", ");
							osw.append(rs.getString("impressions"));
							osw.append(", ");
							osw.append(rs.getString("clicks"));
							osw.append(", ");
							osw.append(rs.getString("conversions1"));
							osw.append(", ");
							osw.append(rs.getString("conversions2"));
							osw.append(", ");
							osw.append(rs.getString("revenue"));
							osw.append(", ");
							osw.append(rs.getString("cost1"));
							osw.append(", ");
							osw.append(rs.getString("cost2"));
							osw.append(", ");
							osw.append(rs.getString("sales1"));
							osw.append(", ");
							osw.append(rs.getString("sales2"));
							osw.append(lineSeparator);
						} else {
							dataset.setValue(rs.getDouble("value"), k1, String.valueOf(tLong));
						}
					}
					rs.close();
					if(null!=dataset) {
						createHtml(osw, dataset, title, subtitle, ytitle, 3600000L, pointStart, pointEnd, orderby, topn, basis);
					}
				} else if(groupby.equals("Is-Mobile")) {
					String title = null;
					String subtitle = null;
					String ytitle = null;
					Timestamp oneDayBefore = new Timestamp(mt-oneDay);
					Timestamp twoDaysBefore = new Timestamp(mt-2L*oneDay);
					String query = null; 
					if(null==metric) {
						query = "select time, is_mobile, sum(requests) requests, sum(impressions) impressions, sum(clicks) clicks, sum(conversions1) conversions1, sum(conversions2) conversions2, sum(revenue) revenue, sum(cost1) cost1, sum(cost2) cost2, sum(sales1) sales1, sum(sales2) sales2 from v_hourly";
					} else {
						String arr[] = getMetricQuery(metric, groupby, filter, likeFilter);
						title = arr[0];
						subtitle = arr[1];
						ytitle = arr[2];
						query = "select time, is_mobile, " + arr[3] + " from v_hourly";
					}
					if(filter.length()>0) {
						query += " where " + filter.toString().trim().replace("\t","and");
					}
					if(likeFilter.length()>0) {
						if(query.indexOf(" where ")>0) {
							query += " and " + likeFilter.toString().trim().replace("\t","and");
						} else {
							query += " where " + likeFilter.toString().trim().replace("\t","and");
						}
					}
					query += " group by time, is_mobile";
					logger.info("Query : " + query);
					long stime = System.currentTimeMillis();
					ResultSet rs = stmt.executeQuery(query);
					logger.info("Elapsed time for executing query : " + (System.currentTimeMillis()-stime)/1000.0 + "s");
					long pointStart = 0L;
					long pointEnd = 0L;
					while(rs.next()) {
						Timestamp t = rs.getTimestamp("time");
						long tLong = t.getTime();
						String c1 = Metadata.isMobiles.get(rs.getString("is_mobile"));
						if(null==c1) {
							continue;
						}
						c1 += " (" + rs.getString("is_mobile") + ")";
						String k1 = c1 + ".";
						Timestamp k2 = null;
						if(t.equals(oneDayBefore)||t.after(oneDayBefore)) {
							k1 += "latest 24 hours";
							k2 = t;
						} else if(t.equals(twoDaysBefore)||t.after(twoDaysBefore)) {
							k1 += "yesterday";
							k2 = new Timestamp(tLong+oneDay);
						} else {
							k1 += "last week";
							k2 = new Timestamp(tLong+7L*oneDay);
						}
						tLong = k2.getTime();
						if(pointStart==0L) {
							pointStart = tLong;
						}
						if(pointEnd<tLong) {
							pointEnd = tLong;
						}
						if(null==metric) {
							String rowKey = k1+", "+k2.toString();
							osw.append(rowKey);
							osw.append(", ");
							osw.append(rs.getString("requests"));
							osw.append(", ");
							osw.append(rs.getString("impressions"));
							osw.append(", ");
							osw.append(rs.getString("clicks"));
							osw.append(", ");
							osw.append(rs.getString("conversions1"));
							osw.append(", ");
							osw.append(rs.getString("conversions2"));
							osw.append(", ");
							osw.append(rs.getString("revenue"));
							osw.append(", ");
							osw.append(rs.getString("cost1"));
							osw.append(", ");
							osw.append(rs.getString("cost2"));
							osw.append(", ");
							osw.append(rs.getString("sales1"));
							osw.append(", ");
							osw.append(rs.getString("sales2"));
							osw.append(lineSeparator);
						} else {
							dataset.setValue(rs.getDouble("value"), k1, String.valueOf(tLong));
						}
					}
					rs.close();
					if(null!=dataset) {
						createHtml(osw, dataset, title, subtitle, ytitle, 3600000L, pointStart, pointEnd, orderby, topn, basis);
					}
				} else if(groupby.equals("Campaign-Type")) {
					String title = null;
					String subtitle = null;
					String ytitle = null;
					Timestamp oneDayBefore = new Timestamp(mt-oneDay);
					Timestamp twoDaysBefore = new Timestamp(mt-2L*oneDay);
					String query = null; 
					if(null==metric) {
						query = "select time, ctype, sum(requests) requests, sum(impressions) impressions, sum(clicks) clicks, sum(conversions1) conversions1, sum(conversions2) conversions2, sum(revenue) revenue, sum(cost1) cost1, sum(cost2) cost2, sum(sales1) sales1, sum(sales2) sales2 from v_hourly";
					} else {
						String arr[] = getMetricQuery(metric, groupby, filter, likeFilter);
						title = arr[0];
						subtitle = arr[1];
						ytitle = arr[2];
						query = "select time, ctype, " + arr[3] + " from v_hourly";
					}
					if(filter.length()>0) {
						query += " where " + filter.toString().trim().replace("\t","and");
					}
					if(likeFilter.length()>0) {
						if(query.indexOf(" where ")>0) {
							query += " and " + likeFilter.toString().trim().replace("\t","and");
						} else {
							query += " where " + likeFilter.toString().trim().replace("\t","and");
						}
					}
					query += " group by time, ctype";
					logger.info("Query : " + query);
					long stime = System.currentTimeMillis();
					ResultSet rs = stmt.executeQuery(query);
					logger.info("Elapsed time for executing query : " + (System.currentTimeMillis()-stime)/1000.0 + "s");
					long pointStart = 0L;
					long pointEnd = 0L;
					while(rs.next()) {
						Timestamp t = rs.getTimestamp("time");
						long tLong = t.getTime();
						String c1 = Metadata.ctypes.get(rs.getString("ctype"));
						if(null==c1) {
							continue;
						}
						c1 += " (" + rs.getString("ctype") + ")";
						String k1 = c1 + ".";
						Timestamp k2 = null;
						if(t.equals(oneDayBefore)||t.after(oneDayBefore)) {
							k1 += "latest 24 hours";
							k2 = t;
						} else if(t.equals(twoDaysBefore)||t.after(twoDaysBefore)) {
							k1 += "yesterday";
							k2 = new Timestamp(tLong+oneDay);
						} else {
							k1 += "last week";
							k2 = new Timestamp(tLong+7L*oneDay);
						}
						tLong = k2.getTime();
						if(pointStart==0L) {
							pointStart = tLong;
						}
						if(pointEnd<tLong) {
							pointEnd = tLong;
						}
						if(null==metric) {
							String rowKey = k1+", "+k2.toString();
							osw.append(rowKey);
							osw.append(", ");
							osw.append(rs.getString("requests"));
							osw.append(", ");
							osw.append(rs.getString("impressions"));
							osw.append(", ");
							osw.append(rs.getString("clicks"));
							osw.append(", ");
							osw.append(rs.getString("conversions1"));
							osw.append(", ");
							osw.append(rs.getString("conversions2"));
							osw.append(", ");
							osw.append(rs.getString("revenue"));
							osw.append(", ");
							osw.append(rs.getString("cost1"));
							osw.append(", ");
							osw.append(rs.getString("cost2"));
							osw.append(", ");
							osw.append(rs.getString("sales1"));
							osw.append(", ");
							osw.append(rs.getString("sales2"));
							osw.append(lineSeparator);
						} else {
							dataset.setValue(rs.getDouble("value"), k1, String.valueOf(tLong));
						}
					}
					rs.close();
					if(null!=dataset) {
						createHtml(osw, dataset, title, subtitle, ytitle, 3600000L, pointStart, pointEnd, orderby, topn, basis);
					}
				} else if(groupby.equals("Zone")) {
					String title = null;
					String subtitle = null;
					String ytitle = null;
					Timestamp oneDayBefore = new Timestamp(mt-oneDay);
					Timestamp twoDaysBefore = new Timestamp(mt-2L*oneDay);
					String query = null; 
					if(null==metric) {
						query = "select time, zoneid, sum(requests) requests, sum(impressions) impressions, sum(clicks) clicks, sum(conversions1) conversions1, sum(conversions2) conversions2, sum(revenue) revenue, sum(cost1) cost1, sum(cost2) cost2, sum(sales1) sales1, sum(sales2) sales2 from v_hourly";
					} else {
						String arr[] = getMetricQuery(metric, groupby, filter, likeFilter);
						title = arr[0];
						subtitle = arr[1];
						ytitle = arr[2];
						query = "select time, zoneid, " + arr[3] + " from v_hourly";
					}
					if(filter.length()>0) {
						query += " where " + filter.toString().trim().replace("\t","and");
					}
					if(likeFilter.length()>0) {
						if(query.indexOf(" where ")>0) {
							query += " and " + likeFilter.toString().trim().replace("\t","and");
						} else {
							query += " where " + likeFilter.toString().trim().replace("\t","and");
						}
					}
					query += " group by time, zoneid";
					logger.info("Query : " + query);
					long stime = System.currentTimeMillis();
					ResultSet rs = stmt.executeQuery(query);
					logger.info("Elapsed time for executing query : " + (System.currentTimeMillis()-stime)/1000.0 + "s");
					long pointStart = 0L;
					long pointEnd = 0L;
					while(rs.next()) {
						Timestamp t = rs.getTimestamp("time");
						long tLong = t.getTime();
						String c1 = Metadata.zones.get(rs.getString("zoneid"));
						if(null==c1) {
							continue;
						}
						c1 += " (" + rs.getString("zoneid") + ")";
						String k1 = c1 + ".";
						Timestamp k2 = null;
						if(t.equals(oneDayBefore)||t.after(oneDayBefore)) {
							k1 += "latest 24 hours";
							k2 = t;
						} else if(t.equals(twoDaysBefore)||t.after(twoDaysBefore)) {
							k1 += "yesterday";
							k2 = new Timestamp(tLong+oneDay);
						} else {
							k1 += "last week";
							k2 = new Timestamp(tLong+7L*oneDay);
						}
						tLong = k2.getTime();
						if(pointStart==0L) {
							pointStart = tLong;
						}
						if(pointEnd<tLong) {
							pointEnd = tLong;
						}
						if(null==metric) {
							String rowKey = k1+", "+k2.toString();
							osw.append(rowKey);
							osw.append(", ");
							osw.append(rs.getString("requests"));
							osw.append(", ");
							osw.append(rs.getString("impressions"));
							osw.append(", ");
							osw.append(rs.getString("clicks"));
							osw.append(", ");
							osw.append(rs.getString("conversions1"));
							osw.append(", ");
							osw.append(rs.getString("conversions2"));
							osw.append(", ");
							osw.append(rs.getString("revenue"));
							osw.append(", ");
							osw.append(rs.getString("cost1"));
							osw.append(", ");
							osw.append(rs.getString("cost2"));
							osw.append(", ");
							osw.append(rs.getString("sales1"));
							osw.append(", ");
							osw.append(rs.getString("sales2"));
							osw.append(lineSeparator);
						} else {
							dataset.setValue(rs.getDouble("value"), k1, String.valueOf(tLong));
						}
					}
					rs.close();
					if(null!=dataset) {
						createHtml(osw, dataset, title, subtitle, ytitle, 3600000L, pointStart, pointEnd, orderby, topn, basis);
					}
				} else if(groupby.equals("Client")) {
					String title = null;
					String subtitle = null;
					String ytitle = null;
					Timestamp oneDayBefore = new Timestamp(mt-oneDay);
					Timestamp twoDaysBefore = new Timestamp(mt-2L*oneDay);
					String query = null; 
					if(null==metric) {
						query = "select time, clientid, sum(requests) requests, sum(impressions) impressions, sum(clicks) clicks, sum(conversions1) conversions1, sum(conversions2) conversions2, sum(revenue) revenue, sum(cost1) cost1, sum(cost2) cost2, sum(sales1) sales1, sum(sales2) sales2 from v_hourly";
					} else {
						String arr[] = getMetricQuery(metric, groupby, filter, likeFilter);
						title = arr[0];
						subtitle = arr[1];
						ytitle = arr[2];
						query = "select time, clientid, " + arr[3] + " from v_hourly";
					}
					if(filter.length()>0) {
						query += " where " + filter.toString().trim().replace("\t","and");
					}
					if(likeFilter.length()>0) {
						if(query.indexOf(" where ")>0) {
							query += " and " + likeFilter.toString().trim().replace("\t","and");
						} else {
							query += " where " + likeFilter.toString().trim().replace("\t","and");
						}
					}
					query += " group by time, clientid";
					logger.info("Query : " + query);
					long stime = System.currentTimeMillis();
					ResultSet rs = stmt.executeQuery(query);
					logger.info("Elapsed time for executing query : " + (System.currentTimeMillis()-stime)/1000.0 + "s");
					long pointStart = 0L;
					long pointEnd = 0L;
					while(rs.next()) {
						Timestamp t = rs.getTimestamp("time");
						long tLong = t.getTime();
						String c1 = Metadata.clients.get(rs.getString("clientid"));
						if(null==c1) {
							continue;
						}
						c1 += " (" + rs.getString("clientid") + ")";
						String k1 = c1 + ".";
						Timestamp k2 = null;
						if(t.equals(oneDayBefore)||t.after(oneDayBefore)) {
							k1 += "latest 24 hours";
							k2 = t;
						} else if(t.equals(twoDaysBefore)||t.after(twoDaysBefore)) {
							k1 += "yesterday";
							k2 = new Timestamp(tLong+oneDay);
						} else {
							k1 += "last week";
							k2 = new Timestamp(tLong+7L*oneDay);
						}
						tLong = k2.getTime();
						if(pointStart==0L) {
							pointStart = tLong;
						}
						if(pointEnd<tLong) {
							pointEnd = tLong;
						}
						if(null==metric) {
							String rowKey = k1+", "+k2.toString();
							osw.append(rowKey);
							osw.append(", ");
							osw.append(rs.getString("requests"));
							osw.append(", ");
							osw.append(rs.getString("impressions"));
							osw.append(", ");
							osw.append(rs.getString("clicks"));
							osw.append(", ");
							osw.append(rs.getString("conversions1"));
							osw.append(", ");
							osw.append(rs.getString("conversions2"));
							osw.append(", ");
							osw.append(rs.getString("revenue"));
							osw.append(", ");
							osw.append(rs.getString("cost1"));
							osw.append(", ");
							osw.append(rs.getString("cost2"));
							osw.append(", ");
							osw.append(rs.getString("sales1"));
							osw.append(", ");
							osw.append(rs.getString("sales2"));
							osw.append(lineSeparator);
						} else {
							dataset.setValue(rs.getDouble("value"), k1, String.valueOf(tLong));
						}
					}
					rs.close();
					if(null!=dataset) {
						createHtml(osw, dataset, title, subtitle, ytitle, 3600000L, pointStart, pointEnd, orderby, topn, basis);
					}
				} else if(groupby.equals("Client-Campaign")) {
					String title = null;
					String subtitle = null;
					String ytitle = null;
					Timestamp oneDayBefore = new Timestamp(mt-oneDay);
					Timestamp twoDaysBefore = new Timestamp(mt-2L*oneDay);
					String query = null; 
					if(null==metric) {
						query = "select time, clientid, campaignid sum(requests) requests, sum(impressions) impressions, sum(clicks) clicks, sum(conversions1) conversions1, sum(conversions2) conversions2, sum(revenue) revenue, sum(cost1) cost1, sum(cost2) cost2, sum(sales1) sales1, sum(sales2) sales2 from v_hourly";
					} else {
						String arr[] = getMetricQuery(metric, groupby, filter, likeFilter);
						title = arr[0];
						subtitle = arr[1];
						ytitle = arr[2];
						query = "select time, clientid, campaignid, " + arr[3] + " from v_hourly";
					}
					if(filter.length()>0) {
						query += " where " + filter.toString().trim().replace("\t","and");
					}
					if(likeFilter.length()>0) {
						if(query.indexOf(" where ")>0) {
							query += " and " + likeFilter.toString().trim().replace("\t","and");
						} else {
							query += " where " + likeFilter.toString().trim().replace("\t","and");
						}
					}
					query += " group by time, clientid, campaignid";
					logger.info("Query : " + query);
					long stime = System.currentTimeMillis();
					ResultSet rs = stmt.executeQuery(query);
					logger.info("Elapsed time for executing query : " + (System.currentTimeMillis()-stime)/1000.0 + "s");
					long pointStart = 0L;
					long pointEnd = 0L;
					while(rs.next()) {
						Timestamp t = rs.getTimestamp("time");
						long tLong = t.getTime();
						String c1 = Metadata.clients.get(rs.getString("clientid"));
						if(null==c1) {
							continue;
						}
						String c2 = Metadata.campaigns.get(rs.getString("campaignid"));
						if(null==c2) {
							continue;
						}
						c1 += " (" + rs.getString("clientid") + ")";
						c2 += " (" + rs.getString("campaignid") + ")";
						String k1 = c1 + " - " + c2 + ".";
						Timestamp k2 = null;
						if(t.equals(oneDayBefore)||t.after(oneDayBefore)) {
							k1 += "latest 24 hours";
							k2 = t;
						} else if(t.equals(twoDaysBefore)||t.after(twoDaysBefore)) {
							k1 += "yesterday";
							k2 = new Timestamp(tLong+oneDay);
						} else {
							k1 += "last week";
							k2 = new Timestamp(tLong+7L*oneDay);
						}
						tLong = k2.getTime();
						if(pointStart==0L) {
							pointStart = tLong;
						}
						if(pointEnd<tLong) {
							pointEnd = tLong;
						}
						if(null==metric) {
							String rowKey = k1+", "+k2.toString();
							osw.append(rowKey);
							osw.append(", ");
							osw.append(rs.getString("requests"));
							osw.append(", ");
							osw.append(rs.getString("impressions"));
							osw.append(", ");
							osw.append(rs.getString("clicks"));
							osw.append(", ");
							osw.append(rs.getString("conversions1"));
							osw.append(", ");
							osw.append(rs.getString("conversions2"));
							osw.append(", ");
							osw.append(rs.getString("revenue"));
							osw.append(", ");
							osw.append(rs.getString("cost1"));
							osw.append(", ");
							osw.append(rs.getString("cost2"));
							osw.append(", ");
							osw.append(rs.getString("sales1"));
							osw.append(", ");
							osw.append(rs.getString("sales2"));
							osw.append(lineSeparator);
						} else {
							dataset.setValue(rs.getDouble("value"), k1, String.valueOf(tLong));
						}
					}
					rs.close();
					if(null!=dataset) {
						createHtml(osw, dataset, title, subtitle, ytitle, 3600000L, pointStart, pointEnd, orderby, topn, basis);
					}
				} else if(groupby.equals("Client-Category")) {
					String title = null;
					String subtitle = null;
					String ytitle = null;
					Timestamp oneDayBefore = new Timestamp(mt-oneDay);
					Timestamp twoDaysBefore = new Timestamp(mt-2L*oneDay);
					String query = null; 
					if(null==metric) {
						query = "select time, category, sum(requests) requests, sum(impressions) impressions, sum(clicks) clicks, sum(conversions1) conversions1, sum(conversions2) conversions2, sum(revenue) revenue, sum(cost1) cost1, sum(cost2) cost2, sum(sales1) sales1, sum(sales2) sales2 from v_hourly a join clients b on (a.clientid=b.client)";
					} else {
						String arr[] = getMetricQuery(metric, groupby, filter, likeFilter);
						title = arr[0];
						subtitle = arr[1];
						ytitle = arr[2];
						query = "select time, category, " + arr[3] + " from v_hourly a join clients b on (a.clientid=b.client)";
					}
					if(filter.length()>0) {
						query += " where " + filter.toString().trim().replace("\t","and");
					}
					if(likeFilter.length()>0) {
						if(query.indexOf(" where ")>0) {
							query += " and " + likeFilter.toString().trim().replace("\t","and");
						} else {
							query += " where " + likeFilter.toString().trim().replace("\t","and");
						}
					}
					query += " group by time, category";
					logger.info("Query : " + query);
					long stime = System.currentTimeMillis();
					ResultSet rs = stmt.executeQuery(query);
					logger.info("Elapsed time for executing query : " + (System.currentTimeMillis()-stime)/1000.0 + "s");
					long pointStart = 0L;
					long pointEnd = 0L;
					while(rs.next()) {
						Timestamp t = rs.getTimestamp("time");
						long tLong = t.getTime();
						String k1 = rs.getString("category");
						if(k1==null) {
							k1 = "None";
						}
						k1 = k1 + ".";
						Timestamp k2 = null;
						if(t.equals(oneDayBefore)||t.after(oneDayBefore)) {
							k1 += "latest 24 hours";
							k2 = t;
						} else if(t.equals(twoDaysBefore)||t.after(twoDaysBefore)) {
							k1 += "yesterday";
							k2 = new Timestamp(tLong+oneDay);
						} else {
							k1 += "last week";
							k2 = new Timestamp(tLong+7L*oneDay);
						}
						tLong = k2.getTime();
						if(pointStart==0L) {
							pointStart = tLong;
						}
						if(pointEnd<tLong) {
							pointEnd = tLong;
						}
						if(null==metric) {
							String rowKey = k1+", "+k2.toString();
							osw.append(rowKey);
							osw.append(", ");
							osw.append(rs.getString("requests"));
							osw.append(", ");
							osw.append(rs.getString("impressions"));
							osw.append(", ");
							osw.append(rs.getString("clicks"));
							osw.append(", ");
							osw.append(rs.getString("conversions1"));
							osw.append(", ");
							osw.append(rs.getString("conversions2"));
							osw.append(", ");
							osw.append(rs.getString("revenue"));
							osw.append(", ");
							osw.append(rs.getString("cost1"));
							osw.append(", ");
							osw.append(rs.getString("cost2"));
							osw.append(", ");
							osw.append(rs.getString("sales1"));
							osw.append(", ");
							osw.append(rs.getString("sales2"));
							osw.append(lineSeparator);
						} else {
							dataset.setValue(rs.getDouble("value"), k1, String.valueOf(tLong));
						}
					}
					rs.close();
					if(null!=dataset) {
						createHtml(osw, dataset, title, subtitle, ytitle, 3600000L, pointStart, pointEnd, orderby, topn, basis);
					}
				} else {
					osw.append("Invalid condition!");
					osw.append(lineSeparator);
				}
			} else if(viewType.equals("Daily")) {
				Timestamp time = Metadata.getMaxTime(2, logger);
				long mt = time.getTime();
				if(groupby.equals("Time-Period")) {
					String title = null;
					String subtitle = null;
					String ytitle = null;
					Timestamp sevenDaysBefore = new Timestamp(mt-7L*oneDay);
					Timestamp forteenDaysBefore = new Timestamp(mt-14L*oneDay);
					String query = null;
					if(null==metric) {
						query = "select time, sum(requests) requests, sum(impressions) impressions, sum(clicks) clicks, sum(conversions1) conversions1, sum(conversions2) conversions2, sum(revenue) revenue, sum(cost1) cost1, sum(cost2) cost2, sum(sales1) sales1, sum(sales2) sales2 from v_daily";
					} else {
						String arr[] = getMetricQuery(metric, groupby, filter, likeFilter);
						title = arr[0];
						subtitle = arr[1];
						ytitle = arr[2];
						query = "select time, " + arr[3] + " from v_daily";
					}
					if(filter.length()>0) {
						query += " where " + filter.toString().trim().replace("\t","and");
					}
					if(likeFilter.length()>0) {
						if(query.indexOf(" where ")>0) {
							query += " and " + likeFilter.toString().trim().replace("\t","and");
						} else {
							query += " where " + likeFilter.toString().trim().replace("\t","and");
						}
					}
					query += " group by time";
					logger.info("Query : " + query);
					long stime = System.currentTimeMillis();
					ResultSet rs = stmt.executeQuery(query);
					logger.info("Elapsed time for executing query : " + (System.currentTimeMillis()-stime)/1000.0 + "s");
					long pointStart = 0L;
					long pointEnd = 0L;
					while(rs.next()) {
						Timestamp t = rs.getTimestamp("time");
						long tLong = t.getTime();
						String k1 = null;
						Timestamp k2 = null;
						if(t.after(sevenDaysBefore)) {
							k1 = "latest one week";
							k2 = t;
						} else if(t.after(forteenDaysBefore)) {
							k1 = "one week before";
							k2 = new Timestamp(tLong+7L*oneDay);
						} else {
							k1 = "two weeks before";
							k2 = new Timestamp(tLong+14L*oneDay);
						}
						tLong = k2.getTime();
						if(pointStart==0L) {
							pointStart = tLong;
						}
						if(pointEnd<tLong) {
							pointEnd = tLong;
						}
						if(null==metric) {
							String rowKey = k1+", "+k2;
							osw.append(rowKey);
							osw.append(", ");
							osw.append(rs.getString("requests"));
							osw.append(", ");
							osw.append(rs.getString("impressions"));
							osw.append(", ");
							osw.append(rs.getString("clicks"));
							osw.append(", ");
							osw.append(rs.getString("conversions1"));
							osw.append(", ");
							osw.append(rs.getString("conversions2"));
							osw.append(", ");
							osw.append(rs.getString("revenue"));
							osw.append(", ");
							osw.append(rs.getString("cost1"));
							osw.append(", ");
							osw.append(rs.getString("cost2"));
							osw.append(", ");
							osw.append(rs.getString("sales1"));
							osw.append(", ");
							osw.append(rs.getString("sales2"));
							osw.append(lineSeparator);
						} else {
							dataset.setValue(rs.getDouble("value"), k1, String.valueOf(tLong));
						}
					}
					rs.close();
					if(null!=dataset) {
						createHtml(osw, dataset, title, subtitle, ytitle, oneDay, pointStart, pointEnd, orderby, topn, basis);
					}
				} else if(groupby.equals("Cost-Type")) {
					String title = null;
					String subtitle = null;
					String ytitle = null;
					String query = null;
					if(null==metric) {
						query = "select time, cost_type, sum(requests) requests, sum(impressions) impressions, sum(clicks) clicks, sum(conversions1) conversions1, sum(conversions2) conversions2, sum(revenue) revenue, sum(cost1) cost1, sum(cost2) cost2, sum(sales1) sales1, sum(sales2) sales2 from v_daily";
					} else {
						String arr[] = getMetricQuery(metric, groupby, filter, likeFilter);
						title = arr[0];
						subtitle = arr[1];
						ytitle = arr[2];
						query = "select time, cost_type, " + arr[3] + " from v_daily";
					}
					if(filter.length()>0) {
						query += " where " + filter.toString().trim().replace("\t","and");
					}
					if(likeFilter.length()>0) {
						if(query.indexOf(" where ")>0) {
							query += " and " + likeFilter.toString().trim().replace("\t","and");
						} else {
							query += " where " + likeFilter.toString().trim().replace("\t","and");
						}
					}
					query += " group by time, cost_type";
					logger.info("Query : " + query);
					long stime = System.currentTimeMillis();
					ResultSet rs = stmt.executeQuery(query);
					logger.info("Elapsed time for executing query : " + (System.currentTimeMillis()-stime)/1000.0 + "s");
					long pointStart = 0L;
					long pointEnd = 0L;
					while(rs.next()) {
						String k1 = Metadata.costTypes.get(rs.getString("cost_type"));
						if(null==k1) {
							continue;
						}
						k1 += " (" + rs.getString("cost_type") + ")";
						Timestamp t = rs.getTimestamp("time");
						long tLong = t.getTime();
						if(pointStart==0L) {
							pointStart = tLong;
						}
						if(pointEnd<tLong) {
							pointEnd = tLong;
						}
						if(null==metric) {
							String rowKey = k1+", "+t.toString();
							osw.append(rowKey);
							osw.append(", ");
							osw.append(rs.getString("requests"));
							osw.append(", ");
							osw.append(rs.getString("impressions"));
							osw.append(", ");
							osw.append(rs.getString("clicks"));
							osw.append(", ");
							osw.append(rs.getString("conversions1"));
							osw.append(", ");
							osw.append(rs.getString("conversions2"));
							osw.append(", ");
							osw.append(rs.getString("revenue"));
							osw.append(", ");
							osw.append(rs.getString("cost1"));
							osw.append(", ");
							osw.append(rs.getString("cost2"));
							osw.append(", ");
							osw.append(rs.getString("sales1"));
							osw.append(", ");
							osw.append(rs.getString("sales2"));
							osw.append(lineSeparator);
						} else {
							dataset.setValue(rs.getDouble("value"), k1, String.valueOf(tLong));
						}
					}
					rs.close();
					if(null!=dataset) {
						createHtml(osw, dataset, title, subtitle, ytitle, oneDay, pointStart, pointEnd, orderby, topn, basis);
					}
				} else if(groupby.equals("Is-Mobile")) {
					String title = null;
					String subtitle = null;
					String ytitle = null;
					String query = null;
					if(null==metric) {
						query = "select time, is_mobile, sum(requests) requests, sum(impressions) impressions, sum(clicks) clicks, sum(conversions1) conversions1, sum(conversions2) conversions2, sum(revenue) revenue, sum(cost1) cost1, sum(cost2) cost2, sum(sales1) sales1, sum(sales2) sales2 from v_daily";
					} else {
						String arr[] = getMetricQuery(metric, groupby, filter, likeFilter);
						title = arr[0];
						subtitle = arr[1];
						ytitle = arr[2];
						query = "select time, is_mobile, " + arr[3] + " from v_daily";
					}
					if(filter.length()>0) {
						query += " where " + filter.toString().trim().replace("\t","and");
					}
					if(likeFilter.length()>0) {
						if(query.indexOf(" where ")>0) {
							query += " and " + likeFilter.toString().trim().replace("\t","and");
						} else {
							query += " where " + likeFilter.toString().trim().replace("\t","and");
						}
					}
					query += " group by time, is_mobile";
					logger.info("Query : " + query);
					long stime = System.currentTimeMillis();
					ResultSet rs = stmt.executeQuery(query);
					logger.info("Elapsed time for executing query : " + (System.currentTimeMillis()-stime)/1000.0 + "s");
					long pointStart = 0L;
					long pointEnd = 0L;
					while(rs.next()) {
						String k1 = Metadata.isMobiles.get(rs.getString("is_mobile"));
						if(null==k1) {
							continue;
						}
						k1 += " (" + rs.getString("is_mobile") + ")";
						Timestamp t = rs.getTimestamp("time");
						long tLong = t.getTime();
						if(pointStart==0L) {
							pointStart = tLong;
						}
						if(pointEnd<tLong) {
							pointEnd = tLong;
						}
						if(null==metric) {
							String rowKey = k1+", "+t.toString();
							osw.append(rowKey);
							osw.append(", ");
							osw.append(rs.getString("requests"));
							osw.append(", ");
							osw.append(rs.getString("impressions"));
							osw.append(", ");
							osw.append(rs.getString("clicks"));
							osw.append(", ");
							osw.append(rs.getString("conversions1"));
							osw.append(", ");
							osw.append(rs.getString("conversions2"));
							osw.append(", ");
							osw.append(rs.getString("revenue"));
							osw.append(", ");
							osw.append(rs.getString("cost1"));
							osw.append(", ");
							osw.append(rs.getString("cost2"));
							osw.append(", ");
							osw.append(rs.getString("sales1"));
							osw.append(", ");
							osw.append(rs.getString("sales2"));
							osw.append(lineSeparator);
						} else {
							dataset.setValue(rs.getDouble("value"), k1, String.valueOf(tLong));
						}
					}
					rs.close();
					if(null!=dataset) {
						createHtml(osw, dataset, title, subtitle, ytitle, oneDay, pointStart, pointEnd, orderby, topn, basis);
					}
				} else if(groupby.equals("Campaign-Type")) {
					String title = null;
					String subtitle = null;
					String ytitle = null;
					String query = null;
					if(null==metric) {
						query = "select time, ctype, sum(requests) requests, sum(impressions) impressions, sum(clicks) clicks, sum(conversions1) conversions1, sum(conversions2) conversions2, sum(revenue) revenue, sum(cost1) cost1, sum(cost2) cost2, sum(sales1) sales1, sum(sales2) sales2 from v_daily";
					} else {
						String arr[] = getMetricQuery(metric, groupby, filter, likeFilter);
						title = arr[0];
						subtitle = arr[1];
						ytitle = arr[2];
						query = "select time, ctype, " + arr[3] + " from v_daily";
					}
					if(filter.length()>0) {
						query += " where " + filter.toString().trim().replace("\t","and");
					}
					if(likeFilter.length()>0) {
						if(query.indexOf(" where ")>0) {
							query += " and " + likeFilter.toString().trim().replace("\t","and");
						} else {
							query += " where " + likeFilter.toString().trim().replace("\t","and");
						}
					}
					query += " group by time, ctype";
					logger.info("Query : " + query);
					long stime = System.currentTimeMillis();
					ResultSet rs = stmt.executeQuery(query);
					logger.info("Elapsed time for executing query : " + (System.currentTimeMillis()-stime)/1000.0 + "s");
					long pointStart = 0L;
					long pointEnd = 0L;
					while(rs.next()) {
						String k1 = Metadata.ctypes.get(rs.getString("ctype"));
						if(null==k1) {
							continue;
						}
						k1 += " (" + rs.getString("ctype") + ")";
						Timestamp t = rs.getTimestamp("time");
						long tLong = t.getTime();
						if(pointStart==0L) {
							pointStart = tLong;
						}
						if(pointEnd<tLong) {
							pointEnd = tLong;
						}
						if(null==metric) {
							String rowKey = k1+", "+t.toString();
							osw.append(rowKey);
							osw.append(", ");
							osw.append(rs.getString("requests"));
							osw.append(", ");
							osw.append(rs.getString("impressions"));
							osw.append(", ");
							osw.append(rs.getString("clicks"));
							osw.append(", ");
							osw.append(rs.getString("conversions1"));
							osw.append(", ");
							osw.append(rs.getString("conversions2"));
							osw.append(", ");
							osw.append(rs.getString("revenue"));
							osw.append(", ");
							osw.append(rs.getString("cost1"));
							osw.append(", ");
							osw.append(rs.getString("cost2"));
							osw.append(", ");
							osw.append(rs.getString("sales1"));
							osw.append(", ");
							osw.append(rs.getString("sales2"));
							osw.append(lineSeparator);
						} else {
							dataset.setValue(rs.getDouble("value"), k1, String.valueOf(tLong));
						}
					}
					rs.close();
					if(null!=dataset) {
						createHtml(osw, dataset, title, subtitle, ytitle, oneDay, pointStart, pointEnd, orderby, topn, basis);
					}
				} else if(groupby.equals("Zone")) {
					
					String title = null;
					String subtitle = null;
					String ytitle = null;
					String query = null;
					if(null==metric) {
						query = "select time, zoneid, sum(requests) requests, sum(impressions) impressions, sum(clicks) clicks, sum(conversions1) conversions1, sum(conversions2) conversions2, sum(revenue) revenue, sum(cost1) cost1, sum(cost2) cost2, sum(sales1) sales1, sum(sales2) sales2 from v_daily";
					} else {
						String arr[] = getMetricQuery(metric, groupby, filter, likeFilter);
						title = arr[0];
						subtitle = arr[1];
						ytitle = arr[2];
						query = "select time, zoneid, " + arr[3] + " from v_daily";
					}
					if(filter.length()>0) {
						query += " where " + filter.toString().trim().replace("\t","and");
					}
					if(likeFilter.length()>0) {
						if(query.indexOf(" where ")>0) {
							query += " and " + likeFilter.toString().trim().replace("\t","and");
						} else {
							query += " where " + likeFilter.toString().trim().replace("\t","and");
						}
					}
					query += " group by time, zoneid";
					logger.info("Query : " + query);
					long stime = System.currentTimeMillis();
					ResultSet rs = stmt.executeQuery(query);
					logger.info("Elapsed time for executing query : " + (System.currentTimeMillis()-stime)/1000.0 + "s");
					long pointStart = 0L;
					long pointEnd = 0L;
					while(rs.next()) {
						String k1 = Metadata.zones.get(rs.getString("zoneid"));
						if(null==k1) {
							continue;
						}
						k1 += " (" + rs.getString("zoneid") + ")";
						Timestamp t = rs.getTimestamp("time");
						long tLong = t.getTime();
						if(pointStart==0L) {
							pointStart = tLong;
						}
						if(pointEnd<tLong) {
							pointEnd = tLong;
						}
						if(null==metric) {
							String rowKey = k1+", "+t.toString();
							osw.append(rowKey);
							osw.append(", ");
							osw.append(rs.getString("requests"));
							osw.append(", ");
							osw.append(rs.getString("impressions"));
							osw.append(", ");
							osw.append(rs.getString("clicks"));
							osw.append(", ");
							osw.append(rs.getString("conversions1"));
							osw.append(", ");
							osw.append(rs.getString("conversions2"));
							osw.append(", ");
							osw.append(rs.getString("revenue"));
							osw.append(", ");
							osw.append(rs.getString("cost1"));
							osw.append(", ");
							osw.append(rs.getString("cost2"));
							osw.append(", ");
							osw.append(rs.getString("sales1"));
							osw.append(", ");
							osw.append(rs.getString("sales2"));
							osw.append(lineSeparator);
						} else {
							double d = rs.getDouble("value");
							dataset.setValue(d, k1, String.valueOf(tLong));
						}
					}
					rs.close();
					if(null!=dataset) {
						createHtml(osw, dataset, title, subtitle, ytitle, oneDay, pointStart, pointEnd, orderby, topn, basis);
					}
				} else if(groupby.equals("Client")) {
					String title = null;
					String subtitle = null;
					String ytitle = null;
					String query = null;
					if(null==metric) {
						query = "select time, clientid, sum(requests) requests, sum(impressions) impressions, sum(clicks) clicks, sum(conversions1) conversions1, sum(conversions2) conversions2, sum(revenue) revenue, sum(cost1) cost1, sum(cost2) cost2, sum(sales1) sales1, sum(sales2) sales2 from v_daily";
					} else {
						String arr[] = getMetricQuery(metric, groupby, filter, likeFilter);
						title = arr[0];
						subtitle = arr[1];
						ytitle = arr[2];
						query = "select time, clientid, " + arr[3] + " from v_daily";
					}
					if(filter.length()>0) {
						query += " where " + filter.toString().trim().replace("\t","and");
					}
					if(likeFilter.length()>0) {
						if(query.indexOf(" where ")>0) {
							query += " and " + likeFilter.toString().trim().replace("\t","and");
						} else {
							query += " where " + likeFilter.toString().trim().replace("\t","and");
						}
					}
					query += " group by time, clientid";
					logger.info("Query : " + query);
					long stime = System.currentTimeMillis();
					ResultSet rs = stmt.executeQuery(query);
					logger.info("Elapsed time for executing query : " + (System.currentTimeMillis()-stime)/1000.0 + "s");
					long pointStart = 0L;
					long pointEnd = 0L;
					while(rs.next()) {
						String k1 = Metadata.clients.get(rs.getString("clientid"));
						if(null==k1) {
							continue;
						}
						k1 += " (" + rs.getString("clientid") + ")";
						Timestamp t = rs.getTimestamp("time");
						long tLong = t.getTime();
						if(pointStart==0L) {
							pointStart = tLong;
						}
						if(pointEnd<tLong) {
							pointEnd = tLong;
						}
						if(null==metric) {
							String rowKey = k1+", "+t.toString();
							osw.append(rowKey);
							osw.append(", ");
							osw.append(rs.getString("requests"));
							osw.append(", ");
							osw.append(rs.getString("impressions"));
							osw.append(", ");
							osw.append(rs.getString("clicks"));
							osw.append(", ");
							osw.append(rs.getString("conversions1"));
							osw.append(", ");
							osw.append(rs.getString("conversions2"));
							osw.append(", ");
							osw.append(rs.getString("revenue"));
							osw.append(", ");
							osw.append(rs.getString("cost1"));
							osw.append(", ");
							osw.append(rs.getString("cost2"));
							osw.append(", ");
							osw.append(rs.getString("sales1"));
							osw.append(", ");
							osw.append(rs.getString("sales2"));
							osw.append(lineSeparator);
						} else {
							double d = rs.getDouble("value");
							dataset.setValue(d, k1, String.valueOf(tLong));
						}
					}
					rs.close();
					if(null!=dataset) {
						createHtml(osw, dataset, title, subtitle, ytitle, oneDay, pointStart, pointEnd, orderby, topn, basis);
					}
				} else if(groupby.equals("Client-Campaign")) {
					String title = null;
					String subtitle = null;
					String ytitle = null;
					String query = null;
					if(null==metric) {
						query = "select time, clientid, campaignid, sum(requests) requests, sum(impressions) impressions, sum(clicks) clicks, sum(conversions1) conversions1, sum(conversions2) conversions2, sum(revenue) revenue, sum(cost1) cost1, sum(cost2) cost2, sum(sales1) sales1, sum(sales2) sales2 from v_daily";
					} else {
						String arr[] = getMetricQuery(metric, groupby, filter, likeFilter);
						title = arr[0];
						subtitle = arr[1];
						ytitle = arr[2];
						query = "select time, clientid, campaignid, " + arr[3] + " from v_daily";
					}
					if(filter.length()>0) {
						query += " where " + filter.toString().trim().replace("\t","and");
					}
					if(likeFilter.length()>0) {
						if(query.indexOf(" where ")>0) {
							query += " and " + likeFilter.toString().trim().replace("\t","and");
						} else {
							query += " where " + likeFilter.toString().trim().replace("\t","and");
						}
					}
					query += " group by time, clientid, campaignid";
					logger.info("Query : " + query);
					long stime = System.currentTimeMillis();
					ResultSet rs = stmt.executeQuery(query);
					logger.info("Elapsed time for executing query : " + (System.currentTimeMillis()-stime)/1000.0 + "s");
					long pointStart = 0L;
					long pointEnd = 0L;
					while(rs.next()) {
						String c1 = Metadata.clients.get(rs.getString("clientid"));
						String c2 = Metadata.campaigns.get(rs.getString("campaignid"));
						if(null==c1||null==c2) {
							continue;
						}
						String k1 = c1 + " ("+rs.getString("clientid")+") - " + c2 + " ("+rs.getString("campaignid")+")";
						Timestamp t = rs.getTimestamp("time");
						long tLong = t.getTime();
						if(pointStart==0L) {
							pointStart = tLong;
						}
						if(pointEnd<tLong) {
							pointEnd = tLong;
						}
						if(null==metric) {
							String rowKey = k1+", "+t.toString();
							osw.append(rowKey);
							osw.append(", ");
							osw.append(rs.getString("requests"));
							osw.append(", ");
							osw.append(rs.getString("impressions"));
							osw.append(", ");
							osw.append(rs.getString("clicks"));
							osw.append(", ");
							osw.append(rs.getString("conversions1"));
							osw.append(", ");
							osw.append(rs.getString("conversions2"));
							osw.append(", ");
							osw.append(rs.getString("revenue"));
							osw.append(", ");
							osw.append(rs.getString("cost1"));
							osw.append(", ");
							osw.append(rs.getString("cost2"));
							osw.append(", ");
							osw.append(rs.getString("sales1"));
							osw.append(", ");
							osw.append(rs.getString("sales2"));
							osw.append(lineSeparator);
						} else {
							double d = rs.getDouble("value");
							dataset.setValue(d, k1, String.valueOf(tLong));
						}
					}
					rs.close();
					if(null!=dataset) {
						createHtml(osw, dataset, title, subtitle, ytitle, oneDay, pointStart, pointEnd, orderby, topn, basis);
					}
				} else if(groupby.equals("Client-Category")) {
					String title = null;
					String subtitle = null;
					String ytitle = null;
					String query = null;
					if(null==metric) {
						query = "select time, category, sum(requests) requests, sum(impressions) impressions, sum(clicks) clicks, sum(conversions1) conversions1, sum(conversions2) conversions2, sum(revenue) revenue, sum(cost1) cost1, sum(cost2) cost2, sum(sales1) sales1, sum(sales2) sales2 from v_daily a join clients b on (a.clientid=b.client)";
					} else {
						String arr[] = getMetricQuery(metric, groupby, filter, likeFilter);
						title = arr[0];
						subtitle = arr[1];
						ytitle = arr[2];
						query = "select time, category, " + arr[3] + " from v_daily a join clients b on (a.clientid=b.client)";
					}
					if(filter.length()>0) {
						query += " where " + filter.toString().trim().replace("\t","and");
					}
					if(likeFilter.length()>0) {
						if(query.indexOf(" where ")>0) {
							query += " and " + likeFilter.toString().trim().replace("\t","and");
						} else {
							query += " where " + likeFilter.toString().trim().replace("\t","and");
						}
					}
					query += " group by time, category";
					logger.info("Query : " + query);
					long stime = System.currentTimeMillis();
					ResultSet rs = stmt.executeQuery(query);
					logger.info("Elapsed time for executing query : " + (System.currentTimeMillis()-stime)/1000.0 + "s");
					long pointStart = 0L;
					long pointEnd = 0L;
					while(rs.next()) {
						String k1 = rs.getString("category");
						if(k1==null) {
							k1 = "None";
						}
						Timestamp t = rs.getTimestamp("time");
						long tLong = t.getTime();
						if(pointStart==0L) {
							pointStart = tLong;
						}
						if(pointEnd<tLong) {
							pointEnd = tLong;
						}
						if(null==metric) {
							String rowKey = k1+", "+t.toString();
							osw.append(rowKey);
							osw.append(", ");
							osw.append(rs.getString("requests"));
							osw.append(", ");
							osw.append(rs.getString("impressions"));
							osw.append(", ");
							osw.append(rs.getString("clicks"));
							osw.append(", ");
							osw.append(rs.getString("conversions1"));
							osw.append(", ");
							osw.append(rs.getString("conversions2"));
							osw.append(", ");
							osw.append(rs.getString("revenue"));
							osw.append(", ");
							osw.append(rs.getString("cost1"));
							osw.append(", ");
							osw.append(rs.getString("cost2"));
							osw.append(", ");
							osw.append(rs.getString("sales1"));
							osw.append(", ");
							osw.append(rs.getString("sales2"));
							osw.append(lineSeparator);
						} else {
							double d = rs.getDouble("value");
							dataset.setValue(d, k1, String.valueOf(tLong));
						}
					}
					rs.close();
					if(null!=dataset) {
						createHtml(osw, dataset, title, subtitle, ytitle, oneDay, pointStart, pointEnd, orderby, topn, basis);
					}
				} else {
					osw.append("Invalid condition!");
					osw.append(lineSeparator);
				}
			} else if(viewType.equals("Monthly")) {
				Timestamp time = Metadata.getMaxTime(3, logger);
				Timestamp oneYearBefore = new Timestamp(time.getTime()-oneYear);
				Timestamp twoYearsBefore = new Timestamp(time.getTime()-2L*oneYear);
				if(groupby.equals("Time-Period")) {
					String title = null;
					String subtitle = null;
					String ytitle = null;
					String query = null;
					if(null==metric) {
						query = "select time, sum(requests) requests, sum(impressions) impressions, sum(clicks) clicks, sum(conversions1) conversions1, sum(conversions2) conversions2, sum(revenue) revenue, sum(cost1) cost1, sum(cost2) cost2, sum(sales1) sales1, sum(sales2) sales2 from v_monthly where time>'" + twoYearsBefore + "'";
					} else {
						String arr[] = getMetricQuery(metric, groupby, filter, likeFilter);
						title = arr[0];
						subtitle = arr[1];
						ytitle = arr[2];
						query = "select time, " + arr[3] + " from v_monthly where time>'" + twoYearsBefore + "'";
					}
					if(filter.length()>0) {
						query += " and " + filter.toString().trim().replace("\t","and");
					}
					if(likeFilter.length()>0) {
						query += " and " + likeFilter.toString().trim().replace("\t","and");
					}
					query += " group by time";
					logger.info("Query : " + query);
					long stime = System.currentTimeMillis();
					ResultSet rs = stmt.executeQuery(query);
					logger.info("Elapsed time for executing query : " + (System.currentTimeMillis()-stime)/1000.0 + "s");
					long pointStart = 0L;
					long pointEnd = 0L;
					while(rs.next()) {
						Timestamp t = rs.getTimestamp("time");
						long tLong = t.getTime();
						String k1 = null;
						Timestamp k2 = null;
						if(t.after(oneYearBefore)) {
							k1 = "latest one year";
							k2 = t;
						} else {
							k1 = "two years before";
							k2 = new Timestamp(tLong+oneYear);
						}
						tLong = k2.getTime();
						if(pointStart==0L) {
							pointStart = tLong;
						}
						if(pointEnd<tLong) {
							pointEnd = tLong;
						}
						if(null==metric) {
							String rowKey = k1+", "+k2;
							osw.append(rowKey);
							osw.append(", ");
							osw.append(rs.getString("requests"));
							osw.append(", ");
							osw.append(rs.getString("impressions"));
							osw.append(", ");
							osw.append(rs.getString("clicks"));
							osw.append(", ");
							osw.append(rs.getString("conversions1"));
							osw.append(", ");
							osw.append(rs.getString("conversions2"));
							osw.append(", ");
							osw.append(rs.getString("revenue"));
							osw.append(", ");
							osw.append(rs.getString("cost1"));
							osw.append(", ");
							osw.append(rs.getString("cost2"));
							osw.append(", ");
							osw.append(rs.getString("sales1"));
							osw.append(", ");
							osw.append(rs.getString("sales2"));
							osw.append(lineSeparator);
						} else {
							dataset.setValue(rs.getDouble("value"), k1, String.valueOf(tLong));
						}
					}
					rs.close();
					if(null!=dataset) {
						createHtml(osw, dataset, title, subtitle, ytitle, -1, pointStart, pointEnd, orderby, topn, basis);
					}
				} else if(groupby.equals("Cost-Type")) {
					String title = null;
					String subtitle = null;
					String ytitle = null;
					String query = null;
					if(null==metric) {
						query = "select time, cost_type, sum(requests) requests, sum(impressions) impressions, sum(clicks) clicks, sum(conversions1) conversions1, sum(conversions2) conversions2, sum(revenue) revenue, sum(cost1) cost1, sum(cost2) cost2, sum(sales1) sales1, sum(sales2) sales2 from v_monthly where time>'" + twoYearsBefore + "'";
					} else {
						String arr[] = getMetricQuery(metric, groupby, filter, likeFilter);
						title = arr[0];
						subtitle = arr[1];
						ytitle = arr[2];
						query = "select time, cost_type, " + arr[3] + " from v_monthly where time>'" + twoYearsBefore + "'";
					}
					if(filter.length()>0) {
						query += " and " + filter.toString().trim().replace("\t","and");
					}
					if(likeFilter.length()>0) {
						query += " and " + likeFilter.toString().trim().replace("\t","and");
					}
					query += " group by time, cost_type";
					logger.info("Query : " + query);
					long stime = System.currentTimeMillis();
					ResultSet rs = stmt.executeQuery(query);
					logger.info("Elapsed time for executing query : " + (System.currentTimeMillis()-stime)/1000.0 + "s");
					long pointStart = 0L;
					long pointEnd = 0L;
					while(rs.next()) {
						String k1 = Metadata.costTypes.get(rs.getString("cost_type"));
						if(null==k1) {
							continue;
						}
						k1 += " (" + rs.getString("cost_type") + ")";
						Timestamp t = rs.getTimestamp("time");
						long tLong = t.getTime();
						if(pointStart==0L) {
							pointStart = tLong;
						}
						if(pointEnd<tLong) {
							pointEnd = tLong;
						}
						if(null==metric) {
							String rowKey = k1+", "+t.toString();
							osw.append(rowKey);
							osw.append(", ");
							osw.append(rs.getString("requests"));
							osw.append(", ");
							osw.append(rs.getString("impressions"));
							osw.append(", ");
							osw.append(rs.getString("clicks"));
							osw.append(", ");
							osw.append(rs.getString("conversions1"));
							osw.append(", ");
							osw.append(rs.getString("conversions2"));
							osw.append(", ");
							osw.append(rs.getString("revenue"));
							osw.append(", ");
							osw.append(rs.getString("cost1"));
							osw.append(", ");
							osw.append(rs.getString("cost2"));
							osw.append(", ");
							osw.append(rs.getString("sales1"));
							osw.append(", ");
							osw.append(rs.getString("sales2"));
							osw.append(lineSeparator);
						} else {
							dataset.setValue(rs.getDouble("value"), k1, String.valueOf(tLong));
						}
					}
					rs.close();
					if(null!=dataset) {
						createHtml(osw, dataset, title, subtitle, ytitle, -1, pointStart, pointEnd, orderby, topn, basis);
					}
				} else if(groupby.equals("Is-Mobile")) {
					String title = null;
					String subtitle = null;
					String ytitle = null;
					String query = null;
					if(null==metric) {
						query = "select time, is_mobile, sum(requests) requests, sum(impressions) impressions, sum(clicks) clicks, sum(conversions1) conversions1, sum(conversions2) conversions2, sum(revenue) revenue, sum(cost1) cost1, sum(cost2) cost2, sum(sales1) sales1, sum(sales2) sales2 from v_monthly where time>'" + twoYearsBefore + "'";
					} else {
						String arr[] = getMetricQuery(metric, groupby, filter, likeFilter);
						title = arr[0];
						subtitle = arr[1];
						ytitle = arr[2];
						query = "select time, is_mobile, " + arr[3] + " from v_monthly where time>'" + twoYearsBefore + "'";
					}
					if(filter.length()>0) {
						query += " and " + filter.toString().trim().replace("\t","and");
					}
					if(likeFilter.length()>0) {
						query += " and " + likeFilter.toString().trim().replace("\t","and");
					}
					query += " group by time, is_mobile";
					logger.info("Query : " + query);
					long stime = System.currentTimeMillis();
					ResultSet rs = stmt.executeQuery(query);
					logger.info("Elapsed time for executing query : " + (System.currentTimeMillis()-stime)/1000.0 + "s");
					long pointStart = 0L;
					long pointEnd = 0L;
					while(rs.next()) {
						String k1 = Metadata.isMobiles.get(rs.getString("is_mobile"));
						if(null==k1) {
							continue;
						}
						k1 += " (" + rs.getString("is_mobile") + ")";
						Timestamp t = rs.getTimestamp("time");
						long tLong = t.getTime();
						if(pointStart==0L) {
							pointStart = tLong;
						}
						if(pointEnd<tLong) {
							pointEnd = tLong;
						}
						if(null==metric) {
							String rowKey = k1+", "+t.toString();
							osw.append(rowKey);
							osw.append(", ");
							osw.append(rs.getString("requests"));
							osw.append(", ");
							osw.append(rs.getString("impressions"));
							osw.append(", ");
							osw.append(rs.getString("clicks"));
							osw.append(", ");
							osw.append(rs.getString("conversions1"));
							osw.append(", ");
							osw.append(rs.getString("conversions2"));
							osw.append(", ");
							osw.append(rs.getString("revenue"));
							osw.append(", ");
							osw.append(rs.getString("cost1"));
							osw.append(", ");
							osw.append(rs.getString("cost2"));
							osw.append(", ");
							osw.append(rs.getString("sales1"));
							osw.append(", ");
							osw.append(rs.getString("sales2"));
							osw.append(lineSeparator);
						} else {
							dataset.setValue(rs.getDouble("value"), k1, String.valueOf(tLong));
						}
					}
					rs.close();
					if(null!=dataset) {
						createHtml(osw, dataset, title, subtitle, ytitle, -1, pointStart, pointEnd, orderby, topn, basis);
					}
				} else if(groupby.equals("Campaign-Type")) {
					String title = null;
					String subtitle = null;
					String ytitle = null;
					String query = null;
					if(null==metric) {
						query = "select time, ctype, sum(requests) requests, sum(impressions) impressions, sum(clicks) clicks, sum(conversions1) conversions1, sum(conversions2) conversions2, sum(revenue) revenue, sum(cost1) cost1, sum(cost2) cost2, sum(sales1) sales1, sum(sales2) sales2 from v_monthly where time>'" + twoYearsBefore + "'";
					} else {
						String arr[] = getMetricQuery(metric, groupby, filter, likeFilter);
						title = arr[0];
						subtitle = arr[1];
						ytitle = arr[2];
						query = "select time, ctype, " + arr[3] + " from v_monthly where time>'" + twoYearsBefore + "'";
					}
					if(filter.length()>0) {
						query += " and " + filter.toString().trim().replace("\t","and");
					}
					if(likeFilter.length()>0) {
						query += " and " + likeFilter.toString().trim().replace("\t","and");
					}
					query += " group by time, ctype";
					logger.info("Query : " + query);
					long stime = System.currentTimeMillis();
					ResultSet rs = stmt.executeQuery(query);
					logger.info("Elapsed time for executing query : " + (System.currentTimeMillis()-stime)/1000.0 + "s");
					long pointStart = 0L;
					long pointEnd = 0L;
					while(rs.next()) {
						String k1 = Metadata.ctypes.get(rs.getString("ctype"));
						if(null==k1) {
							continue;
						}
						k1 += " (" + rs.getString("ctype") + ")";
						Timestamp t = rs.getTimestamp("time");
						long tLong = t.getTime();
						if(pointStart==0L) {
							pointStart = tLong;
						}
						if(pointEnd<tLong) {
							pointEnd = tLong;
						}
						if(null==metric) {
							String rowKey = k1+", "+t.toString();
							osw.append(rowKey);
							osw.append(", ");
							osw.append(rs.getString("requests"));
							osw.append(", ");
							osw.append(rs.getString("impressions"));
							osw.append(", ");
							osw.append(rs.getString("clicks"));
							osw.append(", ");
							osw.append(rs.getString("conversions1"));
							osw.append(", ");
							osw.append(rs.getString("conversions2"));
							osw.append(", ");
							osw.append(rs.getString("revenue"));
							osw.append(", ");
							osw.append(rs.getString("cost1"));
							osw.append(", ");
							osw.append(rs.getString("cost2"));
							osw.append(", ");
							osw.append(rs.getString("sales1"));
							osw.append(", ");
							osw.append(rs.getString("sales2"));
							osw.append(lineSeparator);
						} else {
							dataset.setValue(rs.getDouble("value"), k1, String.valueOf(tLong));
						}
					}
					rs.close();
					if(null!=dataset) {
						createHtml(osw, dataset, title, subtitle, ytitle, -1, pointStart, pointEnd, orderby, topn, basis);
					}
				} else if(groupby.equals("Zone")) {
					
					String title = null;
					String subtitle = null;
					String ytitle = null;
					String query = null;
					if(null==metric) {
						query = "select time, zoneid, sum(requests) requests, sum(impressions) impressions, sum(clicks) clicks, sum(conversions1) conversions1, sum(conversions2) conversions2, sum(revenue) revenue, sum(cost1) cost1, sum(cost2) cost2, sum(sales1) sales1, sum(sales2) sales2 from v_monthly where time>'" + twoYearsBefore + "'";
					} else {
						String arr[] = getMetricQuery(metric, groupby, filter, likeFilter);
						title = arr[0];
						subtitle = arr[1];
						ytitle = arr[2];
						query = "select time, zoneid, " + arr[3] + " from v_monthly where time>'" + twoYearsBefore + "'";
					}
					if(filter.length()>0) {
						query += " and " + filter.toString().trim().replace("\t","and");
					}
					if(likeFilter.length()>0) {
						query += " and " + likeFilter.toString().trim().replace("\t","and");
					}
					query += " group by time, zoneid";
					logger.info("Query : " + query);
					long stime = System.currentTimeMillis();
					ResultSet rs = stmt.executeQuery(query);
					logger.info("Elapsed time for executing query : " + (System.currentTimeMillis()-stime)/1000.0 + "s");
					long pointStart = 0L;
					long pointEnd = 0L;
					while(rs.next()) {
						String k1 = Metadata.zones.get(rs.getString("zoneid"));
						if(null==k1) {
							continue;
						}
						k1 += " (" + rs.getString("zoneid") + ")";
						Timestamp t = rs.getTimestamp("time");
						long tLong = t.getTime();
						if(pointStart==0L) {
							pointStart = tLong;
						}
						if(pointEnd<tLong) {
							pointEnd = tLong;
						}
						if(null==metric) {
							String rowKey = k1+", "+t.toString();
							osw.append(rowKey);
							osw.append(", ");
							osw.append(rs.getString("requests"));
							osw.append(", ");
							osw.append(rs.getString("impressions"));
							osw.append(", ");
							osw.append(rs.getString("clicks"));
							osw.append(", ");
							osw.append(rs.getString("conversions1"));
							osw.append(", ");
							osw.append(rs.getString("conversions2"));
							osw.append(", ");
							osw.append(rs.getString("revenue"));
							osw.append(", ");
							osw.append(rs.getString("cost1"));
							osw.append(", ");
							osw.append(rs.getString("cost2"));
							osw.append(", ");
							osw.append(rs.getString("sales1"));
							osw.append(", ");
							osw.append(rs.getString("sales2"));
							osw.append(lineSeparator);
						} else {
							double d = rs.getDouble("value");
							dataset.setValue(d, k1, String.valueOf(tLong));
						}
					}
					rs.close();
					if(null!=dataset) {
						createHtml(osw, dataset, title, subtitle, ytitle, -1, pointStart, pointEnd, orderby, topn, basis);
					}
				} else if(groupby.equals("Client")) {
					String title = null;
					String subtitle = null;
					String ytitle = null;
					String query = null;
					if(null==metric) {
						query = "select time, clientid, sum(requests) requests, sum(impressions) impressions, sum(clicks) clicks, sum(conversions1) conversions1, sum(conversions2) conversions2, sum(revenue) revenue, sum(cost1) cost1, sum(cost2) cost2, sum(sales1) sales1, sum(sales2) sales2 from v_monthly where time>'" + twoYearsBefore + "'";
					} else {
						String arr[] = getMetricQuery(metric, groupby, filter, likeFilter);
						title = arr[0];
						subtitle = arr[1];
						ytitle = arr[2];
						query = "select time, clientid, " + arr[3] + " from v_monthly where time>'" + twoYearsBefore + "'";
					}
					if(filter.length()>0) {
						query += " and " + filter.toString().trim().replace("\t","and");
					}
					if(likeFilter.length()>0) {
						query += " and " + likeFilter.toString().trim().replace("\t","and");
					}
					query += " group by time, clientid";
					logger.info("Query : " + query);
					long stime = System.currentTimeMillis();
					ResultSet rs = stmt.executeQuery(query);
					logger.info("Elapsed time for executing query : " + (System.currentTimeMillis()-stime)/1000.0 + "s");
					long pointStart = 0L;
					long pointEnd = 0L;
					while(rs.next()) {
						String k1 = Metadata.clients.get(rs.getString("clientid"));
						if(null==k1) {
							continue;
						}
						k1 += " (" + rs.getString("clientid") + ")";
						Timestamp t = rs.getTimestamp("time");
						long tLong = t.getTime();
						if(pointStart==0L) {
							pointStart = tLong;
						}
						if(pointEnd<tLong) {
							pointEnd = tLong;
						}
						if(null==metric) {
							String rowKey = k1+", "+t.toString();
							osw.append(rowKey);
							osw.append(", ");
							osw.append(rs.getString("requests"));
							osw.append(", ");
							osw.append(rs.getString("impressions"));
							osw.append(", ");
							osw.append(rs.getString("clicks"));
							osw.append(", ");
							osw.append(rs.getString("conversions1"));
							osw.append(", ");
							osw.append(rs.getString("conversions2"));
							osw.append(", ");
							osw.append(rs.getString("revenue"));
							osw.append(", ");
							osw.append(rs.getString("cost1"));
							osw.append(", ");
							osw.append(rs.getString("cost2"));
							osw.append(", ");
							osw.append(rs.getString("sales1"));
							osw.append(", ");
							osw.append(rs.getString("sales2"));
							osw.append(lineSeparator);
						} else {
							double d = rs.getDouble("value");
							dataset.setValue(d, k1, String.valueOf(tLong));
						}
					}
					rs.close();
					if(null!=dataset) {
						createHtml(osw, dataset, title, subtitle, ytitle, -1, pointStart, pointEnd, orderby, topn, basis);
					}
				} else if(groupby.equals("Client-Campaign")) {
					String title = null;
					String subtitle = null;
					String ytitle = null;
					String query = null;
					if(null==metric) {
						query = "select time, clientid, campaignid, sum(requests) requests, sum(impressions) impressions, sum(clicks) clicks, sum(conversions1) conversions1, sum(conversions2) conversions2, sum(revenue) revenue, sum(cost1) cost1, sum(cost2) cost2, sum(sales1) sales1, sum(sales2) sales2 from v_monthly where time>'" + twoYearsBefore + "'";
					} else {
						String arr[] = getMetricQuery(metric, groupby, filter, likeFilter);
						title = arr[0];
						subtitle = arr[1];
						ytitle = arr[2];
						query = "select time, clientid, campaignid, " + arr[3] + " from v_monthly where time>'" + twoYearsBefore + "'";
					}
					if(filter.length()>0) {
						query += " and " + filter.toString().trim().replace("\t","and");
					}
					if(likeFilter.length()>0) {
						query += " and " + likeFilter.toString().trim().replace("\t","and");
					}
					query += " group by time, clientid, campaignid";
					logger.info("Query : " + query);
					long stime = System.currentTimeMillis();
					ResultSet rs = stmt.executeQuery(query);
					logger.info("Elapsed time for executing query : " + (System.currentTimeMillis()-stime)/1000.0 + "s");
					long pointStart = 0L;
					long pointEnd = 0L;
					while(rs.next()) {
						String c1 = Metadata.clients.get(rs.getString("clientid"));
						String c2 = Metadata.campaigns.get(rs.getString("campaignid"));
						if(null==c1||null==c2) {
							continue;
						}
						String k1 = c1 + " ("+rs.getString("clientid")+") - " + c2 + " ("+rs.getString("campaignid")+")";
						Timestamp t = rs.getTimestamp("time");
						long tLong = t.getTime();
						if(pointStart==0L) {
							pointStart = tLong;
						}
						if(pointEnd<tLong) {
							pointEnd = tLong;
						}
						if(null==metric) {
							String rowKey = k1+", "+t.toString();
							osw.append(rowKey);
							osw.append(", ");
							osw.append(rs.getString("requests"));
							osw.append(", ");
							osw.append(rs.getString("impressions"));
							osw.append(", ");
							osw.append(rs.getString("clicks"));
							osw.append(", ");
							osw.append(rs.getString("conversions1"));
							osw.append(", ");
							osw.append(rs.getString("conversions2"));
							osw.append(", ");
							osw.append(rs.getString("revenue"));
							osw.append(", ");
							osw.append(rs.getString("cost1"));
							osw.append(", ");
							osw.append(rs.getString("cost2"));
							osw.append(", ");
							osw.append(rs.getString("sales1"));
							osw.append(", ");
							osw.append(rs.getString("sales2"));
							osw.append(lineSeparator);
						} else {
							double d = rs.getDouble("value");
							dataset.setValue(d, k1, String.valueOf(tLong));
						}
					}
					rs.close();
					if(null!=dataset) {
						createHtml(osw, dataset, title, subtitle, ytitle, -1, pointStart, pointEnd, orderby, topn, basis);
					}
				} else if(groupby.equals("Client-Category")) {
					String title = null;
					String subtitle = null;
					String ytitle = null;
					String query = null;
					if(null==metric) {
						query = "select time, category, sum(requests) requests, sum(impressions) impressions, sum(clicks) clicks, sum(conversions1) conversions1, sum(conversions2) conversions2, sum(revenue) revenue, sum(cost1) cost1, sum(cost2) cost2, sum(sales1) sales1, sum(sales2) sales2 from v_monthly a join clients b on (a.clientid=b.client) where time>'" + twoYearsBefore + "'";
					} else {
						String arr[] = getMetricQuery(metric, groupby, filter, likeFilter);
						title = arr[0];
						subtitle = arr[1];
						ytitle = arr[2];
						query = "select time, category, " + arr[3] + " from v_monthly a join clients b on (a.clientid=b.client) where time>'" + twoYearsBefore + "'";
					}
					if(filter.length()>0) {
						query += " and " + filter.toString().trim().replace("\t","and");
					}
					if(likeFilter.length()>0) {
						query += " and " + likeFilter.toString().trim().replace("\t","and");
					}
					query += " group by time, category";
					logger.info("Query : " + query);
					long stime = System.currentTimeMillis();
					ResultSet rs = stmt.executeQuery(query);
					logger.info("Elapsed time for executing query : " + (System.currentTimeMillis()-stime)/1000.0 + "s");
					long pointStart = 0L;
					long pointEnd = 0L;
					while(rs.next()) {
						String k1 = rs.getString("category");
						if(k1==null) {
							k1 = "None";
						}
						Timestamp t = rs.getTimestamp("time");
						long tLong = t.getTime();
						if(pointStart==0L) {
							pointStart = tLong;
						}
						if(pointEnd<tLong) {
							pointEnd = tLong;
						}
						if(null==metric) {
							String rowKey = k1+", "+t.toString();
							osw.append(rowKey);
							osw.append(", ");
							osw.append(rs.getString("requests"));
							osw.append(", ");
							osw.append(rs.getString("impressions"));
							osw.append(", ");
							osw.append(rs.getString("clicks"));
							osw.append(", ");
							osw.append(rs.getString("conversions1"));
							osw.append(", ");
							osw.append(rs.getString("conversions2"));
							osw.append(", ");
							osw.append(rs.getString("revenue"));
							osw.append(", ");
							osw.append(rs.getString("cost1"));
							osw.append(", ");
							osw.append(rs.getString("cost2"));
							osw.append(", ");
							osw.append(rs.getString("sales1"));
							osw.append(", ");
							osw.append(rs.getString("sales2"));
							osw.append(lineSeparator);
						} else {
							double d = rs.getDouble("value");
							dataset.setValue(d, k1, String.valueOf(tLong));
						}
					}
					rs.close();
					if(null!=dataset) {
						createHtml(osw, dataset, title, subtitle, ytitle, -1, pointStart, pointEnd, orderby, topn, basis);
					}
				} else {
					osw.append("Invalid condition!");
					osw.append(lineSeparator);
				}
			} else if(viewType.equals("Three-Weeks")) {
				Timestamp time = Metadata.getMaxTime(1, logger);
				long mt = time.getTime();
				int filterSum = (zoneFilter+costTypeFilter+isMobileFilter+clientFilter+ctypeFilter+campaignFilter);
				if(groupby.equals("Time-Period")) {
					String title = null;
					String subtitle = null;
					String ytitle = null;
					String query = null;
					boolean fromView = (filterSum==0||(filterSum==1&&isMobileFilter==1));
					if(fromView) {
						if(null==metric) {
							query = "select time, sum(requests) requests, sum(impressions) impressions, sum(clicks) clicks, sum(conversions1) conversions1, sum(conversions2) conversions2, sum(revenue) revenue, sum(cost1) cost1, sum(cost2) cost2, sum(sales1) sales1, sum(sales2) sales2 from v_hourly_by_is_mobile";
						} else {
							String arr[] = getMetricQuery(metric, groupby, filter, likeFilter);
							title = arr[0];
							subtitle = arr[1];
							ytitle = arr[2];
							if(arr[3].indexOf("count(distinct")>=0) {
								if(arr[3].indexOf("count(distinct zoneid")>=0) {
									query = "select time, " + arr[3] + " from v_hourly_by_zone";
								} else if(arr[3].indexOf("count(distinct clientid")>=0) {
									query = "select time, " + arr[3] + " from v_hourly_by_client";
								} else if(arr[3].indexOf("count(distinct campaignid")>=0) {
									query = "select time, " + arr[3] + " from v_hourly_by_campaign";
								} else {
									query = "select time, " + arr[3] + " from v_hourly_by_is_mobile";
								}
							} else {
								query = "select time, " + arr[3] + " from v_hourly_by_is_mobile";
							}
						}
					} else {
						time = Metadata.getMaxTime(2, logger);
						mt = time.getTime();
						if(null==metric) {
							query = "select time, sum(requests) requests, sum(impressions) impressions, sum(clicks) clicks, sum(conversions1) conversions1, sum(conversions2) conversions2, sum(revenue) revenue, sum(cost1) cost1, sum(cost2) cost2, sum(sales1) sales1, sum(sales2) sales2 from v_daily";
						} else {
							String arr[] = getMetricQuery(metric, groupby, filter, likeFilter);
							title = arr[0];
							subtitle = arr[1];
							ytitle = arr[2];
							query = "select time, " + arr[3] + " from v_daily";
						}
					}
					if(filter.length()>0) {
						query += " where " + filter.toString().trim().replace("\t","and");
					}
					if(likeFilter.length()>0) {
						if(query.indexOf(" where ")>0) {
							query += " and " + likeFilter.toString().trim().replace("\t","and");
						} else {
							query += " where " + likeFilter.toString().trim().replace("\t","and");
						}
					}
					query += " group by time";
					Timestamp sevenDaysBefore = new Timestamp(mt-7L*oneDay);
					Timestamp forteenDaysBefore = new Timestamp(mt-14L*oneDay);
					logger.info("Query : " + query);
					long stime = System.currentTimeMillis();
					ResultSet rs = stmt.executeQuery(query);
					logger.info("Elapsed time for executing query : " + (System.currentTimeMillis()-stime)/1000.0 + "s");
					long pointStart = 0L;
					long pointEnd = 0L;
					while(rs.next()) {
						Timestamp t = rs.getTimestamp("time");
						long tLong = t.getTime();
						String k1 = null;
						Timestamp k2 = null;
						if(query.indexOf("from v_daily")>0) {
							if(t.after(sevenDaysBefore)) {
								k1 = "latest one week";
								k2 = t;
							} else if(t.after(forteenDaysBefore)) {
								k1 = "one week before";
								k2 = new Timestamp(tLong+7L*oneDay);
							} else {
								k1 = "two weeks before";
								k2 = new Timestamp(tLong+14L*oneDay);
							}
						} else {
							if(t.equals(sevenDaysBefore)||t.after(sevenDaysBefore)) {
								k1 = "latest one week";
								k2 = t;
							} else if(t.equals(forteenDaysBefore)||t.after(forteenDaysBefore)) {
								k1 = "one week before";
								k2 = new Timestamp(tLong+7L*oneDay);
							} else {
								k1 = "two weeks before";
								k2 = new Timestamp(tLong+14L*oneDay);
							}
						}
						tLong = k2.getTime();
						if(pointStart==0L) {
							pointStart = tLong;
						}
						if(pointEnd<tLong) {
							pointEnd = tLong;
						}
						if(null==metric) {
							String rowKey = k1+", "+k2;
							osw.append(rowKey);
							osw.append(", ");
							osw.append(rs.getString("requests"));
							osw.append(", ");
							osw.append(rs.getString("impressions"));
							osw.append(", ");
							osw.append(rs.getString("clicks"));
							osw.append(", ");
							osw.append(rs.getString("conversions1"));
							osw.append(", ");
							osw.append(rs.getString("conversions2"));
							osw.append(", ");
							osw.append(rs.getString("revenue"));
							osw.append(", ");
							osw.append(rs.getString("cost1"));
							osw.append(", ");
							osw.append(rs.getString("cost2"));
							osw.append(", ");
							osw.append(rs.getString("sales1"));
							osw.append(", ");
							osw.append(rs.getString("sales2"));
							osw.append(lineSeparator);
						} else {
							dataset.setValue(rs.getDouble("value"), k1, String.valueOf(tLong));
						}
					}
					rs.close();
					if(null!=dataset) {
						if(fromView) {
							createHtml(osw, dataset, title, subtitle, ytitle, 3600000L, pointStart, pointEnd, orderby, topn, basis);
						} else {
							createHtml(osw, dataset, title, subtitle, ytitle, oneDay, pointStart, pointEnd, orderby, topn, basis);
						}
					}
				} else if(groupby.equals("Cost-Type")) {
					String title = null;
					String subtitle = null;
					String ytitle = null;
					String query = null;
					boolean fromView = (filterSum==0||(filterSum==1&&costTypeFilter==1));
					if(fromView) {
						if(null==metric) {
							query = "select time, cost_type, sum(requests) requests, sum(impressions) impressions, sum(clicks) clicks, sum(conversions1) conversions1, sum(conversions2) conversions2, sum(revenue) revenue, sum(cost1) cost1, sum(cost2) cost2, sum(sales1) sales1, sum(sales2) sales2 from v_hourly_by_cost_type";
						} else {
							String arr[] = getMetricQuery(metric, groupby, filter, likeFilter);
							title = arr[0];
							subtitle = arr[1];
							ytitle = arr[2];
							if(arr[3].indexOf("count(distinct")>=0) {
								fromView = false;
								query = "select time, cost_type, " + arr[3] + " from v_daily";
							} else {
								query = "select time, cost_type, " + arr[3] + " from v_hourly_by_cost_type";
							}
						}
					} else {
						if(null==metric) {
							query = "select time, cost_type, sum(requests) requests, sum(impressions) impressions, sum(clicks) clicks, sum(conversions1) conversions1, sum(conversions2) conversions2, sum(revenue) revenue, sum(cost1) cost1, sum(cost2) cost2, sum(sales1) sales1, sum(sales2) sales2 from v_daily";
						} else {
							String arr[] = getMetricQuery(metric, groupby, filter, likeFilter);
							title = arr[0];
							subtitle = arr[1];
							ytitle = arr[2];
							query = "select time, cost_type, " + arr[3] + " from v_daily";
						}
					}
					if(filter.length()>0) {
						query += " where " + filter.toString().trim().replace("\t","and");
					}
					if(likeFilter.length()>0) {
						if(query.indexOf(" where ")>0) {
							query += " and " + likeFilter.toString().trim().replace("\t","and");
						} else {
							query += " where " + likeFilter.toString().trim().replace("\t","and");
						}
					}
					query += " group by time, cost_type";
					logger.info("Query : " + query);
					long stime = System.currentTimeMillis();
					ResultSet rs = stmt.executeQuery(query);
					logger.info("Elapsed time for executing query : " + (System.currentTimeMillis()-stime)/1000.0 + "s");
					long pointStart = 0L;
					long pointEnd = 0L;
					while(rs.next()) {
						String k1 = Metadata.costTypes.get(rs.getString("cost_type"));
						if(null==k1) {
							continue;
						}
						k1 += " (" + rs.getString("cost_type") + ")";
						Timestamp t = rs.getTimestamp("time");
						long tLong = t.getTime();
						if(pointStart==0L) {
							pointStart = tLong;
						}
						if(pointEnd<tLong) {
							pointEnd = tLong;
						}
						if(null==metric) {
							String rowKey = k1+", "+t.toString();
							osw.append(rowKey);
							osw.append(", ");
							osw.append(rs.getString("requests"));
							osw.append(", ");
							osw.append(rs.getString("impressions"));
							osw.append(", ");
							osw.append(rs.getString("clicks"));
							osw.append(", ");
							osw.append(rs.getString("conversions1"));
							osw.append(", ");
							osw.append(rs.getString("conversions2"));
							osw.append(", ");
							osw.append(rs.getString("revenue"));
							osw.append(", ");
							osw.append(rs.getString("cost1"));
							osw.append(", ");
							osw.append(rs.getString("cost2"));
							osw.append(", ");
							osw.append(rs.getString("sales1"));
							osw.append(", ");
							osw.append(rs.getString("sales2"));
							osw.append(lineSeparator);
						} else {
							dataset.setValue(rs.getDouble("value"), k1, String.valueOf(tLong));
						}
					}
					rs.close();
					if(null!=dataset) {
						if(fromView) {
							createHtml(osw, dataset, title, subtitle, ytitle, 3600000L, pointStart, pointEnd, orderby, topn, basis);
						} else {
							createHtml(osw, dataset, title, subtitle, ytitle, oneDay, pointStart, pointEnd, orderby, topn, basis);
						}
					}
				} else if(groupby.equals("Is-Mobile")) {
					String title = null;
					String subtitle = null;
					String ytitle = null;
					String query = null;
					boolean fromView = (filterSum==0||(filterSum==1&&isMobileFilter==1));
					if(fromView) {
						if(null==metric) {
							query = "select time, is_mobile, sum(requests) requests, sum(impressions) impressions, sum(clicks) clicks, sum(conversions1) conversions1, sum(conversions2) conversions2, sum(revenue) revenue, sum(cost1) cost1, sum(cost2) cost2, sum(sales1) sales1, sum(sales2) sales2 from v_hourly_by_is_mobile";
						} else {
							String arr[] = getMetricQuery(metric, groupby, filter, likeFilter);
							title = arr[0];
							subtitle = arr[1];
							ytitle = arr[2];
							if(arr[3].indexOf("count(distinct")>=0) {
								fromView = false;
								query = "select time, is_mobile, " + arr[3] + " from v_daily";
							} else {
								query = "select time, is_mobile, " + arr[3] + " from v_hourly_by_is_mobile";
							}
							
						}
					} else {
						if(null==metric) {
							query = "select time, is_mobile, sum(requests) requests, sum(impressions) impressions, sum(clicks) clicks, sum(conversions1) conversions1, sum(conversions2) conversions2, sum(revenue) revenue, sum(cost1) cost1, sum(cost2) cost2, sum(sales1) sales1, sum(sales2) sales2 from v_daily";
						} else {
							String arr[] = getMetricQuery(metric, groupby, filter, likeFilter);
							title = arr[0];
							subtitle = arr[1];
							ytitle = arr[2];
							query = "select time, is_mobile, " + arr[3] + " from v_daily";
						}
					}
					if(filter.length()>0) {
						query += " where " + filter.toString().trim().replace("\t","and");
					}
					if(likeFilter.length()>0) {
						if(query.indexOf(" where ")>0) {
							query += " and " + likeFilter.toString().trim().replace("\t","and");
						} else {
							query += " where " + likeFilter.toString().trim().replace("\t","and");
						}
					}
					query += " group by time, is_mobile";
					logger.info("Query : " + query);
					long stime = System.currentTimeMillis();
					ResultSet rs = stmt.executeQuery(query);
					logger.info("Elapsed time for executing query : " + (System.currentTimeMillis()-stime)/1000.0 + "s");
					long pointStart = 0L;
					long pointEnd = 0L;
					while(rs.next()) {
						String k1 = Metadata.isMobiles.get(rs.getString("is_mobile"));
						if(null==k1) {
							continue;
						}
						k1 += " (" + rs.getString("is_mobile") + ")";
						Timestamp t = rs.getTimestamp("time");
						long tLong = t.getTime();
						if(pointStart==0L) {
							pointStart = tLong;
						}
						if(pointEnd<tLong) {
							pointEnd = tLong;
						}
						if(null==metric) {
							String rowKey = k1+", "+t.toString();
							osw.append(rowKey);
							osw.append(", ");
							osw.append(rs.getString("requests"));
							osw.append(", ");
							osw.append(rs.getString("impressions"));
							osw.append(", ");
							osw.append(rs.getString("clicks"));
							osw.append(", ");
							osw.append(rs.getString("conversions1"));
							osw.append(", ");
							osw.append(rs.getString("conversions2"));
							osw.append(", ");
							osw.append(rs.getString("revenue"));
							osw.append(", ");
							osw.append(rs.getString("cost1"));
							osw.append(", ");
							osw.append(rs.getString("cost2"));
							osw.append(", ");
							osw.append(rs.getString("sales1"));
							osw.append(", ");
							osw.append(rs.getString("sales2"));
							osw.append(lineSeparator);
						} else {
							dataset.setValue(rs.getDouble("value"), k1, String.valueOf(tLong));
						}
					}
					rs.close();
					if(null!=dataset) {
						if(fromView) {
							createHtml(osw, dataset, title, subtitle, ytitle, 3600000L, pointStart, pointEnd, orderby, topn, basis);
						} else {
							createHtml(osw, dataset, title, subtitle, ytitle, oneDay, pointStart, pointEnd, orderby, topn, basis);
						}
					}
				} else if(groupby.equals("Campaign-Type")) {
					String title = null;
					String subtitle = null;
					String ytitle = null;
					String query = null;
					boolean fromView = (filterSum==0||(filterSum==1&&ctypeFilter==1));
					if(fromView) {
						if(null==metric) {
							query = "select time, ctype, sum(requests) requests, sum(impressions) impressions, sum(clicks) clicks, sum(conversions1) conversions1, sum(conversions2) conversions2, sum(revenue) revenue, sum(cost1) cost1, sum(cost2) cost2, sum(sales1) sales1, sum(sales2) sales2 from v_hourly_by_campaign_type";
						} else {
							String arr[] = getMetricQuery(metric, groupby, filter, likeFilter);
							title = arr[0];
							subtitle = arr[1];
							ytitle = arr[2];
							if(arr[3].indexOf("count(distinct")>=0) {
								fromView = false;
								query = "select time, ctype, " + arr[3] + " from v_daily";
							} else {
								query = "select time, ctype, " + arr[3] + " from v_hourly_by_campaign_type";
							}
						}
					} else {
						if(null==metric) {
							query = "select time, ctype, sum(requests) requests, sum(impressions) impressions, sum(clicks) clicks, sum(conversions1) conversions1, sum(conversions2) conversions2, sum(revenue) revenue, sum(cost1) cost1, sum(cost2) cost2, sum(sales1) sales1, sum(sales2) sales2 from v_daily";
						} else {
							String arr[] = getMetricQuery(metric, groupby, filter, likeFilter);
							title = arr[0];
							subtitle = arr[1];
							ytitle = arr[2];
							query = "select time, ctype, " + arr[3] + " from v_daily";
						}
					}
					if(filter.length()>0) {
						query += " where " + filter.toString().trim().replace("\t","and");
					}
					if(likeFilter.length()>0) {
						if(query.indexOf(" where ")>0) {
							query += " and " + likeFilter.toString().trim().replace("\t","and");
						} else {
							query += " where " + likeFilter.toString().trim().replace("\t","and");
						}
					}
					query += " group by time, ctype";
					logger.info("Query : " + query);
					long stime = System.currentTimeMillis();
					ResultSet rs = stmt.executeQuery(query);
					logger.info("Elapsed time for executing query : " + (System.currentTimeMillis()-stime)/1000.0 + "s");
					long pointStart = 0L;
					long pointEnd = 0L;
					while(rs.next()) {
						String k1 = Metadata.ctypes.get(rs.getString("ctype"));
						if(null==k1) {
							continue;
						}
						k1 += " (" + rs.getString("ctype") + ")";
						Timestamp t = rs.getTimestamp("time");
						long tLong = t.getTime();
						if(pointStart==0L) {
							pointStart = tLong;
						}
						if(pointEnd<tLong) {
							pointEnd = tLong;
						}
						if(null==metric) {
							String rowKey = k1+", "+t.toString();
							osw.append(rowKey);
							osw.append(", ");
							osw.append(rs.getString("requests"));
							osw.append(", ");
							osw.append(rs.getString("impressions"));
							osw.append(", ");
							osw.append(rs.getString("clicks"));
							osw.append(", ");
							osw.append(rs.getString("conversions1"));
							osw.append(", ");
							osw.append(rs.getString("conversions2"));
							osw.append(", ");
							osw.append(rs.getString("revenue"));
							osw.append(", ");
							osw.append(rs.getString("cost1"));
							osw.append(", ");
							osw.append(rs.getString("cost2"));
							osw.append(", ");
							osw.append(rs.getString("sales1"));
							osw.append(", ");
							osw.append(rs.getString("sales2"));
							osw.append(lineSeparator);
						} else {
							dataset.setValue(rs.getDouble("value"), k1, String.valueOf(tLong));
						}
					}
					rs.close();
					if(null!=dataset) {
						if(fromView) {
							createHtml(osw, dataset, title, subtitle, ytitle, 3600000L, pointStart, pointEnd, orderby, topn, basis);
						} else {
							createHtml(osw, dataset, title, subtitle, ytitle, oneDay, pointStart, pointEnd, orderby, topn, basis);
						}
					}
				} else if(groupby.equals("Zone")) {
					String title = null;
					String subtitle = null;
					String ytitle = null;
					String query = null;
					boolean fromView = (filterSum==0||(filterSum==1&&zoneFilter==1));
					if(fromView) {
						if(null==metric) {
							query = "select time, zoneid, sum(requests) requests, sum(impressions) impressions, sum(clicks) clicks, sum(conversions1) conversions1, sum(conversions2) conversions2, sum(revenue) revenue, sum(cost1) cost1, sum(cost2) cost2, sum(sales1) sales1, sum(sales2) sales2 from v_hourly_by_zone";
						} else {
							String arr[] = getMetricQuery(metric, groupby, filter, likeFilter);
							title = arr[0];
							subtitle = arr[1];
							ytitle = arr[2];
							if(arr[3].indexOf("count(distinct")>=0) {
								fromView = false;
								query = "select time, zoneid, " + arr[3] + " from v_daily";
							} else {
								query = "select time, zoneid, " + arr[3] + " from v_hourly_by_zone";
							}
						}
					} else {
						if(null==metric) {
							query = "select time, zoneid, sum(requests) requests, sum(impressions) impressions, sum(clicks) clicks, sum(conversions1) conversions1, sum(conversions2) conversions2, sum(revenue) revenue, sum(cost1) cost1, sum(cost2) cost2, sum(sales1) sales1, sum(sales2) sales2 from v_daily";
						} else {
							String arr[] = getMetricQuery(metric, groupby, filter, likeFilter);
							title = arr[0];
							subtitle = arr[1];
							ytitle = arr[2];
							query = "select time, zoneid, " + arr[3] + " from v_daily";
						}
					}
					if(filter.length()>0) {
						query += " where " + filter.toString().trim().replace("\t","and");
					}
					if(likeFilter.length()>0) {
						if(query.indexOf(" where ")>0) {
							query += " and " + likeFilter.toString().trim().replace("\t","and");
						} else {
							query += " where " + likeFilter.toString().trim().replace("\t","and");
						}
					}
					query += " group by time, zoneid";
					logger.info("Query : " + query);
					long stime = System.currentTimeMillis();
					ResultSet rs = stmt.executeQuery(query);
					logger.info("Elapsed time for executing query : " + (System.currentTimeMillis()-stime)/1000.0 + "s");
					long pointStart = 0L;
					long pointEnd = 0L;
					while(rs.next()) {
						String k1 = Metadata.zones.get(rs.getString("zoneid"));
						if(null==k1) {
							continue;
						}
						k1 += " (" + rs.getString("zoneid") + ")";
						Timestamp t = rs.getTimestamp("time");
						long tLong = t.getTime();
						if(pointStart==0L) {
							pointStart = tLong;
						}
						if(pointEnd<tLong) {
							pointEnd = tLong;
						}
						if(null==metric) {
							String rowKey = k1+", "+t.toString();
							osw.append(rowKey);
							osw.append(", ");
							osw.append(rs.getString("requests"));
							osw.append(", ");
							osw.append(rs.getString("impressions"));
							osw.append(", ");
							osw.append(rs.getString("clicks"));
							osw.append(", ");
							osw.append(rs.getString("conversions1"));
							osw.append(", ");
							osw.append(rs.getString("conversions2"));
							osw.append(", ");
							osw.append(rs.getString("revenue"));
							osw.append(", ");
							osw.append(rs.getString("cost1"));
							osw.append(", ");
							osw.append(rs.getString("cost2"));
							osw.append(", ");
							osw.append(rs.getString("sales1"));
							osw.append(", ");
							osw.append(rs.getString("sales2"));
							osw.append(lineSeparator);
						} else {
							double d = rs.getDouble("value");
							dataset.setValue(d, k1, String.valueOf(tLong));
						}
					}
					rs.close();
					if(null!=dataset) {
						if(fromView) {
							createHtml(osw, dataset, title, subtitle, ytitle, 3600000L, pointStart, pointEnd, orderby, topn, basis);
						} else {
							createHtml(osw, dataset, title, subtitle, ytitle, oneDay, pointStart, pointEnd, orderby, topn, basis);
						}
					}
				} else if(groupby.equals("Client")) {
					String title = null;
					String subtitle = null;
					String ytitle = null;
					String query = null;
					boolean fromView = (filterSum==0||(filterSum==1&&clientFilter==1));
					if(fromView) {
						if(null==metric) {
							query = "select time, clientid, sum(requests) requests, sum(impressions) impressions, sum(clicks) clicks, sum(conversions1) conversions1, sum(conversions2) conversions2, sum(revenue) revenue, sum(cost1) cost1, sum(cost2) cost2, sum(sales1) sales1, sum(sales2) sales2 from v_hourly_by_client";
						} else {
							String arr[] = getMetricQuery(metric, groupby, filter, likeFilter);
							title = arr[0];
							subtitle = arr[1];
							ytitle = arr[2];
							if(arr[3].indexOf("count(distinct")>=0) {
								fromView = false;
								query = "select time, clientid, " + arr[3] + " from v_daily";
							} else {
								query = "select time, clientid, " + arr[3] + " from v_hourly_by_client";
							}
						}
					} else {
						if(null==metric) {
							query = "select time, clientid, sum(requests) requests, sum(impressions) impressions, sum(clicks) clicks, sum(conversions1) conversions1, sum(conversions2) conversions2, sum(revenue) revenue, sum(cost1) cost1, sum(cost2) cost2, sum(sales1) sales1, sum(sales2) sales2 from v_daily";
						} else {
							String arr[] = getMetricQuery(metric, groupby, filter, likeFilter);
							title = arr[0];
							subtitle = arr[1];
							ytitle = arr[2];
							query = "select time, clientid, " + arr[3] + " from v_daily";
						}
					}
					if(filter.length()>0) {
						query += " where " + filter.toString().trim().replace("\t","and");
					}
					if(likeFilter.length()>0) {
						if(query.indexOf(" where ")>0) {
							query += " and " + likeFilter.toString().trim().replace("\t","and");
						} else {
							query += " where " + likeFilter.toString().trim().replace("\t","and");
						}
					}
					query += " group by time, clientid";
					logger.info("Query : " + query);
					long stime = System.currentTimeMillis();
					ResultSet rs = stmt.executeQuery(query);
					logger.info("Elapsed time for executing query : " + (System.currentTimeMillis()-stime)/1000.0 + "s");
					long pointStart = 0L;
					long pointEnd = 0L;
					while(rs.next()) {
						String k1 = Metadata.clients.get(rs.getString("clientid"));
						if(null==k1) {
							continue;
						}
						k1 += " (" + rs.getString("clientid") + ")";
						Timestamp t = rs.getTimestamp("time");
						long tLong = t.getTime();
						if(pointStart==0L) {
							pointStart = tLong;
						}
						if(pointEnd<tLong) {
							pointEnd = tLong;
						}
						if(null==metric) {
							String rowKey = k1+", "+t.toString();
							osw.append(rowKey);
							osw.append(", ");
							osw.append(rs.getString("requests"));
							osw.append(", ");
							osw.append(rs.getString("impressions"));
							osw.append(", ");
							osw.append(rs.getString("clicks"));
							osw.append(", ");
							osw.append(rs.getString("conversions1"));
							osw.append(", ");
							osw.append(rs.getString("conversions2"));
							osw.append(", ");
							osw.append(rs.getString("revenue"));
							osw.append(", ");
							osw.append(rs.getString("cost1"));
							osw.append(", ");
							osw.append(rs.getString("cost2"));
							osw.append(", ");
							osw.append(rs.getString("sales1"));
							osw.append(", ");
							osw.append(rs.getString("sales2"));
							osw.append(lineSeparator);
						} else {
							double d = rs.getDouble("value");
							dataset.setValue(d, k1, String.valueOf(tLong));
						}
					}
					rs.close();
					if(null!=dataset) {
						if(fromView) {
							createHtml(osw, dataset, title, subtitle, ytitle, 3600000L, pointStart, pointEnd, orderby, topn, basis);
						} else {
							createHtml(osw, dataset, title, subtitle, ytitle, oneDay, pointStart, pointEnd, orderby, topn, basis);
						}
					}
				} else if(groupby.equals("Client-Campaign")) {
					String title = null;
					String subtitle = null;
					String ytitle = null;
					String query = null;
					boolean fromView = (filterSum==0||(filterSum==1&&clientFilter==1)||(filterSum==1&&campaignFilter==1)||(filterSum==2&&campaignFilter==1&&clientFilter==1));
					if(fromView) {
						if(null==metric) {
							query = "select time, clientid, campaignid, sum(requests) requests, sum(impressions) impressions, sum(clicks) clicks, sum(conversions1) conversions1, sum(conversions2) conversions2, sum(revenue) revenue, sum(cost1) cost1, sum(cost2) cost2, sum(sales1) sales1, sum(sales2) sales2 from v_hourly_by_campaign";
						} else {
							String arr[] = getMetricQuery(metric, groupby, filter, likeFilter);
							title = arr[0];
							subtitle = arr[1];
							ytitle = arr[2];
							if(arr[3].indexOf("count(distinct")>=0) {
								fromView = false;
								query = "select time, clientid, campaignid, " + arr[3] + " from v_daily";
							} else {
								query = "select time, clientid, campaignid, " + arr[3] + " from v_hourly_by_campaign";
							}
						}
					} else {
						if(null==metric) {
							query = "select time, clientid, campaignid, sum(requests) requests, sum(impressions) impressions, sum(clicks) clicks, sum(conversions1) conversions1, sum(conversions2) conversions2, sum(revenue) revenue, sum(cost1) cost1, sum(cost2) cost2, sum(sales1) sales1, sum(sales2) sales2 from v_daily";
						} else {
							String arr[] = getMetricQuery(metric, groupby, filter, likeFilter);
							title = arr[0];
							subtitle = arr[1];
							ytitle = arr[2];
							query = "select time, clientid, campaignid, " + arr[3] + " from v_daily";
						}
					}
					if(filter.length()>0) {
						query += " where " + filter.toString().trim().replace("\t","and");
					}
					if(likeFilter.length()>0) {
						if(query.indexOf(" where ")>0) {
							query += " and " + likeFilter.toString().trim().replace("\t","and");
						} else {
							query += " where " + likeFilter.toString().trim().replace("\t","and");
						}
					}
					query += " group by time, clientid, campaignid";
					logger.info("Query : " + query);
					long stime = System.currentTimeMillis();
					ResultSet rs = stmt.executeQuery(query);
					logger.info("Elapsed time for executing query : " + (System.currentTimeMillis()-stime)/1000.0 + "s");
					long pointStart = 0L;
					long pointEnd = 0L;
					while(rs.next()) {
						String c1 = Metadata.clients.get(rs.getString("clientid"));
						String c2 = Metadata.campaigns.get(rs.getString("campaignid"));
						if(null==c1||null==c2) {
							continue;
						}
						String k1 = c1 + " ("+rs.getString("clientid")+") - " + c2 + " ("+rs.getString("campaignid")+")";
						Timestamp t = rs.getTimestamp("time");
						long tLong = t.getTime();
						if(pointStart==0L) {
							pointStart = tLong;
						}
						if(pointEnd<tLong) {
							pointEnd = tLong;
						}
						if(null==metric) {
							String rowKey = k1+", "+t.toString();
							osw.append(rowKey);
							osw.append(", ");
							osw.append(rs.getString("requests"));
							osw.append(", ");
							osw.append(rs.getString("impressions"));
							osw.append(", ");
							osw.append(rs.getString("clicks"));
							osw.append(", ");
							osw.append(rs.getString("conversions1"));
							osw.append(", ");
							osw.append(rs.getString("conversions2"));
							osw.append(", ");
							osw.append(rs.getString("revenue"));
							osw.append(", ");
							osw.append(rs.getString("cost1"));
							osw.append(", ");
							osw.append(rs.getString("cost2"));
							osw.append(", ");
							osw.append(rs.getString("sales1"));
							osw.append(", ");
							osw.append(rs.getString("sales2"));
							osw.append(lineSeparator);
						} else {
							double d = rs.getDouble("value");
							dataset.setValue(d, k1, String.valueOf(tLong));
						}
					}
					rs.close();
					if(null!=dataset) {
						if(fromView) {
							createHtml(osw, dataset, title, subtitle, ytitle, 3600000L, pointStart, pointEnd, orderby, topn, basis);
						} else {
							createHtml(osw, dataset, title, subtitle, ytitle, oneDay, pointStart, pointEnd, orderby, topn, basis);
						}
					}
				} else if(groupby.equals("Client-Category")) {
					String title = null;
					String subtitle = null;
					String ytitle = null;
					String query = null;
					boolean fromView = (filterSum==0||(filterSum==1&&clientFilter==1));
					if(fromView) {
						if(null==metric) {
							query = "select time, category, sum(requests) requests, sum(impressions) impressions, sum(clicks) clicks, sum(conversions1) conversions1, sum(conversions2) conversions2, sum(revenue) revenue, sum(cost1) cost1, sum(cost2) cost2, sum(sales1) sales1, sum(sales2) sales2 from v_hourly_by_client a join clients b on (a.clientid=b.client)";
						} else {
							String arr[] = getMetricQuery(metric, groupby, filter, likeFilter);
							title = arr[0];
							subtitle = arr[1];
							ytitle = arr[2];
							if(arr[3].indexOf("count(distinct")>=0) {
								fromView = false;
								query = "select time, category, " + arr[3] + " from v_daily a join clients b on (a.clientid=b.client)";
							} else {
								query = "select time, category, " + arr[3] + " from v_hourly_by_client a join clients b on (a.clientid=b.client)";
							}
						}
					} else {
						if(null==metric) {
							query = "select time, category, sum(requests) requests, sum(impressions) impressions, sum(clicks) clicks, sum(conversions1) conversions1, sum(conversions2) conversions2, sum(revenue) revenue, sum(cost1) cost1, sum(cost2) cost2, sum(sales1) sales1, sum(sales2) sales2 from v_daily a join clients b on (a.clientid=b.client)";
						} else {
							String arr[] = getMetricQuery(metric, groupby, filter, likeFilter);
							title = arr[0];
							subtitle = arr[1];
							ytitle = arr[2];
							query = "select time, category, " + arr[3] + " from v_daily a join clients b on (a.clientid=b.client)";
						}
					}
					if(filter.length()>0) {
						query += " where " + filter.toString().trim().replace("\t","and");
					}
					if(likeFilter.length()>0) {
						if(query.indexOf(" where ")>0) {
							query += " and " + likeFilter.toString().trim().replace("\t","and");
						} else {
							query += " where " + likeFilter.toString().trim().replace("\t","and");
						}
					}
					query += " group by time, clientid";
					logger.info("Query : " + query);
					long stime = System.currentTimeMillis();
					ResultSet rs = stmt.executeQuery(query);
					logger.info("Elapsed time for executing query : " + (System.currentTimeMillis()-stime)/1000.0 + "s");
					long pointStart = 0L;
					long pointEnd = 0L;
					while(rs.next()) {
						String k1 = rs.getString("category");
						if(k1==null) {
							k1 = "None";
						}
						Timestamp t = rs.getTimestamp("time");
						long tLong = t.getTime();
						if(pointStart==0L) {
							pointStart = tLong;
						}
						if(pointEnd<tLong) {
							pointEnd = tLong;
						}
						if(null==metric) {
							String rowKey = k1+", "+t.toString();
							osw.append(rowKey);
							osw.append(", ");
							osw.append(rs.getString("requests"));
							osw.append(", ");
							osw.append(rs.getString("impressions"));
							osw.append(", ");
							osw.append(rs.getString("clicks"));
							osw.append(", ");
							osw.append(rs.getString("conversions1"));
							osw.append(", ");
							osw.append(rs.getString("conversions2"));
							osw.append(", ");
							osw.append(rs.getString("revenue"));
							osw.append(", ");
							osw.append(rs.getString("cost1"));
							osw.append(", ");
							osw.append(rs.getString("cost2"));
							osw.append(", ");
							osw.append(rs.getString("sales1"));
							osw.append(", ");
							osw.append(rs.getString("sales2"));
							osw.append(lineSeparator);
						} else {
							double d = rs.getDouble("value");
							dataset.setValue(d, k1, String.valueOf(tLong));
						}
					}
					rs.close();
					if(null!=dataset) {
						if(fromView) {
							createHtml(osw, dataset, title, subtitle, ytitle, 3600000L, pointStart, pointEnd, orderby, topn, basis);
						} else {
							createHtml(osw, dataset, title, subtitle, ytitle, oneDay, pointStart, pointEnd, orderby, topn, basis);
						}
					}
				} else {
					osw.append("Invalid condition!");
					osw.append(lineSeparator);
				}
			}
			stmt.close();
			conn.close();
			osw.close();
		} catch(Exception e) {
			e.printStackTrace();
			logger.log(Level.SEVERE, "Error!", e);
		}
	}
	
}//The end of the class 'Csv'
