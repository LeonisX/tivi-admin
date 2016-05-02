import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.*;
import javax.servlet.*;
import javax.servlet.http.*;

// не работает. для отладки следует сперва поставить локальный smtp сервер

public class EmailServlet extends HttpServlet {
	private static final long serialVersionUID = -1132118233282525651L;
	private final static String DEFAULT_SERVER = "localhost";
			
	@Override
	public void service(ServletRequest request, ServletResponse response) throws ServletException, IOException {
		// получаем значения компонентов сообщения из параметров запроса
		String smtpServ = request.getParameter("smtp");
		if (smtpServ == null || smtpServ.isEmpty()) {
			smtpServ = DEFAULT_SERVER;
		}
		String from = request.getParameter("from");
		String to = request.getParameter("to");
		String subject = request.getParameter("subject");
		String emailContent = request.getParameter("emailContent");
		
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<html><head><title>Email message sender</title></head><body>");
		try {
			sendMessage(smtpServ, to, from, subject, emailContent);
		} catch (Exception e) {
			throw new ServletException(e.getMessage());
		}
		out.println("<b>The message was send successfully</b>");
		out.println("</body></html>");
	}

	private void sendMessage(String smtpServ, String to, String from,
			String subject, String emailContent) throws Exception {
		// заполняем объект Properties адресом почтового сервера чтобы
		// объект по умолчанию типа Session мог его использовать
		final String username = "tv-games";
		final String password = "pass";

		Properties properties = new Properties();
		//Properties properties = System.getProperties();
		properties.put("mail.transport.protocol", "smtp");
		properties.put("mail.smtp.auth", "false");
		properties.put("mail.smtp.starttls.enable", "false");
		properties.put("mail.smtp.host", "localhost");
		//properties.put("mail.smtp.port", "465");

		//Session session = Session.getDefaultInstance(properties);
		Session session = Session.getInstance(properties,
		        new javax.mail.Authenticator() {
		            protected PasswordAuthentication getPasswordAuthentication() {
		                return new PasswordAuthentication(username, password);
		            }
		        });
		Message message = new MimeMessage(session);	// новое почтовое сообщение
		InternetAddress[] addresses = null;
		try {
			if (to != null) {
				// выбрасываем исключение AddressException если адрес получателя
				// нарушает синтаксис, установленный в RFC822
				addresses = InternetAddress.parse(to, false);
				message.setRecipients(Message.RecipientType.TO, addresses);
			} else {
				throw new MessagingException("The mail message requires a 'To' address.");
			}
			if (from != null) {
				message.setFrom(new InternetAddress(from));
			} else {
				throw new MessagingException("The mail message requires a 'From' address.");
			}
			if (subject != null) {
				message.setSubject(subject);
			}
			if (emailContent != null) {
				message.setText(emailContent);
			}
			// посылаем сообщение. если для кого-то из получателей задан
			// некорректный адрес - выбрасываем SendFailedException
			Transport.send(message);
		} catch (Exception e) {
			throw e;
		}
	}
}
