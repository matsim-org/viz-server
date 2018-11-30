package org.matsim.viz.frameAnimation.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.matsim.viz.database.AbstractEntity;

import java.security.Principal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Agent extends AbstractEntity implements Principal {

    private static final String publicPermissionId = "allUsers";
    private static final Agent publicAgent = new Agent(publicPermissionId);

    private String authId;

    public static Agent getPublicAgent() {
        return publicAgent;
    }

    @Override
    public String getName() {
        return authId;
    }
}
