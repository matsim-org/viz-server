package token;

import data.entities.AccessToken;
import data.entities.User;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import requests.Answer;
import requests.ErrorCode;
import requests.ErrorResponse;
import requests.HttpStatus;
import spark.Request;
import spark.Response;
import util.TestUtils;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TokenRequestHandlerTest {

    private TokenRequestHandler testObject;

    @BeforeClass
    public static void setUpFixture() throws UnsupportedEncodingException, FileNotFoundException {
        TestUtils.loadTestConfig();
    }

    @Before
    public void setUp() throws Exception {
        testObject = new TokenRequestHandler();
    }

    //Test handling of url-parameter

    @Test
    public void wrongQueryParams_serverError() {

        Request request = mock(Request.class);
        when(request.body()).thenReturn("someinvalidcontend");
        when(request.contentType()).thenReturn("application/x-www-form-urlencoded");
        Response response = mock(Response.class);

        testObject.handle(request, response);

        verify(response).status(HttpStatus.BAD_REQUEST);
        verify(response).body(any());
    }

    @Test
    public void wrongContentType_badRequest() {
        Request request = mock(Request.class);
        when(request.body()).thenReturn("someinvalidcontend");
        when(request.contentType()).thenReturn("invalid content type");
        Response response = mock(Response.class);

        testObject.handle(request, response);

        verify(response).status(HttpStatus.BAD_REQUEST);
        verify(response).body(any());
    }

    //Test token handling
    @Test
    public void unknownGrantType_internalError() {

        Map<String, String> parameters = new HashMap<>();
        parameters.put("grant_type", "invalidGrandType");
        TokenRequest request = new TokenRequest(parameters);

        Answer answer = testObject.process(request);

        assertEquals(HttpStatus.BAD_REQUEST, answer.getStatusCode());
        ErrorResponse response = (ErrorResponse) answer.getResponse();
        assertEquals(ErrorCode.UNSUPPORTED_GRANT_TYPE, response.getError());
    }

    @Test
    public void noUsernameSupplied_badRequest() {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("grant_type", "password");
        parameters.put("password", "somePassword");
        TokenRequest request = new TokenRequest(parameters);

        Answer answer = testObject.process(request);

        assertEquals(HttpStatus.BAD_REQUEST, answer.getStatusCode());
        ErrorResponse response = (ErrorResponse) answer.getResponse();
        assertEquals(ErrorCode.INVALID_REQUEST, response.getError());
    }

    @Test
    public void noPasswordSupplied_badRequest() {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("grant_type", "password");
        parameters.put("username", "someusername");
        TokenRequest request = new TokenRequest(parameters);

        Answer answer = testObject.process(request);

        assertEquals(HttpStatus.BAD_REQUEST, answer.getStatusCode());
        ErrorResponse response = (ErrorResponse) answer.getResponse();
        assertEquals(ErrorCode.INVALID_REQUEST, response.getError());
    }

    @Test
    public void tokenServiceThrowException_forbidden() throws Exception {

        TokenService mockService = mock(TokenService.class);
        testObject.tokenService = mockService;
        when(mockService.grantWithPassword(any(), any())).thenThrow(new Exception("message"));

        Map<String, String> parameters = new HashMap<>();
        parameters.put("grant_type", "password");
        parameters.put("username", "someusername");
        parameters.put("password", "somePassword");
        TokenRequest request = new TokenRequest(parameters);

        Answer answer = testObject.process(request);

        assertEquals(HttpStatus.FORBIDDEN, answer.getStatusCode());
        ErrorResponse response = (ErrorResponse) answer.getResponse();
        assertEquals(ErrorCode.FORBIDDEN, response.getError());
    }

    @Test
    public void allParametersSupplied_ok() throws Exception {
        AccessToken testToken = new AccessToken();
        testToken.setRefreshToken("refreshToken");
        testToken.setUser(new User());
        testToken.setToken("token");
        testToken.setId("id");
        testToken.setExpiresAt(Instant.now().plus(Duration.ofHours(1)));

        TokenService mockService = mock(TokenService.class);
        testObject.tokenService = mockService;
        when(mockService.grantWithPassword(any(), any())).thenReturn(testToken);

        Map<String, String> parameters = new HashMap<>();
        parameters.put("grant_type", "password");
        parameters.put("username", "someusername");
        parameters.put("password", "somePassword");
        TokenRequest request = new TokenRequest(parameters);

        Answer answer = testObject.process(request);

        assertEquals(HttpStatus.OK, answer.getStatusCode());
        assertTrue(answer.getResponse() instanceof AccessTokenResponse);
    }
}
