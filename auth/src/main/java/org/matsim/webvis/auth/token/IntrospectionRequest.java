package org.matsim.webvis.auth.token;

import lombok.Getter;
import org.matsim.webvis.common.communication.RequestWithParams;
import org.matsim.webvis.common.service.InvalidInputException;
import spark.Request;

import java.util.Base64;

@Getter
class IntrospectionRequest extends RequestWithParams {

    private String rpId;
    private String rpSecret;
    private String token;

    IntrospectionRequest(Request request) throws InvalidInputException {

        parseCredentials(request.headers("Authorization"));
        token = extractRequiredValue(OAuthParameters.TOKEN, request.queryMap());
    }

    private void parseCredentials(String authorization) throws InvalidInputException {
        if (authorization == null) {
            throw new InvalidInputException("request must contain 'Authorization' header");
        }
        String[] args = authorization.split(" ");


        boolean isValidRequest = false;

        if (args.length == 2 && args[0].equals("Basic")) {
            String[] credentials = new String(Base64.getDecoder().decode(args[1])).split(":");
            if (credentials.length == 2) {
                this.rpId = credentials[0];
                this.rpSecret = credentials[1];
                isValidRequest = true;
            }
        }
        if (!isValidRequest) {
            throw new InvalidInputException("Invalid Basic Authentication request");
        }
    }
}
