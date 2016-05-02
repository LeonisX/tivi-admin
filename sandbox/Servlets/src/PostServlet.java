import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;

//Сервлет, посылающий post данные на JSP

public class PostServlet extends HttpServlet {
	private static final long serialVersionUID = 2915825380300607954L;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpClient httpClient = new HttpClient();
		
		String serverPath = request.getServletPath();
	    String url = "http://" + request.getLocalName() + ":" + request.getLocalPort() + request.getContextPath() + serverPath.substring(0, serverPath.lastIndexOf('/') + 1);
		
		PostMethod postMethod = new PostMethod(url + "adrotator.jsp");
		NameValuePair[] postData = {
				new NameValuePair("firstName", "Leonis"),
				new NameValuePair("lastName", "Silver"),
				new NameValuePair("email", "tv-games@mail.ru")
		};
		postMethod.addParameters(postData);
		//postMethod.setRequestBody(postData);
		httpClient.executeMethod(postMethod);
		
		response.setContentType("text/html, UTF-8");
		PrintWriter out = response.getWriter();
		
		if (postMethod.getStatusCode() == HttpStatus.SC_OK) {
			out.println(postMethod.getResponseBodyAsString());
		} else {
			out.println("The POST action raised an error: " + postMethod.getStatusLine());
		}
		
		//освобождаем соединение, использованное методом
		postMethod.releaseConnection();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request, response);
	}
	
}
