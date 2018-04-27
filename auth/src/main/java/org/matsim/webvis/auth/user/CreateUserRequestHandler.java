package org.matsim.webvis.auth.user;

import org.matsim.webvis.auth.entities.User;
import org.matsim.webvis.common.communication.Answer;
import org.matsim.webvis.common.communication.JsonRequestHandler;
import org.matsim.webvis.common.service.Error;
import spark.Request;

public class CreateUserRequestHandler extends JsonRequestHandler<CreateUserRequest> {

    UserService userService = new UserService();

    public CreateUserRequestHandler() {
        super(CreateUserRequest.class);
    }

    @Override
    protected Answer process(CreateUserRequest body, Request request) {
        Answer answer;

        try {
            User user = userService.createUser(body.eMail, body.password, body.passwordRepeated);
            answer = Answer.ok(user);
        } catch (Exception e) {
            answer = Answer.internalError(Error.RESOURCE_EXISTS, e.getMessage());
        }
        return answer;
    }
}
