package org.matsim.viz.files.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;

@Getter
@Setter
@Entity
public class PublicAgent extends Agent {

    private static final String PublicAgentId = "allUsers";

    /**
     * Use static Method 'create' for a new instance of PublicAgent. This constructor is used by Hibernate when
     * a public agent is created from the database.
     */
    public PublicAgent() {
        super();
    }

    public static PublicAgent create() {

        PublicAgent result = new PublicAgent();
        result.setId(PublicAgentId);
        result.setAuthId(PublicAgentId);
        return result;
    }
}
