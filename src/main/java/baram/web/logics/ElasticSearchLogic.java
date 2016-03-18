package baram.web.logics;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import baram.web.AbstractLogic;

/**
 * 미리 설정된 ElasticSearch URL로 클라이언트에서 보내온 요청을 보내고 그 응답을
 * 클라이언트에 돌려준다. 다시말해, 응답을 리다이렉션하는 로직이다. 
 * @author SGcom
 *
 */
public class ElasticSearchLogic extends AbstractLogic {
    private final int BUFFER_SIZE = 1024;
    public ElasticSearchLogic(String path) {
        super(path);
    }

    @Override
    public void process(HttpServletRequest req,HttpServletResponse resp) {
        // TODO Auto-generated method stub
        try {
            final String DST_HOST="192.168.0.124",
                    DST_PORT="9200",
                    ABS_URL = "http://"+DST_HOST+":"+DST_PORT+this.getPath() +"?" + req.getQueryString();
            
            System.out.println(ABS_URL);
            HttpURLConnection conn = (HttpURLConnection)new URL(ABS_URL).openConnection();
            conn.setRequestMethod(req.getMethod());
            conn.setDoInput(true);
            conn.setDoOutput(true);
            
            Enumeration<String> i = req.getHeaderNames();
            
            //요청 헤더 쓰기
            while(i.hasMoreElements()){
                String key = i.nextElement();
                String value = req.getHeader(key);
                switch(key.toLowerCase()){
                case "origin":
                    value = req.getRemoteAddr(); 
                    break;
                case "host":
                    value = DST_HOST + ":" + DST_PORT;
                    break;
                }
                conn.setRequestProperty(key, value);
            }
            
            //요청 바디 쓰기
            if(req.getMethod() == "POST") {
                this.redirectStream(req.getInputStream(), conn.getOutputStream());
            }
            
            //요청 ElasticSearch로 보낸다.
            conn.connect();
            
            resp.setStatus(conn.getResponseCode());
            
            //응답 헤더 쓰기
            Map<String,List<String>> Headers = conn.getHeaderFields();
            Set<String> keys = Headers.keySet();
            for(String key:keys){
                resp.setHeader(key, conn.getHeaderField(key));
            }
            //응답 바디 쓰기
            if(conn.getResponseCode() == 200) {
                this.redirectStream(conn.getInputStream(), resp.getOutputStream());
            }else if(conn.getResponseCode()>=400){
                this.redirectStream(conn.getErrorStream(), resp.getOutputStream());
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    private void redirectStream(InputStream in,OutputStream out) throws IOException {
        byte[] by = new byte[BUFFER_SIZE];
        while(in.read(by)>-1){
            out.write(by);
        }
        out.flush();
    }
}
