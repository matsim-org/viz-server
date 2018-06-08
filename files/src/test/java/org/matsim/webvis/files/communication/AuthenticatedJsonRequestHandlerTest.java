package org.matsim.webvis.files.communication;

import com.google.gson.Gson;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.matsim.webvis.common.communication.Answer;
import org.matsim.webvis.common.communication.ContentType;
import org.matsim.webvis.files.entities.User;
import org.matsim.webvis.files.permission.Subject;
import org.matsim.webvis.files.util.TestUtils;
import spark.Request;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class AuthenticatedJsonRequestHandlerTest {

    private TestableHandler testObject;

    @Before
    public void setUp() {
        testObject = new TestableHandler();
    }

    @After
    public void tearDown() {
        TestUtils.removeAllEntities();
    }

    @Test
    public void process_userFound_takeThatUser() {

        User user = TestUtils.persistUser("auth-id");

        Request request = TestUtils.mockRequest(ContentType.APPLICATION_JSON, "user-client", user.getAuthId());

        testObject.process(new TestableRequest(), request);

        assertTrue(testObject.wasProcessedCalled);
        assertEquals(user, testObject.subject.getAgent());
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
