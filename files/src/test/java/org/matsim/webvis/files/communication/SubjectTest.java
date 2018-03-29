package org.matsim.webvis.files.communication;

import org.junit.Test;
import org.matsim.webvis.files.entities.User;
import org.matsim.webvis.files.user.UserService;
import spark.Request;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class SubjectTest {

    @Test
    public void setAttribute() {

        Request request = mock(Request.class);
        AuthenticationResult authentication = new AuthenticationResult();

        Subject.setAuthenticationAsAttribute(request, authentication);

        verify(request).attribute(anyString(), eq(authentication));
    }

    @Test(expected = RuntimeException.class)
    public void getSubject_noAttributeSet_runtimeException() {

        Request request = mock(Request.class);
        when(request.attribute(anyString())).thenReturn(null);

        Subject.getSubject(request);

        fail("Exception should be thrown if no attribute with key 'subject' was set");
    }

    @Test
    public void getSubject_noUserFound_createNewUser() {

        Request request = mock(Request.class);
        AuthenticationResult authenticationResult = mock(AuthenticationResult.class);
        when(authenticationResult.getSub()).thenReturn("some-id");
        when(request.attribute(anyString())).thenReturn(authenticationResult);

        User created = new User();
        Subject.userService = mock(UserService.class);
        when(Subject.userService.findByIdentityProviderId(anyString())).thenReturn(null);
        when(Subject.userService.createUser(anyString())).thenReturn(created);

        Subject subject = Subject.getSubject(request);

        assertEquals(created, subject.getUser());
        assertEquals(authenticationResult, subject.getAuthentication());
    }

    @Test
    public void getSubject_attributeSetAndUserPresent_subject() {

        Request request = mock(Request.class);
        AuthenticationResult authenticationResult = mock(AuthenticationResult.class);
        when(authenticationResult.getSub()).thenReturn("some-id");
        when(request.attribute(anyString())).thenReturn(authenticationResult);

        User persistent = new User();
        Subject.userService = mock(UserService.class);
        when(Subject.userService.findByIdentityProviderId(anyString())).thenReturn(persistent);

        Subject subject = Subject.getSubject(request);

        assertEquals(persistent, subject.getUser());
        assertEquals(authenticationResult, subject.getAuthentication());
        verify(Subject.userService, never()).createUser(anyString());
    }
}
