package org.matsim.webvis.files.communication;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.matsim.webvis.common.auth.AuthenticationResult;
import org.matsim.webvis.files.entities.User;
import org.matsim.webvis.files.user.UserService;

@Getter
@AllArgsConstructor
public class Subject {

    private User user;
    //this is public for unit testing
    public static UserService userService = new UserService();
    private AuthenticationResult authenticationResult;

    public static Subject createSubject(AuthenticationResult authResult) {
        User user = findOrCreateUser(authResult.getSub());
        return new Subject(user, authResult);
    }

    private static User findOrCreateUser(String authId) {

        User user = userService.findByIdentityProviderId(authId);
        if (user == null)
            user = userService.createUser(authId);
        return user;
    }
}
