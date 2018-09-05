package org.matsim.webvis.files.permission;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.matsim.webvis.error.ForbiddenException;
import org.matsim.webvis.files.entities.FileEntry;
import org.matsim.webvis.files.entities.Permission;
import org.matsim.webvis.files.entities.Project;
import org.matsim.webvis.files.entities.User;
import org.matsim.webvis.files.util.TestUtils;

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
