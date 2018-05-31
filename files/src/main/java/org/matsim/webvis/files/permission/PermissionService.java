package org.matsim.webvis.files.permission;

import org.matsim.webvis.common.service.ForbiddenException;
import org.matsim.webvis.files.entities.Agent;
import org.matsim.webvis.files.entities.Permission;
import org.matsim.webvis.files.entities.Resource;

import java.util.List;

public class PermissionService {

    private PermissionDAO permissionDAO = new PermissionDAO();

    public static Permission createUserPermission(Resource resource, Agent user, Permission.Type type) {

        //TODO: this should get in here otherwise but is good for now
        String scope = "user";
        return new Permission(resource, user, type);
    }

    public static Permission createServicePermission(Resource resource) {
        return new Permission(resource, SpecialAgents.Instance.getServiceAgent(), Permission.Type.Read);
    }

    public static Permission createPublicPermission(Resource resource) {
        return new Permission(resource, SpecialAgents.Instance.getPublicUser(), Permission.Type.Read);
    }

    public Permission find(Agent agent, String resourceId) {
        return permissionDAO.find(agent, resourceId);
    }

    public List<Permission> find(Agent agent, List<String> resourceIds) {
        return permissionDAO.find(agent, resourceIds);
    }

    public Permission findReadPermission(Agent agent, String resourceId) throws ForbiddenException {

        Permission permission = find(agent, resourceId);
        if (permission == null || !permission.canRead())
            throw new ForbiddenException("agent does not have permission");
        return permission;
    }

    public Permission findWritePermission(Agent agent, String resourceId) {

        Permission permission = find(agent, resourceId);
        if (permission == null || !permission.canWrite())
            throw new ForbiddenException("agent does not have write permission");
        return permission;
    }

    public Permission findDeletePermission(Agent agent, String resourceId) {

        Permission permission = find(agent, resourceId);
        if (permission == null || !permission.canDelete())
            throw new ForbiddenException("agent does not have delete permission");
        return permission;
    }
}
