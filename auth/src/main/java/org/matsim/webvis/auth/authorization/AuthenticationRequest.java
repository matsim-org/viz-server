package org.matsim.webvis.auth.authorization;

import org.apache.commons.lang3.StringUtils;
import org.matsim.webvis.common.errorHandling.InvalidInputException;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

abstract class AuthenticationRequest {

    private static final String ID_TOKEN = "id_token";
    private static final String TOKEN = "token";
    private static final List<String> validResponseTypes = getValidResponseTypes();
    final String RESPONSE_TYPE = "response_type";
    final String REDIRECT_URI = "redirect_uri";
    final String SCOPE = "scope";
    final String CLIENT_ID = "client_id";
    final String STATE = "state";
    final String NONCE = "nonce";
    private final String OPEN_ID = "openid";

    private static List<String> getValidResponseTypes() {
        List<String> types = new ArrayList<>();
        types.add(TOKEN);
        types.add(ID_TOKEN);
        return types;
    }

    abstract String getScope();

    abstract String getResponseType();

    abstract URI getRedirectUri();

    abstract String getState();

    abstract String getClientId();

    abstract String getNonce();

    void validate() {
        if (isInvalidScope())
            throw new InvalidInputException("scope must be set and contain 'openid'");
        if (isInvalidResponseType())
            throw new InvalidInputException("response type was not set");
        if (isResponseTypeIdToken() && StringUtils.isBlank(getNonce()))
            throw new InvalidInputException("nonce must be provided if response type is id_token");
    }

    boolean isResponseTypeIdToken() {
        return getResponseType().contains(ID_TOKEN);
    }

    boolean isResponseTypeToken() {
        return getResponseType().contains(TOKEN);
    }

    private boolean isInvalidScope() {
        return StringUtils.isBlank(getScope()) || !getScope().contains(OPEN_ID);
    }

    private boolean isInvalidResponseType() {

        String[] responseTypes = getResponseType().split(" ");
        return Arrays.stream(responseTypes).noneMatch(validResponseTypes::contains);
    }

}
