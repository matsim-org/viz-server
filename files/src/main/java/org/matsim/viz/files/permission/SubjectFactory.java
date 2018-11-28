package org.matsim.viz.files.permission;

import lombok.AllArgsConstructor;
import org.matsim.viz.clientAuth.AuthenticationResult;
import org.matsim.viz.files.agent.AgentService;
import org.matsim.viz.files.entities.Agent;
import org.matsim.viz.files.entities.User;

import java.util.Optional;

@AllArgsConstructor
public class SubjectFactory {

    static final String USER = "user-client";
    static final String PUBLIC_USER = "public-client";
    static final String SERVICE = "service-client";

    private final AgentService agentService;

    public Optional<Agent> createSubject(AuthenticationResult authResult) {

        switch (authResult.getScope()) {
            case USER:
                return Optional.of(findOrCreateUser(authResult.getSubjectId()));
            case SERVICE:
                return Optional.of(agentService.getServiceAgent());
            default:
                return Optional.of(agentService.getPublicAgent());
        }
    }

    public Optional<Agent> createPublicAgent() {
        return Optional.of(agentService.getPublicAgent());
    }

    private User findOrCreateUser(String authId) {

        User user = agentService.findUserByIdentityProviderId(authId);
        if (user == null)
            user = agentService.createUser(authId);
        return user;
    }
}
