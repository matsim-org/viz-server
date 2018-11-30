package org.matsim.viz.frameAnimation.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.matsim.viz.database.AbstractEntity;

import java.security.Principal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Permission extends AbstractEntity implements Principal {

    private static Permission publicPermission = new Permission(Agent.getPublicAgent());
    private Agent agent;

    public static Permission getPublicPermission() {
        return publicPermission;
    }

    public static Permission createFromAuthId(String authId) {
        return new Permission(new Agent(authId));
    }

    @Override
    public String getName() {
        return agent.getName();
    }


}
