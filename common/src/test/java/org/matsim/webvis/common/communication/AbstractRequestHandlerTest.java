package org.matsim.webvis.common.communication;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import spark.Request;
import spark.Response;

public class AbstractRequestHandlerTest {

    private TestableRequestHandler testObject;

    @Before
    public void setUp() {
        testObject = new TestableRequestHandler();
    }

    @Test
    public void reqBodyNotJson_badRequest() {

        String body = "some string which is not json";
        Request req = Mockito.mock(Request.class);
        Mockito.when(req.body()).thenReturn(body);
        Mockito.when(req.contentType()).thenReturn("application/json");
        Response res = Mockito.mock(Response.class);

        testObject.handle(req, res);

        Mockito.verify(res).status(HttpStatus.BAD_REQUEST);
        Mockito.verify(res).body(ArgumentMatchers.any());
    }

    @Test
    public void reqContentTypeNotSet_badRequest() {
        Request req = Mockito.mock(Request.class);
        Mockito.when(req.contentType()).thenReturn("");
        Response res = Mockito.mock(Response.class);

        testObject.handle(req, res);

        Mockito.verify(res).status(HttpStatus.BAD_REQUEST);
        Mockito.verify(res).body(ArgumentMatchers.any());
    }

    @Test
    public void reqContentTypeIsNull_badRequest() {
        Request req = Mockito.mock(Request.class);
        Mockito.when(req.contentType()).thenReturn(null);
        Response res = Mockito.mock(Response.class);

        testObject.handle(req, res);

        Mockito.verify(res).status(HttpStatus.BAD_REQUEST);
        Mockito.verify(res).body(ArgumentMatchers.any());
    }

    @Test
    public void processingFails_badRequest() {

        String body = "{ message: 'message' }";
        Request req = Mockito.mock(Request.class);
        Mockito.when(req.body()).thenReturn(body);
        Mockito.when(req.contentType()).thenReturn("application/json");
        Response res = Mockito.mock(Response.class);
        testObject.shouldFailProcess = true;

        testObject.handle(req, res);

        Mockito.verify(res).status(HttpStatus.INTERNAL_SERVER_ERROR);
        Mockito.verify(res).body(ArgumentMatchers.anyString());
    }

    @Test
    public void processingSucceeds_ok() {

        String body = "{ message: 'message' }";
        Request req = Mockito.mock(Request.class);
        Mockito.when(req.body()).thenReturn(body);
        Mockito.when(req.contentType()).thenReturn("application/json");
        Response res = Mockito.mock(Response.class);

        testObject.handle(req, res);

        Mockito.verify(res).status(HttpStatus.OK);
        Mockito.verify(res).body("{\"message\":\"bla\"}");
    }


    private class TestRequest {
        public String message;
    }

    private class TestableRequestHandler extends AbstractRequestHandler<TestRequest> {

        boolean shouldFailProcess = false;

        TestableRequestHandler() {
            super(TestRequest.class);
        }

        @Override
        protected Answer process(TestRequest body) {
            if (shouldFailProcess) return Answer.internalError("some code", "some message");

            TestRequest test = new TestRequest();
            test.message = "bla";
            return Answer.ok(test);
        }
    }
}
