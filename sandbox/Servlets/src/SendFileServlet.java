import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
// import java.net.URL;
// import java.net.URLConnection;

import javax.servlet.*;
import javax.servlet.http.*;

public class SendFileServlet extends HttpServlet {
	private static final long serialVersionUID = -6172636229074090211L;
	
public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	String fileName = "";
	String contentType = "";
    String url = getServletContext().getRealPath("/files");

	
	String parameter = (String) request.getParameter("fileNumber");

	if (parameter != null)
	switch (parameter) {
		case "1":
			fileName = "А. Ф. Лепехин - Программирование на языке Java - 2002 (older).pdf";
			contentType = "application/pdf";
			break;
		case "2":
			fileName = "Дмитрий Рамодин - Начинаем программировать на языке Java - 1996.doc";
			contentType = "application/msword";
			break;
		case "3":
			fileName = "Мартин Одерский. Введение в функциональное программирование на скале. Конспект лекций.html.maff";
			contentType = "application/zip";
			break;
		case "4":
			fileName = "007. АлисА - Трасса Е-95.mp3";
			contentType = "audio/mpeg";
			break;
		default:
			throw new ServletException("Какой-то не такой параметр.");
	}

	ServletOutputStream stream = null;
	BufferedInputStream buf = null;
	try {
	     stream = response.getOutputStream();
	     File file = new File(url + "/" + fileName);

	     // как вариант, можно обратиться к файлу как к url;
	     // URL fileUrl = getServletContext().getResource("/files/новый.txt");
	     // URLConnection conn = fileUrl.openConnection();
	     // int size = conn.getContentLength();
	     // if (size >= 0) response.setContentLength(size); 
	     // conn.getInputStream().close();
	     	     
	     

	     response.setContentType(contentType + "; charset=utf8");

	     // переводим в кодировку tomcat по умолчанию
	     // иначе имя файла будет корявым
	     String encodedFileName = new String(fileName.getBytes(),"ISO-8859-1");

	     response.addHeader("Content-Disposition", 
	    		 "attachment;filename=\"" + encodedFileName + "\"");

	     response.setContentLength( (int) file.length() );
	      
	     FileInputStream input = new FileInputStream(file);
	     buf = new BufferedInputStream(input);
	     int readBytes = 0;

	     while((readBytes = buf.read()) != -1)
	        stream.write(readBytes);
	     
	     stream.flush();
	     } catch (IOException ioe){
	        throw new ServletException(ioe.getMessage());
	     } finally {
	    	 if(stream != null) stream.close();
	    	 if(buf != null) buf.close();
	     }
		//doGet(request, response);
    } //end doGet
   
    public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
    	// вывожу формы
        response.setContentType("text/html; charset=UTF8");
        PrintWriter out = response.getWriter();

        try {
            out.println("<html>");
            out.println("<head><title>Файл для загрузки</title></head>");
            out.println("<body>");

            out.println("<h2>Пожалуйста, выберите файл для загрузки</h2>");
            out.println("<form action=\"http://localhost:8080/servletexamples/SendFileServlet\" method=\"post\">");
       		out.println("<p><select size=\"4\" name=\"fileNumber\">");
   			out.println("<option value=\"1\" selected>PDF</option>");
           	out.println("<option value=\"2\">DOC</option>");
           	out.println("<option value=\"3\">ZIP</option>");
            out.println("<option value=\"4\">MP3</option>");
            out.println("</select></p>");
            out.println("<p><input type=\"submit\" value=\"Отправить\"></p>");
            out.println("</form>");

            out.println("</body></html>");
         } finally {
            out.close();  // Всегда закрывать Writer
         }
  
    } 
}