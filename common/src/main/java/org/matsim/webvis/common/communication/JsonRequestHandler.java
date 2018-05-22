package org.matsim.webvis.common.communication;

import com.google.gson.Gson;
import org.matsim.webvis.common.service.InvalidInputException;
import spark.Request;
import spark.Response;

public abstract class JsonRequestHandler<T> extends JsonResponseHandler {

    private Class<T> requestClass;

    protected JsonRequestHandler(Class<T> requestClass, Gson parser) {

        super(parser);
        this.requestClass = requestClass;
    }

    protected JsonRequestHandler(Class<T> requestClass) {
        this(requestClass, null);
    }

    protected abstract Answer process(T body, Request rawRequest);

    @Override
    protected Answer process(Request request, Response response) {

        T body;
        try {
            if (!ContentType.isJson(request.contentType())) {
                return Answer.badRequest(RequestError.INVALID_REQUEST, "only content-type: 'application/json' allowed");
            } else
                body = parseJsonBody(request.body());
        } catch (InvalidInputException e) {
            return Answer.badRequest(e.getErrorCode(), e.getMessage());
        }
        return process(body, request);
    }

    private T parseJsonBody(String body) throws InvalidInputException {
        try {
            return JsonHelper.parseJson(body, requestClass, getParser());
        } catch (Throwable e) {
            throw new InvalidInputException("could not parse json-request");
        }
    }
}
