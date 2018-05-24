package org.matsim.webvis.common.database;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class AbstractEntityTest {

    private TestableEntity testObject;

    @Before
    public void setUp() {
        testObject = new TestableEntity();
        testObject.setId("test-id");
    }

    @Test
    public void equalId_compareToNull_false() {

        boolean result = testObject.equalId(null);

        assertFalse(result);
    }

    @Test
    public void equalId_compareToIdNull_false() {

        TestableEntity entity = new TestableEntity();

        boolean result = testObject.equalId(entity);

        assertFalse(result);
    }

    @Test
    public void equalId_entityIdNull_false() {

        TestableEntity entity = new TestableEntity();

        boolean result = entity.equalId(testObject);

        assertFalse(result);
    }

    @Test
    public void equalId_idsNotEqual_false() {

        TestableEntity entity = new TestableEntity();
        entity.setId("wrong-id");

        boolean result = testObject.equalId(entity);

        assertFalse(result);
    }

    @Test
    public void equalId_equalIds_true() {

        TestableEntity entity = new TestableEntity();
        entity.setId(testObject.getId());

        boolean result = testObject.equalId(entity);

        assertTrue(result);
    }

    @Test
    public void equalId_equalObjects_true() {

        boolean result = testObject.equalId(testObject);

        assertTrue(result);
    }

    private class TestableEntity extends AbstractEntity {

    }
}
