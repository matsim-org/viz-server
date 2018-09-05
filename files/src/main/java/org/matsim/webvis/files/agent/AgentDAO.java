package org.matsim.webvis.files.agent;

import com.querydsl.core.types.dsl.EntityPathBase;
import org.matsim.webvis.database.PersistenceUnit;
import org.matsim.webvis.files.entities.*;

public class AgentDAO extends DAO {

    AgentDAO(PersistenceUnit persistenceUnit) {
        super(persistenceUnit);
    }

    public <T extends Agent> T persist(T agent) {

        return database.persist(agent);
    }

    ServiceAgent findOrCreateServiceAgent() {
        return findOrCreateAgent(new ServiceAgent(), QServiceAgent.serviceAgent);
    }

    PublicAgent findOrCreatePublicAgent() {
        return findOrCreateAgent(new PublicAgent(), QPublicAgent.publicAgent);
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
