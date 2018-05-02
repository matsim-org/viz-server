package org.matsim.webvis.files.visualization;

import org.matsim.webvis.files.entities.*;

public class VisualizationDAO extends DAO {

    Visualization persist(Visualization viz) {
        if (viz.getId() == null)
            return database.persistOne(viz);

        return database.updateOne(viz);
    }

    public VisualizationType persistType(VisualizationType type) {
        if (type.getId() == null)
            return database.persistOne(type);
        return database.updateOne(type);
    }

    public Visualization find(String vizId) {

        QVisualization visualization = QVisualization.visualization;
        return database.executeQuery(query -> query.selectFrom(visualization)
                .where(visualization.id.eq(vizId))
                .fetchOne());
    }

    VisualizationType findType(String key) {
        QVisualizationType type = QVisualizationType.visualizationType;

        return database.executeQuery(query -> query.selectFrom(type)
                .where(type.key.eq(key))
                .fetchOne());
    }

    void removeType(String key) {

        QVisualizationType type = QVisualizationType.visualizationType;
        database.executeTransactionalQuery(query -> query.delete(type).where(type.key.eq(key)).execute());
    }
}
