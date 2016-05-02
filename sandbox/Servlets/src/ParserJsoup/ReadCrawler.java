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

public class ReadCrawler {
	private Document doc = null;
	static private final String host = "read.ru";
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
	
	public ReadCrawler(String url) throws IOException {
		doc = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows NT 6.3; rv:36.0) Gecko/20100101 Firefox/36.0").timeout(10000).get();
	}
	
	public ReadCrawler(Document doc) throws IOException {
		this.doc = doc;
	}
	

	public boolean isBooksPage() {
        //<a class="progress-quick-links" itemprop="url" href="/books/"><span itemprop="title">Книги</span></a>
		Element el = doc.getElementsByClass("progress-quick-links").first();
		if (el == null) return false;
        return el.text().equals("Книги");
    }
    
	public BookRecord parse() {
		bookRecord = new BookRecord();
		
		//категории
        Elements links = doc.getElementsByClass("progress-quick-links");
        for (Element link : links) {
        	bookRecord.appendValue("category", link.text());
       	}
		
        //<img id="cover_img" class="book_image__cover" alt="Изучаем Java EE 7" src="//static1.read.ru/covers_rr/b/84/22/3802284.jpg"></img>
        String imageUrl = doc.getElementsByClass("book_image__cover").first().attr("src");
        if (!(imageUrl == null || imageUrl.isEmpty())) {
        	bookRecord.setValue("imageUrl", "http:" + imageUrl);
        }

        // <h1 class="book_card__header">Изучаем Java EE 7</h1>
        bookRecord.setValue("title", doc.select("h1.book_card__header").first().text().trim());

        // <span class="dNone" itemprop="average">4.46</span>
        String rating = doc.select("span.dNone").first().text();
        if (!rating.equals("0")) {
        	bookRecord.setValue("rating", "" + rating);
        }

        
        // <a class="book_card__voting_total total_reviews book_card3__voting_total" href="#"><span itemprop="count">48</span>отзывов</a>
        // <a class="book_card__voting_total total_reviews book_card3__voting_total" href="#" style="display:none;"><span itemprop="count"></span>&nbsp;отзывов</a>
        String reviewCount = doc.select("a.book_card__voting_total").first().text();
        reviewCount = reviewCount.substring(0, reviewCount.indexOf("о")-1);
   		bookRecord.setValue("reviewCount", reviewCount);
        
        // <div class="j-book_autors book_autors">Автор:<span><span><a href="/author/86056/">Герберт Шилдт</a></span></span>
   		String author = doc.select("div.book_autors").first().text().replace("Автор:", "").trim();
        bookRecord.setValue("author", author.replace(", ", ";"));
        
        // издатель, 
        // <ul class="book_properties"><li class="book_properties__item">
        // <span><span>Твердый переплет. Плотная бумага или картон</span></span></li></ul>
        // <ul class="book_properties"><li class="book_properties__item j-book_pub">
        // <span><span><a href="/pubhouse/2392/">Издательство: Вильямс</a></span></span>
        //<li multilinks-noscroll="true" class="book_properties__item j-book_series">Серия:
        //<span multilinks-noscroll="true">
        //<span multilinks-noscroll="true"><a multilinks-noscroll="true" href="/series/16085/">Провинцiальный детективъ</a></span>
        //</span>

        Elements lis = doc.select("li.book_properties__item");
        for (Element li : lis) {
        	if (li.classNames().contains("j-book_pub")) {
        		String publisher = li.text().replace("Издательство: ", "");
        		bookRecord.setValue("publisher", publisher.replace(", ", ";"));
        	} else if (li.classNames().contains("j-book_series")) {
        		String serie = li.text().replace("Серия: ", "");
        		bookRecord.setValue("serie", serie);
        	} else {
        		if (li.text().contains("переплет")) {
        			bookRecord.setValue("paperback", li.text().replace(", ", ","));
        		} else {
        			bookRecord.appendValue("comment", li.text());
        		}
        	}
        }
        
        
        
        // <li class="book_properties__item-promo">Год выпуска: <span class="book_field">2015</span>
        // <br></br>ID: <span class="book_field">3983814</span><br></br>
        // <li class="book_properties__item-promo">Кол. страниц: <span class="book_field">1376</span>
        // <br></br>ISBN: <span><span>978-5-8459-1918-2</span></span> <br></br>
        
        // <li class="book_properties__item-promo"><br>ID:<span class="book_field">560060</span>
        // <li class="book_properties__item-promo"><br><br></li>

        lis = doc.select("li.book_properties__item-promo");
   		Elements spans = lis.get(0).select("span");
   		if (spans.size() == 2) {
   			bookRecord.setValue("year", spans.get(0).text());
   			bookRecord.setValue("readId", spans.get(1).text());
   		} else {
   			// полюбому будет ID
   			bookRecord.setValue("readId", spans.get(0).text());
   		}
   		// вторая запись. не оптимизировано
   		spans = lis.get(1).select("span");
   		int isbnBebin = 0;
   		if (spans.size() > 0) {
   			if (lis.get(1).text().contains("Кол. страниц:")) {
   				bookRecord.setValue("pages", spans.get(0).text());
   				isbnBebin++;
   			}
   			if (lis.get(1).text().contains("ISBN:")) {
   				//System.out.println(spans.get(isbnBebin).text());
   	   			bookRecord.appendValue("isbn", spans.get(isbnBebin).text().replace(", ",  ";"));
   	   		}
   		}
   		
  		// <div class="book_price3__fullprice">2 550<span class="book_price3__fullprice_currency">руб</span></div>
  		Element div = doc.select("div.book_price3__fullprice").first();
  		if (div != null ) {
  			Element span = doc.select("span.book_price3__fullprice_currency").first();
  			String currency = span.text();
  			String price = div.text().replace(currency, "");
  			bookRecord.setValue("price", price.replace(" ", ""));
  			bookRecord.setValue("currency", currency);
  		}
        
  		// <div id="descr_text">
  		// ava — один из самых важных и широко применяемых языков программирования в мире на протяжении многих лет. В отличие от некоторых других языков программирования, влияние Java не только не уменьшилось со временем, а, наоборот, возросло. С момента первого выпуска он выдвинулся на передний край программирования приложений для Интернета. И каждая последующая версия лишь укрепляла эту позицию. Ныне Java по-прежнему остается первым и самым лучшим языком для разработки веб-ориентированных приложений. Проще говоря, большая часть современного кода написана на Java. И это свидетельствует об особом значении языка Java для программирования.<br>
  		// Основная причина успеха Java - его гибкость. Начиная с первой версии 1.0, этот язык непрерывно адаптируется к изменениям в среде программирования и подходам к написанию программ. А самое главное - он не просто следует тенденциям в программировании, а помогает, их создавать. Способность Java адаптироваться к быстрым изменениям в вычислительной технике служит основной причиной, по которой этот язык программирования продолжается оставаться столь успешным.
  		// </div>
  		bookRecord.appendValue("description", doc.getElementById("descr_text").html().replace("\n", ""));
  		
  		// <table id="book_fields_1" class="book_fields"><tr>
  	    // <td class="f">Штрихкод:</td>
  		// <td class="ean"><span><span>9785845919182</span></span></td></tr>
  		// <td class="age"></td>
  		// <td class="paper"></td>
  		// <td class="weight"></td>
  		// <td class="size"></td>
  		// <td class="edition"></td>
  		// <td class="lit_form"></td>
  		// <td class="information"></td>
  		// <td class="book_illustrations"></td>
  		// <td class="translator">

  		Element table = doc.getElementById("book_fields_1");
  		Elements tds = table.getElementsByTag("TD");
  		for (Element td : tds) {
  			if (td.className().equals("f")) continue;
  			switch (td.className()) {
  				case "ean":
  					bookRecord.setValue("ean", td.text());
  					break;
  				case "age":
  					bookRecord.setValue("age", td.text());
  					break;
  				case "paper":
  					bookRecord.appendValue("comment", "Бумага: " + td.text());
  					break;
  				case "weight":
  					bookRecord.setValue("weight", td.text());
  					break;
  				case "size":
  					bookRecord.setValue("format", td.text().replace("x ", "x"));
  					break;
  				case "edition":
  					bookRecord.setValue("edition", td.text().replace(" ", ""));
  					break;
  				case "lit_form":
  					bookRecord.appendValue("comment", "Литературная форма: " + td.text());
  					break;
  				case "information":
  					bookRecord.appendValue("comment", "Сведения об издании: " + td.text());
  					break;
  				case "book_illustrations":
  					bookRecord.appendValue("comment", "Тип иллюстраций: " + td.text());
  					break;
  				case "translator":
  					bookRecord.setValue("translator", td.text());
  					break;
  				case "translate":
  					bookRecord.appendValue("comment", "Перевод заглавия: " + td.text());
  					break;
  				case "editor":
  					bookRecord.setValue("editor", td.text());
  					break;
  				case "design":
  					bookRecord.appendValue("comment", "Оформление: " + td.text());
  					break;
  				case "illustrator":
  					bookRecord.appendValue("comment", "Художник-иллюстратор: " + td.text());
  					break;
  				case "language":
  					bookRecord.setValue("language", td.text().replace(" ,", ","));
  					break;
  				case "":
  					bookRecord.appendValue("comment", "Негабаритный груз: " + td.text());
  					break;
  				case "publication":
  					bookRecord.appendValue("comment", "Формат: " + td.text());
  					break;
  				case "length":
  					bookRecord.appendValue("comment", "Продолжительность: " + td.text());
  					break;
  				case "speaker":
  					bookRecord.appendValue("comment", "Текст читают: " + td.text());
  					break;
  				case "sound":
  					bookRecord.appendValue("comment", "Звук: " + td.text());
  					break;
  				case "country":
  					bookRecord.appendValue("comment", "Страна: " + td.text());
  					break;	
  				case "volume":
  					bookRecord.appendValue("comment", "Том: " + td.text());
  					break;
  				case "volume_cnt":
  					bookRecord.appendValue("comment", "Томов: " + td.text());
  					break;
  				case "child_volumes":
  					bookRecord.appendValue("comment", "Тома: " + td.text());
  					break;
  				case "supplement":
  					bookRecord.appendValue("comment", "Приложение: " + td.text());
  					break;
  				case "performer":
  					bookRecord.appendValue("comment", "Исполнитель: " + td.text());
  					break;
  				case "quantity":
  					bookRecord.appendValue("comment", "Количество в упаковке: " + td.text());
  					break;	
  				case "color":
  					bookRecord.appendValue("comment", "Цвет: " + td.text());
  					break;
  					
  				case "producer":
  					bookRecord.appendValue("comment", "Режиссер: " + td.text());
  					break;
  				case "script_author":
  					bookRecord.appendValue("comment", "Автор сценария: " + td.text());
  					break;
  				case "composer":
  					bookRecord.appendValue("comment", "Композитор: " + td.text());
  					break;
  				case "creator":
  					bookRecord.appendValue("comment", "Разработчик: " + td.text());
  					break;
  					
  				case "supplement_of":
  					bookRecord.appendValue("comment", "Является приложением для: " + td.text());
  					break;
  				case "number":
  					bookRecord.appendValue("comment", "Номер журнала: " + td.text());
  					break;
  				case "periodicity":
  					bookRecord.appendValue("comment", "Периодичность: " + td.text());
  					break;
  				case "month":
  					bookRecord.appendValue("comment", "Месяц: " + td.text());
  					break;
  					
  				case "compiler":
  					bookRecord.setValue("compiler", td.text());
  					break;
  				default:
  					System.out.println("Неизвестный тэг: " + td.className() + "; " + bookRecord.getValue("readId"));
  			}
  		}

  		// <div class="book__other-info-item book__contents">
  		// <div class="book__type_book_h2">Содержание</div>
  		// <div class="j-extending book_annotation__wrap" data-jext-height="154" style="height: 165px;">
  		// <div class="j-extending_content">Об авторах<br></br>.....
  		
  		//<div class="book__other-info-item book__contents-part" id="fragment_text">

  		//<div class="book__other-info-container book__other-info-container-bottom">
  		//<div class="book__other-info-item book__contents-part" id="fragment_text">
  		//<div class="book__type_book_h2">Отрывок из книги «Шантарам»</div>
  		
  		Elements text = doc.select("div.book__other-info-item");
  		if (text != null) {
  			for (int i = 0; i < text.size(); i++) {
  				Element text1 = text.get(i).select("div.book__type_book_h2").first();
  				if (text1 != null) {
  					if (!text1.text().equals("Характеристики товара")) {
  						bookRecord.appendValue("otherText", text1.text());
  						//System.out.println(text1.text());
  					}
  				}
  				Element text2 = text.get(i).select("div.j-extending_content").first();
  				if (text2 != null) {
  					//	String descr = text2.html().replace(" class=\"j-extending_content\"", "");
  					String descr = text2.html().replace("\n", "");
  					descr = descr.replace("\n", "");
  					//System.out.println(descr);
  					bookRecord.appendValue("otherText", "<br />" + descr);
  				}
  			}
  		}
  		
  		
  		// под настроение обработать эти комментарии
//  		<div id="review" class="review corner-bottom review_full">
//  	    <div id="r368988" class="short_review_item short_review_item_positive" review_id="368988">
//  	        <div class="review_item_container">
//  	            <table class="review_item">
//  	                    <tr>
//  	                        <td class="header">
//  	                            <span class="date">06.07.2015 11:18</span>
//  	                            <a class="profile_link" href="/user/169695/review/"></a>
//  	                            <a class="profile_link" href="/user/169695/review/">Сардинка</a>
//  	                        </td>
//  	                    </tr>
//  	                    <tr>
//  	                        <td class="body">
//  	                            <div class="avatar"></div>
//  	                            <div class="short_review"></div>
//  	                            <div class="full_review hidden">
//  	                                «Шантарам» - это такая невообразимая огромность, ч…
//  	                            </div>
  		
  		
  		
  		
		return bookRecord;
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
