package org.matsim.webvis.common.auth;

import org.matsim.webvis.common.service.InternalException;
import spark.Request;

public class AuthenticationStore {

    public static final String SUBJECT_ATTRIBUTE = "subject";

    public static AuthenticationResult getAuthenticationResult(Request request) {

        AuthenticationResult authentication = request.attribute(SUBJECT_ATTRIBUTE);

        if (authentication == null) {
            throw new InternalException("Attribute 'subject' was not set. 'setAuthenticationAsAttribute' must be called first");
        }
        return authentication;
    }

    static void setAuthenticationAttribute(Request request, AuthenticationResult authenticationResult) {
        request.attribute(SUBJECT_ATTRIBUTE, authenticationResult);
    }
}
