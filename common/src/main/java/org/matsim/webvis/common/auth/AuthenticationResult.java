package org.matsim.webvis.common.auth;

import lombok.Getter;
import org.matsim.webvis.common.errorHandling.InternalException;
import spark.Request;

@Getter
public class AuthenticationResult {

    private boolean active;
    private String scope;
    private String client_id;
    private String username;
    private String token_type;
    private long exp;
    private long iat;
    private String sub;
    private String aud;
    private String iss;

    public static final String SUBJECT_ATTRIBUTE = "subject";

    public static AuthenticationResult fromRequestAttribute(Request request) {

        AuthenticationResult authentication = request.attribute(SUBJECT_ATTRIBUTE);

        if (authentication == null) {
            throw new InternalException("Attribute 'subject' was not set. 'setAuthenticationAsAttribute' must be called first");
        }
        return authentication;
    }

    static void intoRequestAttribute(Request request, AuthenticationResult authenticationResult) {
        request.attribute(SUBJECT_ATTRIBUTE, authenticationResult);
    }

    /**
     * for testing purposes
     *
     * @param isActive whether authentication Result is active
     * @return returns AuthenticationResult with only active parameter set. Everything else is null
     */
    static AuthenticationResult create(boolean isActive) {
        AuthenticationResult result = new AuthenticationResult();
        result.active = isActive;
        return result;
    }
}
