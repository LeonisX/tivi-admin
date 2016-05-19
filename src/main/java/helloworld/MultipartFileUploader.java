package helloworld;

import md.leonis.tivi.admin.utils.MultipartUtility;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * This program demonstrates a usage of the MultipartUtility class.
 * @author www.codejava.net
 *
 */
public class MultipartFileUploader {

    public static void main(String[] args) {
        String charset = "UTF-8";
        File uploadFile = new File("/home/leonis/jdeli.jar");
        String requestURL = "http://wap.tv-games.ru/video.php?to=upload_thumb";

        try {
            MultipartUtility multipart = new MultipartUtility(requestURL, charset);

            multipart.addHeaderField("User-Agent", "TiVi's admin client");
            multipart.addFormField("description", "Cool Pictures");
            multipart.addFilePart("image", uploadFile);

            List<String> response = multipart.finish();

            for (String line : response) {
                System.out.println(line);
            }
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }
}