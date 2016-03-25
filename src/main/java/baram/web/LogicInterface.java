package baram.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface LogicInterface {
    public void process(HttpServletRequest req,HttpServletResponse resp);
}
