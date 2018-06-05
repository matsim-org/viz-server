package org.matsim.webvis.auth.authorization;

import lombok.Getter;
import org.matsim.webvis.common.communication.RequestWithParams;
import org.matsim.webvis.common.errorHandling.InvalidInputException;
import spark.QueryParamsMap;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

@Getter
public class AuthenticationRequest extends RequestWithParams {

    public static final String RESPONSE_TYPE = "response_type";
    public static final String REDIRECT_URI = "redirect_uri";
    public static final String SCOPE = "scope";
    public static final String CLIENT_ID = "client_id";
    public static final String STATE = "state";
    public static final String NONCE = "nonce";

    AuthenticationRequest(QueryParamsMap params) throws URIException, InvalidInputException {

        initializeRequiredParameters(params);
        initializeOptionalParameters(params);
    }

    //required params
    private String[] responseType;
    private URI redirectUri;
    private String[] scopes;
    private String clientId;
    private AuthenticationRequest.Type type;


    //optional params
    private String state = "";
    private String nonce = "";
    private String display = "";
    private String prompt = "";
    private String maxAge = "";
    private String idTokenHint = "";
    private String loginHint = "";
    private String arcValues = "";

    private void initializeRequiredParameters(QueryParamsMap params) throws URIException, InvalidInputException {

        this.scopes = initScope(params);
        this.redirectUri = initRedirectURI(params);
        this.responseType = initResponseType(params);
        clientId = extractRequiredValue(CLIENT_ID, params);

        this.state = extractOptionalValue(STATE, params);
        if (!this.type.equals(Type.AuthCode))
            nonce = extractRequiredValue(NONCE, params);
    }

    private void initializeOptionalParameters(QueryParamsMap params) {
        state = extractOptionalValue(STATE, params);
    }

    private String[] initScope(QueryParamsMap params) throws InvalidInputException {
        String[] scopes = extractRequiredValue(SCOPE, params).split(" ");
        boolean openid = Arrays.stream(scopes).anyMatch(scope -> scope.equals("openid"));

        if (!openid) throw new InvalidInputException("scope must contain 'openid'");
        return scopes;
    }

    private URI initRedirectURI(QueryParamsMap params) throws URIException {
        try {
            if (params.hasKey(REDIRECT_URI)) {
                return new URI(params.get(REDIRECT_URI).value());
            }
        } catch (URISyntaxException ignored) {
        }
        throw new URIException();
    }

    private String[] initResponseType(QueryParamsMap params) throws InvalidInputException {
        String[] responseTypes = extractRequiredValue(RESPONSE_TYPE, params).split(" ");

        if (responseTypes.length == 1 && responseTypes[0].equals("code")) {
            this.type = Type.AuthCode;
        }
        else if (responseTypes.length == 1 && responseTypes[0].equals("id_token"))
            this.type = Type.IdToken;
        else if (responseTypes.length == 2 &&
                responseTypes[0].equals("id_token") &&
                responseTypes[1].equals("token"))
            this.type = Type.AccessAndIdToken;
        else
            throw new InvalidInputException("response types may be: 'code', 'id_token', 'id_token token'.");

        return responseTypes;
    }

    public enum Type {AuthCode, AccessAndIdToken, IdToken}


}
