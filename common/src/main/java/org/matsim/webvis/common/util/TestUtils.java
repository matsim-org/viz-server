package org.matsim.webvis.common.util;

import java.util.Base64;

public class TestUtils {

    public static String encodeBasicAuth(String principal, String credential) {
        return "Basic " + Base64.getEncoder().encodeToString((principal + ":" + credential).getBytes());
    }
}
