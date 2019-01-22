package org.matsim.viz.frameAnimation.persistenceModel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.matsim.viz.database.AbstractEntity;

import javax.persistence.Entity;
import java.security.Principal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Agent extends AbstractEntity implements Principal {

    public static final String publicPermissionId = "allUsers";

    private String authId;

    @Override
    public String getName() {
        return authId;
    }
}
