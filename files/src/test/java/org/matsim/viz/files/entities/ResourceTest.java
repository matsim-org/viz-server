package org.matsim.viz.files.entities;

import org.junit.Before;
import org.junit.Test;
import org.matsim.viz.files.permission.PermissionService;
import org.matsim.viz.files.util.TestUtils;

import static org.junit.Assert.assertEquals;

public class ResourceTest {

    private PermissionService permissionService;

    @Before
    public void setUp() {
        permissionService = TestUtils.getPermissionService();
    }

    @Test
    public void addPermission_permissionWithEqualAgentIsPresent_ignored() {

        Resource resource = new FileEntry();
        User agent = new User();

        Permission permission1 = permissionService.createUserPermission(resource, agent, Permission.Type.Read);
        Permission permission2 = permissionService.createUserPermission(resource, agent, Permission.Type.Read);

        resource.addPermission(permission1);
        assertEquals(1, resource.getPermissions().size());

        resource.addPermission(permission2);
        assertEquals(1, resource.getPermissions().size());
    }

    @Test
    public void addPermission_permissionWithDifferentAgent_added() {

        Resource resource = new FileEntry();
        User agent1 = new User();
        agent1.setId("some-id");
        User agent2 = new User();
        agent2.setId("other-id");

        Permission permission1 = permissionService.createUserPermission(resource, agent1, Permission.Type.Read);
        Permission permission2 = permissionService.createUserPermission(resource, agent2, Permission.Type.Read);

        resource.addPermission(permission1);
        assertEquals(1, resource.getPermissions().size());

        resource.addPermission(permission2);
        assertEquals(2, resource.getPermissions().size());
    }
}
