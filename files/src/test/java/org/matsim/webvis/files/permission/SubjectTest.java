package org.matsim.webvis.files.permission;

import org.junit.After;
import org.matsim.webvis.files.util.TestUtils;

public class SubjectTest {

    @After
    public void tearDown() {
        TestUtils.removeAllEntities();
    }

/*
    @Test
    public void createSubject_userScope_subjectWithUser() {

        User user = TestUtils.persistUser("some-id");
        AuthenticationResult result = TestUtils.mockAuthResult(Subject.USER, user.getAuthId());

        Subject subject = Subject.createSubject(result);

        assertEquals(result, subject.getAuthenticationResult());
        assertEquals(user, subject.getAgent());
    }

    @Test
    public void createSubject_serviceScope_subjectWithServiceAgent() {

        AuthenticationResult result = TestUtils.mockAuthResult(Subject.SERVICE, "any-id");

        Subject subject = Subject.createSubject(result);

        assertEquals(result, subject.getAuthenticationResult());
        assertEquals(AgentService.Instance.getServiceAgent(), subject.getAgent());
    }

    @Test
    public void createSubject_publicUserScope_subjectWithPublicUserAgent() {

        AuthenticationResult result = TestUtils.mockAuthResult(Subject.PUBLIC_USER, "any-id");

        Subject subject = Subject.createSubject(result);

        assertEquals(result, subject.getAuthenticationResult());
        assertEquals(AgentService.Instance.getPublicAgent(), subject.getAgent());
    }

    @Test
    public void createSubject_unknownScope_subjectWithPublicUserAgent() {

        AuthenticationResult result = TestUtils.mockAuthResult("unknown-scope", "any-id");

        Subject subject = Subject.createSubject(result);

        assertEquals(result, subject.getAuthenticationResult());
        assertEquals(AgentService.Instance.getPublicAgent(), subject.getAgent());
    }
    */
}
