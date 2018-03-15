package user;

import communication.Answer;
import communication.ErrorResponse;
import communication.HttpStatus;
import data.entities.User;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CreateUserRequestHandlerTest {

    private CreateUserRequestHandler testObject;

    @Before
    public void setUp() {
        testObject = new CreateUserRequestHandler();
    }

    @Test
    public void errorInUserService_internalError() throws Exception {

        CreateUserRequest request = new CreateUserRequest();
        request.eMail = "mail";
        request.password = new char[]{'p', 'a', 's', 's', 'w', 'o', 'r', 'd'};
        request.passwordRepeated = request.password;

        final String errorMessage = "error message";
        UserService service = mock(UserService.class);
        testObject.userService = service;
        when(service.createUser(any(), any(), any())).thenThrow(new Exception(errorMessage));

        Answer answer = testObject.process(request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, answer.getStatusCode());
        assertTrue(answer.getResponse() instanceof ErrorResponse);

    }

    @Test
    public void works_ok() throws Exception {
        CreateUserRequest request = new CreateUserRequest();
        request.eMail = "mail";
        request.password = new char[]{'p', 'a', 's', 's', 'w', 'o', 'r', 'd'};
        request.passwordRepeated = request.password;

        final User user = new User();
        user.setEMail(request.eMail);
        UserService service = mock(UserService.class);
        testObject.userService = service;
        when(service.createUser(any(), any(), any())).thenReturn(user);

        Answer answer = testObject.process(request);

        assertEquals(HttpStatus.OK, answer.getStatusCode());
        assertEquals(user, answer.getResponse());
    }
}
