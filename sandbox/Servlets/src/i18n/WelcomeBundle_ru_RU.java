package i18n;


import java.util.ListResourceBundle;

public class WelcomeBundle_ru_RU extends ListResourceBundle {
	static final Object[][] contents = {
		{"Welcome", "Добро пожаловать!"}
	};
	
	@Override
	protected Object[][] getContents() {
		return contents;
	}

}
