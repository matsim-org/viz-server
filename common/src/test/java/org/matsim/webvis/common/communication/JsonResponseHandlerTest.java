package org.matsim.webvis.common.communication;

import com.google.gson.Gson;
import org.junit.Test;
import spark.Request;
import spark.Response;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class JsonResponseHandlerTest {

    private Answer testAnswer = Answer.badRequest("code", "message");

    @Test
    public void handle_answerIntoJsonResponse() {

        Response response = mock(Response.class);
        TestableJsonReponseHandler handler = new TestableJsonReponseHandler();

        handler.handle(null, response);

        verify(response).status(testAnswer.getStatusCode());
        verify(response).type(ContentType.APPLICATION_JSON);
        verify(response).body(new Gson().toJson(testAnswer.getResponse()));
    }

    private class TestableJsonReponseHandler extends JsonResponseHandler {


        @Override
        protected Answer process(Request request, Response response) {
            return testAnswer;
        }
    }
}
