package org.matsim.webvis.common.communication;

import org.junit.Before;
import org.junit.Test;
import spark.Request;
import spark.Response;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JsonRequestHandlerTest {

    private TestableJsonRequestHandler testObject;

    @Before
    public void setUp() {
        testObject = new TestableJsonRequestHandler(TestRequest.class);
    }

    @Test
    public void process_invalidJson_badRequest() {

        Request request = mock(Request.class);
        when(request.body()).thenReturn("invalidJson");
        when(request.contentType()).thenReturn(ContentType.APPLICATION_JSON);

        Answer answer = testObject.process(request, mock(Response.class));

        assertEquals(HttpStatus.BAD_REQUEST, answer.getStatusCode());
        assertTrue(answer.getResponse() instanceof ErrorResponse);
    }

    @Test
    public void process_contentTypeNotJson_badRequest() {

        Request request = mock(Request.class);
        when(request.body()).thenReturn("{\"someProperty\": \"some-value\"}");
        when(request.contentType()).thenReturn("invalid-content-type");

        Answer answer = testObject.process(request, mock(Response.class));

        assertEquals(HttpStatus.BAD_REQUEST, answer.getStatusCode());
        assertTrue(answer.getResponse() instanceof ErrorResponse);
    }

    @Test
    public void process_validRequest_processOfChildCalled() {

        Request request = mock(Request.class);
        when(request.body()).thenReturn("{\"someProperty\": \"some-value\"}");
        when(request.contentType()).thenReturn(ContentType.APPLICATION_JSON);

        Answer answer = testObject.process(request, mock(Response.class));

        assertEquals(HttpStatus.OK, answer.getStatusCode());
        assertTrue(testObject.processWasCalled);
    }

    private class TestRequest {

        String someProperty = "";
    }

    private class TestableJsonRequestHandler extends JsonRequestHandler<TestRequest> {

        boolean processWasCalled = false;

        TestableJsonRequestHandler(Class<TestRequest> requestClass) {
            super(requestClass);
        }


        @Override
        protected Answer process(TestRequest body, Request rawRequest) {
            processWasCalled = true;
            return Answer.ok("some answer");
        }
    }
}
