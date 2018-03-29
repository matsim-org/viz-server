package org.matsim.webvis.files.communication;

import lombok.Getter;
import org.matsim.webvis.files.entities.User;
import org.matsim.webvis.files.user.UserService;
import spark.Request;

@Getter
public class Subject {

    private static final String SUBJECT_ATTRIBUTE = "subject";
    static UserService userService = new UserService();

    private User user;
    private AuthenticationResult authentication;

    private Subject(AuthenticationResult authentication, User user) {
        this.user = user;
        this.authentication = authentication;
    }

    public static Subject getSubject(Request request) {

        AuthenticationResult authentication = request.attribute(SUBJECT_ATTRIBUTE);

        if (authentication == null) {
            throw new RuntimeException("Attribute 'subject' was not set. 'setAuthenticationAsAttribute' must be called first");
        }

        User user = userService.findByIdentityProviderId(authentication.getSub());
        if (user == null)
            user = userService.createUser(authentication.getSub());

        return new Subject(authentication, user);
    }

    public static void setAuthenticationAsAttribute(Request request, AuthenticationResult authentication) {
        request.attribute(SUBJECT_ATTRIBUTE, authentication);
    }
}
