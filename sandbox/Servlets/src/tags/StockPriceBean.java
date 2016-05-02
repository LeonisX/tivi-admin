package tags;  

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.net.URL;
import java.net.MalformedURLException;

import javax.swing.text.html.HTMLEditorKit.ParserCallback;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.parser.ParserDelegator;

public class StockPriceBean {

    /**  
     *   The URL base for requesting a stock price; it looks like
     *   "http://finance.yahoo.com/q?d=t&s="
     */
     private static final String urlBase =  "http://finance.yahoo.com/q?d=t&s=";
    
    /**  
     *   The character stream of HTML that is parsed for the stock price 
     *    returned by java.net.URL.openStream()
     *   
     *   see java.net.URL
     *   @see java.io.BufferedReader
     */
    private BufferedReader webPageStream = null;
     
    /**  
     *   The java.net.URL object that represents the stock Web page
     *   
     */
     private URL stockSite = null;
    
    /**  
     *   The ParserDelegator object for which ParserDelegator.parse() is
     *   called for the Web page
     *
     *   @see javax.swing.text.html.parser.ParserDelegator
     */
     private ParserDelegator htmlParser = null;
    
    /**  
     *   The MyParserCallback object (inner class); this object is an
     *   argument to the ParserDelegator.parse() method
     *
     *   @see javax.swing.text.html.HTMLEditorKit.ParserCallback
     */
     private MyParserCallback callback = null;

    /**  
     *   This String holds the HTML text as the Web page is parsed.
     *   
     *   @see MyParserCallback
     */
     private String htmlText = "";
	 private String symbol = "";
     private float stockVal = 0f;

  //A JavaBean has to have a no-args constructor (we explicitly show this 
  //constructor as a reminder; the compiler would have generated a default
  //constructor with no arguments automatically
  public StockPriceBean() {}
	
  //Setter or mutator method for the stock symbol
  public void setSymbol(String symbol){
	
      this.symbol = symbol;
  }
   
  class MyParserCallback extends ParserCallback {

      //bread crumbs that lead us to the stock price
      private boolean lastTradeFlag = false; 
      private boolean boldFlag = false;
  
    public MyParserCallback(){
	  
      //Reset the enclosing class' instance variable
	  if (stockVal != 0)
          stockVal = 0f;
	  
	 }
        
    public void handleStartTag(javax.swing.text.html.HTML.Tag t,
      MutableAttributeSet a,int pos) {
        
        if (lastTradeFlag && (t == javax.swing.text.html.HTML.Tag.B )){
            
            boldFlag = true;
       }
        
    }//handleStartTag

    public void handleText(char[] data,int pos){
              
        htmlText  = new String(data);
		
		//System.out.println(htmlText);
			
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
                        
                    // tease out any commas in the number using 
                    //NumberFormat
                        
                    java.text.NumberFormat nf = java.text.NumberFormat.
                      getInstance();
                    
                    Double f = (Double) nf.parse(htmlText);
                    
                    stockVal =  (float) f.doubleValue();
                     
                } catch (java.text.ParseException pe){
                        
                     throw new IllegalStateException(
		            "The extracted text " + htmlText +
                         " cannot be parsed as a number!");
                        
                 }//try
            }//try
            
            lastTradeFlag = false;
            boldFlag = false;
			
	       }//if
                
      } //handleText

  }//MyParserCallback

  public float getLatestPrice() throws IOException,MalformedURLException {

      stockSite = new URL(urlBase + symbol);
       
      webPageStream = new BufferedReader(new InputStreamReader(stockSite.
       openStream()));
	   
      htmlParser = new ParserDelegator();
	   
      callback = new MyParserCallback();//ParserCallback
	   
      synchronized(htmlParser){	
	
          htmlParser.parse(webPageStream,callback,true);

	     }//synchronized
	   
	  //reset symbol
	  symbol = "";

     return stockVal;

  }//getLatestPrice

}//StockPriceBean
