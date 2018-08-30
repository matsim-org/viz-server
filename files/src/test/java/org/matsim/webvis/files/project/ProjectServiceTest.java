package org.matsim.webvis.files.project;

import org.junit.*;
import org.matsim.webvis.error.CodedException;
import org.matsim.webvis.error.ForbiddenException;
import org.matsim.webvis.error.InternalException;
import org.matsim.webvis.files.config.AppConfiguration;
import org.matsim.webvis.files.entities.FileEntry;
import org.matsim.webvis.files.entities.Permission;
import org.matsim.webvis.files.entities.Project;
import org.matsim.webvis.files.entities.User;
import org.matsim.webvis.files.file.DiskProjectRepository;
import org.matsim.webvis.files.file.FileDownload;
import org.matsim.webvis.files.file.FileUpload;
import org.matsim.webvis.files.file.RepositoryFactory;
import org.matsim.webvis.files.util.TestUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
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
                new RepositoryFactory());
    }

    @After
    public void tearDown() {
        TestUtils.removeAllEntities();
    }

    @AfterClass
    public static void tearDownFixture() throws IOException {
        TestUtils.removeFileTree(Paths.get(AppConfiguration.getInstance().getUploadFilePath()));
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

        DiskProjectRepository repository = spy(new DiskProjectRepository(project));
        //spy(repository);
        RepositoryFactory factory = mock(RepositoryFactory.class);
        when(factory.getRepository(project)).thenReturn(repository);
        testObject = new ProjectService(
                new ProjectDAO(TestUtils.getPersistenceUnit()),
                TestUtils.getPermissionService(),
                factory
        );

        try {
            testObject.addFilesToProject(uploads, project.getId(), project.getCreator());
            fail("exception while persisting project should raise exception and delete written files");
        } catch (InternalException e) {
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

        DiskProjectRepository repository = mock(DiskProjectRepository.class);
        InputStream stream = mock(InputStream.class);
        when(repository.getFileStream(any())).thenReturn(stream);

        RepositoryFactory factory = mock(RepositoryFactory.class);
        when(factory.getRepository(project)).thenReturn(repository);
        testObject = new ProjectService(
                new ProjectDAO(TestUtils.getPersistenceUnit()),
                TestUtils.getPermissionService(),
                factory
        );

        FileDownload result = testObject.getFileDownload(project.getId(), entry.getId(), project.getCreator());

        assertEquals(entry, result.getFileEntry());
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

        RepositoryFactory factory = mock(RepositoryFactory.class);
        DiskProjectRepository repository = mock(DiskProjectRepository.class);
        doNothing().when(repository).removeFile(any());
        when(factory.getRepository(any())).thenReturn(repository);

        testObject = new ProjectService(
                new ProjectDAO(TestUtils.getPersistenceUnit()),
                TestUtils.getPermissionService(),
                factory
        );

        Project updated = testObject.removeFileFromProject(project.getId(), entry.getId(), project.getCreator());

        assertEquals(0, updated.getFiles().size());
        assertEquals(0, TestUtils.getProjectDAO().find(updated.getId()).getFiles().size());
        verify(repository).removeFile(any());
    }

    private Project addFileEntry(Project project) {
        FileEntry entry = new FileEntry();
        project.addFileEntry(entry);
        return TestUtils.getProjectDAO().persist(project);
    }

}
