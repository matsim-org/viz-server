package org.matsim.webvis.common.communication;

import com.google.gson.Gson;
import org.matsim.webvis.common.service.InvalidInputException;
import spark.Request;
import spark.Response;
import spark.Route;

public abstract class JsonResponseHandler implements Route {

    private Gson parser;

    protected JsonResponseHandler(Gson parser) {
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

        if (!ContentType.isJson(request.contentType()))
            throw new InvalidInputException("only content-type '" + ContentType.APPLICATION_JSON + "' allowed");
        Answer answer = process(request, response);
        return JsonHelper.createJsonResponse(answer, response, parser);
    }

    protected abstract Answer process(Request request, Response response);
}
