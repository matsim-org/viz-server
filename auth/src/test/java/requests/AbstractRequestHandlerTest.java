package requests;

import org.junit.Before;
import org.junit.Test;
import spark.Request;
import spark.Response;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class AbstractRequestHandlerTest {

    private TestableRequestHandler testObject;

    @Before
    public void setUp() {
        testObject = new TestableRequestHandler();
    }

    @Test
    public void reqBodyNotJson_badRequest() {

        String body = "some string which is not json";
        Request req = mock(Request.class);
        when(req.body()).thenReturn(body);
        when(req.contentType()).thenReturn("application/json");
        Response res = mock(Response.class);

        testObject.handle(req, res);

        verify(res).status(HttpStatus.BAD_REQUEST);
        verify(res).body(any());
    }

    @Test
    public void reqContentTypeNotSet_badRequest() {
        Request req = mock(Request.class);
        when(req.contentType()).thenReturn("");
        Response res = mock(Response.class);

        testObject.handle(req, res);

        verify(res).status(HttpStatus.BAD_REQUEST);
        verify(res).body(any());
    }

    @Test
    public void reqContentTypeIsNull_badRequest() {
        Request req = mock(Request.class);
        when(req.contentType()).thenReturn(null);
        Response res = mock(Response.class);

        testObject.handle(req, res);

        verify(res).status(HttpStatus.BAD_REQUEST);
        verify(res).body(any());
    }

    @Test
    public void processingFails_badRequest() {

        String body = "{ message: 'message' }";
        Request req = mock(Request.class);
        when(req.body()).thenReturn(body);
        when(req.contentType()).thenReturn("application/json");
        Response res = mock(Response.class);
        testObject.shouldFailProcess = true;

        testObject.handle(req, res);

        verify(res).status(HttpStatus.INTERNAL_SERVER_ERROR);
        verify(res).body(anyString());
    }

    @Test
    public void processingSucceeds_ok() {

        String body = "{ message: 'message' }";
        Request req = mock(Request.class);
        when(req.body()).thenReturn(body);
        when(req.contentType()).thenReturn("application/json");
        Response res = mock(Response.class);

        testObject.handle(req, res);

        verify(res).status(HttpStatus.OK);
        verify(res).body("{\"message\":\"bla\"}");
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
