package org.matsim.webvis.files.permission;

import org.matsim.webvis.database.PersistenceUnit;
import org.matsim.webvis.files.entities.Agent;
import org.matsim.webvis.files.entities.DAO;
import org.matsim.webvis.files.entities.Permission;
import org.matsim.webvis.files.entities.QPermission;

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
