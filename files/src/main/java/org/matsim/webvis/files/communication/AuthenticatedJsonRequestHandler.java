package org.matsim.webvis.files.communication;

public abstract class AuthenticatedJsonRequestHandler<T> {

  /*  protected AuthenticatedJsonRequestHandler(Class<T> requestClass, Gson gson) {
        super(requestClass, gson);
    }

    protected abstract Answer process(T body, Subject subject);

    @Override
    protected Answer process(T body, Request rawRequest) {

        AuthenticationResult authResult = AuthenticationResult.fromRequestAttribute(rawRequest);
        //return process(body, Subject.createSubject(null));
        return null;
    }
    */
}
