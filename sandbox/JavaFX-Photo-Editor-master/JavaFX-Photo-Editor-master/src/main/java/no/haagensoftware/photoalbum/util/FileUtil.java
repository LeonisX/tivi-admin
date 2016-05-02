package no.haagensoftware.photoalbum.util;

import org.apache.log4j.Logger;

import java.net.URL;


public class FileUtil {
    private static final Logger logger = Logger.getLogger(FileUtil.class.getName());

    public static URL getUrlForResource(String resourceName) {

        URL resource = null;

        try {
            resource = FileUtil.class.getResource(resourceName);
            logger.info(resource.getPath());
        }
        catch (Exception e) {
            logger.info("fant ikke " + resourceName + ", prøver /" + resourceName);
            resource =  FileUtil.class.getResource("/" + resourceName);
            logger.info(resource.getPath());
        }

        logger.info("Got Resource: " + resource.getFile());

        return resource;
    }
}
