package org.matsim.webvis.common.communication;

import org.junit.Test;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

public class ContentTypeTest {

    @Test
    public void isJson_actuallyIsJson_true() {
        final String type = "application/json";

        boolean result = ContentType.isJson(type);

        assertTrue(result);
    }

    @Test
    public void isJson_notJson_false() {
        final String type = "not-type-json";

        boolean result = ContentType.isJson(type);

        assertFalse(result);
    }

    @Test
    public void isFormUrlEncoded_no_false() {
        final String type = "not-type-formurlencoded";

        boolean result = ContentType.isFormUrlEncoded(type);

        assertFalse(result);
    }

    @Test
    public void isFormUrlEncoded_yes_true() {
        final String type = "application/x-www-form-urlencoded";

        boolean result = ContentType.isFormUrlEncoded(type);

        assertTrue(result);
    }
}
