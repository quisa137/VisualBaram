package baram.manager.monitor;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import org.json.JSONObject;


public class TailLogfile {
    public TailLogfile() {
    }
    private static final int MAX_LINE = 1000;
    public String getTailLines(String filePath,int seek) {
        File file = new File(filePath);
        RandomAccessFile RASeek = null;
        BufferedReader reader = null;
        String line = null;
        int lineCnt = 0;
        HashMap<String,Object> jsonData = new HashMap<String, Object>();
        
        StringBuffer sb = new StringBuffer();
        try {
            if(!file.exists()){
                //원래는 FileNotFoundException
                return "";
            }
            
            RASeek = new RandomAccessFile(file, "r");
            if(seek == 0){
                jsonData.put("offset", RASeek.length());
                jsonData.put("content", "");
                return (new JSONObject(jsonData)).toString();
            }else{
                RASeek.seek(seek);
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(RASeek.getFD()),"UTF-8"), 1024);
                boolean lineOpen = false;
                while((line = reader.readLine()) != null) {
                    if(lineOpen == false) {
                        lineOpen = (line.indexOf("ERROR") > -1);
                    }else{
                        lineOpen = (line.indexOf("INFO") == -1) && (line.indexOf("WARN") == -1) && (line.indexOf("DEBUG") == -1);
                    }
                    if(lineOpen){
                        lineCnt++;
                        sb.append(line).append("\n");
                    }
                    if(lineOpen == false && lineCnt >= MAX_LINE) {
                        break;
                    }
                }
                jsonData.put("offset", RASeek.length());
                jsonData.put("content", sb.toString());
                return (new JSONObject(jsonData)).toString();
            }
        } catch( java.io.IOException e ) {
            e.printStackTrace();
            return "";
        } finally {
            if (RASeek != null ){
                try {
                    RASeek.close();
                } catch (IOException e) {
                }
            }
            if(reader != null) {
                try { 
                    reader.close();
                } catch (IOException e) {
                }
            }
        }        
    }
}
