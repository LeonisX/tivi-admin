import java.io.IOException;
import java.io.PrintWriter;

import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//JAAS
//?userName=Leonis&password=test
//WebCallbackHandler
//DataSourceLoginModule

public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = -8431430892643244256L;

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// CallbackHandler получает имя и пароль из параметров запроса в URL;
		// Поэтому, ServletRequest (запрос) передаётся в конструктор класса CallbackHandler
		WebCallbackHandler webCallback = new WebCallbackHandler(request);
		LoginContext lContext = null;
		boolean loginSuccess = true;
		try {
			lContext = new LoginContext("WebLogin", webCallback);
			// этот метод выбрасывает исключение LoginException
			// если аутентификация завершилась отказом
			lContext.login();
		} catch (LoginException e) {
			loginSuccess = false;
		}
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<html><head><title>Thanks for logging in</title></head><body>");
		out.println("" + (loginSuccess ? "Logged in" : "Failed login"));
		out.println("</body></html>");
	}

}
