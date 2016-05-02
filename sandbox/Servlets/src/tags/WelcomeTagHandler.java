package tags;
// нестандартный обработчик тэгов
import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

public class WelcomeTagHandler extends TagSupport {
	private static final long serialVersionUID = 1137367212537585057L;

	// метод, вызываемый чтобы начать обработку тэга
	public int doStartTag() throws JspException {
		try {
			// получение объекта JspWriter для вывода содержимого
			JspWriter out = pageContext.getOut();
			
			out.print("Первый JSP тэг!");
		} catch (IOException e) {
			throw new JspException(e.getMessage());
		}
		
		return SKIP_BODY;	//игнорировать тело тэга
	}
}
