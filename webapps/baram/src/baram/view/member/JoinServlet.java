package baram.view.member;

import javax.servlet.http.HttpServlet;


import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import baram.view.User;
 
public class JoinServlet extends HttpServlet {
    
	private static final long serialVersionUID = 201510201543L;
 
    public JoinServlet() {
        super();
    }
    
    Logger logger =  Logger.getLogger(JoinServlet.class.getName());
    
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
    }
}
/*package baram.view.member;
 
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
 


import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
 


import baram.view.member.MemberDTO;
 
public class RegisterMemberServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
 
    public RegisterMemberServlet() {
        super();
    }
 
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
 
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
 
        // 요청 파라미터 조회
        ServletContext context = getServletContext();
 
        System.out.println(context.getInitParameter("url"));
        request.setCharacterEncoding("UTF-8");
 
        String id = request.getParameter("id");
        String pw = request.getParameter("password");
        String name = request.getParameter("name");
        String regNum = request.getParameter("regNum1");
        String groupEvel = request.getParameter("groupEvel");
 
        System.out.println(id + " " + pw + " " + name + " " + regNum);
 
        // Business Logic 처리
        // 1. Connection
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        String resUrl = null;
        RequestDispatcher dispatcher = null;
        try {
            connection = DriverManager.getConnection(
            		context.getInitParameter("baram.view.jdbc.member.url"),
					context.getInitParameter("baram.view.jdbc.userName"),
					context.getInitParameter("baram.view.jdbc.password"));
            // 2. PreparedStatement
            String sql = "insert into member values(?, ?, ?, ?, ?)";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, id);
            preparedStatement.setString(2, pw);
            preparedStatement.setString(3, name);
            preparedStatement.setString(4, regNum);
            preparedStatement.setString(5, groupEvel);
 
            // 3. SQL 문 전송(insert)
            preparedStatement.executeUpdate();
 
            // 응답 처리
            // 클라이언트로 부터 받은 정보를 출력한 뒤 가입처리 성공 메세지 응답.
 
            request.setAttribute("member", new MemberDTO(id, pw, name, regNum,
                    groupEvel));
          //  resUrl = "./res/register_success.jsp";
            
            out.println("<HTML>");
            out.println("<head>");
            out.println("<title>회원가입 완료");
            out.println("</title>");
            out.println("<script>");
         	out.println("alert('회원가입이 완료 되었습니다.');");
   		  	out.println("</script>");
            out.println("</head>");
    		out.println("<BODY>");
   		 out.println("가입정보는 다음과 같습니다.<br/>");
   		 out.println("id : "+id+"<br/>");
   		 out.println("password : "+pw+"<br/>");
   		 out.println("이름 : "+name+"<br/>");
   		 out.println("e-mail : "+regNum+"<br/>");
   		 out.println("groupEvel : "+groupEvel+"<br/>");
   		 out.println("<a href='servletMemberIndex.html'>메인화면으로 가기</a>");
    		out.println("</BODY>");
    		out.println("</HTML>");
            
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          // request.setAttribute("error_message", e.getMessage());
            //resUrl = "./res/error.jsp";
            out.println("<script>");
        	out.println("alert('회원가입 실패 이미 등록된 아이디 입니다.');");
  		  	out.println("location.href='register_form.html'");
  		  	out.println("</script>");
 
        } finally {
            // 4. close
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
 
      //  dispatcher = request.getRequestDispatcher(resUrl);
      //  dispatcher.forward(request, response);
 
    }
 
}*/