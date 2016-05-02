

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LocaleDisplay extends HttpServlet {
	private static final long serialVersionUID = 4041893257423386081L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// все локали клиента
		Enumeration<Locale> locales = request.getLocales();
		// наиболее предпочтительный вариант локали
		String preferredDisplay = "";
		Locale preferredLocale = request.getLocale();
		if (preferredLocale != null) {
			preferredDisplay = preferredLocale.getDisplayName();
		}
		
		response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = response.getWriter();
		out.println("<html><head>");
		out.println("<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\" />");
		out.println("<title>Locale Display</title></head><body>");
		out.println("Preferred Locale: " + preferredDisplay + "<br /><br />");

		if (preferredLocale != null) {
			showLocale(preferredLocale, out);
		}
		
		out.println("<p>Lower priorities Locales:</p>");
		Locale loc = null;
		while (locales.hasMoreElements()) {
			loc = locales.nextElement();
			if (!loc.getDisplayName().equals(preferredDisplay)) {
				showLocale(loc, out);
			}
		}
		out.println("</body></html>");
	}

	private void showLocale(Locale loc, PrintWriter out) {
		out.println("locale: ");
		out.println(loc);
		out.println("<br />");

		out.println("getDisplayName: ");
		out.println(loc.getDisplayName());
		out.println("<br />");
		out.println("getDisplayLanguage: ");
		out.println(loc.getDisplayLanguage());
		out.println("<br />");
		out.println("getISO3Language: ");
		out.println(loc.getISO3Language());
		out.println("<br />");
		out.println("getDisplayCountry: ");
		out.println(loc.getDisplayCountry());
		out.println("<br />");
		out.println("getISO3Country: ");
		out.println(loc.getISO3Country());
		out.println("<br />");

		out.println("getCountry: ");
		out.println(loc.getCountry());
		out.println("<br />");
		out.println("getVariant: ");
		out.println(loc.getVariant());
		out.println("<br />");
		out.println("getDisplayScript: ");
		out.println(loc.getDisplayScript());
		out.println("<br />");
		out.println("getDisplayVariant: ");
		out.println(loc.getDisplayVariant());
		out.println("<br />");				
		out.println("getScript: ");
		out.println(loc.getScript());
		out.println("<br />");
		out.println("getUnicodeLocaleAttributes: ");
		out.println(loc.getUnicodeLocaleAttributes());
		out.println("<br />");
		out.println("<br />");
	}
}
