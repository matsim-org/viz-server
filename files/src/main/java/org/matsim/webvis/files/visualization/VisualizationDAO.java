package org.matsim.webvis.files.visualization;

import org.matsim.webvis.database.PersistenceUnit;
import org.matsim.webvis.files.entities.*;

import java.time.Instant;
import java.util.List;

public class VisualizationDAO extends DAO {

    public VisualizationDAO(PersistenceUnit persistenceUnit) {
        super(persistenceUnit);
    }

    Visualization persist(Visualization viz) {
        return database.persist(viz);
    }

    VisualizationType persistType(VisualizationType type) {
        return database.persist(type);
    }

    public Visualization findFlat(String vizId) {

        QVisualization visualization = QVisualization.visualization;
        return database.executeQuery(query -> query.selectFrom(visualization)
                .where(visualization.id.eq(vizId))
                .fetchOne());
    }

    public Visualization find(String vizId) {

        QVisualization visualization = QVisualization.visualization;
        return database.executeQuery(query -> query.selectFrom(visualization)
                .where(visualization.id.eq(vizId))
                .leftJoin(visualization.inputFiles).fetchJoin()
                .leftJoin(visualization.parameters).fetchJoin()
                .fetchOne());
    }

    List<Visualization> findAllByTypeIfHasPermission(String typeName, Instant after, Agent agent) {

        QVisualization visualization = QVisualization.visualization;
        QPermission permission = QPermission.permission;

        return database.executeQuery(query -> query.selectFrom(visualization)
                .where(visualization.type.typeName.eq(typeName).and(visualization.createdAt.after(after)))
                .innerJoin(visualization.permissions, permission).on(permission.agent.eq(agent))
                .leftJoin(visualization.inputFiles).fetchJoin()
                .leftJoin(visualization.parameters).fetchJoin()
                .distinct()
                .fetch()
        );
    }

    VisualizationType findType(String typeName) {
        QVisualizationType type = QVisualizationType.visualizationType;

        return database.executeQuery(query -> query.selectFrom(type)
                .where(type.typeName.eq(typeName))
                .fetchOne());
    }


    List<VisualizationType> findAllTypes() {
        QVisualizationType type = QVisualizationType.visualizationType;

        return database.executeQuery(query -> query.selectFrom(type)
                .leftJoin(type.requiredFileKeys).fetchJoin()
                .leftJoin(type.requiredParamKeys).fetchJoin()
                .distinct()
                .fetch()
        );
    }

    void removeType(String typeName) {

        QVisualizationType type = QVisualizationType.visualizationType;
        database.executeTransactionalQuery(query -> query.delete(type).where(type.typeName.eq(typeName)).execute());
    }

    void removeVisualization(Visualization viz) {

        database.remove(viz);
    }
}
