package org.matsim.webvis.common.communication;

import org.junit.Test;
import org.matsim.webvis.common.service.InvalidInputException;
import spark.Response;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class JsonHelperTest {

    @Test(expected = InvalidInputException.class)
    public void parseJson_noJson_exception() {
        JsonHelper.parseJson(null, JsonHelper.class);
    }

    @Test(expected = InvalidInputException.class)
    public void parseJson_illegalJson_exception() {

        JsonHelper.parseJson("{'not':json", Object.class);
    }

    @Test
    public void parseJson_actualJson() {

        final String value = "some";

        TestTemplate result = JsonHelper.parseJson("{'value': '" + value + "'}", TestTemplate.class);

        assertEquals(value, result.value);
    }

    @Test
    public void createJsonResponse() {
        final String errorCode = RequestError.INVALID_REQUEST;
        final String message = "message";
        final Answer answer = Answer.badRequest(errorCode, message);
        Response response = mock(Response.class);

        JsonHelper.createJsonResponse(answer, response);

        verify(response).status(answer.getStatusCode());
        verify(response).type(ContentType.APPLICATION_JSON);
        verify(response).body(anyString());
    }

    public class TestTemplate {
        String value;
    }
}
