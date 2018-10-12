package org.matsim.webvis.files.permission;

import org.matsim.webvis.error.ForbiddenException;
import org.matsim.webvis.files.agent.AgentService;
import org.matsim.webvis.files.entities.Agent;
import org.matsim.webvis.files.entities.Permission;
import org.matsim.webvis.files.entities.Resource;

import java.util.List;

public class PermissionService {

    private final PermissionDAO permissionDAO;
    private final AgentService agentService;

    public PermissionService(AgentService agentService, PermissionDAO permissionDAO) {
        this.permissionDAO = permissionDAO;
        this.agentService = agentService;
    }

    public Permission createUserPermission(Resource resource, Agent user, Permission.Type type) {

        if (agentService.getPublicAgent().equals(user))
            return createPublicPermission(resource);
        return new Permission(resource, user, type);
    }

    public Permission createServicePermission(Resource resource) {
        return new Permission(resource, agentService.getServiceAgent(), Permission.Type.Read);
    }

    Permission createPublicPermission(Resource resource) {
        return new Permission(resource, agentService.getPublicAgent(), Permission.Type.Read);
    }

    public List<Permission> persist(List<Permission> permissions) {
        return permissionDAO.persist(permissions);
    }

    public Permission find(String resourceId, Agent... agent) {
        return permissionDAO.find(resourceId, agent);
    }

    public Permission findReadPermission(Agent agent, String resourceId) throws ForbiddenException {

        Permission permission = find(resourceId, agent, agentService.getPublicAgent());
        if (permission == null || !permission.canRead())
            throw new ForbiddenException("agent does not have permission");
        return permission;
    }

    public Permission findWritePermission(Agent agent, String resourceId) {

        Permission permission = find(resourceId, agent);
        if (permission == null || !permission.canWrite())
            throw new ForbiddenException("agent does not have write permission");
        return permission;
    }

    public Permission findDeletePermission(Agent agent, String resourceId) {

        Permission permission = find(resourceId, agent);
        if (permission == null || !permission.canDelete())
            throw new ForbiddenException("agent does not have delete permission");
        return permission;
    }

    public Permission findOwnerPermission(Agent agent, String resourceId) {

        Permission permission = find(resourceId, agent);
        if (permission == null || !permission.isOwner())
            throw new ForbiddenException("agent is not owner of the resource");
        return permission;
    }
}
