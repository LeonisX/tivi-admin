import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class HelloServlet extends GenericServlet {
	private static final long serialVersionUID = 3889238263815236764L;

	@Override
	public void service(ServletRequest request, ServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html, UTF-8");
		
		// тут непонятки.
		
		// вот так нормально читаются текста из bundle
		// зато проблема с форматированием дат (кодировка)
		//PrintWriter printWriter = response.getWriter();
		// вот так даты и текст будут выводиться в своей кодировке
		// зато проблемы с bundle, хотя, русский выводится как надо.
		// в обоих случаях русский текст в чистом виде выводится нормально.
		
		// проблема в resourcebundle - 
		// As per the javadoc, they are by default read as ISO-8859-1. 
		// решение - читать своими силами. Переопределять ResourceBundle.Control
		// либо можно попробовать сохранять бандл в ISO-8859-1 ("\u4eca\u65e5\u306f\u4e16\u754c")
		PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(response.getOutputStream(), "UTF-8"), true);

		printWriter.println("<html><head>");
		printWriter.println("<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\" />");
		printWriter.println("<title>Locale Display</title></head><body>");
		
		// локализация
		// получаем локаль клиента
		
	      Locale locale = new Locale("en", "US");
	      DateFormat full = DateFormat.getDateTimeInstance(DateFormat.LONG, 
	                                            DateFormat.LONG,
	                                            locale);
	      printWriter.println("In English appropriate for the US:");
	      printWriter.println("Hello World!");
	      printWriter.println(full.format(new Date()));
	      printWriter.println("<br />");

	      locale = new Locale("es", "");
	      full = DateFormat.getDateTimeInstance(DateFormat.LONG, 
	                                            DateFormat.LONG,
	                                            locale);
	      printWriter.println("En Espa\u00f1ol:");
	      printWriter.println("\u00a1Hola Mundo!");
	      printWriter.println(full.format(new Date()));
	      printWriter.println("<br />");

	      locale = new Locale("ja", "");
	      full = DateFormat.getDateTimeInstance(DateFormat.LONG,
	                                            DateFormat.LONG,
	                                            locale);
	      printWriter.println("In Japanese:");
	      printWriter.println("\u4eca\u65e5\u306f\u4e16\u754c");
	      printWriter.println(full.format(new Date()));
	      printWriter.println("<br />");

	      locale = new Locale("zh", "");
	      full = DateFormat.getDateTimeInstance(DateFormat.LONG,
	                                            DateFormat.LONG,
	                                            locale);
	      printWriter.println("In Chinese:");
	      printWriter.println("\u4f60\u597d\u4e16\u754c");
	      printWriter.println(full.format(new Date()));
	      printWriter.println("<br />");

	      locale = new Locale("ko", "");
	      full = DateFormat.getDateTimeInstance(DateFormat.LONG,
	                                            DateFormat.LONG,
	                                            locale);
	      printWriter.println("In Korean:");
	      printWriter.println("\uc548\ub155\ud558\uc138\uc694\uc138\uacc4");
	      printWriter.println(full.format(new Date()));
	      printWriter.println("<br />");

	      locale = new Locale("ru", "");
	      full = DateFormat.getDateTimeInstance(DateFormat.LONG,
	                                            DateFormat.LONG,
	                                            locale);
	      printWriter.println("In Russian (Cyrillic):");
	      printWriter.print("\u0417\u0434\u0440\u0430\u0432\u0441\u0442");
	      printWriter.println("\u0432\u0443\u0439, \u041c\u0438\u0440");
	      printWriter.println(" ещё");
	      printWriter.println(full.format(new Date()));
	      printWriter.println("<br />");
	      printWriter.println("<br />");
	      printWriter.println("<br />");
		
	      
		outText(request.getLocale(), printWriter);
		outText(new Locale("ru"), printWriter);
		outText(new Locale("en", "GB"), printWriter);
		outText(new Locale("en", "US"), printWriter);
		outText(new Locale("fr", "FR"), printWriter);
		
		
		
		printWriter.println("</body></html>");
		printWriter.close();
	}
	
	private void outText(Locale locale, PrintWriter printWriter) {
		// некорректно вычитывает текст из бандла, если
		// -- PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(response.getOutputStream(), "UTF-8"), true);
		//ResourceBundle bundle = ResourceBundle.getBundle("i18n.WelcomeBundle", locale);
		ResourceBundle bundle = ResourceBundle.getBundle("i18n.WelcomeBundle", locale, new EncodedControl("UTF8"));
		// или ResourceBundle bundle = ResourceBundle.getBundle("i18n.WelcomeBundle", locale, new MyControl("UTF8"));
		printWriter.println(bundle.getString("Welcome") + "<br />");
		printWriter.println(locale.getLanguage() + "_" + locale.getCountry() + "<br />");
		printWriter.println("Date: ");
		printWriter.println(DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.SHORT, locale).format(new Date()) + "<br />");
		printWriter.println("Currency: ");
		NumberFormat nft = NumberFormat.getCurrencyInstance(locale);
		String formattedCurr = nft.format(1000000);
		printWriter.println(formattedCurr + "<br />");
		printWriter.println("Percent: ");
		nft = NumberFormat.getPercentInstance(locale);
		formattedCurr = nft.format(0.51);
		printWriter.println(formattedCurr + "<br />" + "<br />");
	}
}


// http://www.coderanch.com/t/525337/java/java/read-Franch-ResourceBundle
	
class EncodedControl extends ResourceBundle.Control {
    private String encoding;
 
    public EncodedControl(String encoding)
    {
        this.encoding = encoding;
    }
 
    @Override
    public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload)
           throws IllegalAccessException, InstantiationException, IOException
    {
        if (!format.equals("java.properties"))
        {
            return super.newBundle(baseName, locale, format, loader, reload);
        }
        String bundleName = toBundleName(baseName, locale);
        ResourceBundle bundle = null;
        // code below copied from Sun's/Oracle's code; that's their indentation, not mine ;)
        final String resourceName = toResourceName(bundleName, "properties");
        final ClassLoader classLoader = loader;
        final boolean reloadFlag = reload;
        InputStream stream = null;
        try {
            stream = AccessController.doPrivileged(
            new PrivilegedExceptionAction<InputStream>() {
                public InputStream run() throws IOException {
                InputStream is = null;
                if (reloadFlag) {
                    URL url = classLoader.getResource(resourceName);
                    if (url != null) {
                    URLConnection connection = url.openConnection();
                    if (connection != null) {
                        // Disable caches to get fresh data for
                        // reloading.
                        connection.setUseCaches(false);
                        is = connection.getInputStream();
                    }
                    }
                } else {
                    is = classLoader.getResourceAsStream(resourceName);
                }
                return is;
                }
            });
        } catch (PrivilegedActionException e) {
            throw (IOException) e.getException();
        }
        if (stream != null) {
            try {
// CHANGE HERE
//          bundle = new PropertyResourceBundle(stream);
            Reader reader = new InputStreamReader(stream, encoding);
            bundle = new PropertyResourceBundle(reader);
// END CHANGE
            } finally {
            stream.close();
            }
        }
        // and to finish it off
        return bundle;
    }
}


class MyControl extends ResourceBundle.Control {
	 
    public List<String> getFormats(String basename) {
 
        if (basename == null) {
            throw new NullPointerException();
        }
        return Arrays.asList("properties");
 
    }
 
    @Override
    public ResourceBundle newBundle(String baseName, Locale locale, String format,
            ClassLoader loader, boolean reload) {
 
        if (baseName == null || locale == null || format == null || loader == null) {
            throw new NullPointerException();
        }
         
        ResourceBundle bundle = null;
 
        if (format.equals("properties")) {
 
            String bundleName = toBundleName(baseName, locale);
            String resourceName = toResourceName(bundleName, format);
            InputStream stream = null;
 
            try {
                if (reload) {
                    URL url = loader.getResource(resourceName);
                    if (url != null) {
                        URLConnection connection;
                        connection = url.openConnection();
 
                        if (connection != null) {
                            connection.setUseCaches(false);
                            stream = connection.getInputStream();
                        }
                    }
                } else {
                    stream = loader.getResourceAsStream(resourceName);
                }
 
                if (stream != null) {
                    InputStreamReader is = new InputStreamReader(stream, "UTF-8");
                    bundle = new PropertyResourceBundle(is);
                    is.close();
                }
            } catch (IOException e) {               
                e.printStackTrace();
            }
        }
 
        return bundle;
    }
}
