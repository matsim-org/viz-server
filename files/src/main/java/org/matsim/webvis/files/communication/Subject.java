package org.matsim.webvis.files.communication;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.matsim.webvis.common.auth.AuthenticationResult;
import org.matsim.webvis.files.entities.Agent;
import org.matsim.webvis.files.entities.User;
import org.matsim.webvis.files.agent.AgentService;

@Getter
@AllArgsConstructor
public class Subject {

    private static final String USER = "user";
    private static final String PUBLIC_USER = "public-user";
    private static final String SERVICE = "service";

    private Agent user;
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
                return new Subject(agentService.getPublicUser(), authResult);
        }
    }

    private static User findOrCreateUser(String authId) {

        User user = agentService.findByIdentityProviderId(authId);
        if (user == null)
            user = agentService.createUser(authId);
        return user;
    }
}
