package org.matsim.webvis.files.agent;

import lombok.Getter;
import org.matsim.webvis.files.entities.PublicUser;
import org.matsim.webvis.files.entities.ServiceAgent;
import org.matsim.webvis.files.entities.User;

public class AgentService {

    public static final AgentService Instance = new AgentService();
    private UserDAO userDAO = new UserDAO();

    @Getter
    private ServiceAgent serviceAgent = loadOrCreateServiceAgent();
    @Getter
    private PublicUser publicUser = loadOrCreatePublicUser();

    private AgentService() {

    }

    private ServiceAgent loadOrCreateServiceAgent() {

        return userDAO.findOrCreateServiceAgent();
    }
    private PublicUser loadOrCreatePublicUser() { return userDAO.findOrCreatePublicUser(); }


    public User createUser(String authId) {

        User user = new User();
        user.setAuthId(authId);

        return userDAO.update(user);
    }

    public User findByIdentityProviderId(String id) {
        return userDAO.findByIdentityProviderId(id);
    }
}
