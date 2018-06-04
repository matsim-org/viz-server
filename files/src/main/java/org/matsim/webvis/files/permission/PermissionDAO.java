package org.matsim.webvis.files.permission;

import org.matsim.webvis.files.entities.Agent;
import org.matsim.webvis.files.entities.DAO;
import org.matsim.webvis.files.entities.Permission;
import org.matsim.webvis.files.entities.QPermission;

import java.util.List;

class PermissionDAO extends DAO {

    List<Permission> persist(List<Permission> permissions) {
        return database.persistMany(permissions);
    }

    Permission find(Agent agent, String resourceId) {

        QPermission permission = QPermission.permission;
        return database.executeQuery(query -> query.selectFrom(permission)
                .where(permission.agent.eq(agent).and(permission.resource.id.eq(resourceId)))
                .fetchOne()
        );
    }

    List<Permission> find(Agent agent, List<String> resourceIds) {

        QPermission permission = QPermission.permission;
        return database.executeQuery(query -> query.selectFrom(permission)
                .where(permission.agent.eq(agent).and(permission.resource.id.in(resourceIds)))
                .fetch()
        );
    }
}
