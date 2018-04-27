package org.matsim.webvis.common.communication;

import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.Route;

public abstract class JsonResponseHandler implements Route {

    private Gson parser;

    JsonResponseHandler(Gson parser) {
        this.parser = parser;
    }

    protected JsonResponseHandler() {
        this(null);
    }

    Gson getParser() {
        return parser;
    }

    @Override
    public Object handle(Request request, Response response) {

        Answer answer = process(request, response);
        return JsonHelper.createJsonResponse(answer, response, parser);
    }

    protected abstract Answer process(Request request, Response response);
}
