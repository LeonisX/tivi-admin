import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

public class DataSourceLoginModule implements LoginModule {
	CallbackHandler handler;
	Subject subject;
	Map<String, ?> sharedState;
	Map<String, ?> options;
	private boolean loginPassed = false;

	public DataSourceLoginModule() {
	}
	
	@Override
	public void initialize(Subject subject, CallbackHandler handler,
			Map<String, ?> sharedState, Map<String, ?> options) {
		this.subject = subject;
		this.handler = handler;
		this.sharedState = sharedState;
		this.options = options;
	}

	@Override
	public boolean login() throws LoginException {
		String name = "";
		String pass = "";
		loginPassed = false;
		try {
			//Создаём массив CallBack для передачи в метод CallbackHandler.handle();
			Callback[] callbacks = new Callback[2];
			// Не используйте конструктор NameCallback без аргументов!
			callbacks[0] = new NameCallback("Username:");
			// Не используйте без аргументов конструктор PasswordCallback!
			callbacks[1] = new PasswordCallback("Password:", false);
			handler.handle(callbacks);
			// Получаем имя и пароль
			NameCallback nameCall = (NameCallback) callbacks[0];
			name = nameCall.getName();
			PasswordCallback passCall = (PasswordCallback) callbacks[1];
			pass = new String(passCall.getPassword());
			// тут я утрировал, на самом деле надо обращаться в базе данных
			if (!name.equals("Leonis")) {
				throw new FailedLoginException("The user was not successfully authenticated");
			}
			if (!pass.equals("test")) {
				throw new FailedLoginException("The password was not successfully authenticated");
			}
			loginPassed = true;
		} catch (Exception e) {
			throw new LoginException(e.getMessage());
		}
		return loginPassed;
	}

	@Override
	public boolean commit() throws LoginException {
		return loginPassed;
	}

	@Override
	public boolean abort() throws LoginException {
		boolean bool = loginPassed;
		loginPassed = false;
		return bool;
	}

	@Override
	public boolean logout() throws LoginException {
		loginPassed = false;
		return true;
	}

}
