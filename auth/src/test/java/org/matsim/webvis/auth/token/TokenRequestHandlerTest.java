package org.matsim.webvis.auth.token;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.matsim.webvis.auth.util.TestUtils;
import org.matsim.webvis.common.communication.*;
import spark.Request;
import spark.Response;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class TokenRequestHandlerTest {

    private TokenRequestHandler testObject;

    @BeforeClass
    public static void setUpFixture() throws UnsupportedEncodingException, FileNotFoundException {
        TestUtils.loadTestConfig();
    }

    @Before
    public void setUp() {
        testObject = new TokenRequestHandler();
    }

    //Test handling of url-parameter

    @Test
    public void wrongQueryParams_serverError() {

        Request request = TestUtils.mockRequestWithQueryParamsMap(new HashMap<>(), ContentType.FORM_URL_ENCODED);
        Response response = mock(Response.class);

        testObject.handle(request, response);

        verify(response).status(HttpStatus.BAD_REQUEST);
        verify(response).body(any());
    }

    @Test
    public void wrongContentType_badRequest() {
        Request request = TestUtils.mockRequestWithQueryParamsMap(new HashMap<>(), "invalid content type");
        Response response = mock(Response.class);

        testObject.handle(request, response);

        verify(response).status(HttpStatus.BAD_REQUEST);
        verify(response).body(any());
    }

    //Test token handling
    @Test
    public void unknownGrantType_internalError() {

        Map<String, String[]> parameters = new HashMap<>();
        parameters.put("grant_type", new String[]{"invalid-grant type"});
        parameters.put("username", new String[]{"someusername"});
        parameters.put("password", new String[]{"password"});
        Request request = TestUtils.mockRequestWithQueryParamsMap(parameters, ContentType.FORM_URL_ENCODED);

        Answer answer = testObject.process(request, null);

        assertEquals(HttpStatus.BAD_REQUEST, answer.getStatusCode());
        ErrorResponse response = (ErrorResponse) answer.getResponse();
        assertEquals(RequestError.UNSUPPORTED_GRANT_TYPE, response.getError());
    }

    @Test
    public void noUsernameSupplied_badRequest() {

        Map<String, String[]> parameters = new HashMap<>();
        parameters.put("grant_type", new String[]{"password"});
        parameters.put("password", new String[]{"password"});
        Request request = TestUtils.mockRequestWithQueryParamsMap(parameters, ContentType.FORM_URL_ENCODED);

        Answer answer = testObject.process(request, null);

        assertEquals(HttpStatus.BAD_REQUEST, answer.getStatusCode());
        ErrorResponse response = (ErrorResponse) answer.getResponse();
        assertEquals(RequestError.INVALID_REQUEST, response.getError());
    }

    @Test
    public void noPasswordSupplied_badRequest() {

        Map<String, String[]> parameters = new HashMap<>();
        parameters.put("grant_type", new String[]{"password"});
        parameters.put("username", new String[]{"someusername"});
        Request request = TestUtils.mockRequestWithQueryParamsMap(parameters, ContentType.FORM_URL_ENCODED);

        Answer answer = testObject.process(request, null);

        assertEquals(HttpStatus.BAD_REQUEST, answer.getStatusCode());
        ErrorResponse response = (ErrorResponse) answer.getResponse();
        assertEquals(RequestError.INVALID_REQUEST, response.getError());
    }

   /* @Test
    public void tokenServiceThrowException_forbidden() {

        TokenService mockService = mock(TokenService.class);
        testObject.tokenService = mockService;
        when(mockService.grantWithPassword(any(), any())).thenThrow(new UnauthorizedException("message"));

        Map<String, String[]> parameters = new HashMap<>();
        parameters.put("grant_type", new String[]{"password"});
        parameters.put("username", new String[]{"someusername"});
        parameters.put("password", new String[]{"somepassword"});
        Request request = TestUtils.mockRequestWithQueryParamsMap(parameters, ContentType.FORM_URL_ENCODED);

        Answer answer = testObject.process(request, null);

        assertEquals(HttpStatus.FORBIDDEN, answer.getStatusCode());
        ErrorResponse response = (ErrorResponse) answer.getResponse();
        assertEquals(Error.FORBIDDEN, response.getError());
    }

    @Test
    public void allParametersSupplied_ok() {
        AccessToken testToken = new AccessToken();
        testToken.setRefreshToken("refreshToken");
        testToken.setUser(new User());
        testToken.setToken("token");
        testToken.setId("id");
        testToken.setExpiresAt(Instant.now().plus(Duration.ofHours(1)));

        TokenService mockService = mock(TokenService.class);
        testObject.tokenService = mockService;
        when(mockService.grantWithPassword(any(), any())).thenReturn(testToken);

        Map<String, String[]> parameters = new HashMap<>();
        parameters.put("grant_type", new String[]{"password"});
        parameters.put("username", new String[]{"someusername"});
        parameters.put("password", new String[]{"somepassword"});
        Request request = TestUtils.mockRequestWithQueryParamsMap(parameters, ContentType.FORM_URL_ENCODED);

        Answer answer = testObject.process(request, null);

        assertEquals(HttpStatus.OK, answer.getStatusCode());
        assertTrue(answer.getResponse() instanceof AccessTokenResponse);
    }*/
}
