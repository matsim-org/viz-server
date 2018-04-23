package org.matsim.webvis.files.communication;

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
            if (!ContentType.isJson(request.contentType()))
                return Answer.badRequest(ErrorCode.INVALID_REQUEST, "only content-type: 'application/json' allowed");
            else
                body = parseJsonBody(request.body());
        } catch (RequestException e) {
            return Answer.badRequest(e.getErrorCode(), e.getMessage());
        }
        Subject subject = Subject.getSubject(request);
        return process(body, subject);
    }

    private T parseJsonBody(String body) throws RequestException {
        try {
            return JsonHelper.parseJson(body, requestClass);
        } catch (Throwable e) {
            throw new RequestException(ErrorCode.INVALID_REQUEST, "could not parse json-request");
        }
    }
}
