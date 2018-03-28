package org.matsim.webvis.files.communication;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.matsim.webvis.common.communication.Answer;
import org.matsim.webvis.common.communication.EntityAdapterFactory;
import org.matsim.webvis.common.communication.IterableSerializer;
import org.matsim.webvis.files.entities.User;
import spark.Request;
import spark.Response;

public abstract class JsonResponseHandler extends AuthenticatedRequestHandler {

    private Gson gson = new GsonBuilder().
            registerTypeHierarchyAdapter(Iterable.class, new IterableSerializer()).
            registerTypeAdapterFactory(new EntityAdapterFactory()).create();

    @Override
    protected Object handle(Request request, Response response, User subject) {

        Answer answer = process(request, response, subject);
        return createJsonResponse(answer, response);
    }

    Gson getGson() {
        return gson;
    }

    protected void setGson(Gson value) {
        this.gson = value;
    }

    private Object createJsonResponse(Answer answer, Response response) {
        response.status(answer.getStatusCode());
        response.type("application/json");
        String json = getGson().toJson(answer.getResponse());
        response.body(json);
        return response.body();
    }

    protected abstract Answer process(Request request, Response response, User subject);
}
