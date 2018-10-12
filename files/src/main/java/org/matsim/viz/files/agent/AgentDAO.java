package org.matsim.viz.files.agent;

import com.querydsl.core.types.dsl.EntityPathBase;
import org.matsim.viz.database.PersistenceUnit;
import org.matsim.viz.files.entities.*;

public class AgentDAO extends DAO {

    AgentDAO(PersistenceUnit persistenceUnit) {
        super(persistenceUnit);
    }

    public <T extends Agent> T persist(T agent) {

        return database.persist(agent);
    }

    ServiceAgent findOrCreateServiceAgent() {
        return findOrCreateAgent(ServiceAgent.create(), QServiceAgent.serviceAgent);
    }

    PublicAgent findOrCreatePublicAgent() {
        return findOrCreateAgent(PublicAgent.create(), QPublicAgent.publicAgent);
    }

    private <T extends Agent> T findOrCreateAgent(T emptyInstance, EntityPathBase<T> entityPath) {
        T agent = database.executeQuery(query -> query.selectFrom(entityPath).fetchFirst());
        if (agent == null)
            agent = persist(emptyInstance);
        return agent;
    }

    void removeAllAgents() {
        database.executeTransactionalQuery(query ->
                query.delete(QAgent.agent).execute()
        );
    }
}
