package user;

import data.entities.User;
import requests.AbstractRequestHandler;
import requests.Answer;

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
            answer = Answer.ok(getGson().toJson(user));
        } catch (Exception e) {
            answer = Answer.internalError(e.getMessage());
        }
        return answer;
    }
}
