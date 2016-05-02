import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

// При создании сессия с одинаковым ID создаётся как в сервлете,
// так и на стороне браузера в куках.
// Разные окна одного браузера будут видеть сессию.
// При закрытии браузера и открытии снова сессия на стороне
// сервлета уничтожается, то есть недействительна.
// При этом в куках браузера сессия сразу не удаляется.
// JSESSIONID одна для всех сервлетов на одном хосте.

// Сеанс (сессия) – соединение между клиентом и сервером, 
// устанавливаемое на определенное время, за которое клиент может 
// отправить на сервер сколько угодно запросов. Сеанс устанавливается
// непосредственно между клиентом и Web-сервером. Каждый клиент 
// устанавливает с сервером свой собственный сеанс.

public class DateServlet extends HttpServlet{
	private static final long serialVersionUID = 1126719112963933045L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		// берём текущую сессию. если её нет - создаём новую
		HttpSession session = request.getSession(true);

		response.setContentType("text/html");
		PrintWriter pw = response.getWriter();
		
		// если сессия только создана
		if (session.isNew()) {
			pw.print("Created new session with ID: " + session.getId() + "<br />");
		}

		// InactiveInterval по умолчанию равен 1800.
		// Сбиваем его до 5 секунд.
		// по истечении срока сессия удаляется в приложении Java
		// в куках браузера JSESSIONID будет висеть
		if (session.getMaxInactiveInterval()==1800) {
			session.setMaxInactiveInterval(10);
			pw.print("Session inactive interval changed from 1800 to " + session.getMaxInactiveInterval() + "<br />");
		}
		
		pw.println("<b>");
		
		Date date = (Date)session.getAttribute("date");
		if (date != null) {
			pw.print("Last access: " + date + "<br />");
		}
		
		date = new Date();
		session.setAttribute("date",  date);
		pw.println("Current date: " + date);
		
		pw.println("<form name=\"Form1\" method=\"post\" action=\"" + request.getRequestURI() + "\">");
		pw.println("<input type=\"hidden\" name=\"reset\" value=\"yes\">");
		pw.println("<input type=submit value=\"Finalize session\">");
		pw.close();
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// берём текущую сессию. если её нет - создаём новую
		HttpSession session = request.getSession(true);

		response.setContentType("text/html");
		PrintWriter pw = response.getWriter();
		
		String reset = request.getParameter("reset");
		
		pw.print("POST parameter: reset = " + reset + "<br />");
		
		// устраняем сессию. в куках она останется фигурировать,
		// но для Java приложения её не будет существовать.
		
		//Завершить сеанс можно методом invalidate(). Сеанс уничтожает все 
		// связи с объектами, и данные, сохраненные в старом сеансе, будут 
		// потеряны для всех приложений.
		if (reset.equals("yes")) {
			session.invalidate();
			pw.print("Session with ID: " + session.getId() + " invalidated.");
			return;
		}
		pw.close();
	}

}
