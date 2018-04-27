package org.matsim.webvis.files.communication;

import com.google.gson.Gson;
import org.matsim.webvis.common.communication.Answer;
import org.matsim.webvis.common.communication.JsonRequestHandler;
import spark.Request;

public abstract class AuthenticatedJsonRequestHandler<T> extends JsonRequestHandler<T> {

    protected AuthenticatedJsonRequestHandler(Class<T> requestClass) {
        super(requestClass);
    }

    protected AuthenticatedJsonRequestHandler(Class<T> requestClass, Gson gson) {
        super(requestClass, gson);
    }

    protected abstract Answer process(T body, Subject subject);

    @Override
    protected Answer process(T body, Request rawRequest) {

        Subject subject = Subject.getSubject(rawRequest);
        return process(body, subject);
    }
}
