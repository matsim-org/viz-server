package org.matsim.viz.files.project;

import org.junit.*;
import org.matsim.viz.error.CodedException;
import org.matsim.viz.error.ForbiddenException;
import org.matsim.viz.files.entities.*;
import org.matsim.viz.files.file.FileDownload;
import org.matsim.viz.files.file.FileUpload;
import org.matsim.viz.files.file.LocalRepository;
import org.matsim.viz.files.file.Repository;
import org.matsim.viz.files.notifications.Notifier;
import org.matsim.viz.files.util.TestUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SuppressWarnings("ConstantConditions")
public class ProjectServiceTest {

    private ProjectService testObject;

    @BeforeClass
    public static void setUpFixture() {
        TestUtils.loadTestConfig();
    }

    @Before
    public void setUp() {
        testObject = new ProjectService(
                new ProjectDAO(TestUtils.getPersistenceUnit()),
                TestUtils.getPermissionService(),
                mock(Repository.class),
                mock(Notifier.class));
    }

    @After
    public void tearDown() {
        TestUtils.removeAllEntities();
    }

    @Test(expected = CodedException.class)
    public void createNewProject_projectNameExists_exception() {

        String name = "name";
        User user = TestUtils.persistUser();

        testObject.createNewProject(name, user);
        testObject.createNewProject(name, user);

        fail("inserting already present project should throw exception");
    }

    @Test(expected = CodedException.class)
    public void createNewProject_userDoesNotExist_exception() {

        String name = "name";
        User user = new User();

        testObject.createNewProject(name, user);

        fail("inserting project with invalid creator should throw an exception");
    }

    @Test
    public void createNewProject_allGood_newProject() {

        String name = "name";
        User user = TestUtils.persistUser();

        Project project = testObject.createNewProject(name, user);

        assertNotNull(project);
        assertEquals(name, project.getName());
        assertEquals(user.getId(), project.getCreator().getId());

        Optional<Permission> optional = project.getPermissions().stream().filter(p -> p.getAgent().equals(user)).findFirst();
        assertTrue(optional.isPresent());
        assertEquals(user, optional.get().getAgent());
    }

    @Test(expected = ForbiddenException.class)
    public void removeProject_noPermission_exception() {

        final String id = "user has no permission";
        final User user = TestUtils.persistUser();

        testObject.removeProject(id, user);

        fail("invalid permission should cause forbidden exception");
    }

    @Test
    public void removeProject_allGood() {

        VisualizationType type = new VisualizationType("test-type", false, null, null, null);
        type = TestUtils.getVisualizationDAO().persistType(type);

        Project project = TestUtils.persistProjectWithCreator("test name");
        FileEntry fileEntry = new FileEntry();
        project.addFileEntry(fileEntry);
        Visualization viz = new Visualization();
        viz.setType(type);
        project.addVisualization(viz);
        project = TestUtils.getProjectDAO().persist(project);

        testObject.removeProject(project.getId(), project.getCreator());

        Project shouldNotBeFound = TestUtils.getProjectDAO().find(project.getId());

        assertNull(shouldNotBeFound);
        Visualization shouldAlsoNotBeFound = TestUtils.getVisualizationDAO().find(project.getVisualizations().iterator().next().getId());
        assertNull(shouldAlsoNotBeFound);
        FileEntry fileShouldAlsoBeDeleted = TestUtils.getProjectDAO().findFileEntry(project.getId(), project.getFiles().iterator().next().getId());
        assertNull(fileShouldAlsoBeDeleted);
    }

    @Test(expected = ForbiddenException.class)
    public void findFlat_noProject_exception() {

        User user = TestUtils.persistUser("id");

        testObject.findFlat("some-id", user);

        fail("invalid project id should throw exception");
    }

    @Test(expected = ForbiddenException.class)
    public void findFlat_userNotAllowed_exception() {

        Project project = TestUtils.persistProjectWithCreator("project-name", "id");
        User user = TestUtils.persistUser("other-id");

        testObject.findFlat(project.getId(), user);

        fail("invalid user should throw exception");
    }

    @Test
    public void findFlat_allGood_project() {

        Project project = TestUtils.persistProjectWithCreator("project-name");

        Project result = testObject.findFlat(project.getId(), project.getCreator());

        assertEquals(project.getId(), result.getId());
        assertEquals(project.getCreator().getId(), result.getCreator().getId());
    }

    @Test(expected = ForbiddenException.class)
    public void find_noProject_exception() {

        User user = TestUtils.persistUser("some-auth-id");

        testObject.find("some-id", user);

        fail("invalid project id should throw exception");
    }

    @Test(expected = ForbiddenException.class)
    public void find_userNotAllowed_exception() {

        Project project = TestUtils.persistProjectWithCreator("project-name");
        User otherUser = TestUtils.persistUser("other-user");

        testObject.find(project.getId(), otherUser);

        fail("invalid user should throw exception");
    }

    @Test
    public void find_allGood_project() {

        Project project = TestUtils.persistProjectWithCreator("project-name", "auth-id");
        //make sure there is more than one project
        TestUtils.persistProjectWithCreator("other-project", "other-auth-id");

        Project result = testObject.find(project.getId(), project.getCreator());

        assertEquals(project.getId(), result.getId());
        assertEquals(project.getCreator().getId(), result.getCreator().getId());

        //testing the size of relational collections as proof of loaded object graph
        assertEquals(0, result.getFiles().size());
        assertEquals(0, result.getVisualizations().size());
    }

    @Test
    public void findAllProjectsForUser_listOfProjects() {

        User user = TestUtils.persistUser("auth-id");
        Project firstProject = testObject.createNewProject("first", user);
        Project secondProject = testObject.createNewProject("second", user);

        Project otherProject = TestUtils.persistProjectWithCreator("other-project");

        List<Project> result = testObject.findAllForUserFlat(user);

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(e -> e.getName().equals(firstProject.getName())));
        assertTrue(result.stream().anyMatch(e -> e.getName().equals(secondProject.getName())));

        assertTrue(result.stream().noneMatch(e -> e.getName().equals(otherProject.getName())));
    }

    @Test
    public void addFilesToProject() {

        LocalRepository repository = spy(new LocalRepository("some-directory"));
        doAnswer(args -> {
            FileUpload upload = args.getArgument(0);
            FileEntry result = new FileEntry();
            result.setPersistedFileName(upload.getFileName());
            result.setUserFileName(upload.getFileName());
            return result;
        }).when(repository).addFile(any(FileUpload.class));
        testObject = new ProjectService(new ProjectDAO(TestUtils.getPersistenceUnit()),
                TestUtils.getPermissionService(), repository, mock(Notifier.class));
        Project project = TestUtils.persistProjectWithCreator("test");
        List<FileUpload> uploads = new ArrayList<>();
        uploads.add(new FileUpload("first.txt", "plain/text", mock(InputStream.class)));
        uploads.add(new FileUpload("second.txt", "plain/text", mock(InputStream.class)));

        Project result = testObject.addFilesToProject(uploads, project.getId(), project.getCreator());

        assertEquals(project, result);
        assertEquals(uploads.size(), result.getFiles().size());
    }

    @Test(expected = ForbiddenException.class)
    public void addFilesToProject_noPermission_exception() {

        User user = TestUtils.persistUser("some-id");
        Project project = TestUtils.persistProjectWithCreator("project", "auth-id");

        testObject.addFilesToProject(null, project.getId(), user);

        fail("user without permission should raise forbidden exception");
    }

    @Test
    public void addFilesToProject_errorWhilePersisting_cleanupFiles() {

        Project project = TestUtils.persistProjectWithCreator("test");
        List<FileUpload> uploads = new ArrayList<>();
        uploads.add(new FileUpload("same-name.txt", "plain/text", mock(InputStream.class)));
        uploads.add(new FileUpload("same-name.txt", "plain/text", mock(InputStream.class)));

        LocalRepository repository = spy(new LocalRepository("some-directory"));
        doReturn(new FileEntry()).when(repository).addFile(any(FileUpload.class));
        doNothing().when(repository).removeFile(any(FileEntry.class));
        ProjectDAO mockedDao = mock(ProjectDAO.class);
        when(mockedDao.findWithFullGraph(anyString())).thenReturn(project);
        when(mockedDao.persist(any(Project.class))).thenThrow(new RuntimeException("persisting error"));

        testObject = new ProjectService(mockedDao, TestUtils.getPermissionService(), repository, mock(Notifier.class));
        try {
            testObject.addFilesToProject(uploads, project.getId(), project.getCreator());
            fail("exception while persisting project should raise exception and delete written files");
        } catch (RuntimeException e) {
            verify(repository).removeFiles(any());
        }
    }

    @Test(expected = ForbiddenException.class)
    public void getFileDownload_noPermission() {

        User user = TestUtils.persistUser("some-id");
        Project project = TestUtils.persistProjectWithCreator("project", "auth-id");

        testObject.getFileDownload(project.getId(), "some-id", user);

        fail("user without permission should raise forbidden exception");
    }

    @Test
    public void getFileDownload_success() {

        Project project = TestUtils.persistProjectWithCreator("project");
        project = addFileEntry(project);
        FileEntry entry = project.getFiles().iterator().next();

        LocalRepository repository = mock(LocalRepository.class);
        InputStream stream = mock(InputStream.class);
        when(repository.getFileStream(any())).thenReturn(stream);

        testObject =
                new ProjectService(new ProjectDAO(TestUtils.getPersistenceUnit()), TestUtils.getPermissionService(), repository, mock(Notifier.class));

        FileDownload result = testObject.getFileDownload(project.getId(), entry.getId(), project.getCreator());

        Assert.assertEquals(entry, result.getFileEntry());
        assertEquals(stream, result.getFile());
    }

    @Test(expected = ForbiddenException.class)
    public void removeFile_fileNotPartOfProject_exception() {

        Project project = TestUtils.persistProjectWithCreator("test");

        testObject.removeFileFromProject(project.getId(), "test", project.getCreator());

        fail("should have thrown exception");
    }

    @Test
    public void removeFile_fileIsRemoved() {
        Project project = TestUtils.persistProjectWithCreator("test");
        project = addFileEntry(project);
        FileEntry entry = project.getFiles().iterator().next();

        // ensure file is added to project
        assertEquals(1, project.getFiles().size());

        LocalRepository repository = mock(LocalRepository.class);
        doNothing().when(repository).removeFile(any());

        testObject = new ProjectService(
                new ProjectDAO(TestUtils.getPersistenceUnit()),
                TestUtils.getPermissionService(),
                repository, mock(Notifier.class)
        );

        Project updated = testObject.removeFileFromProject(project.getId(), entry.getId(), project.getCreator());

        assertEquals(0, updated.getFiles().size());
        assertEquals(0, TestUtils.getProjectDAO().find(updated.getId()).getFiles().size());
        verify(repository).removeFile(any());
    }

    @Test(expected = ForbiddenException.class)
    public void addPermission_subjectIsNotOwner_exception() {

        Project project = TestUtils.persistProjectWithCreator("some-name");
        User otherUser = TestUtils.getAgentService().createUser("some-other-auth-id");

        testObject.addPermission(project.getId(), otherUser, Permission.Type.Owner, otherUser);

        fail("user without owner permission should cause exception");
    }

    @Test
    public void addPermission_allGood_permissionAdded() {

        Project project = TestUtils.persistProjectWithCreator("some-name");
        FileEntry entry = new FileEntry();
        entry.setUserFileName("filename.file");
        entry.setPersistedFileName("persisted.file");
        project.addFileEntry(entry);
        TestUtils.getProjectDAO().persist(project);

        User otherUser = TestUtils.getAgentService().createUser("some-other-auth-id");
        Permission.Type permissionType = Permission.Type.Owner;

        Project result = testObject.addPermission(project.getId(), otherUser, permissionType, project.getCreator());

        assertTrue(result.getPermissions().stream().anyMatch(permission -> permission.getAgent().equals(otherUser) &&
                permission.getType().equals(permissionType)));

        // check whether permissions are also set for resources contained in project
        result.getFiles().forEach(file -> assertTrue(file.getPermissions().stream().anyMatch(
                permission -> permission.getAgent().equals(otherUser) &&
                        permission.getType().equals(permissionType))));

    }

    @Test(expected = ForbiddenException.class)
    public void removePermission_subjectIsNotOwner_excpetion() {

        Project project = TestUtils.persistProjectWithCreator("some-name");
        User otherUser = TestUtils.getAgentService().createUser("some-other-auth-id");

        testObject.removePermission(project.getId(), otherUser, otherUser);

        fail("user without owner permission should cause exception");
    }

    @Test
    public void removePermission_allGood_permissionRemoved() {

        Project project = TestUtils.persistProjectWithCreator("some-name");
        FileEntry entry = new FileEntry();
        entry.setUserFileName("filename.file");
        entry.setPersistedFileName("persisted.file");
        project.addFileEntry(entry);
        TestUtils.getProjectDAO().persist(project);
        User otherUser = TestUtils.getAgentService().createUser("some-other-auth-id");
        Permission.Type permissionType = Permission.Type.Owner;

        testObject.addPermission(project.getId(), otherUser, permissionType, project.getCreator());

        Project result = testObject.removePermission(project.getId(), otherUser, project.getCreator());

        assertTrue(result.getPermissions().stream().noneMatch(permission -> permission.getAgent().equals(otherUser) &&
                permission.getType().equals(permissionType)));

        // check whether permissions are also set for resources contained in project
        result.getFiles().forEach(file -> assertTrue(file.getPermissions().stream().noneMatch(
                permission -> permission.getAgent().equals(otherUser) &&
                        permission.getType().equals(permissionType))));
    }

    private Project addFileEntry(Project project) {
        FileEntry entry = new FileEntry();
        project.addFileEntry(entry);
        return TestUtils.getProjectDAO().persist(project);
    }
}
