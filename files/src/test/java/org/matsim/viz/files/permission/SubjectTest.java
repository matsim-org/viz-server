package org.matsim.viz.files.permission;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.matsim.viz.clientAuth.AuthenticationResult;
import org.matsim.viz.files.agent.AgentService;
import org.matsim.viz.files.entities.Agent;
import org.matsim.viz.files.entities.User;
import org.matsim.viz.files.util.TestUtils;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SubjectTest {

    private AgentService agentService = TestUtils.getAgentService();
    private SubjectFactory testObject;

    @Before
    public void setUp() {
        testObject = new SubjectFactory(agentService);
    }

    @After
    public void tearDown() {
        TestUtils.removeAllEntities();
    }


    @Test
    public void createSubject_userScope_subjectWithUser() {

        User user = TestUtils.persistUser("some-id");
        AuthenticationResult result = new AuthenticationResult(user.getAuthId(), SubjectFactory.USER);

        Optional<Agent> subject = testObject.createSubject(result);

        assertTrue(subject.isPresent());
        assertEquals(user.getAuthId(), subject.get().getAuthId());
    }

    @Test
    public void createSubject_serviceScope_subjectWithServiceAgent() {

        AuthenticationResult result = new AuthenticationResult("any-id", SubjectFactory.SERVICE);

        Optional<Agent> subject = testObject.createSubject(result);

        assertTrue(subject.isPresent());
        assertEquals(agentService.getServiceAgent(), subject.get());
    }

    @Test
    public void createSubject_publicUserScope_subjectWithPublicUserAgent() {

        AuthenticationResult result = new AuthenticationResult("any-id", SubjectFactory.PUBLIC_USER);

        Optional<Agent> subject = testObject.createSubject(result);

        assertTrue(subject.isPresent());
        assertEquals(agentService.getPublicAgent(), subject.get());
    }

    @Test
    public void createSubject_unknownScope_subjectWithPublicUserAgent() {

        AuthenticationResult result = new AuthenticationResult("any-id", "unknown-scope");

        Optional<Agent> subject = testObject.createSubject(result);

        assertTrue(subject.isPresent());
        assertEquals(agentService.getPublicAgent(), subject.get());
    }
}
