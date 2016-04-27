package baram.web;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Config {
    private Config() {
        readConfig();
    }
    private static Config instance = null;
    private static JSONObject config = null;
    private static void readConfig() {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(new File("conf/config.json")));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while((line=br.readLine())!=null){
                sb.append(line.trim());
            }
            br.close();
            config  = new JSONObject(sb.toString());
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
    public JSONObject getConfigObject(String key) {
        return config.getJSONObject(key.toLowerCase());
    }
    public JSONArray getConfigArray(String key) {
        return config.getJSONArray(key.toLowerCase());
    }
}
