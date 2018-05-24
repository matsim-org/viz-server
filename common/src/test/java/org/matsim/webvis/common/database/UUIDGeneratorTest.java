package org.matsim.webvis.common.database;

import org.junit.Before;
import org.junit.Test;

import java.io.Serializable;
import java.util.UUID;

import static junit.framework.TestCase.*;

public class UUIDGeneratorTest {

    private UUIDGenerator generator;
    private TestableEntity entity;


    @Before
    public void setUp() {
        generator = new UUIDGenerator();
        entity = new TestableEntity();
    }

    @Test
    public void generate_notAbstractEntity_newUuid() {

        Serializable result = generator.generate(null, new Object());

        assertNotNull(result);
        assertTrue(result instanceof String);
        UUID uuid = UUID.fromString((String) result);
        assertNotNull(uuid);
    }

    @Test
    public void generate_abstractEntityAndIdIsNull_newUuid() {

        Serializable result = generator.generate(null, entity);

        assertNotNull(result);
        assertTrue(result instanceof String);
        UUID uuid = UUID.fromString((String) result);
        assertNotNull(uuid);
    }

    @Test
    public void generate_abstractEntityAndIdIsSet_existingId() {

        entity.setId("some-id");
        Serializable result = generator.generate(null, entity);

        assertEquals(entity.getId(), result);
    }

    private class TestableEntity extends AbstractEntity {

    }
}
