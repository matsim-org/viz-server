package org.matsim.viz.frameAnimation.persistenceModel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.matsim.viz.database.AbstractEntity;

import javax.persistence.Entity;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Agent extends AbstractEntity {

    public static final String publicPermissionId = "allUsers";

    private String authId;
}
