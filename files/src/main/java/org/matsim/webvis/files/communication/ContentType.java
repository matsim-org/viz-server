package org.matsim.webvis.files.communication;

public class ContentType {

    public static String APPLICATION_JSON = "application/json";

    public static boolean isJson(String contentType) {
        return contentType.equals(APPLICATION_JSON);
    }
}
