package user;

import communication.AbstractRequestHandler;
import communication.Answer;
import communication.ErrorCode;
import entities.User;

public class CreateUserRequestHandler extends AbstractRequestHandler<CreateUserRequest> {

    UserService userService = new UserService();

    public CreateUserRequestHandler() {
        super(CreateUserRequest.class);
    }

    @Override
    protected Answer process(CreateUserRequest body) {
        Answer answer;

        try {
            User user = userService.createUser(body.eMail, body.password, body.passwordRepeated);
            answer = Answer.ok(user);
        } catch (Exception e) {
            answer = Answer.internalError(ErrorCode.RESOURCE_EXISTS, e.getMessage());
        }
        return answer;
    }
}
