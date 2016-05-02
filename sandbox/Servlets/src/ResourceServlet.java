// Просмотр внутренних ресурсов в сервлете

import java.io.BufferedInputStream;
import java.io.PrintWriter;
import java.io.IOException;

import java.net.URL;
import java.net.URLConnection;
import java.net.MalformedURLException;

import javax.servlet.*;
import javax.servlet.http.*;

public class ResourceServlet extends HttpServlet {
	private static final long serialVersionUID = -7712345688026922152L;

public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    
      // получить web.xml для отображения сеервлетом
      String file = "/WEB-INF/web.xml";
     
      URL url = null;
      URLConnection urlConn = null;  
      PrintWriter out = null;
      BufferedInputStream buf = null;
     try{
     out = response.getWriter();
     // доступ к ресурсу в пределах этого же веб-приложения как к объекту url
     url = getServletContext().getResource(file);

     response.setContentType("text/xml; charset=utf8");
    
      urlConn = url.openConnection();
     // устанавливаем соединение с URL, представляющим web.xml
     urlConn.connect();
     buf = new BufferedInputStream(urlConn.getInputStream());
     int readBytes = 0;

     while((readBytes = buf.read()) != -1)
        out.write(readBytes);

     } catch (MalformedURLException mue){
           throw new ServletException(mue.getMessage());
           
     } catch (IOException ioe){
        throw new ServletException(ioe.getMessage());
         
     } finally {
     if(out != null) out.close();
      if(buf != null) buf.close();
     }
    } 
}