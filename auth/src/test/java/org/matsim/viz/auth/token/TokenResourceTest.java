package org.matsim.viz.auth.token;

import org.junit.Test;
import org.matsim.viz.auth.entities.RelyingParty;
import org.matsim.viz.auth.entities.Token;
import org.matsim.viz.error.InvalidInputException;

import java.time.Instant;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TokenResourceTest {

    @Test(expected = InvalidInputException.class)
    public void token_grantTypeNotClientCredentials_exception() {

        TokenResource testObject = new TokenResource(mock(TokenService.class));

        testObject.token(null, "invalid-type", "some-scope");

        fail("invalid grant_type should cause exception");
    }

    @Test
    public void token_success_accessTokenResponse() {

        Token token = new Token();
        token.setTokenValue("value");
        token.setScope("scope");
        token.setExpiresAt(Instant.now());

        TokenService tokenService = mock(TokenService.class);
        when(tokenService.grantForScope(any(), any())).thenReturn(token);
        TokenResource testObject = new TokenResource(tokenService);

        AccessTokenResponse response = testObject.token(new RelyingParty(), "client_credentials", "scope");

        assertEquals(token.getTokenValue(), response.getAccess_token());
        assertEquals(token.getScope(), response.getScope());
        assertEquals(token.getExpiresAt().getEpochSecond(), response.getExpires_in());
    }
}
