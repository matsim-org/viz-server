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
        Response res = mock(Response.class);

        testObject.handle(req, res);

        verify(res).status(HttpStatus.STATUS_BADREQUEST);
        verify(res).body(any());
    }

    @Test
    public void processingFails_badRequest() {

        String body = "{ message: 'message' }";
        Request req = mock(Request.class);
        when(req.body()).thenReturn(body);
        Response res = mock(Response.class);
        testObject.shouldFailProcess = true;

        testObject.handle(req, res);

        verify(res).status(HttpStatus.STATUS_INTERNAL_SERVER_ERROR);
        verify(res).body(anyString());
    }

    @Test
    public void processingSucceeds_ok() {

        String body = "{ message: 'message' }";
        Request req = mock(Request.class);
        when(req.body()).thenReturn(body);
        Response res = mock(Response.class);

        testObject.handle(req, res);

        verify(res).status(HttpStatus.STATUS_OK);
        verify(res).body("ok");
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
            if (shouldFailProcess) return Answer.internalError("some error");

            return Answer.ok("ok");
        }
    }
}
