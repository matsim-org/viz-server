package token;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.fail;

public class TokenRequestHandlerTest {

    private TokenRequestHandler testObject;

    @Before
    public void setUp() {
        testObject = new TokenRequestHandler();
    }

    //Test handling of url-parameter

    @Test
    public void wrongQueryParams_exception() {
        fail("not implemented");
    }

    @Test
    public void passwordGrantParams_answerOk() {
        fail("not implemented");
    }

    //Test token handling
    @Test
    public void unknownGrantType_internalError() {
        fail("not implemented");
    }

    @Test
    public void noUsernameSupplied_badRequest() {
        fail("not implemented");
    }

    @Test
    public void noPasswordSupplied_badRequest() {
        fail("not implemented");
    }

    @Test
    public void allParametersSupplied_ok() {
        fail("not implemented");
    }
}
