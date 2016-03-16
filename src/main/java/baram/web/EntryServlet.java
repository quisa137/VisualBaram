package baram.web;


import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.HashMap;


/**
 * 1. request URI 파싱
 * 2. 리소스의 종류에 따라 리소스 로딩 : Frame,Fragment,Module,Static
 *    - Frame : 데이터와 컴포넌트들을 로딩하기 전의 말 그대로의 껍데기 부분 디폴트는 여기로 간다. 
 *    /elk/ 뒤로 붙는 상대경로로 필요한 태그들이 프레임에 덧붙여진다. /만 오면 빈 프레임 파일만 보낸다. 
 *    - Fragment : 페이지가 클라이언트에서 렌더링 된 뒤에 추가 UI들이 필요할 때, 이상태가 된다. /ui/ 로 시작된다.
 *    - Module : 외부 정적리소스를 로딩해야 할 때 쓰인다. 
 *    정적리소스를 내/외부로 나누는 이유는 외부의 경우, 폴더구조가 내부의 경우와 달라 모듈별로 구분하기 위함이다  
 *    /module/으로 시작되며 확장자로 리소스의 종류를 파악하여 적절한 디렉토리에서 로딩한다.
 *    - Data : Logic 클래스에서 넘어온 데이터들 중 일부를 JSON으로 출럭한다. /api/ 로 시작한다. 
 * 3. 클라이언트에서는 로딩된 리소스와 컴포넌트들을 바탕으로 화면을 조립/조작한다. 이 역할을 맡을 JS 라이브러리는 React.js로 한다. 
 * 선택 이유는 비슷한 역할의 다른 라이브러리에 비해 쉽고 크리티컬한 데이터를 다루지 않기 때문이다. Angular나 Polymer는 학습난이도가 상당하다.
 * http://netil.github.io/slides/angularjs/index3.html#/28
 * 
 * 처음에는 비동기 서블릿이 빠를 줄 알고 써봤지만, 특수한 설정이나 여타의 환경들을 만들어 주어야 빠른듯하다.
 * 그냥 쓰면 엄청느리다. 
 * @author SGcom
 *
 */
public class EntryServlet  extends HttpServlet {
    private static final long serialVersionUID = -5491016571296829887L;
    public EntryServlet() {
        // TODO Auto-generated constructor stub
        super();
    }
    @Override
    protected void doGet(HttpServletRequest req,HttpServletResponse resp) throws IOException, ServletException {
//        final AsyncContext asyncContext = req.startAsync(req, resp);
//        asyncContext.start(new EntryServletInternal(asyncContext));
//        System.out.println("Current Time : " + new Date().getTime() +"\n Name : "+ Thread.currentThread().getName()+"\n reqURI : "+req.getRequestURI());
        
        EntryHttpServletRequestWrapper wrapped = new EntryHttpServletRequestWrapper(req);
        wrapped.setUri(req.getRequestURI());
        
        if(wrapped.getState().equals(EntryHttpServletRequestWrapper.State.DATA)){
            //TODO : Data에 해당하는 비즈니스 로직을 수행 후, 그 결과를 JSON 형식으로 반환하는 로직 작성
        }else{
            //서버 로직 없는 일반 파일들은 모두 기본 디스패처들이 처리하도록 넘긴다. 
            RequestDispatcher rd = getServletContext().getNamedDispatcher("default");
            rd.forward(wrapped, resp);
        }
    }
    @Override
    protected void doPost(HttpServletRequest req,HttpServletResponse resp) throws IOException, ServletException {
//        final AsyncContext asyncContext = req.startAsync(req, resp);
//        asyncContext.start(new EntryServletInternal(asyncContext));
    }
    private HashMap<String,?> executeBL() {
        return null;
    }
    @Override
    public void destroy() {
        // TODO Auto-generated method stub
        super.destroy();
    }
}
