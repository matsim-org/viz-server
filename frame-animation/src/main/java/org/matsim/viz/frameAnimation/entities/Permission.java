package org.matsim.viz.frameAnimation.entities;

import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.matsim.viz.error.InternalException;

import java.security.Principal;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class Permission implements Principal {

    private static final String publicPermissionId = "allUsers";
    private static final String authIdPropertyKey = "authId";
    private static final Permission publicPermission = new Permission(publicPermissionId);
    private String agentId;

    public static Permission getPublicPermission() {
        return publicPermission;
    }

    public static Permission createFromAuthId(String authId) {
        return new Permission(authId);
    }

    @Override
    public String getName() {
        return agentId;
    }

    @JsonSetter("agent")
    public void setAgent(Map<String, String> agentProperties) {
        if (agentProperties.containsKey(authIdPropertyKey))
            this.agentId = agentProperties.get(authIdPropertyKey);
        else
            throw new InternalException("Could not parse " + authIdPropertyKey + " of permission.agent");
    }

    boolean isPublicPermission() {
        return agentId.equals(publicPermissionId);
    }
}
