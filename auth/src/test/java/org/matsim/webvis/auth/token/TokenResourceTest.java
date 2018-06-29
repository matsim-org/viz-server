package org.matsim.webvis.auth.token;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.matsim.webvis.auth.entities.RelyingParty;
import org.matsim.webvis.auth.entities.Token;
import org.matsim.webvis.auth.util.TestUtils;
import org.matsim.webvis.error.InvalidInputException;

import java.time.Instant;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TokenResourceTest {

    private TokenResource testObject;

    @BeforeClass
    public static void setUpFixture() {
        TestUtils.loadTestConfigIfNecessary();
    }

    @Before
    public void setUp() {
        testObject = new TokenResource();
        testObject.tokenService = mock(TokenService.class);
    }

    @Test(expected = InvalidInputException.class)
    public void token_grantTypeNotClientCredentials_exception() {

        testObject.token(null, "invalid-type", "some-scope");

        fail("invalid grant_type should cause exception");
    }

    @Test
    public void token_success_accessTokenResponse() {

        Token token = new Token();
        token.setTokenValue("value");
        token.setScope("scope");
        token.setExpiresAt(Instant.now());
        when(testObject.tokenService.grantForScope(any(), any())).thenReturn(token);

        AccessTokenResponse response = testObject.token(new RelyingParty(), "client_credentials", "scope");

        assertEquals(token.getTokenValue(), response.getAccess_token());
        assertEquals(token.getScope(), response.getScope());
        assertEquals(token.getExpiresAt().getEpochSecond(), response.getExpires_in());
    }
}
