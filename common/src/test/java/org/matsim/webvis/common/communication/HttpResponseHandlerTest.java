package org.matsim.webvis.common.communication;

import com.google.gson.Gson;
import org.apache.http.HttpResponse;
import org.junit.Before;
import org.junit.Test;
import org.matsim.webvis.common.service.CodedException;
import org.matsim.webvis.common.service.InternalException;
import org.matsim.webvis.common.service.UnauthorizedException;
import org.matsim.webvis.common.util.TestUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class HttpResponseHandlerTest {

    private TestableResponseHandler testObject;

    @Before
    public void setUp() {
        testObject = new TestableResponseHandler();
    }

    @Test(expected = UnauthorizedException.class)
    public void handleResponse_status401_unauthorizedException() {

        HttpResponse response = TestUtils.mockHttpResponse(401, "some message");

        testObject.handleResponse(response);
    }

    @Test
    public void handleResponse_statusNotOk_codedException() {

        ErrorResponse error = new ErrorResponse("error", "description");

        try {
            HttpResponse response = TestUtils.mockHttpResponse(400, new Gson().toJson(error));
            testObject.handleResponse(response);
        } catch (CodedException e) {
            assertEquals(error.getError(), e.getErrorCode());
            assertEquals(error.getError_description(), e.getMessage());
        }
    }

    @Test(expected = InternalException.class)
    public void handleResponse_statusNotOkAndNotErrorResponse_internalException() {

        HttpResponse response = TestUtils.mockHttpResponse(400, "not Json");
        testObject.handleResponse(response);
    }

    @Test
    public void handleResponse_statusOk_processResponseCalled() {

        HttpResponse response = TestUtils.mockHttpResponse(HttpStatus.OK, "Some Text");
        testObject.handleResponse(response);

        assertTrue(testObject.processWasCalled);
    }

    private static class TestableResponseHandler extends HttpResponseHandler<String> {

        boolean processWasCalled = false;

        @Override
        protected String processResponse(HttpResponse httpResponse) {
            processWasCalled = true;
            return null;
        }
    }
}
