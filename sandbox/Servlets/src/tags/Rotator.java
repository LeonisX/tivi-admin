package tags;

public class Rotator {
	private String links[] = {
			"http://tv-games.ru",
			"http://tv-games.ru/forum",
			"http://tv-games.ru/wiki/index.php"
	};
	
	private int selectedIndex = 0;
	
	public String getLink() {
		return links[selectedIndex];
	}
	
	public void nextAd() {
		selectedIndex = (++selectedIndex) % links.length;
	}
}
