//read.ru
//livelib.ru

package ParserJsoup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

//import java.io.OutputStreamWriter;

//import javafx.application.Application;
//import javafx.beans.value.ChangeListener;
//import javafx.beans.value.ObservableValue;
//import javafx.concurrent.Worker;
//import javafx.concurrent.Worker.State;
//import javafx.scene.Group;
//import javafx.scene.Scene;
//import javafx.scene.control.ScrollPane;
//import javafx.scene.layout.VBox;
//import javafx.scene.web.WebEngine;
//import javafx.scene.web.WebView;
//import javafx.stage.Stage;

public class OzonCrawler {
	private Document doc = null;
	static private final String host = "ozon.ru";
	private BookRecord bookRecord = null;
	
	public static String correctLink(String link, String referral) {
		if (link.startsWith("/")) {
			return "http://www." + host + link;			
		}
		if (link.startsWith("./")) {
			System.out.println("Unsupported url: " + link);
		}
		if (link.startsWith("../")) {
			System.out.println("Unsupported url" + link);
		}
		return link;
	}
	
	public Document getDoc() {
		return doc;
	}
	
	public OzonCrawler(String url) throws IOException {
		doc = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows NT 6.3; rv:36.0) Gecko/20100101 Firefox/36.0").timeout(10000).get();
	}
	
	public OzonCrawler(Document doc) throws IOException {
		this.doc = doc;
	}
	

	public boolean isBooksPage() {
        //<a class="eBreadCrumbs_link " href="/context/div_book/">Книги</a>
        //<a onclick="var x=&quot;.tl(&quot;;s_objectID=&quot;http://www.ozon.ru/context/div_book/_2&quot;;return this.s_oc?this.s_oc(e):true" class="eBreadCrumbs_link " href="/context/div_book/">Книги</a>  <a onclick="var x=&quot;.tl(&quot;;s_objectID=&quot;http://www.ozon.ru/context/nonfiction/_2&quot;;return this.s_oc?this.s_oc(e):true" class="eBreadCrumbs_link " href="/context/nonfiction/">Нехудожественная литература</a>  <a onclick="var x=&quot;.tl(&quot;;s_objectID=&quot;http://www.ozon.ru/catalog/1137926/_1&quot;;return this.s_oc?this.s_oc(e):true" class="eBreadCrumbs_link " href="/catalog/1137926/">Компьютерная литература</a>  <a onclick="var x=&quot;.tl(&quot;;s_objectID=&quot;http://www.ozon.ru/catalog/1139453/_1&quot;;return this.s_oc?this.s_oc(e):true" class="eBreadCrumbs_link " href="/catalog/1139453/">Языки и системы программирования</a>  <a onclick="var x=&quot;.tl(&quot;;s_objectID=&quot;http://www.ozon.ru/catalog/1139482/_1&quot;;return this.s_oc?this.s_oc(e):true" class="eBreadCrumbs_link " href="/catalog/1139482/">Java, J++. Языки программирования</a>
		Element el = doc.getElementsByClass("eBreadCrumbs_link").first();
		if (el == null) return false;
        return el.text().equals("Книги");
    }
    
	public BookRecord parse() {
		bookRecord = new BookRecord();
		
		//категории
        Elements links = doc.getElementsByClass("eBreadCrumbs_link");
        for (Element link : links) {
        	bookRecord.appendValue("category", link.text());
       	}
		
        //<img class="eMicroGallery_fullImage mVisible" src="//static1.ozone.ru/multimedia/books_covers/c300/1005131292.jpg">
        String imageUrl = doc.getElementsByClass("eMicroGallery_fullImage").first().attr("src");
        if (!(imageUrl == null || imageUrl.isEmpty())) {
        	imageUrl = imageUrl.substring(2);
        	bookRecord.setValue("imageUrl", "http://" + imageUrl.replace("c300/", ""));
        }

        // <h1 class="fn" itemprop="name">Java. Полное руководство</h1>
        bookRecord.setValue("title", doc.select("h1.fn").first().text());

        // тут можно получать ID (впоследствии, например, использовать для получения цены)
        bookRecord.setValue("ozonId",doc.getElementsByClass("eDetail_ProductId").first().text().substring(3));
            
        // <div class="hidden" itemprop="ratingValue">4</div>
        // <div class="hidden" itemprop="reviewCount">17</div>
        Element content = doc.getElementById("rating_bar");
        Elements divHidden = content.select("div.hidden");
        for (Element div : divHidden) {
        	if (div.attr("itemprop").equals("ratingValue")) {
        		bookRecord.setValue("rating", div.text());
        	} else {
        		bookRecord.setValue("reviewCount", div.text());
        	}
        }

        // <span class="eVoted" id="likeButtonCountText">97</span> 
        // <span class="eVoted" id="notlikeButtonCountText">14</span>
        bookRecord.setValue("likes", doc.getElementById("likeButtonCountText").text());
        bookRecord.setValue("disLikes", doc.getElementById("notlikeButtonCountText").text());

       	// <div class="bDetailLogoBlock">  
       	// <p> Java 7: The Complete Reference            		  </p>  
        content = doc.getElementsByClass("bDetailLogoBlock").first();
       	Elements allP = content.getElementsByTag("P");
       	//вероятно надо проверять на itemprop
//       	Element originalTitle = allP.first();
//       	System.out.println(originalTitle.text());
       	String isbn="";
       	for (Element p: allP) {
       		switch (p.attr("itemprop")) {
       			// <p itemprop="author"> Автор: <a onclick="..." href="/person/265199/" title="Герберт Шилдт">Герберт Шилдт</a>         		  </p>
       			case "author" :
       				links = p.getElementsByTag("A");
       				for (Element link : links) {
       					bookRecord.appendValue("author", link.text());   
       				}
       				break;
       			// <p itemprop="publisher"> Издательство: <a onclick="..." href="/brand/856490/" title="Вильямс">Вильямс</a>            		  </p>	
       			case "publisher" :
       				links = p.getElementsByTag("A");
       				for (Element link : links) {
       					bookRecord.appendValue("publisher", link.text());
       				}
       				break;
       			// <p itemprop="isbn"> ISBN 978-5-8459-1759-1; 2012 г.            		  </p>  </div>
       			// разбирать на год выпуска
       			// может быть просто 2002 г. 
       			// ISBN 5-03-003265-7, 1-884133-08-8; 12/1/1999 г.
       			// ISBN 5-224-00183-8; 12/1/1999 г. 
       			case "isbn" :
       				isbn = p.text();
       				isbn = isbn.replace("ISBN ", "");
   					isbn = isbn.replace(", ", ",");
       				int k = isbn.indexOf(";"); 
       				if (k == -1) {
       					//только год
       					bookRecord.setValue("year", isbn.trim().replace(" г.", ""));
       				} else {
       					//isbn, вероятно год
       					isbn = isbn.replace(",", ";");
       					bookRecord.setValue("year", isbn.substring(k+1).trim().replace(" г.", ""));
       					bookRecord.setValue("isbn", isbn.substring(0, k).trim());
       				}
       				break;
       			case "inLanguage" :			// аудиокниги
       				String language = p.text().replace("Языки: ", "");
       				language = language.replace("Язык: ", "");
       				bookRecord.setValue("language", language);
       				break;
       			// <p> Серия: <a onclick="..." href="/context/detail/id/1007728/" title="Полный справочник">Полный справочник</a>            		  </p>
       			default:
       				// http://www.ozon.ru/context/detail/id/635809/?item=74954
       				//<p class="js_ReleaseYear">Не указан, 1997 г.</p>
       				if (p.className().equals("js_ReleaseYear")) {
       					bookRecord.setValue("year", p.text().replace(" г.", ""));
       					break;
       				}
       				
       				// перебором загоняем данные в базу
       				String workString = p.text().trim();
       				
       				if (workString.startsWith("Серия: ")) {
       					bookRecord.setValue("serie", workString.replace("Серия: ", ""));
       					break;
       				}
       				
       				if (workString.startsWith("Составитель: ")) {
       					bookRecord.setValue("compiler", workString.replace("Составитель: ", ""));
       					break;
       				}
       				
       				if (workString.startsWith("Составители: ")) {
       					bookRecord.appendValue("compiler", workString.replace("Составители: ", ""));
       					break;
       				}
       				
       				if (workString.startsWith("Редактор: ")) {
       					bookRecord.setValue("editor", workString.replace("Редактор: ", ""));
       					break;
       				}

       				if (workString.startsWith("Переводчик: ")) {
       					bookRecord.setValue("translator", workString.replace("Переводчик: ", ""));
       					break;
       				}
       				if (workString.startsWith("Возрастные ограничения: ")) {
       					bookRecord.setValue("age", workString.replace("Возрастные ограничения: ", ""));
       					break;
       				}
       				
       				if (workString.startsWith("Сохраннось: ")) {
       					// не сохраняем
       					break;
       				}   
       				
       				k = p.text().indexOf(":");
       				// додумать
       				if (k == -1) {
       					bookRecord.appendValue("comment", p.text());
       				} else {
       					bookRecord.appendValue("comment", p.text());
       				}
       				// Серия:
       				// коллизия. Следует вычитывать слово и в зависимости от него уже задавать атрибут
       		       	// "Антология", оригинальное название, "Цифровая книга", "Авторский сборник"
//       				if (!publisher.isEmpty()) {
//       					a = p.getElementsByTag("A").first();
//           				serie = a.text();
//           				System.out.println(serie);
//       				}
       				// <p>Составитель:<a title="В. Левадный" href="/person/284793/"></a></p>
       				// <p> Редактор: <a href="/person/263543/" title=" Белиоглов Е. В."> Белиоглов Е. В.</a></p>
       				// <p> Переводчик: <a href="/person/253068/" title="В. Бродовой">В. Бродовой</a></p>
       				// <p> Иллюстратор: <a href="/person/242055/" title=" Дюрант Вил"> Дюрант Вил</a></p>
       				// <p> <a href="/context/detail/id/2661395/" title="Сохранность">Сохранность</a>: Хорошая </p>
       				// <p> Возрастные ограничения: 14+ </p>
       				// <p> Букинистическое издание</p>
       		}
       	}
       		
       		// <div class="bTechDescription">
       		// <div class="bTechCover "><div class="bTechLine"></div> 
       		// 		<div class="bTechName"><span class="eName">Переводчик</span></div> 
       		// 		<div class="bTechDescr"><span><a onclick="..." href="/person/279936/" title="В. Коваленко">В. Коваленко</a></span></div> 
       		// </div>  

       		// Переводчики
       		// Язык
       		// Страниц
       		// Формат
       		// Тираж
       		// Переплет
       		// Формат аудиокниги
       		// Тип упаковки
       		// Возрастные ограничения
       		// Цветные иллюстрации

       		
       		Elements bTechCovers = doc.getElementsByClass("bTechDescription").first().getElementsByClass("bTechCover");
       		for (Element bTechCover : bTechCovers) {
       			Element bTechName = bTechCover.getElementsByClass("bTechName").first().getElementsByTag("SPAN").first();
       			Element bTechDescr = bTechCover.getElementsByClass("bTechDescr").first().getElementsByTag("SPAN").first();
       			//System.out.println(bTechName.text() + ": " + bTechDescr.text());
       			
   				// "language", "pages", "format",
       			switch (bTechName.text()) {
       				case "Язык":
       					bookRecord.setValue("language", bTechDescr.text());
       					break;
       				case "Языки":
       					bookRecord.setValue("language", bTechDescr.text());
       					break;
       				case "Страниц":
       					bookRecord.setValue("pages", bTechDescr.text());
       					break;
       				case "Формат":
       					bookRecord.setValue("format", bTechDescr.text());
       					break;
       				case "Тираж":
       					bookRecord.setValue("edition", bTechDescr.text().replace(" экз.", ""));
       					break;
       				case "Переплет":
       					bookRecord.setValue("paperback", bTechDescr.text());
       					break;
       				case "Переводчики":
       					bookRecord.appendValue("translator", bTechDescr.text());
       					break;
       				case "Переводчик":
       					bookRecord.appendValue("translator", bTechDescr.text());
       					break;
  					default:
  		       			bookRecord.appendValue("comment", bTechName.text() + ":" + bTechDescr.text());       						
       			}
       		}
       		
       		// содержание
       		Element soderj = doc.getElementById("js_specs");
       		if (soderj != null) {
           		Elements TDs = soderj.getElementsByTag("TD");
       			for (Element td : TDs) {
       				String tds = td.text().trim();
       				if (tds.isEmpty()) continue;
       				if (tds.endsWith("Сказка, стр.")) {
       					continue;
       				}
   					if (tds.startsWith("стр. ")) {
   						bookRecord.appendValue("otherText", tds, ". ");
   					} else {
   						bookRecord.appendValue("otherText", tds);
       				}
       			}
       		}
       		
       		// дополнительно парсить
       		Elements TDs = doc.getElementById("detail_description").getElementsByTag("TD");
       		for (Element td : TDs) {
       			String tds = td.html();
       			tds = tds.replace("От производителя", "");
       			tds = tds.replace("<!-- ANNOTATION -->", "");
       			tds = tds.replace("<!-- Data[ANNOTATION] -->", "");
       			//tds = tds.replace("<div class=\"eDetail_SectionHeader\">", "");
       			tds = tds.replace(" class=\"eDetail_SectionHeader\"", "");
       			tds = tds.replace("От Ozon.ru", "\n");
       			tds = tds.replace("<!-- COMMENT -->", "");
       			tds = tds.replace("<!-- Data[COMMENT] -->", "");
       			//tds = tds.replace("</div>", "");
       			tds = tds.replace("\n", "<br />");
       			tds = tds.trim();	
       			if (tds.isEmpty()) continue;
       			bookRecord.appendValue("description", tds);
       		}

//    		Ваша цена: <div class="eSaleBlock_yourPrice">  </div>  </div> <div class="bSale_BasePriceCover">  <div class="bOzonPrice"> <span class="eOzonPrice_main">848</span> <span class="eOzonPrice_submain">00</span> <span class="bRub">руб</span>
       		Element price = doc.getElementsByClass("eOzonPrice_main").first();
       		if (price != null) {
       			bookRecord.setValue("price", price.text());
       		}
       		Element subPrice = doc.getElementsByClass("eOzonPrice_submain").first();
       		if (subPrice != null) {
       			bookRecord.appendValue("price", subPrice.text(), ".");
       		}
       		Element currency = doc.getElementsByClass("bRub").first();
       		if (currency != null) {
       			bookRecord.setValue("currency", currency.text());
       		}
			return bookRecord;

       		
       		// отработка комментариев
       		// подгружаются JS, так что вижу только первый.
       		// решения - JavaFX, HtmlUnit (очень большой) 
//       		Elements comments = doc.getElementsByClass("item");
//       		System.out.println("size=" + comments.size());
//       		for (Element comment : comments) {
//       			System.out.println(comment.getElementsByClass("no").first().getElementsByTag("P").first().text());
//       			System.out.println(comment.getElementsByClass("yes").first().getElementsByTag("P").first().text());
//       			System.out.println(comment.getElementsByTag("STRONG").first().text());
//       			System.out.println(comment.select("span[itemprop]").first().attr("content"));
//       			
//       			Elements ps = comment.getElementsByTag("P");
//       			for (Element p : ps) {
//       				if (p.attr("itemprop").equals("author")) {
//       					a = p.getElementsByTag("A").first();
//       					String commentAuthor = p.html();
//       					commentAuthor = commentAuthor.replace(" " + a.outerHtml(), "");
//       					System.out.println(commentAuthor);
//       				}
//       				if (p.attr("itemprop").equals("description")) {
//       					System.out.println(p.text());
//       				}
//       			}
//   				String meta = comment.getElementsByTag("meta").first().attr("content");
//   				System.out.println(meta);
//       		}
	}

	
	static public boolean isCorrectHost(String url) {
		if (url.endsWith(".jpg")) return false;
		if (url.contains("/context/cart/")) return false;
		if (url.contains("/context/summer-sale/")) return false;
		if (url.contains("/context/school2015/")) return false;
		if (url.contains("/context/new/")) return false;
		if (url.contains("/context/div_tech/")) return false;
		if (url.contains("/context/div_appliance/")) return false;
		if (url.contains("/context/div_home/")) return false;
		if (url.contains("/context/div_kid/")) return false;
		if (url.contains("/context/div_beauty/")) return false;
		if (url.contains("/context/div_bs/")) return false;
		if (url.contains("/context/div_fashion/")) return false;
		if (url.contains("/context/div_soft/")) return false;
		if (url.contains("/context/div_dvd/")) return false;
		if (url.contains("/context/div_music/")) return false;
		if (url.contains("/context/div_rar/")) return false;
		if (url.contains("/context/certificat/")) return false;
		if (url.contains("/context/div_travel/")) return false;
		if (url.contains("/context/discount-goods/")) return false;
		if (url.contains("/context/div_egoods/")) return false;
		if (url.contains("/context/mobile/")) return false;
		if (url.contains("/context/tabletpc/")) return false;
		if (url.contains("/context/ebooks/")) return false;
		if (url.contains("/context/laptop/")) return false;
		if (url.contains("/context/comp/")) return false;
		if (url.contains("/context/photo/")) return false;
		if (url.contains("/context/digital/")) return false;
		if (url.contains("/context/hifi/")) return false;
		if (url.contains("/context/gps/")) return false;
		if (url.contains("/context/clock/")) return false;
		if (url.contains("/context/tech_accessory/")) return false;
		if (url.contains("/context/accessories")) return false;
		if (url.contains("/context/largeapp/")) return false;
		if (url.contains("/context/app_")) return false;
		if (url.contains("/context/coffee-tee/")) return false;
		if (url.contains("/context/kitchen/")) return false;
		if (url.contains("/context/textile/")) return false;
		if (url.contains("/context/interior/")) return false;
		if (url.contains("/context/homerepair/")) return false;
		if (url.contains("/context/chemical/")) return false;
		if (url.contains("/context/sauna/")) return false;
		if (url.contains("/context/broderie/")) return false;
		if (url.contains("/context/garden/")) return false;
		if (url.contains("/context/illumination/")) return false;
		if (url.contains("/context/animals/")) return false;
		if (url.contains("/context/cargoods/")) return false;
		if (url.contains("/context/sanengineering/")) return false;
		if (url.contains("/context/picnic/")) return false;
		if (url.contains("/context/toy/")) return false;
		if (url.contains("/context/toys/")) return false;
		if (url.contains("/context/designers/")) return false;
		if (url.contains("/context/creation/")) return false;
		if (url.contains("/context/school/")) return false;
		if (url.contains("/context/mums/")) return false;
		if (url.contains("/context/baby-toys/")) return false;
		if (url.contains("/context/writing/")) return false;
		if (url.contains("/context/safechild/")) return false;
		if (url.contains("/context/child")) return false;
		if (url.contains("/context/cosmotheca/")) return false;
		if (url.contains("/context/decorcosmetics/")) return false;
		if (url.contains("/context/massage-table/")) return false;
		if (url.contains("/context/haircare/")) return false;
		if (url.contains("/context/facecare/")) return false;
		if (url.contains("/context/hygiene/")) return false;
		if (url.contains("/context/cosmaccessories/")) return false;
		if (url.contains("/context/sets/")) return false;
		if (url.contains("/context/glasses/")) return false;
		if (url.contains("/context/hcosm/")) return false;
		if (url.contains("/context/sport/")) return false;
		if (url.contains("/context/tourism/")) return false;
		if (url.contains("/context/bs_fishing/")) return false;
		if (url.contains("/context/luggage/")) return false;
		if (url.contains("/context/book_writing/")) return false;
		if (url.contains("/context/perfum/")) return false;
		if (url.contains("/context/clothing")) return false;
		if (url.contains("/context/shoes/")) return false;
		if (url.contains("/context/child-shoes/")) return false;
		if (url.contains("/context/haberdashery/")) return false;
		if (url.contains("/context/scarves/")) return false;
		if (url.contains("/context/belt/")) return false;
		if (url.contains("/context/accessories/")) return false;
		if (url.contains("/context/caps/")) return false;
		if (url.contains("/context/bijouterie/")) return false;
		if (url.contains("/context/jewelry/")) return false;
		if (url.contains("/context/vintages/")) return false;
		if (url.contains("/context/actionsoft/")) return false;
		if (url.contains("/context/pcgame/")) return false;
		if (url.contains("/context/console/")) return false;
		if (url.contains("/context/gamebox/")) return false;
		if (url.contains("/context/soft/")) return false;
		if (url.contains("/context/edusoft/")) return false;
		if (url.contains("/context/movie/")) return false;
		if (url.contains("/context/kino/")) return false;
		if (url.contains("/context/video/")) return false;
		if (url.contains("/context/detfilm/")) return false;
		if (url.contains("/context/dvdmusic/")) return false;
		if (url.contains("/context/jazz/")) return false;
		if (url.contains("/context/pop/")) return false;
		if (url.contains("/context/theremin/")) return false;
		if (url.contains("/context/rock/")) return false;
		if (url.contains("/context/classic/")) return false;
		if (url.contains("/context/wom/")) return false;
		if (url.contains("/context/vinyl/")) return false;
		if (url.contains("/context/actionantiques/")) return false;
		if (url.contains("/context/art/")) return false;
		if (url.contains("/context/rarity/")) return false;
		if (url.contains("/context/drawing/")) return false;
		if (url.contains("/context/vintjewellery/")) return false;
		if (url.contains("/context/vintage/")) return false;
		if (url.contains("/context/collecting/")) return false;
		if (url.contains("/context/author-antiques/")) return false;
		if (url.contains("/context/faberge/")) return false;
		if (url.contains("/context/div_luxury_gifts/")) return false;
		if (url.contains("/context/game/")) return false;
		if (url.contains("/context/soft_home/")) return false;
		if (url.contains("/context/travel")) return false;
		if (url.contains("/context/help/")) return false;
		if (url.contains("/status/")) return false;
		if (url.contains("/context/login/")) return false;
		if (url.contains("/context/newuser/")) return false;
		if (url.contains("?context=")) return false;
						
		if (url.startsWith("http://www." + host)) return true;
		if (url.startsWith("http://" + host)) return true;
		if (url.startsWith("https://www." + host)) return true;
		if (url.startsWith("https://" + host)) return true;
		if (url.startsWith("ftp://" + host)) return true;
		
		return false;
	}
	
	
	public List<String> getLinks() {
		List<String> result = new ArrayList<String>();
		Elements links = doc.getElementsByTag("A");
		for (Element link : links) {
			result.add(link.attr("href"));
		}
		return result;
	}
}

//<div class="item" name="comment_455984" id="comment_455984" itemprop="review" itemscope="" itemtype="http://schema.org/Review"> 
//<div class="content"> 
//	<div class="rating_form"> 
//		<div class="vote"> 
//			<div> 
//				<div class="no"><p>4</p></div> 
//				<div class="yes"><p>24</p></div> 
//				<div class="vote-persent"> 
//					<div style="width: 85%">&nbsp;</div> 
//				</div> 
//			</div> 
//			<p class="question hidden">Полезен ли отзыв?</p> 
//			<div class="hidden"> </div> 
//		</div> 
//	</div> 
//	<p><strong itemprop="name">Хороший выбор.</strong>, 
//	<span itemprop="datePublished" content="2012-05-18"> 18 мая 2012 г.</span> </p> 
//	<p class="misc" itemprop="author">Kobets Sergey (24 года) 
//	<a rel="nofollow" href="/context/client_opinion/ClientGuid/59243545-dfb2-411d-b7d2-954e61d4b74f/"><noindex>все отзывы</noindex></a> </p>
//	<p itemprop="description">Отличная литература, для тех, кто знаком с программированием и не любит когда материал преподносят, как для детей даунов :)</p> 
//</div> 
//<div class="stars stars5" itemprop="reviewRating" itemscope="" itemtype="http://schema.org/Rating">
//	<meta itemprop="ratingValue" content="5">
//	<div>&nbsp;</div>
//</div>
//</div>  
//
//<div class="item" name="comment_564981" id="comment_564981" itemprop="review" itemscope="" itemtype="http://schema.org/Review"> 
//<div class="content"> 
//	<div class="rating_form"> 
//		<div class="vote"> 
//			<div> 
//				<div class="no"><p>0</p></div> 
//				<div class="yes"><p>6</p></div> 
//				<div class="vote-persent"> 
//					<div style="width: 100%">&nbsp;</div> 
//				</div> 
//			</div> 
//			<p class="question hidden">Полезен ли отзыв?</p> 
//			<div class="hidden"> </div> 
//		</div> 
//	</div> 
//	<p><strong itemprop="name">Книга для начинающих программистов</strong>, 
//	<span itemprop="datePublished" content="2013-04-21"> 21 апреля 2013 г.</span> </p> 
//	<p class="misc" itemprop="author">Дамир (20 лет) <a rel="nofollow" href="/context/client_opinion/ClientGuid/2d272d3e-9474-4528-acbd-5773a46f0d36/"><noindex>все отзывы</noindex></a> </p>
//	<p itemprop="description">Книга по основам J2SE, все рассказывается доступным языком. Мне как перешедшему с c# все доступно, понятно.</p> 
//</div> 
//<div class="stars stars4" itemprop="reviewRating" itemscope="" itemtype="http://schema.org/Rating">
//	<meta itemprop="ratingValue" content="4">
//	<div>&nbsp;</div>
//</div> 
//</div>



//=========================================================================================

//JavaFX, не работает
//loadPage("http://www.ozon.ru/context/detail/id/7821666/");



//@SuppressWarnings("unused")
//private static void loadPage(String url) {
//    final WebView browser = new WebView();
//    final WebEngine webEngine = browser.getEngine();
//
//    ScrollPane scrollPane = new ScrollPane();
//    scrollPane.setContent(browser);
//    
//    webEngine.getLoadWorker().stateProperty().addListener(
//        new ChangeListener<State>() {
//          @SuppressWarnings("rawtypes")
//		@Override public void changed(ObservableValue ov, State oldState, State newState) {
//
//              if (newState == Worker.State.SUCCEEDED) {
//                parseDocument(webEngine.getDocument().getTextContent());
//                System.out.println("called");
//            }
//              
//            }
//
//		private void parseDocument(String textContent) {
//		}
//        });
//    webEngine.load(url);
//}
