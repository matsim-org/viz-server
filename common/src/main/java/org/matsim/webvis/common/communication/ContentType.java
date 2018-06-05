package org.matsim.webvis.common.communication;

import org.apache.commons.lang3.StringUtils;

public class ContentType {

    public static String APPLICATION_JSON = "application/json";
    public static String APPLICATION_OCTET_STREAM = "application/octet-stream";
    public static final String FORM_URL_ENCODED = "application/x-www-form-urlencoded";
    public static final String CONTENT_TYPE_HEADER = "Content-type";

    public static boolean isJson(String contentType) {
        return APPLICATION_JSON.equals(contentType);
    }

    public static boolean isFormUrlEncoded(String contentType) {
        return StringUtils.contains(contentType, FORM_URL_ENCODED);
    }
}
