import javax.servlet.*;
import javax.servlet.http.*;

public class FormServlet extends HttpServlet {
	private static final long serialVersionUID = -4827678476219211862L;

public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, java.io.IOException {
    
    response.setContentType("text/html; charset=utf-8");
    java.io.PrintWriter out = response.getWriter();
    out.println("<html><head>");
      
    RequestDispatcher dispatcher = request.getRequestDispatcher(
        "/js/validate.js");

    dispatcher.include(request, response);
      
    out.println("<title>Страница помощи</title></head><body>");
    out.println("<h2>Пожалуйста, заполните поля</h2>");
   
    out.println(
        "<form action =\"" + request.getContextPath() +
            "/\" onSubmit=\" return validate(this)\">");

    out.println("<table border=\"0\"><tr><td valign=\"top\">");
    out.println("Имя: </td>  <td valign=\"top\">");
    out.println("<input type=\"text\" name=\"username\" size=\"20\">");
    out.println("</td></tr><tr><td valign=\"top\">");
    out.println("email: </td>  <td valign=\"top\">");
    out.println("<input type=\"text\" name=\"email\" size=\"20\">");
    out.println("</td></tr><tr><td valign=\"top\">");

    out.println("<input type=\"submit\" value=\"Отправить\"></td></tr>");
    out.println("</table></form>");
    out.println("</body></html>");
    out.close();
     } //end doGet
}
