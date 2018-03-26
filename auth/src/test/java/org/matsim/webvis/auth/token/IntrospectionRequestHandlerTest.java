package org.matsim.webvis.auth.token;

import org.junit.Before;
import org.junit.Test;
import org.matsim.webvis.auth.entities.IdToken;
import org.matsim.webvis.auth.entities.Token;
import org.matsim.webvis.auth.entities.User;
import org.matsim.webvis.auth.relyingParty.RelyingPartyService;
import org.matsim.webvis.auth.util.TestUtils;
import org.matsim.webvis.common.communication.Answer;
import org.matsim.webvis.common.communication.ErrorResponse;
import org.matsim.webvis.common.communication.HttpStatus;
import org.matsim.webvis.common.communication.RequestException;
import spark.Request;

import java.time.Duration;
import java.time.Instant;
import java.util.Base64;

import static junit.framework.TestCase.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class IntrospectionRequestHandlerTest {

    private IntrospectionRequestHandler testObject;

    @Before
    public void setUp() throws Exception {
        TestUtils.loadTestConfig();
        testObject = new IntrospectionRequestHandler();
        testObject.tokenService = mock(TokenService.class);
        testObject.rpService = mock(RelyingPartyService.class);
    }

    @Test
    public void process_rpNotValid_answerUnauthorized() throws Exception {
        when(testObject.rpService.validateRelyingParty(any(), any())).thenThrow(new Exception());

        Answer result = testObject.process(createRequest());

        assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
        assertTrue(result.getResponse() instanceof ErrorResponse);
    }

    @Test
    public void process_tokenInvalid_answerOkNotActive() throws Exception {
        when(testObject.rpService.validateRelyingParty(any(), any())).thenReturn(null);
        when(testObject.tokenService.getToken("token")).thenReturn(null);

        Answer result = testObject.process(createRequest());

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getResponse() instanceof IntrospectionResponse);
        assertFalse(((IntrospectionResponse) result.getResponse()).isActive());
    }

    @Test
    public void process_validRpAndValidToken_answerOkWithTokenInfos() throws Exception {

        when(testObject.rpService.validateRelyingParty(any(), any())).thenReturn(null);

        Token token = new IdToken();
        token.setExpiresAt(Instant.now().plus(Duration.ofHours(1)));
        User user = new User();
        user.setId("id");
        token.setUser(user);
        when(testObject.tokenService.getToken("token")).thenReturn(token);

        Answer result = testObject.process(createRequest());

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getResponse() instanceof IntrospectionResponse);
        assertTrue(((IntrospectionResponse) result.getResponse()).isActive());
    }

    private IntrospectionRequest createRequest() throws RequestException {
        Request mockReq = mock(Request.class);
        String basicCredentials = Base64.getEncoder().encodeToString(("client:secret").getBytes());
        when(mockReq.headers(any())).thenReturn("Basic " + basicCredentials);
        when(mockReq.queryParams(any())).thenReturn("token");

        return new IntrospectionRequest(mockReq);
    }
}
