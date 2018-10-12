package org.matsim.viz.files.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;

@Getter
@Setter
@Entity
public class ServiceAgent extends Agent {

    private static final String ServiceAgentId = "org.matsim.viz.ServiceAgent";

    /**
     * Use static Method 'create' for a new instance of PublicAgent. This constructor is used by Hibernate when
     * a public agent is created from the database.
     */
    public ServiceAgent() {
        super();
    }

    public static ServiceAgent create() {

        ServiceAgent result = new ServiceAgent();
        result.setId(ServiceAgentId);
        result.setAuthId(ServiceAgentId);
        return result;
    }
}
