//Сервлет, включающий в себя файл JavaScript

import javax.servlet.*;
import javax.servlet.http.*;

public class ModuleServlet extends HttpServlet {
	private static final long serialVersionUID = 7665028791434640903L;

public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, java.io.IOException {
    
     response.setContentType("text/html; charset=UTF-8");
      java.io.PrintWriter out = response.getWriter();
      out.println("<html><head>");
      RequestDispatcher dispatcher = 
    		  request.getRequestDispatcher("/js/functions.js");
      dispatcher.include(request, response);
      
      out.println("<title>Формы клиента</title></head><body>");

      out.println("<h2>Введите своё имя и e-mail</h2>");
      out.println("<h4>Код JavaScript проверит на корректность вводимые данные.</h4>");
      out.println("<form action=\"./\" name=\"entryForm\" onSubmit=\" return CheckEmail(this.email.value, this.name.value)\">");
      out.println("<table border=\"0\"><tr><td valign=\"top\">");
      out.println("Имя и фамилия: </td>  <td valign=\"top\"><input type=\"text\" name=\"name\" size=\"20\"></td></tr>");
      out.println("<tr><td valign=\"top\">");
      out.println("Email: </td>  <td valign=\"top\"><input type=\"text\" name=\"email\" size=\"20\"></td>");
      out.println("<tr><td valign=\"top\"><input type=\"submit\" value=\"Submit\" ></td>");
      out.println("</tr></table></form>");
     
      out.println("</body></html>");
  
     } //end doGet
}
