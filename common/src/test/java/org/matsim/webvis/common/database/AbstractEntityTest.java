package org.matsim.webvis.common.database;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;

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

    @Test
    public void equals_equalReference_true() {

        TestableEntity e1 = new TestableEntity();

        @SuppressWarnings("EqualsWithItself") boolean result = e1.equals(e1);

        assertTrue(result);

    }

    @Test
    public void equals_noIdDifferentReferences_false() {

        TestableEntity e1 = new TestableEntity();
        TestableEntity e2 = new TestableEntity();

        boolean result = e1.equals(e2);

        assertFalse(result);
    }

    @Test
    public void equals_otherHasNoId_false() {

        TestableEntity e1 = new TestableEntity();
        e1.setId("some-id");
        TestableEntity e2 = new TestableEntity();

        boolean result = e1.equals(e2);

        assertFalse(result);
    }

    @Test
    public void equals_thisHasNoId_false() {

        TestableEntity e1 = new TestableEntity();
        TestableEntity e2 = new TestableEntity();
        e2.setId("some-id");

        boolean result = e1.equals(e2);

        assertFalse(result);
    }

    @Test
    public void equals_differentIds_false() {

        TestableEntity e1 = new TestableEntity();
        TestableEntity e2 = new TestableEntity();
        e1.setId("id");
        e2.setId("other-id");

        boolean result = e1.equals(e2);

        assertFalse(result);
    }

    @Test
    public void equals_equalIds_true() {

        TestableEntity e1 = new TestableEntity();
        TestableEntity e2 = new TestableEntity();
        String id = "id";
        e1.setId(id);
        e2.setId(id);

        boolean result = e1.equals(e2);

        assertTrue(result);
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void equals_otherIsNull_false() {

        TestableEntity e1 = new TestableEntity();
        TestableEntity e2 = null;

        boolean result = e1.equals(e2);

        assertFalse(result);
    }

    @Test
    public void hashCode_sameId_sameHashCode() {

        TestableEntity e1 = new TestableEntity();
        TestableEntity e2 = new TestableEntity();
        String id = "id";
        e1.setId(id);
        e2.setId(id);

        int hashCode1 = e1.hashCode();
        int hashCode2 = e2.hashCode();

        assertEquals(hashCode1, hashCode2);
    }

    @Test
    public void hashCode_differentId_differentHashCode() {

        TestableEntity e1 = new TestableEntity();
        TestableEntity e2 = new TestableEntity();
        e1.setId("id");
        e2.setId("other-id");

        int hashCode1 = e1.hashCode();
        int hashCode2 = e2.hashCode();

        assertNotEquals(hashCode1, hashCode2);
    }

    @Test
    public void hashCode_noId_differentHashCode() {

        TestableEntity e1 = new TestableEntity();
        TestableEntity e2 = new TestableEntity();

        int hashCode1 = e1.hashCode();
        int hashCode2 = e2.hashCode();

        assertNotEquals(hashCode1, hashCode2);

    }

    private class TestableEntity extends AbstractEntity {

    }
}
