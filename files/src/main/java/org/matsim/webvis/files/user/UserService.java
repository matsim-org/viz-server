package org.matsim.webvis.files.user;

import org.matsim.webvis.files.entities.User;

public class UserService {

    private UserDAO userDAO = new UserDAO();

    public User createUser(String authId) {

        User user = new User();
        user.setAuthId(authId);

        return userDAO.update(user);
    }

    public User findByIdentityProviderId(String id) {
        return userDAO.findByIdentityProviderId(id);
    }
}
