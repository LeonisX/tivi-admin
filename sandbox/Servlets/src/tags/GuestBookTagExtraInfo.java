package tags;

// класс, который определяет имена и типы переменных
// создаваемых нестандартным обрабботчиком тегов GuestBookTag

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.VariableInfo;

public class GuestBookTagExtraInfo extends TagExtraInfo {

	//метод который возвращает инофрмацию о переменных
	//GuestBookTag, созданных для их использования на JSP странице
	@Override
	public VariableInfo[] getVariableInfo(TagData data) {
		VariableInfo firstName = new VariableInfo("firstName", 
				"String", true, VariableInfo.NESTED);
		VariableInfo lastName = new VariableInfo("lastName", 
				"String", true, VariableInfo.NESTED);
		VariableInfo email = new VariableInfo("email", 
				"String", true, VariableInfo.NESTED);
		VariableInfo variableInfo[] = {firstName, lastName, email};
		return variableInfo;
	}
	
}
