package org.matsim.webvis.files.agent;

import org.junit.After;
import org.junit.Test;
import org.matsim.webvis.error.CodedException;
import org.matsim.webvis.error.Error;
import org.matsim.webvis.files.entities.User;
import org.matsim.webvis.files.util.TestUtils;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AgentServiceTest {

    @After
    public void tearDown() {
        TestUtils.removeAllEntities();
    }

    @Test
    public void createUser_userPersisted() {

        final String authId = "some-id";

        User result = AgentService.Instance.createUser(authId);

        assertEquals(authId, result.getAuthId());
    }

    @Test
    public void createUser_authIdPresent_exception() {

        final String authId = "some-id";
        AgentService.Instance.createUser(authId);

        try {
            AgentService.Instance.createUser(authId);
            fail("duplicate authId should cause exception");
        } catch (CodedException e) {
            assertEquals(Error.RESOURCE_EXISTS, e.getInternalErrorCode());
        }
    }

    @Test
    public void findByIdentityProviderId_found() {

        User user = TestUtils.persistUser("some-id");

        User result = AgentService.Instance.findByIdentityProviderId(user.getAuthId());

        assertTrue(user.equals(result));
    }
}
