package baram.web;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * JSON Config 파일을 읽어와 
 * @author SGcom
 *
 */
public class Config {
    private Config() {
        readConfig("web");
    }
    private static Config instance = null;
    private static ConcurrentHashMap<String, Object> configs = new ConcurrentHashMap<String, Object>();
    private static void readConfig(String filename) {
        BufferedReader br = null;
        try {
            if(!configs.containsKey(filename)) {
                br = new BufferedReader(new FileReader(new File("conf/" + filename + ".json")));
                StringBuffer sb = new StringBuffer();
                String line = "";
                while((line=br.readLine())!=null){
                    sb.append(line.trim());
                }
                br.close();
                String sourceText = sb.toString();
                if(sourceText.startsWith("{")) {
                    configs.put(filename, new JSONObject(sourceText));
                }else if(sourceText.startsWith("[")) {
                    configs.put(filename, new JSONArray(sourceText));
                }
                
            }
        } catch (JSONException | FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public static synchronized Config getInstance() {
        if(instance == null) {
            instance = new Config();
        }
        return instance;
    }
    public Object get(String filename,String configPath) throws JSONException {
        readConfig(filename);
        JSONObject config = this.getConfigObject(filename);
        String[] paths = configPath.split("\\.");
        Object obj = null;
        Pattern arrayPattern = Pattern.compile("(\\S+)\\[([0-9]+)\\]");
        Matcher m = null;
        for(String p:paths){
            m = arrayPattern.matcher(p);
            if(m.matches()){
                JSONArray jsonarray = config.getJSONArray(m.group(1));
                obj = jsonarray.get(Integer.parseInt(m.group(2)));
            }else{
                obj = config.get(p);
            }
            if(obj instanceof JSONObject){
                config = (JSONObject) obj;
            }else{
                return obj;
            }
        }
        return config;
    }
    public JSONObject getConfigObject(String filename) {
        readConfig(filename);
        Object obj = configs.get(filename);
        if(obj instanceof JSONObject) {
            return (JSONObject) obj;
        }
        throw new JSONException("Not Found filename");
    }
    public JSONObject getConfigObject(String filename,String key) {
        return this.getConfigObject(filename).getJSONObject(key);
    }
    
    public JSONArray getConfigArray(String filename) {
        readConfig(filename);
        Object obj = configs.get(filename);
        if(obj instanceof JSONArray) {
            return (JSONArray) obj;
        }
        throw new JSONException("Not Found filename");
    }
}
