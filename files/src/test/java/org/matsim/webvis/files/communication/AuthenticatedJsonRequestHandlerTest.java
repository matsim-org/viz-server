package org.matsim.webvis.files.communication;

import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.matsim.webvis.common.communication.Answer;
import org.matsim.webvis.common.communication.ErrorResponse;
import spark.Request;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AuthenticatedJsonRequestHandlerTest {

    private TestableAuthenticatedJsonRequestHandler testObject;

    @Before
    public void setUp() {
        testObject = new TestableAuthenticatedJsonRequestHandler();
    }

    @Test
    public void process_invalidJson_badRequest() {

        Request request = mock(Request.class);
        when(request.body()).thenReturn("invalidJson");
        when(request.contentType()).thenReturn(ContentType.APPLICATION_JSON);

        Answer answer = testObject.process(request, null);

        assertEquals(HttpStatus.SC_BAD_REQUEST, answer.getStatusCode());
        assertTrue(answer.getResponse() instanceof ErrorResponse);
    }

    @Test
    public void process_contentTypeNotJson_badRequest() {

        Request request = mock(Request.class);
        when(request.body()).thenReturn("{\"someProperty\": \"some-value\"}");
        when(request.contentType()).thenReturn("invalid-content-type");

        Answer answer = testObject.process(request, null);

        assertEquals(HttpStatus.SC_BAD_REQUEST, answer.getStatusCode());
        assertTrue(answer.getResponse() instanceof ErrorResponse);
    }

    @Test
    public void process_validRequest_processOfChildCalled() {

        Request request = mock(Request.class);
        when(request.body()).thenReturn("{\"someProperty\": \"some-value\"}");
        when(request.contentType()).thenReturn(ContentType.APPLICATION_JSON);
        when(request.attribute(anyString())).thenReturn(new AuthenticationResult());

        Answer answer = testObject.process(request, null);

        assertEquals(HttpStatus.SC_OK, answer.getStatusCode());
        assertTrue(testObject.processWasCalled);
    }


    private class TestRequest {

        String someProperty = "";
    }

    private class TestableAuthenticatedJsonRequestHandler extends AuthenticatedJsonRequestHandler<TestRequest> {

        boolean processWasCalled = false;

        TestableAuthenticatedJsonRequestHandler() {
            super(TestRequest.class);
        }

        @Override
        protected Answer process(TestRequest body, Subject subject) {
            processWasCalled = true;
            return Answer.ok("some answer");
        }
    }
}
