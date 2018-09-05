package org.matsim.webvis.files.permission;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.matsim.webis.oauth.IntrospectionResult;
import org.matsim.webvis.files.agent.AgentService;
import org.matsim.webvis.files.entities.Agent;
import org.matsim.webvis.files.entities.User;
import org.matsim.webvis.files.util.TestUtils;

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
        IntrospectionResult result = new IntrospectionResult(true, SubjectFactory.USER, user.getAuthId());

        Optional<Agent> subject = testObject.createSubject(result);

        assertTrue(subject.isPresent());
        assertEquals(user.getAuthId(), subject.get().getAuthId());
    }

    @Test
    public void createSubject_serviceScope_subjectWithServiceAgent() {

        IntrospectionResult result = new IntrospectionResult(true, SubjectFactory.SERVICE, "any-id");

        Optional<Agent> subject = testObject.createSubject(result);

        assertTrue(subject.isPresent());
        assertEquals(agentService.getServiceAgent(), subject.get());
    }

    @Test
    public void createSubject_publicUserScope_subjectWithPublicUserAgent() {

        IntrospectionResult result = new IntrospectionResult(true, SubjectFactory.PUBLIC_USER, "any-id");

        Optional<Agent> subject = testObject.createSubject(result);

        assertTrue(subject.isPresent());
        assertEquals(agentService.getPublicAgent(), subject.get());
    }

    @Test
    public void createSubject_unknownScope_subjectWithPublicUserAgent() {

        IntrospectionResult result = new IntrospectionResult(true, "unknown-scope", "any-id");

        Optional<Agent> subject = testObject.createSubject(result);

        assertTrue(subject.isPresent());
        assertEquals(agentService.getPublicAgent(), subject.get());
    }
}
