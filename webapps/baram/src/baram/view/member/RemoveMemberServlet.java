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
import javax.servlet.http.HttpSession;
 




import baram.view.member.MemberDTO;
 
 
public class RemoveMemberServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
 
    public RemoveMemberServlet() {
        super();
    }
 
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
 
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
    	HttpSession session = request.getSession();
		String memId = (String) session.getAttribute("memId");
		String memPw = (String) session.getAttribute("memPw");
		String memName = (String) session.getAttribute("memName");
		String memRegNum = (String) session.getAttribute("memRegNum");
		String memGroupEvel = (String) session.getAttribute("memGroupEvel");
		 MemberDTO mem = (MemberDTO) session.getAttribute("login_session");
		 session.setAttribute("memId", memId);
			session.setAttribute("memPw", memPw);
			session.setAttribute("memName", memName);
			session.setAttribute("memRegNum", memRegNum);
			session.setAttribute("memGroupEvel", memGroupEvel);
		session.setAttribute("login_session", new MemberDTO(mem.getId(), mem.getPassword(), mem.getName(),mem.getEmail(), mem.getGroupEvel()));
		
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        if (mem != null) {
            String sql = "delete from member where id = ?";
 
            ServletContext context = getServletContext();
            Connection connection = null;
            PreparedStatement preparedStatement = null;
             
            try {
                connection = DriverManager.getConnection(
                		context.getInitParameter("baram.view.jdbc.member.url"),
    					context.getInitParameter("baram.view.jdbc.userName"),
    					context.getInitParameter("baram.view.jdbc.password"));
                 
                preparedStatement = connection.prepareStatement(sql);
                 
                preparedStatement.setString(1, mem.getId());
                if(preparedStatement.executeUpdate()==1){
                    session.invalidate();
                    
                    out.println("<script>");
                	out.println("alert('회원정보가 삭제 되었습니다.');");
          		  	out.println("location.href='servletMemberIndex.html'");
          		  	out.println("</script>");
                   
                }
                 
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                out.println("<script>");
            	out.println("alert('회원정보가 삭제되지 않았습니다."+e.getMessage()+"');");
      		 // 	out.println("location.href='servletMemberIndex.html'");
      		  	out.println("</script>");
                //request.setAttribute("error_message", e.getMessage());
                 
             //   dispatcher = request.getRequestDispatcher("./res/error.jsp");
               // dispatcher.forward(request, response);
            } finally {
                if(preparedStatement != null){
                    try {
                        preparedStatement.close();
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                if(connection != null){
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
 
        } else {
        	 out.println("<script>");
         	out.println("alert('회원정보가 삭제 실패 로그인을 먼저 해주세요');");
   		 out.println("location.href='servletMemberIndex.html'");
   		  	out.println("</script>");
        //    request.setAttribute("login_failure_message",
             //       "탈퇴 실패\n 로그인을 먼저 해주세요");
      //      dispatcher = request.getRequestDispatcher("./login_form.jsp");
         //   dispatcher.forward(request, response);
        }
    }
 
}*/