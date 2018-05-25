package org.matsim.webvis.auth.token;

import org.matsim.webvis.auth.entities.Token;

public class ActiveIntrospectionResponse extends IntrospectionResponse {

    ActiveIntrospectionResponse(Token token) {
        super(token, true);
    }
}
