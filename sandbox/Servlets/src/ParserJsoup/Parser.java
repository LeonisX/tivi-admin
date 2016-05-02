package ParserJsoup;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.jsoup.nodes.Document;

//http://info.javarush.ru/translation/2014/12/21/3-%D0%BF%D1%80%D0%B8%D0%BC%D0%B5%D1%80%D0%B0-%D0%BA%D0%B0%D0%BA-%D1%80%D0%B0%D0%B7%D0%BE%D0%B1%D1%80%D0%B0%D1%82%D1%8C-HTML-%D1%84%D0%B0%D0%B9%D0%BB-%D0%B2-Java-%D0%B8%D1%81%D0%BF%D0%BE%D0%BB%D1%8C%D0%B7%D1%83%D1%8F-Jsoup-.html

// дать юнит-тесты на детальную проверку, чтобы обнаруживать редизайн

/**
* Java Program to parse/read HTML documents from File using Jsoup library.
* Jsoup is an open source library which allows Java developer to parse HTML
* files and extract elements, manipulate data, change style using DOM, CSS and
* JQuery like method.
*
* @author Javin Paul
*/
public class Parser{
    Document doc = null;
    // Мапа. Очередь на загрузку.
    static List<PageLink> downloadList;
    // Набор для просмотра - был ли
    static Set<String> visitedSet;
    
    static OzonCrawler crawler = null;
    
    static PageLink pageLink = null;
    
    public static void main(String args[]) {
    	downloadList = Collections.synchronizedList(new LinkedList<PageLink>());
    	visitedSet = Collections.synchronizedSet(new HashSet<String>());
    	
    	//boolean gata = false;
    	String url = "http://www.ozon.ru/reviews/7821666/";
    	downloadList.add(new PageLink(url));
    	visitedSet.add(url);
    	do {
    		pageLink = downloadList.get(0);
    		try {
    			crawler = new OzonCrawler(pageLink.getUrl());
    			if (crawler.isBooksPage()) {
    				crawler.parse();
    			}
    		} catch (Exception e) {
    			pageLink.incErrors();
    			downloadList.add(pageLink);
    		}
    		downloadList.remove(0);
    		
    		List<String> newLinks = crawler.getLinks();
    		for (String link : newLinks) {
    			link = OzonCrawler.correctLink(link, pageLink.getUrl());
    			if (OzonCrawler.isCorrectHost(link)) {
        			if (!visitedSet.contains(link)) {
        				downloadList.add(new PageLink(link));
        				visitedSet.add(link);
        			}
    			}
    		}
    	} while (!downloadList.isEmpty());
    	
		System.out.println("gata!"); 
    	
    	
    	
    	
    	
    	
    	
    	
        // Parse HTML String using JSoup library
//        String HTMLSTring = "<!DOCTYPE html>"
//                + "<html>"
//                + "<head>"
//                + "<title>JSoup Example</title>"
//                + "</head>"
//                + "<body>"
//                + "<table><tr><td><h1>HelloWorld</h1></tr>"
//                + "</table>"
//                + "</body>"
//                + "</html>";
//  
//        Document html = Jsoup.parse(HTMLSTring);
//        String title = html.title();
//        String h1 = html.body().getElementsByTag("h1").text();
//  
//        System.out.println("Input HTML String to JSoup :" + HTMLSTring);
//        System.out.println("After parsing, Title : " + title);
//        System.out.println("Afte parsing, Heading : " + h1);
//        System.out.println();

      		
       		//System.out.println(doc.html());
       		
       		
       		

       
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        System.out.println();
  
//        System.out.println("Jsoup Can read HTML page from URL, title : " + title);
//        // JSoup Example 3 - Parsing an HTML file in Java
//        //Document htmlFile = Jsoup.parse("login.html", "ISO-8859-1"); // wrong
//        Document htmlFile = null;
//        try {
//            htmlFile = Jsoup.parse(new File("login.html"), "ISO-8859-1");
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } // right
//        title = htmlFile.title();
//        Element div = htmlFile.getElementById("login");
//        String cssClass = div.className(); // getting class form HTML element
//
//        System.out.println("Jsoup can also parse HTML file directly");
//        System.out.println("title : " + title);
//        System.out.println("class of div tag : " + cssClass);
    

}}

class PageLink {
	String url;
	int errors;
	
	public PageLink() {
		url = "";
		errors = 0; 
	}
	
	public PageLink(String url) {
		super();
		this.url = url;
	}
	
	public String getUrl() {
		return url;
	}
	
	public int getErrors() {
		return errors;
	}
	
	public void incErrors() {
		errors ++;
	}
}