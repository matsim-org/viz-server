package org.matsim.webvis.auth.token;

import org.matsim.webvis.auth.entities.Token;

class ActiveIntrospectionResponse extends IntrospectionResponse {

    ActiveIntrospectionResponse(Token token) {
        super(token, true);
    }
}
