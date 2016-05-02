import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendMailSSL {
	public static void main(String[] args) {
		final String username = "tivi.leonis@gmail.com";
		final String password = "пароль";
//https://www.google.com/settings/security/lesssecureapps
//Exception in thread "main" java.lang.RuntimeException: javax.mail.AuthenticationFailedException: 534-5.7.14 <https://accounts.google.com/ContinueSignIn?sarp=1&scc=1&plt=AKgnsbvrQ
		
		//https://support.google.com/mail/answer/13287?hl=en
		
		// способы отправки вложений и прочего
		// http://www.journaldev.com/2532/java-program-to-send-email-using-smtp-gmail-tls-ssl-attachment-image-example
		
		
		// не работало. пашет с smtps
		// http://www.rgagnon.com/javadetails/java-0570.html
		Properties props = new Properties();
		props.put("mail.transport.protocol", "smtps");
		props.put("mail.smtps.host", "smtp.gmail.com");
		//props.put("mail.smtp.socketFactory.port", "465");
		//props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtps.auth", "true");
		//props.put("mail.smtp.port", "465");

		Authenticator auth = new Authenticator() {
            //override the getPasswordAuthentication method
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        };
        
		Session session = Session.getDefaultInstance(props, auth);

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("tivi.leonis@gmail.com"));
			message.setRecipients(Message.RecipientType.TO,
				InternetAddress.parse("tv-games@mail.ru"));
			message.setSubject("Testing Subject");
			message.setText("Dear Mail Crawler,"
				+ "\n\n No spam to my email, please!");

			Transport.send(message);

			System.out.println("Done");

		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}
}