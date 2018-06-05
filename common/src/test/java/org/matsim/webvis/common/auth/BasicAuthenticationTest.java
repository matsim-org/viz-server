package org.matsim.webvis.common.auth;

import org.junit.Test;
import org.matsim.webvis.common.communication.HttpCredential;
import org.matsim.webvis.common.errorHandling.InvalidInputException;
import org.matsim.webvis.common.util.TestUtils;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.fail;

public class BasicAuthenticationTest {

    @Test(expected = InvalidInputException.class)
    public void decodeAuthorizationHeader_noBasicAuthorization_exception() {

        BasicAuthentication.decodeAuthorizationHeader("");

        fail("invalid basic auth should throw InvalidInputException");
    }

    @Test(expected = InvalidInputException.class)
    public void decodeAuthorizationHeader_invalidBasicAuthorizationFormat_exception() {

        BasicAuthentication.decodeAuthorizationHeader("invalidAuthHeader");

        fail("invalid basic auth should throw InvalidInputException");
    }

    @Test(expected = InvalidInputException.class)
    public void decodeAuthorizationHeader_invalidBasicAuthorizationFormat2_exception() {

        BasicAuthentication.decodeAuthorizationHeader("two arts");

        fail("invalid basic auth should throw InvalidInputException");
    }

    @Test(expected = InvalidInputException.class)
    public void decodeAuthorizationHeader_invalidCredentialsFormat_exception() {

        BasicAuthentication.decodeAuthorizationHeader("Basic invalidformating");

        fail("invalid basic auth should throw InvalidInputException");
    }

    @Test
    public void decodeAuthorizationHeader_basicAuth_parameterSet() {

        final String principal = "principal";
        final String credential = "credential";
        final String encoded = TestUtils.encodeBasicAuth(principal, credential);

        PrincipalCredentialToken token = BasicAuthentication.decodeAuthorizationHeader(encoded);

        assertEquals(principal, token.getPrincipal());
        assertEquals(credential, token.getCredential());
    }

    @Test
    public void encodeToAuthorizationHeader_token_encoded() {

        final String principal = "principal";
        final String credential = "credential";
        final String encoded = TestUtils.encodeBasicAuth(principal, credential);

        String result = BasicAuthentication.encodeToAuthorizationHeader(new PrincipalCredentialToken(principal, credential));

        assertEquals(encoded, result);
    }

    @Test
    public void encodeToCredential_encodedCredential() {

        final String principal = "principal";
        final String credential = "credential";
        final String encoded = TestUtils.encodeBasicAuth(principal, credential);

        HttpCredential result = BasicAuthentication.encodeToCredential(new PrincipalCredentialToken(principal, credential));

        assertEquals(encoded, result.headerValue());

    }
}
