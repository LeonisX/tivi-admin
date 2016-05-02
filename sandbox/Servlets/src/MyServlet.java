import java.io.*;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.*;
import javax.servlet.http.*;

public class MyServlet extends HttpServlet {
	private static final long serialVersionUID = 123432423L;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		// установить MIME-type и кодировку ответа
		response.setContentType("text/html; charset=UTF8");
		PrintWriter out = response.getWriter();

		// Отправка веб-страницы
		try {
			out.println("<html>");
			out.println("<head><title>Servlet sample</title></head>");
			out.println("<body>");
			out.println("<h3>Параметры, получаемые из запроса:</h3>");
			
			out.println("request.getRequestURL(): <b>" + request.getRequestURL() + "</b><br />");
			out.println("request.getScheme(): <b>" + request.getScheme() + "</b><br />");
			out.println("request.getLocalName(): <b>" + request.getLocalName() + "</b><br />");
			out.println("request.getServerName(): <b>" + request.getServerName() + "</b><br />");
			out.println("request.getLocalAddr(): <b>" + request.getLocalAddr() + "</b><br />");
			out.println("request.getLocalPort(): <b>" + request.getLocalPort() + "</b><br />");
			out.println("request.getServerPort(): <b>" + request.getServerPort() + "</b><br />");
			out.println("request.getRequestURI(): <b>" + request.getRequestURI() + "</b><br />");
			out.println("request.getQueryString(): <b>" + request.getQueryString() + "</b><br />");
			out.println("request.getContextPath(): <b>" + request.getContextPath() + "</b><br />");
			out.println("request.getServletPath(): <b>" + request.getServletPath() + "</b><br />");
			out.println("<br />");
			out.println("request.getPathInfo(): <b>" + request.getPathInfo() + "</b><br />");
			out.println("request.getPathTranslated(): <b>" + request.getPathTranslated() + "</b><br />");
			out.println("request.getRemoteAddr(): <b>" + request.getRemoteAddr() + "</b><br />");
			out.println("request.getRemoteHost(): <b>" + request.getRemoteHost() + "</b><br />");
			out.println("request.getRemotePort(): <b>" + request.getRemotePort() + "</b><br />");
			out.println("<br />");
			out.println("request.getRequestedSessionId(): <b>" + request.getRequestedSessionId() + "</b><br />");
			out.println("request.getLocale(): <b>" + request.getLocale() + "</b><br />");
			out.println("request.getMethod(): <b>" + request.getMethod() + "</b><br />");
			out.println("request.getDispatcherType(): <b>" + request.getDispatcherType() + "</b><br />");
			out.println("<br />");
			out.println("request.getAuthType(): <b>" + request.getAuthType() + "</b><br />");
			out.println("request.getCharacterEncoding(): <b>" + request.getCharacterEncoding() + "</b><br />");
			out.println("request.getContentLength(): <b>" + request.getContentLength() + "</b><br />");
			out.println("request.getContentLengthLong(): <b>" + request.getContentLengthLong() + "</b><br />");
			out.println("request.getContentType(): <b>" + request.getContentType() + "</b><br />");
			
			// out.println("request.getParts(): <b>" + request.getParts()+"</b><br />");
			out.println("request.getRemoteUser(): <b>" + request.getRemoteUser() + "</b><br />");
			out.println("request.getUserPrincipal(): <b>" + request.getUserPrincipal() + "</b><br />");
			out.println("request.isAsyncStarted(): <b>" + request.isAsyncStarted() + "</b><br />");
			out.println("request.isAsyncSupported(): <b>" + request.isAsyncSupported() + "</b><br />");
			out.println("request.isRequestedSessionIdFromCookie(): <b>" + request.isRequestedSessionIdFromCookie() + "</b><br />");
			out.println("request.isRequestedSessionIdValid(): <b>" + request.isRequestedSessionIdValid() + "</b><br />");
			out.println("request.isSecure(): <b>" + request.isSecure() + "</b><br />");

			
			
			// request.isUserInRole(arg0)
			// так же можно получить параметры, атрибуты, сессию.
			// request.getParts()

			out.println("<h3>Заголовки (headers):</h3>");
			Enumeration<String> headerNames = request.getHeaderNames();
			while (headerNames.hasMoreElements()) {
				String header = (String) headerNames.nextElement();
				out.println("<b>" + header + "</b>: "
						+ request.getHeader(header) + "<br />");
			}

			out.println("<h3>Контекст сервлета:</h3>");
			ServletContext servletContext = request.getServletContext();
			out.println("servletContext.getServerInfo(): <b>" + servletContext.getServerInfo() + "</b><br />");
			out.println("servletContext.getVirtualServerName(): <b>" + servletContext.getVirtualServerName() + "</b><br />");
			out.println("servletContext.getMajorVersion(): <b>" + servletContext.getMajorVersion() + "</b><br />");
			out.println("servletContext.getMinorVersion(): <b>" + servletContext.getMinorVersion() + "</b><br />");
			out.println("servletContext.getDefaultSessionTrackingModes(): <b>" + servletContext.getDefaultSessionTrackingModes() + "</b><br />");
			out.println("servletContext.getEffectiveMajorVersion(): <b>" + servletContext.getEffectiveMajorVersion() + "</b><br />");
			out.println("servletContext.getEffectiveMinorVersion(): <b>" + servletContext.getEffectiveMinorVersion() + "</b><br />");
			out.println("servletContext.getEffectiveSessionTrackingModes(): <b>" + servletContext.getEffectiveSessionTrackingModes() + "</b><br />");
			out.println("<br />");
			out.println("servletContext.getContextPath(): <b>" + servletContext.getContextPath() + "</b><br />");
			out.println("servletContext.getServletContextName(): <b>" + servletContext.getServletContextName() + "</b><br />");
			
			out.println("<h3>SessionCookieConfig:</h3>");
			SessionCookieConfig scc = servletContext.getSessionCookieConfig();
			out.println("scc.getComment(): <b>" + scc.getComment()+ "</b><br />");
			out.println("scc.getMaxAge(): <b>" + scc.getMaxAge()+ "</b><br />");
			out.println("scc.getName(): <b>" + scc.getName()+ "</b><br />");
			out.println("scc.getPath(): <b>" + scc.getPath()+ "</b><br />");
			out.println("scc.isHttpOnly(): <b>" + scc.isHttpOnly()+ "</b><br />");
			out.println("scc.isSecure(): <b>" + scc.isSecure()+ "</b><br />");
			
			// так же можно получить атрибуты, параметры инициализации,
			// типы mime, ресурсы, сервлеты

			
			out.println("<h3>Конфигурация сервлета:</h3>");
			// получаем конфиги
			ServletConfig servletConfig = getServletConfig();

			out.println("<p><b>ServletName:</b> " + servletConfig.getServletName() + "</p>");

			out.println("<p><b>Servlet Init Parameters:</b></p>");
			Enumeration<String> initParameters = servletConfig
					.getInitParameterNames();
			while (initParameters.hasMoreElements()) {
				String parameter = (String) initParameters.nextElement();
				out.println("<br /><b>" + parameter + "</b>: "
						+ servletConfig.getInitParameter(parameter));
			}

			out.println("<p><b>Available servlets:</b></p>");
			Map<String, ? extends ServletRegistration> servletRegistrations = request
					.getServletContext().getServletRegistrations();
			String serverPath = request.getServletPath();
			String url = request.getContextPath()
					+ serverPath.substring(0, serverPath.lastIndexOf('/') + 1);
			for (String servlet : servletRegistrations.keySet()) {
				out.println("<br /><a href=\"" + url + servlet + "\">" + servlet + "</a>");
			}

			out.println("</body></html>");
		} finally {
			out.close(); // Всегда закрывать Writer
		}
	}
}