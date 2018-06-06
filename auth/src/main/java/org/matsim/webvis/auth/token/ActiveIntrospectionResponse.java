package org.matsim.webvis.auth.token;

import org.matsim.webvis.auth.entities.Token;

public class ActiveIntrospectionResponse extends IntrospectionResponse {

    ActiveIntrospectionResponse(Token token, String scope) {
        super(token, scope, true);
    }
}
