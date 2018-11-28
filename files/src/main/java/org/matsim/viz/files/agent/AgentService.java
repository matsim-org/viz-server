package org.matsim.viz.files.agent;

import lombok.Getter;
import org.matsim.viz.error.CodedException;
import org.matsim.viz.error.Error;
import org.matsim.viz.files.entities.Agent;
import org.matsim.viz.files.entities.PublicAgent;
import org.matsim.viz.files.entities.ServiceAgent;
import org.matsim.viz.files.entities.User;

public class AgentService {

    private UserDAO userDAO;

    @Getter
    private ServiceAgent serviceAgent;
    @Getter
    private PublicAgent publicAgent;

    public AgentService(UserDAO dao) {
        this.userDAO = dao;
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
            throw new CodedException(409, Error.RESOURCE_EXISTS, "user already exists");
        }
    }

    public User findUserByIdentityProviderId(String id) {
        return userDAO.findUserByIdentityProviderId(id);
    }

    public Agent findAgentByIdentityProviderId(String id) {
        return userDAO.findAgentByIdentityProviderId(id);
    }
}
