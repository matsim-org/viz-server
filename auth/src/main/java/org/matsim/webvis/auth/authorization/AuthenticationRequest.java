package org.matsim.webvis.auth.authorization;

import lombok.Getter;
import org.matsim.webvis.common.communication.RequestWithParams;
import org.matsim.webvis.common.errorHandling.InvalidInputException;
import spark.QueryParamsMap;

import java.net.URI;

@Getter
public class AuthenticationRequest extends RequestWithParams {

    public static final String OPEN_ID = "openid";

    public static final String RESPONSE_TYPE = "response_type";
    public static final String REDIRECT_URI = "redirect_uri";
    public static final String SCOPE = "scope";
    public static final String CLIENT_ID = "client_id";
    public static final String STATE = "state";
    public static final String NONCE = "nonce";

    private String scope;

    //required params
    private String[] responseType;
    private URI redirectUri;
    //optional params
    private String state;
    private String clientId;
    private AuthenticationRequest.Type type;
    private String nonce;
    AuthenticationRequest(QueryParamsMap params) {
        this.redirectUri = initUri(params);
        this.scope = initScope(params);
        this.responseType = initResponseType(params);
        this.clientId = extractRequiredValue(CLIENT_ID, params);
        this.state = extractOptionalValue(STATE, params);
        this.nonce = initNonce(params);
    }
    private String display = "";
    private String prompt = "";
    private String maxAge = "";
    private String idTokenHint = "";
    private String loginHint = "";
    private String arcValues = "";

    private URI initUri(QueryParamsMap params) {

        String uri = extractRequiredValue(REDIRECT_URI, params);

        try {
            return URI.create(uri);
        } catch (IllegalArgumentException e) {
            throw new InvalidInputException("invalid redirect uri");
        }
    }

    private String initScope(QueryParamsMap params) {

        String scope = extractRequiredValue(SCOPE, params);
        if (!scope.contains("openid"))
            throw new InvalidInputException("only openid-connect is supported. Scope must contain 'openid'");
        return scope;
    }

    private String[] initResponseType(QueryParamsMap params) {

        String[] responseTypes = extractRequiredValue(RESPONSE_TYPE, params).split(" ");

        if (responseTypes.length == 1 && responseTypes[0].equals("token"))
            this.type = Type.AccessToken;
        else if (responseTypes.length == 1 && responseTypes[0].equals("id_token"))
            this.type = Type.IdToken;
        else if (responseTypes.length == 2
                && responseTypes[0].equals("id_token")
                && responseTypes[1].equals("token"))
            this.type = Type.AccessAndIdToken;
        else
            throw new InvalidInputException("response types may be: 'token', 'id_token', 'id_token token'.");

        return responseTypes;
    }

    private String initNonce(QueryParamsMap params) {

        if (type == Type.AccessAndIdToken || type == Type.IdToken)
            return extractRequiredValue(NONCE, params);
        else return extractOptionalValue(NONCE, params);
    }

    public enum Type {AccessToken, AccessAndIdToken, IdToken}
}
