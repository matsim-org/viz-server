package org.matsim.viz.auth.authorization;

import org.junit.Test;
import org.matsim.viz.error.InvalidInputException;

import java.net.URI;

import static org.junit.Assert.*;

public class AuthenticationRequestTest {

    @Test(expected = InvalidInputException.class)
    public void validate_noScope_exception() {

        AuthenticationRequest request = new AuthenticationPostRequest(
                "", "token", URI.create("http://some.uri"),
                "client-id", "state", "nonce"
        );

        request.validate();

        fail();
    }

    @Test(expected = InvalidInputException.class)
    public void validate_noOpenIdScope_exception() {

        AuthenticationRequest request = new AuthenticationPostRequest(
                "some-scope", "token", URI.create("http://some.uri"),
                "client-id", "state", "nonce"
        );

        request.validate();

        fail();
    }

    @Test(expected = InvalidInputException.class)
    public void validate_noResponseType_exception() {

        AuthenticationRequest request = new AuthenticationPostRequest(
                "openid", "", URI.create("http://some.uri"),
                "client-id", "state", "nonce"
        );

        request.validate();

        fail();
    }

    @Test(expected = InvalidInputException.class)
    public void validate_noValidResponseType_exception() {

        AuthenticationRequest request = new AuthenticationPostRequest(
                "openid", "bla-type", URI.create("http://some.uri"),
                "client-id", "state", "nonce"
        );

        request.validate();

        fail();
    }

    @Test(expected = InvalidInputException.class)
    public void validate_validResponseTypePlusInvalid_exception() {

        AuthenticationRequest request = new AuthenticationPostRequest(
                "openid", "token some-other", URI.create("http://some.uri"),
                "client-id", "state", "nonce"
        );

        request.validate();

        fail();
    }

    @Test(expected = InvalidInputException.class)
    public void validate_responseTypeIdTokenButNoNonce_exception() {

        AuthenticationRequest request = new AuthenticationPostRequest(
                "openid", "id_token", URI.create("http://some.uri"),
                "client-id", "state", ""
        );

        request.validate();

        fail();
    }

    @Test(expected = InvalidInputException.class)
    public void validate_noClientId_exception() {

        AuthenticationRequest request = new AuthenticationPostRequest(
                "openid", "token", URI.create("http://some.uri"),
                "", "state", "nonce"
        );

        request.validate();

        fail();
    }

    @Test(expected = InvalidInputException.class)
    public void validate_noRedirectUri_exception() {

        AuthenticationRequest request = new AuthenticationPostRequest(
                "openid", "token", null,
                "client-id", "state", "nonce"
        );

        request.validate();

        fail();
    }

    @Test
    public void validate_valid_true() {

        AuthenticationRequest request = new AuthenticationPostRequest(
                "openid", "token id_token", URI.create("http://some.uri"),
                "client-id", "state", "nonce"
        );

        request.validate();
    }

    @Test
    public void isResponseTypeIdToken_not_false() {

        AuthenticationRequest request = new AuthenticationPostRequest(
                "openid", "token", URI.create("http://some.uri"),
                "client-id", "state", "nonce"
        );

        boolean result = request.isResponseTypeIdToken();

        assertFalse(result);
    }

    @Test
    public void isResponseTypeIdToken_yes_true() {

        AuthenticationRequest request = new AuthenticationPostRequest(
                "openid", "token id_token", URI.create("http://some.uri"),
                "client-id", "state", "nonce"
        );

        boolean result = request.isResponseTypeIdToken();

        assertTrue(result);
    }

    @Test
    public void isResponseTypeToken_no_false() {

        AuthenticationRequest request = new AuthenticationPostRequest(
                "openid", "id_token", URI.create("http://some.uri"),
                "client-id", "state", "nonce"
        );

        boolean result = request.isResponseTypeToken();

        assertFalse(result);
    }

    @Test
    public void isResponseTypeToken_yes_true() {

        AuthenticationRequest request = new AuthenticationPostRequest(
                "openid", "token id_token", URI.create("http://some.uri"),
                "client-id", "state", "nonce"
        );

        boolean result = request.isResponseTypeToken();

        assertTrue(result);
    }
}
