package ParserJsoup;

import java.util.LinkedHashMap;
import java.util.Map;

public class BookRecord {
	private static String headersArray[] = { 
		"ozonId", "readId", "title", "serie", "author", "publisher", "isbn", "ean", "year", "language", "pages", "format", "weight", "edition", "paperback",
		"rating", "reviewCount", "likes", "disLikes", "price", "currency", "compiler", "editor", "translator",
		"age", "category", "imageUrl", "comment", "description", "otherText"
	};

	private static String headerTitlesArray[] = { 
		"ID Ozon", "ID Read", "Название", "Серия", "Автор(ы)", "Издательство", "ISBN", "Штрихкод", "Год выпуска", "Язык(и)", "Страниц", "Формат", "Вес", "Тираж", "Переплёт",
		"Оценка", "Комментариев", "Нравится", "Не нравится", "Цена", "Валюта", "Составитель", "Редактор", "Переводчик",
		"Аудитория", "Категории", "Ссылка на изображение", "Другие данные", "Описание", "Остальной текст"
	};
	
	private Map<String, String> fields;
	 
	public BookRecord() {
		fields = new LinkedHashMap<String, String>();
		for (int i = 0; i < headersArray.length; i++) {
			setValue(headersArray[i], "");
		}
	}
	
	public void setValue(String key, String value) {
		fields.put(key, value);
	}
	
	public String getValue(String key) {
		return fields.get(key);
	}
	
	
	public void appendValue(String key, String value) {
		appendValue(key, value, ";");
	}

	public void appendValue(String key, String value, String radix) {
		if (fields.get(key).isEmpty()) {
			setValue(key, value);
		} else {
			setValue(key, fields.get(key) + radix + value);
		}
	}
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		for (String str : fields.values()) {
			result.append(str);
			result.append("\u0009");
		}
		return result.toString();
	}

	public static String headers() {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < headerTitlesArray.length; i++) {
			result.append(headerTitlesArray[i]);
			result.append("\u0009");
		}
		return result.toString();
	}
}
