package md.leonis.tivi.admin.utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

class HttpUtils {
    static String readFromUrl(String urlAddress) throws IOException {
        URL url = new URL(urlAddress);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("AuthToken", "_da token");
        conn.setRequestProperty("User-Agent", "TiVi's admin client");
        return MultipartUtility.readResponse(conn);
    }
}
