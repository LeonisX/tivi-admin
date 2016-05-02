package tags;

import java.io.Serializable;

// компонент используется страницей для проверки введённых данных

public class ClientValidator implements Serializable {
	private static final long serialVersionUID = 2670920922408089686L;
	String email;
	String password;
	boolean valid;

	public ClientValidator() {
		this.valid = false;
	}
	
	public boolean isValid() {
		this.valid = true;
		return valid;
	}
	
	public void setEmail(String _email) {
		if (_email != null && !_email.isEmpty()) {
			email = _email;
		} else {
			email = "Unknown";
		}
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setPassword(String _password) {
		if (_password != null && !_password.isEmpty()) {
			password = _password;
		} else {
			password = "Unknown";
		}
	}
	
	public String getPassword() {
		return password;
	}
}
