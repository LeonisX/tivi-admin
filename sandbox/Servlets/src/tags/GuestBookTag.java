package tags;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

public class GuestBookTag extends BodyTagSupport {
	private static final long serialVersionUID = -3127442570307347517L;

	//private String firstName;
	//private String lastName;
	//private String email;
	
	private GuestDataBean guestData;
	//private GuestBean guest;
	private Iterator<GuestBean> iterator;
	
	@SuppressWarnings("deprecation")
	//EVAL_BODY_TAG - deprecated
	@Override
	public int doStartTag() throws JspException {
		try {
			guestData = new GuestDataBean();
			
			List<GuestBean> list = guestData.getGuestList();
			iterator = list.iterator();
			
			if (iterator.hasNext()) {
				processNextGuest();
				
				return EVAL_BODY_TAG;	// продолжение обработки тела тэга
			} else {
				return SKIP_BODY;	// завершение обработки тела тэга
			}
		} catch (Exception exception) {
			exception.printStackTrace();
			return SKIP_BODY; // игнорировать тело тэга
		}
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public int doAfterBody() throws JspException {
		try {
			bodyContent.writeOut(getPreviousOut());
		} catch (Exception exception) {
			exception.printStackTrace();
			return SKIP_BODY; // игнорировать тело тэга
		}
		
		bodyContent.clearBody();
		
		if (iterator.hasNext()) {
			processNextGuest();
			
			return EVAL_BODY_TAG;	// продолжение обработки тела тэга
		} else {
			return SKIP_BODY;	// завершение обработки тела тэга
		}
	}
	
	// получение следующего объекта GuestBean и извлечение из него данных

	private void processNextGuest() {
		// получение данных для следующего гостя
		GuestBean guest = (GuestBean) iterator.next();
		
		pageContext.setAttribute("firstName", guest.getFirstName());
		pageContext.setAttribute("lastName", guest.getLastName());
		pageContext.setAttribute("email", guest.getEmail());
	}

}

class GuestDataBean {
	public List<GuestBean> getGuestList() {
		List<GuestBean> guestList = new ArrayList<GuestBean>();
		guestList.add(new GuestBean("Leonis", "Silver", "tv-games@mail.ru"));
		guestList.add(new GuestBean("Leonis2", "Silver", "tv-games@mail.ru"));
		guestList.add(new GuestBean("Leonis3", "Silver", "tv-games@mail.ru"));
		return guestList;
	}
}