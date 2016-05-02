package ParserJsoup;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class BruteOzonParser{
    private static int crawlersCount = 20;
	static FileWriter writeFile = null;
	
    public static void main(String args[]) {
	    final String fileName = "/home/leonis/ozon.txt";
		File textFile = new File(fileName);
		try {
			writeFile = new FileWriter(textFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		BruteOzonParser.writeToFile(BookRecord.headers());
		
    	for (int i = 0; i < crawlersCount; i++) {
    		Thread t = new ParserOzonThread(i); 
    		t.start();
//    		try {
//				t.join();
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
    	}
    	
    	
//	    if (writeFile != null) {
//	        try {
//	            writeFile.close();
//	        } catch (IOException e) {
//	            e.printStackTrace();
//	        }
//	    }
		//System.out.println("gata!"); 
    }
    
	public static synchronized void writeToFile(String text) {
		try {
	    	writeFile.append(text + "\n");
	    	writeFile.flush();
		} catch (IOException e) {
		    e.printStackTrace();
		}
	}
}

class ParserOzonThread extends Thread {
	private static String url = "http://www.ozon.ru/context/detail/id/";

	//private static volatile long startValue = 74134;
	private static volatile long startValue = 83000;
	//private static volatile long startValue = 7821666;
	private int number;
	private long k;
	private boolean isOK;
	private static long dr = 0;
	
	public ParserOzonThread(int number) {
		this.number =number;
	}
	
	@Override
	public void run() {
		if (number == 0) {
//	    	BruteOzonParser.writeToFile(BookRecord.headers());
		}
    	do {
    		k = startValue++;
    		try {
				sleep(5);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
    		
    		if ((number == 0) && ((k - dr) >= 1000)) {
    			dr = k/1000*1000;
    			System.out.println(dr);
    		}
    		
    		do {
        		isOK = true;    		
    			String realUrl = url + k + "/";
    			//String s = number + ": " + realUrl;
    			//System.out.println("\n" + s);
    			try {
    				OzonCrawler crawler = new OzonCrawler(realUrl);
    				if (crawler.isBooksPage()) {
						//System.out.println("\n" + s);
    					BruteOzonParser.writeToFile(crawler.parse().toString());
    					//System.out.println(s + crawler.getDoc().title());
    				}
    			} catch (IOException e) {
    				//System.out.println(s + " - " + e.getMessage());
    				if (!e.getMessage().equals("HTTP error fetching URL")) {
    					isOK = false;
    				}}
//    			} catch (NullPointerException ne) {
//    				//System.out.println(s + ne.getStackTrace());
//    				isOK = false;
//    			}
    		} while (!isOK);
    	} while (k < Long.MAX_VALUE - 500);
	}
	
}