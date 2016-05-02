package ParserJsoup;

import java.io.IOException;
import java.net.MalformedURLException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

// увы, выбрасывает ошибку при обработке JS :(

public class HtmlUnitTest {

	public static void main(String[] args) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		try (final WebClient webClient = new WebClient(BrowserVersion.FIREFOX_38)) {
	        final HtmlPage page = webClient.getPage("http://www.ozon.ru/context/detail/id/8237920/");
	        Document doc = Jsoup.parse(page.asText());
	        OzonCrawler crawler = new OzonCrawler(doc);
	        crawler.parse();
	    }
	}
}
