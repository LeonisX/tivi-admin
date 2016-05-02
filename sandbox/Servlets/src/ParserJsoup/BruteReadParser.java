package ParserJsoup;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class BruteReadParser{
    private static int crawlersCount = 20;
	static FileWriter writeFile = null;
	
    public static void main(String args[]) {
	    final String fileName = "/home/leonis/read.txt";
		File textFile = new File(fileName);
		try {
			writeFile = new FileWriter(textFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		BruteReadParser.writeToFile(BookRecord.headers());
		
    	for (int i = 0; i < crawlersCount; i++) {
    		Thread t = new ParserReadThread(i); 
    		t.start();
//    		try {
//				t.join();
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
    	}
    	
    	System.out.println(crawlersCount + " threads started.");
    	
    	
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

class ParserReadThread extends Thread {
	private static String url = "http://read.ru/id/";

	//private static volatile long startValue = 560055;
	private static volatile long startValue = 1;
	private static volatile long endValue = 4361000;
	private int number;
	private long k;
	private boolean isOK;
	private static long dr = 0;
	
	public ParserReadThread(int number) {
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
    			//System.gc();
    		}
    		
    		do {
        		isOK = true;    		
    			String realUrl = url + k + "/";
    			//String s = number + ": " + realUrl;
    			//System.out.println(s);
    			try {
    				ReadCrawler crawler = new ReadCrawler(realUrl);
    				if (crawler.isBooksPage()) {
						//System.out.println("\n" + s);
    					String text = crawler.parse().toString();
    					crawler = null;
    					BruteReadParser.writeToFile(text);
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
    	} while (k < endValue);
	}
	
}