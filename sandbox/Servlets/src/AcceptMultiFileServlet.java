import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.oreilly.servlet.multipart.FilePart;
import com.oreilly.servlet.multipart.MultipartParser;
import com.oreilly.servlet.multipart.Part;


public class AcceptMultiFileServlet extends HttpServlet {
	private static final long serialVersionUID = -3600483335919023539L;
	private String fileSavePath;
	
	@Override
	public void init() throws ServletException {
		fileSavePath = getServletContext().getRealPath("/") + "upload";
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = response.getWriter();
		out.println("<html><head>");
		out.println("<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\" />");
		out.println("<title>File uploads</title></head><body>");
		out.println("<h2>Here is information about any uploaded files</h2>");
		try {
			// предельный размер файла 5 мб
			MultipartParser parser = new MultipartParser(request, 5 * 1024 * 1024, true, true, "UTF-8");
			Part _part = null;
			while ((_part = parser.readNextPart()) != null) {
				if (_part.isFile()) {
					// получаем некоторую информацию о файле
					FilePart fPart = (FilePart) _part;
					String name = fPart.getFileName();
					if (name != null) {
						long fileSize = fPart.writeTo(new File(fileSavePath));
						out.println("The user's file path for this file: "
								+ fPart.getFilePath() + "<br />");
						out.println("The content type of the file: "
								+ fPart.getContentType() + "<br />");
						out.println("The file size: "
								+ fileSize + "bytes<br />");
						// переходим к следующему файлу, если он есть
					} else {
						out.println("The user did not upload a file for this part.");
					}
				} else if (_part.isParam()) {
					// если это не файл а другой параметр
					// например имя пользователя, делаем что-то иное
				}
			}
			
			out.println("</body></html>");
		} catch (IOException e) {
			throw new IOException("IOException occured in: " + getClass().getName());
		}
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		throw new ServletException("GET method used with "
				+ getClass().getName() + ": POST method required.");
	}
}
