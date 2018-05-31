package org.matsim.webvis.files.agent;

import com.querydsl.core.types.dsl.EntityPathBase;
import org.matsim.webvis.files.entities.*;

public class AgentDAO extends DAO {

    public <T extends Agent> T persist(T agent) {
        if (agent.getId() == null)
           return database.persistOne(agent);

        return database.updateOne(agent);
    }

    public ServiceAgent findOrCreateServiceAgent() {
        return findOrCreateAgent(new ServiceAgent(), QServiceAgent.serviceAgent);
    }

    public PublicUser findOrCreatePublicUser() {
        return findOrCreateAgent(new PublicUser(), QPublicUser.publicUser);
    }

    private <T extends Agent> T findOrCreateAgent(T emptyInstance, EntityPathBase<T> entityPath) {
        T agent = database.executeQuery(query -> query.selectFrom(entityPath).fetchFirst());
        if (agent == null)
            agent = persist(emptyInstance);
        return agent;
    }
}
