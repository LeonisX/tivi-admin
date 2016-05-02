import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class AddCookieServlet extends HttpServlet {
	private static final long serialVersionUID = -434825427834310134L;
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String name = request.getParameter("name");
		String data = request.getParameter("data");
		Cookie cookie = new Cookie(name, data);
		// просто экспериментирую с параметрами
		cookie.setComment("Comment for " + name);
		cookie.setDomain("localhost");
		cookie.setHttpOnly(false);
		cookie.setMaxAge(-1); //sec
		cookie.setPath("/");
		cookie.setSecure(false);
		cookie.setVersion(0);
		
		response.addCookie(cookie);
		response.setContentType("text/html");
		PrintWriter pw = response.getWriter();
		pw.println("<b>" + name + " has been set to");
		pw.println(data);
		pw.close();
	}
}
