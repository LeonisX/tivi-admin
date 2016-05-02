package tags;
// нестандартный обработчик тэгов, обрабатывающий простой тэг
import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

public class WelcomeTagHandler2 extends TagSupport {
	private static final long serialVersionUID = 1137367212537585053L;
	private String firstName = "";
	// метод, вызываемый чтобы начать обработку тэга
	public int doStartTag() throws JspException {
		try {
			// получение объекта JspWriter для вывода содержимого
			JspWriter out = pageContext.getOut();
			
			out.print("Приветствую, " + firstName);
		} catch (IOException e) {
			throw new JspException(e.getMessage());
		}
		
		return SKIP_BODY;	//игнорировать тело тэга
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
}
