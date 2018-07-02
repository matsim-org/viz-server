package org.matsim.webvis.files.permission;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.matsim.webis.oauth.IntrospectionResult;
import org.matsim.webvis.files.agent.AgentService;
import org.matsim.webvis.files.entities.Agent;
import org.matsim.webvis.files.entities.User;

import java.util.Optional;

@Getter
@AllArgsConstructor
public class Subject {

    public static final String USER = "user-client";
    public static final String PUBLIC_USER = "public-client";
    public static final String SERVICE = "service-client";

    private Agent agent;
    //this is public for unit testing
    public static AgentService agentService = AgentService.Instance;
    private IntrospectionResult authenticationResult;

    public static Optional<Agent> createSubject(IntrospectionResult authResult) {

        switch(authResult.getScope()) {
            case USER:
                return Optional.of(findOrCreateUser(authResult.getSub()));
            case SERVICE:
                return Optional.of(agentService.getServiceAgent());
            default:
                return Optional.of(agentService.getPublicAgent());
        }
    }

    private static User findOrCreateUser(String authId) {

        User user = agentService.findByIdentityProviderId(authId);
        if (user == null)
            user = agentService.createUser(authId);
        return user;
    }
}
