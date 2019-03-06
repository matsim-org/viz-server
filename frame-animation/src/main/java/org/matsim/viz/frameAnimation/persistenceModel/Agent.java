package org.matsim.viz.frameAnimation.persistenceModel;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.matsim.viz.database.AbstractEntity;

import javax.persistence.Entity;
import java.security.Principal;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Agent extends AbstractEntity implements Principal {

    public static final String publicPermissionId = "allUsers";

    public Agent(String id) {
        setId(id);
    }

    @Override
    public String getName() {
        return getId();
    }
}
