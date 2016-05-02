import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;





// используем
import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;
// проверялся (не мной) вплоть до 50 мб
// рекомендуют Commons FileUpload - проверялась до 500 мб свободно
import com.oreilly.servlet.multipart.FileRenamePolicy;


public class AcceptFileServlet extends HttpServlet {
	private static final long serialVersionUID = -3600493335919023539L;
	private String webTempPath;
	
	@Override
	public void init() throws ServletException {
		webTempPath = getServletContext().getRealPath("/") + "upload";
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");
		// предельный размер файла 5 мб
		// при загрузке файлов с одинаковым именем будет добавляться цифра.
		// пример своего механизма переименованя ниже.
		MultipartRequest mpr = new MultipartRequest(request, webTempPath, 5 * 1024 * 1024, "UTF-8", new DefaultFileRenamePolicy());
		@SuppressWarnings("unchecked")
		Enumeration<String> fileNames = mpr.getFileNames();
		//response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<html><head>");
		out.println("<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\" />");
		out.println("<title>ServletException upload</title></head><body>");
		for (int i = 1; fileNames.hasMoreElements(); i++) {
			out.println("The name of uploaded file " + i + " is: "
					+ mpr.getFilesystemName(fileNames.nextElement()) + "<br /><br />");
		}
		out.println("</body></html>");
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		throw new ServletException("GET method used with "
				+ getClass().getName() + ": POST method required.");
	}
}


// своё переименование файлов
class MyFileRenamePolicy implements FileRenamePolicy {
	// в соответствии с контрактом интерфейса FileRenamePolicy
	// реализуем метод rename
	
	@Override
	public File rename(File f) {
		// получаем путь к родительскому каталогу
		String parentDir = f.getParent();
		
		// получаем имя файла без пути
		String fname = f.getName();
		
		// получаем расширение файла, если оно есть
		String fileExt = "";
		int i = -1;
		if ((i = fname.indexOf('.')) != -1) {
			fileExt = fname.substring(i);
			fname = fname.substring(0, i);
		}
		
		// добавляем временную метку
		fname += (new Date().getTime() / 1000);
		
		// собираем имя файла
		fname = parentDir + System.getProperty("file.separator") + fname + fileExt;
		return new File(fname);
	}
}
