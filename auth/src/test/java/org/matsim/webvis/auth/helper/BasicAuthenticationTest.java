package org.matsim.webvis.auth.helper;

import org.junit.Test;
import org.matsim.webvis.auth.util.TestUtils;
import org.matsim.webvis.common.service.InvalidInputException;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.fail;

public class BasicAuthenticationTest {


    @Test(expected = InvalidInputException.class)
    public void creation_noBasicAuthorization_exception() {

        new BasicAuthentication("");

        fail("invalid basic auth should throw InvalidInputException");
    }

    @Test(expected = InvalidInputException.class)
    public void creation_invalidBasicAuthorizationFormat_exception() {

        new BasicAuthentication("invalidAuthHeader");

        fail("invalid basic auth should throw InvalidInputException");
    }

    @Test(expected = InvalidInputException.class)
    public void creation_invalidBasicAuthorizationFormat2_exception() {

        new BasicAuthentication("two arts");

        fail("invalid basic auth should throw InvalidInputException");
    }

    @Test(expected = InvalidInputException.class)
    public void creation_invalidCredentialsFormat_exception() {

        new BasicAuthentication("Basic invalidformating");

        fail("invalid basic auth should throw InvalidInputException");
    }

    @Test
    public void creation_basicAuth_parameterSet() {

        final String principal = "principal";
        final String credential = "credential";
        final String encoded = TestUtils.encodeBasicAuth(principal, credential);

        BasicAuthentication instance = new BasicAuthentication(encoded);

        assertEquals(principal, instance.getPrincipal());
        assertEquals(credential, instance.getCredential());
    }
}
