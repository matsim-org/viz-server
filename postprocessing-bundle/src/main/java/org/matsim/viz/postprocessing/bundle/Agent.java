package org.matsim.viz.postprocessing.bundle;

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

    public Agent(String id) {
        setId(id);
    }

    @Override
    public String getName() {
        return null;
    }
}
