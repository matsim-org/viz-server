package org.matsim.webvis.auth.token;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.matsim.webvis.auth.entities.RelyingParty;
import org.matsim.webvis.auth.entities.Token;
import org.matsim.webvis.auth.relyingParty.RelyingPartyService;
import org.matsim.webvis.auth.util.TestUtils;
import org.matsim.webvis.common.communication.Answer;
import org.matsim.webvis.common.communication.HttpStatus;
import org.matsim.webvis.common.errorHandling.CodedException;
import org.matsim.webvis.common.errorHandling.InvalidInputException;
import org.matsim.webvis.common.errorHandling.UnauthorizedException;
import spark.Request;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class IntrospectionRequestHandlerTest {

    private IntrospectionRequestHandler testObject;

    @BeforeClass
    public static void setUpFixture() throws UnsupportedEncodingException, FileNotFoundException {
        TestUtils.loadTestConfig();
    }

    @Before
    public void setUp() {
        testObject = new IntrospectionRequestHandler();
        testObject.tokenService = mock(TokenService.class);
        testObject.rpService = mock(RelyingPartyService.class);
    }

    @Test(expected = InvalidInputException.class)
    public void process_invalidRequest_invalidInputException() {

        Request request = TestUtils.mockRequestWithQueryParamsMapAndBasicAuth(
                new HashMap<>(), "", "principal", "credential"
        );

        testObject.process(request, null);

        fail("invalid request should cause exception");

    }

    @Test(expected = UnauthorizedException.class)
    public void process_rpNotValid_unauthorizedException() throws CodedException {
        when(testObject.rpService.validateRelyingParty(any(), any())).thenThrow(new UnauthorizedException(""));

        testObject.process(createRequest(), null);

        fail("invalid authentication should cause exception");
    }

    @Test
    public void process_tokenInvalid_answerOkNotActive() {

        when(testObject.rpService.validateRelyingParty(any(), any())).thenReturn(null);
        when(testObject.tokenService.validateToken(any())).thenThrow(new RuntimeException(""));

        Answer result = testObject.process(createRequest(), null);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getResponse() instanceof InactiveIntrospectionResponse);
        assertFalse(((IntrospectionResponse) result.getResponse()).isActive());
    }

    @Test
    public void process_validRpAndValidToken_answerOkWithTokenInfos() {

        Token token = new Token();
        token.setTokenValue("value");
        token.setExpiresAt(Instant.now().plus(Duration.ofHours(1)));
        when(testObject.rpService.validateRelyingParty(any(), any())).thenReturn(new RelyingParty());
        when(testObject.tokenService.validateToken(any())).thenReturn(token);

        Answer result = testObject.process(createRequest(), null);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getResponse() instanceof ActiveIntrospectionResponse);
        assertTrue(((IntrospectionResponse) result.getResponse()).isActive());
    }

    private Request createRequest() {
        Map<String, String[]> map = new HashMap<>();
        map.put("token", new String[]{"some-token"});
        return TestUtils.mockRequestWithQueryParamsMapAndBasicAuth(
                map, "", "principal", "credential"
        );
    }
}
