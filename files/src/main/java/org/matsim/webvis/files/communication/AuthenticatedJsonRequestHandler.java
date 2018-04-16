package org.matsim.webvis.files.communication;

import com.google.gson.JsonSyntaxException;
import org.matsim.webvis.common.communication.Answer;
import org.matsim.webvis.common.communication.ErrorCode;
import org.matsim.webvis.common.communication.RequestException;
import spark.Request;
import spark.Response;

public abstract class AuthenticatedJsonRequestHandler<T> extends JsonResponseHandler {

    private Class<T> requestClass;

    protected AuthenticatedJsonRequestHandler(Class<T> requestClass) {
        this.requestClass = requestClass;
    }

    protected abstract Answer process(T body, Subject subject);

    @Override
    protected Answer process(Request request, Response response) {

        T body;
        try {
            if (!isContentTypeJson(request))
                return Answer.badRequest(ErrorCode.INVALID_REQUEST, "only content-type: 'application/json' allowed");
            else
                body = parseJsonBody(request.body());
        } catch (RequestException e) {
            return Answer.badRequest(e.getErrorCode(), e.getMessage());
        }
        Subject subject = Subject.getSubject(request);
        return process(body, subject);
    }

    private boolean isContentTypeJson(Request request) {
        return request.contentType() != null && request.contentType().equals("application/json");
    }

    private T parseJsonBody(String body) throws RequestException {
        try {
            T result = getGson().fromJson(body, requestClass);
            if (result == null) throw new RequestException(ErrorCode.INVALID_REQUEST, "no json-body present");
            return result;
        } catch (Throwable e) {
            throw new RequestException(ErrorCode.INVALID_REQUEST, "could not parse json-request");
        }
    }
}
