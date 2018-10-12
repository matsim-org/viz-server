package org.matsim.webvis.files.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;

@Getter
@Setter
@Entity
public class PublicAgent extends Agent {

    private static final String PublicAgentId = "org.matsim.viz.PublicAgent";

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
