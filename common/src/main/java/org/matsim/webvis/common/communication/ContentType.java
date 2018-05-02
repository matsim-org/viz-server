package org.matsim.webvis.common.communication;

public class ContentType {

    public static String APPLICATION_JSON = "application/json";
    public static String APPLICATION_OCTET_STREAM = "application/octet-stream";
    public static final String FORM_URL_ENCODED = "application/x-www-form-urlencoded";

    public static boolean isJson(String contentType) {
        return contentType.equals(APPLICATION_JSON);
    }

    public static boolean isFormUrlEncoded(String contentType) {
        return contentType.equals(FORM_URL_ENCODED);
    }
}