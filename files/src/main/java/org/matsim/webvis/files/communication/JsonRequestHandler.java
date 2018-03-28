package org.matsim.webvis.files.communication;

import com.google.gson.JsonSyntaxException;
import org.matsim.webvis.common.communication.Answer;
import org.matsim.webvis.common.communication.ErrorCode;
import org.matsim.webvis.common.communication.RequestException;
import org.matsim.webvis.files.entities.User;
import spark.Request;
import spark.Response;

public abstract class JsonRequestHandler<T> extends JsonResponseHandler {

    private Class<T> requestClass;

    protected JsonRequestHandler(Class<T> requestClass) {
        this.requestClass = requestClass;
    }

    protected abstract Answer process(T body, User subject);

    @Override
    protected Answer process(Request request, Response response, User subject) {

        T body = null;
        try {
            if (isContentTypeJson(request))
                body = parseJsonBody(request.body());
        } catch (RequestException e) {
            return Answer.badRequest(e.getErrorCode(), e.getMessage());
        }
        return process(body, subject);
    }

    private boolean isContentTypeJson(Request request) {
        return request.contentType() != null && request.contentType().equals("application/json");
    }

    private T parseJsonBody(String body) throws RequestException {
        try {
            return getGson().fromJson(body, requestClass);
        } catch (JsonSyntaxException e) {
            throw new RequestException(ErrorCode.INVALID_REQUEST, "could not parse json-request");
        }
    }
}
