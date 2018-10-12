package org.matsim.viz.files.agent;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.matsim.viz.error.CodedException;
import org.matsim.viz.error.Error;
import org.matsim.viz.files.entities.User;
import org.matsim.viz.files.util.TestUtils;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;

public class AgentServiceTest {

    private AgentService testObject;

    @Before
    public void setUp() {
        this.testObject = new AgentService(new UserDAO(TestUtils.getPersistenceUnit()));
    }

    @After
    public void tearDown() {
        TestUtils.removeAllEntities();
    }

    @Test
    public void createUser_userPersisted() {

        final String authId = "some-id";

        User result = testObject.createUser(authId);

        assertEquals(authId, result.getAuthId());
    }

    @Test
    public void createUser_authIdPresent_exception() {

        final String authId = "some-id";
        testObject.createUser(authId);

        try {
            testObject.createUser(authId);
            fail("duplicate authId should cause exception");
        } catch (CodedException e) {
            assertEquals(Error.RESOURCE_EXISTS, e.getInternalErrorCode());
        }
    }

    @Test
    public void findByIdentityProviderId_found() {

        User user = TestUtils.persistUser("some-id");

        User result = testObject.findByIdentityProviderId(user.getAuthId());

        assertEquals(user, result);
    }
}
