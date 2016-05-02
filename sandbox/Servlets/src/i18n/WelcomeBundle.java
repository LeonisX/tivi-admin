package i18n;


import java.util.ListResourceBundle;

public class WelcomeBundle extends ListResourceBundle {
	static final Object[][] contents = {
		{"Welcome", "Welcome"}
	};
	
	@Override
	protected Object[][] getContents() {
		return contents;
	}

}
