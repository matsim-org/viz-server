package org.matsim.webvis.auth.token;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.matsim.webvis.auth.entities.Token;
import org.matsim.webvis.auth.util.TestUtils;
import org.matsim.webvis.common.communication.Answer;
import org.matsim.webvis.common.communication.HttpStatus;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.time.Instant;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ClientCredentialsGrantTest {

    private ClientCredentialsGrant testObject;

    @BeforeClass
    public static void setUpFixture() throws UnsupportedEncodingException, FileNotFoundException {
        TestUtils.loadTestConfig();
    }

    @Before
    public void setUp() {
        testObject = new ClientCredentialsGrant();
    }

    @Test
    public void processRequest_allGood_Answer() {

        TokenRequest request = TestUtils.mockTokenRequest("principal", "credential", new String[0]);
        Token token = new Token();
        token.setTokenValue("value");
        token.setExpiresAt(Instant.now());
        testObject.tokenService = mock(TokenService.class);
        when(testObject.tokenService.grantWithClientCredentials(any())).thenReturn(token);

        Answer answer = testObject.processRequest(request);

        assertEquals(HttpStatus.OK, answer.getStatusCode());
        assertTrue(answer.getResponse() instanceof AccessTokenResponse);
        AccessTokenResponse response = (AccessTokenResponse) answer.getResponse();
        assertEquals("Bearer", response.getToken_type());
        assertEquals(token.getTokenValue(), response.getAccess_token());
    }
}
