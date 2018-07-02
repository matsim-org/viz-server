package org.matsim.webvis.files.communication;

import com.google.gson.Gson;
import org.matsim.webvis.common.auth.AuthenticationResult;
import org.matsim.webvis.common.communication.Answer;
import org.matsim.webvis.common.communication.JsonRequestHandler;
import org.matsim.webvis.files.permission.Subject;
import spark.Request;

public abstract class AuthenticatedJsonRequestHandler<T> extends JsonRequestHandler<T> {

    protected AuthenticatedJsonRequestHandler(Class<T> requestClass, Gson gson) {
        super(requestClass, gson);
    }

    protected abstract Answer process(T body, Subject subject);

    @Override
    protected Answer process(T body, Request rawRequest) {

        AuthenticationResult authResult = AuthenticationResult.fromRequestAttribute(rawRequest);
        //return process(body, Subject.createSubject(null));
        return null;
    }
}
