import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;

import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;


public class PostParametersServlet extends GenericServlet {

	private static final long serialVersionUID = -6567022509523803116L;

	@Override
	public void service(ServletRequest request, ServletResponse response)
			throws ServletException, IOException {
		PrintWriter pw = response.getWriter();
		Enumeration<String> e = request.getParameterNames();
		while(e.hasMoreElements()) {
			String pname = (String)e.nextElement();
			pw.print(pname +" = ");
			String pvalue = request.getParameter(pname);
			pw.println(pvalue);
		}
		pw.println();
		
		//два современных способа, надо разобраться какой правильнее.
		
		Map<String, String[]> rm = request.getParameterMap();
		Set<Map.Entry<String,String[]>> entrySet = rm.entrySet();
		for (Map.Entry<String,String[]> pair : entrySet) {
			pw.print(pair.getKey() + " = ");
			pw.println(pair.getValue()[0]);
		}
		pw.println();
		
		for (String key : rm.keySet()) {
			pw.print(key + " = ");
			pw.println(rm.get(key)[0]);
		}
		
		pw.close();
	}

}
