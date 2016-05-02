package tags;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

public class CookieBean {

  private Cookie cookie = null;

  public CookieBean() {
  }

  public void setName(String name) {

    if (name == null || (name.equals("")))
      throw new IllegalArgumentException("Invalid cookie name set in: "
          + getClass().getName());

    cookie = new Cookie(name, "" + new java.util.Date().getTime());
  }

  public void setValue(String value) {

    if (value == null || (value.equals("")))
      throw new IllegalArgumentException("Invalid cookie value set in: "
          + getClass().getName());

    if (cookie != null)
      cookie.setValue(value);

  }

  public void setMaxAge(int maxAge) {

    if (cookie != null)
      cookie.setMaxAge(maxAge);

  }

  public void setPath(String path) {

    if (path == null || (path.equals("")))
      throw new IllegalArgumentException("Invalid cookie path set in: "
          + getClass().getName());

    if (cookie != null)
      cookie.setPath(path);
  }

  public void setCookieHeader(HttpServletResponse response) {

    if (response == null)
      throw new IllegalArgumentException(
          "Invalid HttpServletResponse set in: "
              + getClass().getName());
    if (cookie != null)
      response.addCookie(cookie);
  }

  public String getName() {

    if (cookie != null)
      return cookie.getName();
    else
      return "unavailable";

  }

  public String getValue() {

    if (cookie != null)
      return cookie.getValue();
    else
      return "unavailable";

  }

  public String getPath() {

    if (cookie != null)
      return cookie.getPath();
    else
      return "unavailable";

  }
} 