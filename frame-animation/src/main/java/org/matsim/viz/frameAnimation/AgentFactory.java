package org.matsim.viz.frameAnimation;

import org.matsim.viz.clientAuth.AuthenticationResult;
import org.matsim.viz.frameAnimation.persistenceModel.Agent;

public class AgentFactory {

    public static Agent createAgent(AuthenticationResult authenticationResult) {
        return new Agent(authenticationResult.getSubjectId());
    }
}
