package org.matsim.viz.postprocessing.emissions.persistenceModel;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.matsim.viz.database.AbstractEntity;

import javax.persistence.Entity;
import java.security.Principal;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Agent extends AbstractEntity implements Principal {

    public static final String publicPermissionId = "allUsers";

    public Agent(String authId) {
        this.setId(authId);
    }

    @Override
    public String getName() {
        return getId();
    }
}
