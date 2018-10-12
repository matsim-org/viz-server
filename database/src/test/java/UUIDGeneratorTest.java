import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.junit.Before;
import org.junit.Test;
import org.matsim.viz.database.AbstractEntity;
import org.matsim.viz.database.UUIDGenerator;

import java.io.Serializable;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class UUIDGeneratorTest {

    private UUIDGenerator testObject;
    private SharedSessionContractImplementor mockedSSCI;

    @Before
    public void setUp() {
        testObject = new UUIDGenerator();
        mockedSSCI = mock(SharedSessionContractImplementor.class);
    }

    @Test
    public void generate_notEntity_newUUID() {

        Object o = new Object();

        Serializable result = testObject.generate(mockedSSCI, o);

        assertTrue(result instanceof String);
        UUID uuid = UUID.fromString((String) result);
        assertNotNull(uuid);
    }

    @Test
    public void generate_noId_newUUID() {

        TestableEntity entity = new TestableEntity();

        Serializable result = testObject.generate(mockedSSCI, entity);

        assertTrue(result instanceof String);
        UUID uuid = UUID.fromString((String) result);
        assertNotNull(uuid);
    }

    @Test
    public void generate_hasId_presentId() {

        final String id = "not-a-uuid";
        TestableEntity entity = new TestableEntity();
        entity.setId(id);

        Serializable result = testObject.generate(mockedSSCI, entity);

        assertTrue(result instanceof String);
        assertEquals(id, result);
    }

    private static class TestableEntity extends AbstractEntity {
    }
}
