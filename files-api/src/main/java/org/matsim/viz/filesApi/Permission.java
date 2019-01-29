package org.matsim.viz.filesApi;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.matsim.viz.database.AbstractEntity;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Permission extends AbstractEntity {

    private static Permission publicPermission = new Permission(Agent.getPublicAgent());
    private Agent agent;

    public static Permission getPublicPermission() {
        return publicPermission;
    }

    public static Permission createFromAuthId(String authId) {
        return new Permission(new Agent(authId));
    }
}
