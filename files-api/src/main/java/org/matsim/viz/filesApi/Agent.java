package org.matsim.viz.filesApi;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.matsim.viz.database.AbstractEntity;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Agent extends AbstractEntity {

    public static final String publicPermissionId = "allUsers";
    private static final Agent publicAgent = new Agent(publicPermissionId);

    private String authId;

    public static Agent getPublicAgent() {
        return publicAgent;
    }
}
