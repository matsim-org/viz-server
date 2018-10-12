package org.matsim.viz.auth.token;

import org.matsim.viz.auth.entities.Token;

class ActiveIntrospectionResponse extends IntrospectionResponse {

    ActiveIntrospectionResponse(Token token) {
        super(token, true);
    }
}
