import javax.servlet.*;
import javax.servlet.http.*;

public class RequestFilter implements Filter {
    private FilterConfig config = null;
    
    public RequestFilter() { }
    
    public void init(FilterConfig filterConfig) throws ServletException{
        this.config = filterConfig;
    }
    
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws java.io.IOException, ServletException {
 
    	RequestWrapper wrapper = null;
    	ServletContext context = null;
          
        if (request instanceof HttpServletRequest)
              wrapper = new RequestWrapper((HttpServletRequest)request);
              
        /* используем метод ServletContext.log для протоколирования параметров */
        if (wrapper != null){
        	context = config.getServletContext();
            context.log("Query: " + wrapper.getQueryString());
        }

        // продолжаем обработку запроса в другом фильтре или сервлете назначения
        if (wrapper != null)
            chain.doFilter(wrapper, response);
        else
            chain.doFilter(request, response);
    }
    
    public void destroy(){ }
}


class RequestWrapper extends HttpServletRequestWrapper{
 
	public RequestWrapper(HttpServletRequest request){
		super(request);
	}
 
	public String getQueryString(){
 
		String query = null;
		//	получаем строку запроса и добавляем параметр testParameter
		query = ((HttpServletRequest)getRequest()).getQueryString();
		if (query != null)
			return query + "&testParameter=" + getClass().getName();
		else
			return "testParameter="+getClass().getName();
	}
}

