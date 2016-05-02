package parser;    

import java.io.IOException;  
    
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.MalformedURLException;

import javax.servlet.*;
import javax.servlet.http.*;

import javax.swing.text.html.HTMLEditorKit.ParserCallback;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.parser.ParserDelegator;

public class HtmlParseServlet extends HttpServlet {
	private static final long serialVersionUID = -9166821116812878723L;


	private static final String BASE_URL = "http://finance.yahoo.com/q?d=t&s=";
    
  
    private ParserDelegator htmlParser = null;
    private MyParserCallback callback = null;
    private String htmlText = "";
	private boolean lastTradeFlag = false;
    private boolean boldFlag = false;
    private float stockVal = 0f;
    
  public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, java.io.IOException {
    
	//set the MIME type of the response, "text/html"
    response.setContentType("text/html");
	
	//use a PrintWriter send text data to the client who has requested the servlet
    java.io.PrintWriter out = response.getWriter();
	
	//Begin assembling the HTML content
    out.println("<html><head>");
    
    out.println("<title>Stock Price Fetcher</title></head><body>");
    out.println("<h2>Please submit a new valid stock symbol</h2>");
   
   //make sure method="post" so that the servlet service method
   //calls doPost in the response to this form submit
    out.println(
        "<form method=\"post\" action =\"" + request.getContextPath() +
            "/stockservlet\" >");

    out.println("<table border=\"0\"><tr><td valign=\"top\">");
    out.println("Stock symbol: </td>  <td valign=\"top\">");
    out.println("<input type=\"text\" name=\"symbol\" size=\"10\">");
    out.println("</td></tr><tr><td valign=\"top\">");

    out.println("<input type=\"submit\" value=\"Submit Info\"></td></tr>");
    out.println("</table></form>");
    out.println("</body></html>");
	
     } //end doGet
	 
	 public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws java.io.IOException{
	   
	
	String symbol;//this will hold the stock symbol
	float price;
	
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
	    
		price = getLatestPrice(symbol);
		//price < 1 ? "The symbol is probably invalid." :
	    out.println( (price == 0 ? "The symbol is probably invalid." : ""+price) );
		
		
	}
	
		
	out.println("</body></html>");
	
	
	}// doPost
	
	private float getLatestPrice(String symbol) throws IOException,MalformedURLException {

	    BufferedReader webPageStream = null;
        URL stockSite = new URL(BASE_URL + symbol);
       System.out.println(BASE_URL + symbol);
        webPageStream = new BufferedReader(new InputStreamReader(stockSite.
           openStream()));
	   
        htmlParser = new ParserDelegator();
	   
        callback = new MyParserCallback();
	   
	    synchronized(htmlParser){//the code is designed to make calling parse() thread-safe
		
            htmlParser.parse(webPageStream,callback,true);
		}

       return stockVal;

  }//getLatestPrice
	
  class MyParserCallback extends ParserCallback {
  
      public MyParserCallback(){
	  
	      if (stockVal != 0)
		      stockVal = 0f;
	  
	  }
        
       public void handleStartTag(javax.swing.text.html.HTML.Tag t,MutableAttributeSet a,int pos) {
        
          if (lastTradeFlag && (t == javax.swing.text.html.HTML.Tag.B )){
            
              boldFlag = true;
          }
        
      }//handleStartTag

      public void handleText(char[] data,int pos){
              
            htmlText  = new String(data);
			
			
            if (htmlText.indexOf("No such ticker symbol.") != -1){
             
				throw new IllegalStateException(
				  "Invalid ticker symbol in handleText() method.");
                
            }  else if (htmlText.equals("Last Trade:")){
                    
                lastTradeFlag = true;
                    
            } else if (boldFlag){
                
                try{
                    
                    stockVal = new Float(htmlText).floatValue();
					

                } catch (NumberFormatException ne) {
                    
                    try{
                        
                        // tease out any commas in the number using NumberFormat
                        
                        java.text.NumberFormat nf = java.text.NumberFormat.getInstance();
                    
                        Double f = (Double) nf.parse(htmlText);
                    
                        stockVal =  (float) f.doubleValue();
                     
                    } catch (java.text.ParseException pe){
                        
                        throw new IllegalStateException(
				        "The extracted text " + htmlText + " cannot be parsed as a number!");
                        
                    }
                }
            lastTradeFlag = false;
            boldFlag = false;
			
			}//if
                
        } //handleText
   }//MyParserCallback
}//HttpServlet
