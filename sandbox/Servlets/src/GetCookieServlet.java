import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class GetCookieServlet extends HttpServlet {
	private static final long serialVersionUID = -434825427834310134L;
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Cookie[] cookies = request.getCookies();
		if(cookies == null) return;
		PrintWriter pw = response.getWriter();
		for(int i = 0; i < cookies.length; i++) {
			pw.println("Name=" + cookies[i].getName());
			pw.println("Value=" + cookies[i].getValue());
			pw.println("Comment=" + cookies[i].getComment());
			pw.println("Domain=" + cookies[i].getDomain());
			pw.println("MaxAge=" + cookies[i].getMaxAge());
			pw.println("Path=" + cookies[i].getPath());
			pw.println("Secure=" + cookies[i].getSecure());
			pw.println("Version=" + cookies[i].getVersion());
			pw.println("isHttpOnly=" + cookies[i].isHttpOnly());
			pw.println();
		}
		pw.close();
	}
}
