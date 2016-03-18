package baram.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class AbstractLogic implements LogicInterface {

    private String path;
    
    public AbstractLogic(String path) {
        // TODO Auto-generated constructor stub
        this.path = path;
    }
    
    public String getPath(){
        return path;
    }

    @Override
    public abstract void process(HttpServletRequest req, HttpServletResponse resp);
}
