package baram.web;

import java.util.Iterator;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class EntryHttpServletRequestWrapper extends HttpServletRequestWrapper {
    public enum State {
        FRAME,
        FRAGMENT,
        MODULE,
        STATIC,
        DATA;
    }
    
    private State currentState = null;
    
    public EntryHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
        path = null;
    }
    private String path = null;
    
    @Override
    public String getServletPath() {
        if(this.currentState.equals(State.MODULE)) {
            return super.getServletPath();
        }
        String[] fileNameArr = path.split("\\.");
        if((this.currentState.equals(State.STATIC) || this.currentState.equals(State.FRAGMENT))
                && fileNameArr != null 
                && fileNameArr.length > 1) {
            //.으로 나눈 배열의 마지막 값을 확장자로 본다.
            String ext = fileNameArr[fileNameArr.length-1];
            switch(ext) {
                case "js":
                case "css":
                    return "/src/" + ext;
                case "jsx":
                case "html":
                    return "/src/ui/" + ext;
                case "ico":
                case "jpg":
                case "png":
                case "gif":
                    return "/src/img";
            }
        }
        return super.getServletPath();
    }
    @Override
    public String getPathInfo(){
        return "/"+path;
    }
    public State getState(){
        return this.currentState;
    }
    public String getPath(){
        return this.path;
    }
    public void setUri(String uri){
        Vector<String> parsedUri = new Vector<>();
        String delimiter = "/";
        
        for(String part:uri.split(delimiter)) {
            if(!part.equals("")) {
                parsedUri.add(part);
            }
        }
        
        if(parsedUri.size() > 0){
            switch(parsedUri.get(0)) {
                case "modules":
                    this.currentState = State.MODULE;
                    break;
                case "ui":
                    this.currentState = State.FRAGMENT;
                    parsedUri.remove(0);
                    break;
                case "api":
                    this.currentState = State.DATA;
                    parsedUri.remove(0);
                    break;
                default:
                    this.currentState = State.STATIC;
                    parsedUri.remove(0);
                    break;
            }
        }else{
            this.currentState = State.FRAME;
        }

        //System.out.println("Parsed Uri : " + uri);
        //System.out.println("Parsed State : " + this.currentState);
        
        //상태를 제외한 나머지 값들을 경로로 간주한다. 
        if(parsedUri.size() > 0) {
            Iterator<String> it = parsedUri.iterator();
            StringBuffer sb = new StringBuffer();
            boolean isDelimiterAppend = false;
            
            while(it.hasNext()) {
                if(isDelimiterAppend) {
                    sb.append(delimiter);
                } else {
                    isDelimiterAppend = true;
                }
                sb.append(it.next());
            }
            path = sb.toString();
        } else if(this.currentState.equals(State.FRAME)) {
            path = "src/index.html";
        } else {
            path = "";
        }
        //System.out.println(Thread.currentThread().getName() + "FileName : " + path+"\n\n");
    }
}