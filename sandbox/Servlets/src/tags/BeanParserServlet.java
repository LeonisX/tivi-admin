package tags;    

import javax.servlet.*;
import javax.servlet.http.*;

public class BeanParserServlet extends HttpServlet {
	private static final long serialVersionUID = 1212210068064633160L;

public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, java.io.IOException {
    
	//set the MIME type of the response, "text/html"
    response.setContentType("text/html");
	
	//use a PrintWriter send text data to the client who has requested the servlet
    java.io.PrintWriter out = response.getWriter();
	
	//Begin assembling the HTML content
    out.println("<html><head>");
    
    out.println("<title>Stock Price Fetcher</title></head><body>");
    out.println("<h2>Please submit a valid stock symbol</h2>");
   
   //make sure method="post" so that the servlet service method
   //calls doPost in the response to this form submit
    out.println(
        "<form method=\"post\" action =\"" + request.getContextPath() +
            "/stockbean\" >");

    out.println("<table border=\"0\"><tr><td valign=\"top\">");
    out.println("Stock symbol: </td>  <td valign=\"top\">");
    out.println("<input type=\"text\" name=\"symbol\" size=\"10\">");
    out.println("</td></tr><tr><td valign=\"top\">");

    out.println("<input type=\"submit\" value=\"Submit Info\"></td></tr>");
    out.println("</table></form>");
    out.println("</body></html>");
	
     } //end doGet
	 
	 public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException,java.io.IOException{
	   
	
	String symbol;//this will hold the stock symbol
	float price = 0f;
	
	symbol = request.getParameter("symbol");
	
	boolean isValid = (symbol == null || symbol.length() < 1) ? false : true;

	//set the MIME type of the response, "text/html"
    response.setContentType("text/html");
	
    //use a PrintWriter send text data to the client who has requested the servlet
    java.io.PrintWriter out = response.getWriter();
	
	//Begin assembling the HTML content
    out.println("<html><head>");
    out.println("<title>Latest stock value</title></head><body>");
	
	if (! isValid){
	    out.println("<h2>Sorry, the stock symbol parameter was either empty or null</h2>");
	} else {
	
	    out.println("<h2>Here is the latest value of "+ symbol +"</h2>");
	    
		
		StockPriceBean spbean = new StockPriceBean();
		spbean.setSymbol(symbol);
		price = spbean.getLatestPrice();
		
		
	    out.println( (price == 0 ? "The symbol is probably invalid." : ""+price) );
		
	}
	
		
	out.println("</body></html>");
	
    out.close();
	
	}// doPost
	
}//HttpServlet
