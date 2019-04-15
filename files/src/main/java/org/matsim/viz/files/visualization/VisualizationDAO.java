package org.matsim.viz.files.visualization;

import org.matsim.viz.database.PersistenceUnit;
import org.matsim.viz.files.entities.Agent;
import org.matsim.viz.files.entities.DAO;
import org.matsim.viz.files.entities.QVisualization;
import org.matsim.viz.files.entities.Visualization;

import java.time.Instant;
import java.util.List;

public class VisualizationDAO extends DAO {

    public VisualizationDAO(PersistenceUnit persistenceUnit) {
        super(persistenceUnit);
    }

    Visualization persist(Visualization viz) {
        return database.persist(viz);
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

        return database.executeQuery(query -> query.selectFrom(visualization)
                .where(visualization.type.eq(typeName)
                        .and(visualization.createdAt.after(after))
                        .and(visualization.permissions.any().agent.eq(agent)
                        ))
                .leftJoin(visualization.inputFiles).fetchJoin()
                .leftJoin(visualization.parameters).fetchJoin()
                .leftJoin(visualization.permissions).fetchJoin()
				.leftJoin(visualization.tags).fetchJoin()
                .distinct()
                .fetch()
        );
    }

    void removeVisualization(Visualization viz) {
        database.remove(viz);
    }

	List<Visualization> findAllForProject(String projectId, Agent agent) {

        QVisualization visualization = QVisualization.visualization;

		// this query fetches a possibly large dataset and it relations if we should ever encounter performance issues
		// the result set could be limited.
        return database.executeQuery(query -> query.selectFrom(visualization)
                .where(visualization.project.id.eq(projectId)
                        .and(visualization.permissions.any().agent.eq(agent)))
				.leftJoin(visualization.inputFiles).fetchJoin()
				.leftJoin(visualization.parameters).fetchJoin()
				.leftJoin(visualization.permissions).fetchJoin()
				.leftJoin(visualization.tags).fetchJoin()
                .leftJoin(visualization.properties).fetchJoin()
                .distinct()
                .fetch()
        );
    }
}
