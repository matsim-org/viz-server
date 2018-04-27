package org.matsim.webvis.common.communication;

import spark.Request;
import spark.Response;
import spark.Route;

public abstract class JsonResponseHandler implements Route {

    @Override
    public Object handle(Request request, Response response) {

        Answer answer = process(request, response);
        return JsonHelper.createJsonResponse(answer, response);
    }

    protected abstract Answer process(Request request, Response response);
}
