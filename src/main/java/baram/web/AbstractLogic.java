package baram.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class AbstractLogic implements LogicInterface {

    private String path;
    protected enum HTTP_METHODS {DELETE,HEAD,GET,POST,PUT,TRACE};
    
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
