package org.matsim.viz.files.permission;

import org.matsim.viz.database.PersistenceUnit;
import org.matsim.viz.files.entities.Agent;
import org.matsim.viz.files.entities.DAO;
import org.matsim.viz.files.entities.Permission;
import org.matsim.viz.files.entities.QPermission;

import java.util.List;

public class PermissionDAO extends DAO {

    public PermissionDAO(PersistenceUnit persistenceUnit) {
        super(persistenceUnit);
    }

    List<Permission> persist(List<Permission> permissions) {
        return database.persistMany(permissions);
    }

    Permission find(String resourceId, Agent... agent) {

        QPermission permission = QPermission.permission;
        return database.executeQuery(query -> query.selectFrom(permission)
                .where(permission.agent.in(agent)
                        .and(permission.resource.id.eq(resourceId)))
                .fetchOne()
        );
    }
}
