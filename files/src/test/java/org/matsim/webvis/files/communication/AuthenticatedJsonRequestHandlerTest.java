package org.matsim.webvis.files.communication;

import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;
import org.matsim.webvis.common.auth.AuthenticationResult;
import org.matsim.webvis.common.communication.Answer;
import org.matsim.webvis.files.entities.User;
import org.matsim.webvis.files.agent.AgentService;
import spark.Request;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AuthenticatedJsonRequestHandlerTest {

    private TestableHandler testObject;

    @Before
    public void setUp() {
        testObject = new TestableHandler();
    }

    @Test
    public void process_userFound_takeThatUser() {

        User user = new User();
        Subject.agentService = mock(AgentService.class);
        when(Subject.agentService.findByIdentityProviderId(any())).thenReturn(user);

        Request request = mock(Request.class);
        when(request.attribute("subject")).thenReturn(new AuthenticationResult());

        Answer answer = testObject.process(new TestableRequest(), request);

        assertTrue(testObject.wasProcessedCalled);
        assertEquals(user, testObject.subject.getUser());
    }


    static class TestableRequest {

    }

    static class TestableHandler extends AuthenticatedJsonRequestHandler<TestableRequest> {

        boolean wasProcessedCalled = false;
        Subject subject;

        TestableHandler() {
            super(TestableRequest.class, new Gson());
        }

        @Override
        protected Answer process(TestableRequest body, Subject subject) {
            wasProcessedCalled = true;
            this.subject = subject;
            return Answer.ok(true);
        }
    }
}
