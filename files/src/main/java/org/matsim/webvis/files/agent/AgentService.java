package org.matsim.webvis.files.agent;

import lombok.Getter;
import org.matsim.webvis.common.errorHandling.CodedException;
import org.matsim.webvis.common.errorHandling.Error;
import org.matsim.webvis.files.entities.PublicAgent;
import org.matsim.webvis.files.entities.ServiceAgent;
import org.matsim.webvis.files.entities.User;

public class AgentService {

    public static final AgentService Instance = new AgentService();
    private UserDAO userDAO = new UserDAO();

    @Getter
    private ServiceAgent serviceAgent;
    @Getter
    private PublicAgent publicAgent;

    private AgentService() {
        initializeSpecialAgents();
    }

    void initializeSpecialAgents() {
        serviceAgent = userDAO.findOrCreateServiceAgent();
        publicAgent = userDAO.findOrCreatePublicAgent();
    }

    public User createUser(String authId) {

        User user = new User();
        user.setAuthId(authId);

        try {
            return userDAO.persist(user);
        } catch (Exception e) {
            throw new CodedException(Error.RESOURCE_EXISTS, "user already exists");
        }
    }

    public User findByIdentityProviderId(String id) {
        return userDAO.findByIdentityProviderId(id);
    }
}
