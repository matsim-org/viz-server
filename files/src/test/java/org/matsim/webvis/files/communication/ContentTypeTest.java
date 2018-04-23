package org.matsim.webvis.files.communication;

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
    public void isJson_notJson_true() {
        final String type = "not-type-json";

        boolean result = ContentType.isJson(type);

        assertFalse(result);
    }
}
