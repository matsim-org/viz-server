package org.matsim.webvis.common.auth;

import org.junit.Test;
import org.matsim.webvis.common.service.InternalException;
import spark.Request;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class AuthenticationResultTest {

    @Test
    public void intoAttribute() {

        Request request = mock(Request.class);
        AuthenticationResult authentication = new AuthenticationResult();

        AuthenticationResult.intoRequestAttribute(request, authentication);

        verify(request).attribute(anyString(), eq(authentication));
    }

    @Test(expected = InternalException.class)
    public void fromAttribute_noAttributeSet_internalException() {

        Request request = mock(Request.class);
        when(request.attribute(anyString())).thenReturn(null);

        AuthenticationResult.fromRequestAttribute(request);

        fail("Exception should be thrown if no attribute with key 'subject' was set");
    }

    @Test
    public void fromAttribute_resultPresent_authResult() {

        AuthenticationResult authentication = new AuthenticationResult();
        Request request = mock(Request.class);
        when(request.attribute(anyString())).thenReturn(authentication);

        AuthenticationResult result = AuthenticationResult.fromRequestAttribute(request);

        assertEquals(authentication, result);
    }
}
