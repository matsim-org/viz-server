package org.matsim.webvis.files.permission;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.matsim.webvis.common.auth.AuthenticationResult;
import org.matsim.webvis.files.agent.AgentService;
import org.matsim.webvis.files.entities.Agent;
import org.matsim.webvis.files.entities.User;

@Getter
@AllArgsConstructor
public class Subject {

    public static final String USER = "user";
    public static final String PUBLIC_USER = "public-agent";
    public static final String SERVICE = "service-agent";

    private Agent agent;
    //this is public for unit testing
    public static AgentService agentService = AgentService.Instance;
    private AuthenticationResult authenticationResult;

    public static Subject createSubject(AuthenticationResult authResult) {

        switch(authResult.getScope()) {
            case USER:
                return new Subject(findOrCreateUser(authResult.getSub()), authResult);
            case SERVICE:
                return new Subject(agentService.getServiceAgent(), authResult);
            default:
                return new Subject(agentService.getPublicAgent(), authResult);
        }
    }

    private static User findOrCreateUser(String authId) {

        User user = agentService.findByIdentityProviderId(authId);
        if (user == null)
            user = agentService.createUser(authId);
        return user;
    }
}
