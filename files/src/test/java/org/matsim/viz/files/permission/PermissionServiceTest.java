package org.matsim.viz.files.permission;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.matsim.viz.error.ForbiddenException;
import org.matsim.viz.files.entities.*;
import org.matsim.viz.files.util.TestUtils;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@SuppressWarnings("ConstantConditions")
public class PermissionServiceTest {

    private PermissionService testObject;

    @Before
    public void setUp() {
        testObject = new PermissionService(TestUtils.getAgentService(), new PermissionDAO(TestUtils.getPersistenceUnit()));
    }

    @After
    public void tearDown() {
        TestUtils.removeAllEntities();
    }

    @Test
    public void createUserPermission() {

        FileEntry entry = new FileEntry();
        User user = new User();

        Permission permission = testObject.createUserPermission(entry, user, Permission.Type.Delete);

        assertEquals(entry, permission.getResource());
        assertEquals(user, permission.getAgent());
        assertEquals(Permission.Type.Delete, permission.getType());
    }

    @Test
    public void createUserPermission_agentIsPublicAgent_permissionIsRead() {

        FileEntry fileEntry = new FileEntry();
        PublicAgent publicAgent = PublicAgent.create();

        Permission permission = testObject.createUserPermission(fileEntry, publicAgent, Permission.Type.Delete);

        assertEquals(publicAgent, permission.getAgent());
        assertEquals(Permission.Type.Read, permission.getType());
        assertEquals(fileEntry, permission.getResource());
    }

    @Test
    public void createServicePermission() {

        FileEntry entry = new FileEntry();

        Permission permission = testObject.createServicePermission(entry);

        assertEquals(entry, permission.getResource());
        assertEquals(TestUtils.getAgentService().getServiceAgent(), permission.getAgent());
        assertEquals(Permission.Type.Read, permission.getType());
    }

    @Test
    public void createPublicPermission() {

        FileEntry entry = new FileEntry();

        Permission permission = testObject.createPublicPermission(entry);

        assertEquals(entry, permission.getResource());
        assertEquals(TestUtils.getAgentService().getPublicAgent(), permission.getAgent());
        assertEquals(Permission.Type.Read, permission.getType());
    }

    @Test(expected = ForbiddenException.class)
    public void findReadPermission_noPermission_forbiddenException() {

        User user = TestUtils.persistUser("auth-id");
        testObject.findReadPermission(user, "some-id");

        fail("missing permission should cause exception");
    }

    @Test
    public void findReadPermission_permissionGranted_permission() {

        Project project = TestUtils.persistProjectWithCreator("project-name", "auth-id");

        Permission permission = testObject.findReadPermission(project.getCreator(), project.getId());

        assertEquals(project.getCreator(), permission.getAgent());
        assertEquals(project, permission.getResource());
        assertTrue(permission.canRead());
    }

    @Test
    public void findReadPermission_noPermissionButPublicPermission_permission() {

        Project project = TestUtils.persistProjectWithCreator("project-name", "some-auth-id");
        project.addPermission(testObject.createPublicPermission(project));
        TestUtils.getProjectDAO().persist(project);
        User otherUser = TestUtils.persistUser("other-auth-id");

        Permission permission = testObject.findReadPermission(otherUser, project.getId());

        assertEquals(TestUtils.getAgentService().getPublicAgent(), permission.getAgent());
        assertEquals(project, permission.getResource());
        assertTrue(permission.canRead());
    }

    @Test(expected = ForbiddenException.class)
    public void findDeletePermission_noPermission_forbiddenException() {

        User user = TestUtils.persistUser("auth-id");
        testObject.findDeletePermission(user, "some-id");

        fail("missing permission should cause exception");
    }

    @Test
    public void findDeletePermission_permissionGranted_permission() {

        Project project = TestUtils.persistProjectWithCreator("project-name", "auth-id");

        Permission permission = testObject.findDeletePermission(project.getCreator(), project.getId());

        assertEquals(project.getCreator(), permission.getAgent());
        assertEquals(project, permission.getResource());
        assertTrue(permission.canDelete());
    }

    @Test(expected = ForbiddenException.class)
    public void findWritePermission_noPermission_forbiddenException() {

        User user = TestUtils.persistUser("auth-id");
        testObject.findWritePermission(user, "some-id");

        fail("missing permission should cause exception");
    }

    @Test
    public void findWritePermission_permissionGranted_permission() {

        Project project = TestUtils.persistProjectWithCreator("project-name", "auth-id");

        Permission permission = testObject.findWritePermission(project.getCreator(), project.getId());

        assertEquals(project.getCreator(), permission.getAgent());
        assertEquals(project, permission.getResource());
        assertTrue(permission.canWrite());
    }
}
