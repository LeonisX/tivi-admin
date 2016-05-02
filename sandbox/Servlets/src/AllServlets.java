import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

public class AllServlets extends HttpServlet {
	private static final long serialVersionUID = 123432423L;
	private PrintWriter out = null;
	private String url = null;
	
@Override
   public void doGet(HttpServletRequest request, HttpServletResponse response)
		throws IOException, ServletException {
 
      response.setContentType("text/html; charset=UTF8");
      out = response.getWriter();
      String serverPath = request.getServletPath();
      url = request.getContextPath() + serverPath.substring(0, serverPath.lastIndexOf('/') + 1);
      
      try {
         out.println("<html>");
         out.println("<head><title>Список всех доступных сервлетов</title></head>");
         out.println("<body>");

         
         out.println("<table width=\"100%\" cellspacing=\"5\"><tr valign=\"top\"><td>");
         out.println("<h2><b>Сервлеты:</b></h2>");
         
         out.println("<p><b>Страницы:</b></p>");
         writePage("AddCookie", "Добавление куки");
         writePage("ColorGet", "Передача цвета как параметр GET");
         writePage("ColorPost", "Передача цвета как параметр POST");
         writePage("UploadFile", "Предача файла на сервер (тоже cos.jar???");
         writePage("MultiUploadFile", "Множественная загрузка файлов на сервер (com.oreilly.servlet.multipart)");
         writePage("PostParameters", "передача параметров методом POST");


         out.println("<p><b>Сервлеты:</b></p>");
         
         writeServlet("AllServlets", "Страница по умолчанию. Перечислены все сервлеты и JSP");
         writeServlet("DateServlet", "Работа с сессией - старт, удаление");
         writeServlet("DBServlet", "Обращение к БД. Вычитка параметров полей, данных");
         writeServlet("EmailServlet", "JavaMail. ?to=tv-games@mail.ru&from=tv@ya.ru&subject=Заголовок&emailContent=\"Текст письма\". См. ещё SendEmail");
         writeServlet("GetCookieServlet", "Выводит все куки");
         writeServlet("HelloServlet", "Работа с локалями, локализациями");
         writeServlet("LocaleDisplay", "Вывод списка локалей - главной и остальных");
         writeServlet("LoginServlet", "Логин JAAS. ?userName=Leonis&password=test");
         writeServlet("MyServlet", "Характеристики и параметры запроса, заголовки, контекст сервера. Фильтр автоматически добавляет параметр запроса");
         writeServlet("PostServlet", "Передача параметров методом POST (Commons.HttpClient)");
         writeServlet("ModuleServlet", "Включает в себя файл JavaScript для проверки вводимых данных");
         writeServlet("FormServlet", "Тоже JS, проверяет все поля оптом");
         writeServlet("SendFileServlet", "Передача файла клиенту через сервлет, 2 варианта");
         writeServlet("ResourceServlet", "Вывод /WEB-INF/web.xml (как ресурс)");
         
         out.println("</td><td>");
         
         out.println("<p><b>JSP:</b></p>");
         
         writeJSP("clock","Комментарии &lt;%-- ... --%&gt;, объявления &lt;%= ... %&gt;, выражения &lt;%! ... %&gt;");
         writeJSP("welcome","Скриптлеты &lt;% ... %&gt; Ввод имени, вывод");
         writeJSP("include","Стандартное действие &lt;jsp:include ... /&gt;");
         writeJSP("forward","Стандартные действия &lt;jsp:forward ... /&gt;, &lt;jsp:param ... /&gt;");
         writeJSP("adrotator","Стандартные действия &lt;jsp:useBean ... /&gt; ?firstName=Leonis&lastName=Silver&email=tv-games@mail.ru");
         writeJSP("tags","Пользовательские тэги. Запускать с ?name=имя");
         writeJSP("validCheck","Проверка полученных данных. Запускать с ?name=Leonis&password=pass&email=tv-games@mail.ru");
         writeJSP("error","Вываливает ошибку IOException. Она перехватывается");
         writeJSP("cookieSet","Устанавливает куки, читает их, время работы сессии");
         writeJSP("importMod","Импортирует JS для проверки введённых данных");
         writeJSP("validate","Импортирует JS для проверки введённых данных");
         writeJSP("headers","Выводит заголовки страницы");
         writeJSP("coreTags","Проверяет текущее время, выводит все временные зоны");
         
   
//       Map<String, ? extends ServletRegistration> servletRegistrations = request.getServletContext().getServletRegistrations();
//       
//       for (String servlet: servletRegistrations.keySet()) {
//      	 out.println("<a href=\"" + url + servlet + "\">" + servlet + "</a><br />");
//       }

         out.println("</body></html>");
      } finally {
         out.close();  // Всегда закрывать Writer
      }
   }

	private void writeServlet(String servlet, String text) {
		if (out == null) return;
		if (url == null) return;
		out.println("<a href=\"" + url + servlet + "\">" + servlet + "</a> <a href=\"" + url + "src/" + servlet + ".java\">код</a> - " + text + "<br />");
	}
	
	private void writePage(String page, String text) {
		if (out == null) return;
		if (url == null) return;
		out.println("<a href=\"" + url + page + ".html\">" + page + ".html</a> - " + text + "<br />");
	}	

	private void writeJSP(String jsp, String text) {
		if (out == null) return;
		if (url == null) return;
		out.println("<a href=\"" + url + jsp + ".jsp\">" + jsp + "</a> <a href=\"" + url + "src/" + jsp + ".java\">код</a> - " + text + "<br />");
	}

}