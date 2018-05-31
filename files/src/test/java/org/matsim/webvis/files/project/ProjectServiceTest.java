package org.matsim.webvis.files.project;

import org.apache.commons.fileupload.FileItem;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.matsim.webvis.common.service.CodedException;
import org.matsim.webvis.common.service.ForbiddenException;
import org.matsim.webvis.files.config.Configuration;
import org.matsim.webvis.files.entities.*;
import org.matsim.webvis.files.permission.PermissionService;
import org.matsim.webvis.files.agent.UserDAO;
import org.matsim.webvis.files.util.TestUtils;

import java.io.FileInputStream;
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
    private UserDAO userDAO = new UserDAO();
    private ProjectDAO projectDAO = new ProjectDAO();

    @Before
    public void setUp() {
        testObject = new ProjectService();
    }

    @After
    public void tearDown() {
        projectDAO.removeAllProjects();
        userDAO.removeAllUser();
    }

    @AfterClass
    public static void tearDownFixture() throws IOException {
        TestUtils.removeFileTree(Paths.get(Configuration.getInstance().getUploadedFilePath()));
    }

    @Test(expected = CodedException.class)
    public void createNewProject_projectNameExists_exception() {

        String name = "name";
        User user = userDAO.persist(new User());

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
        User user = userDAO.persist(new User());

        Project project = testObject.createNewProject(name, user);

        assertNotNull(project);
        assertEquals(name, project.getName());
        assertEquals(user.getId(), project.getCreator().getId());

        Optional<Permission> optional = project.getPermissions().stream().filter(p -> p.getAgent().equalId(user)).findFirst();
        assertTrue(optional.isPresent());
        assertTrue(user.equalId(optional.get().getAgent()));
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

        User user = new User();
        user.setAuthId("auth-id");
        user = userDAO.persist(user);
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

        final String filename = "filename";
        final String contentType = "content-type";
        final long size = 1L;

        Project project = TestUtils.persistProjectWithCreator("test");
        List<FileItem> items = new ArrayList<>();
        items.add(TestUtils.mockFileItem(filename, contentType, size));

        Project result = testObject.addFilesToProject(items, project, project.getCreator());

        assertEquals(project.getId(), result.getId());
        assertEquals(1, project.getFiles().size());
    }

    @Test(expected = ForbiddenException.class)
    public void addFilesToProject_noPermission_forbiddenException() {

        User user = TestUtils.persistUser("some-id");
        Project project = TestUtils.persistProjectWithCreator("project", "auth-id");

        testObject.addFilesToProject(null, project, user);

        fail("user without permission should raise forbidden exception");
    }

    @Test
    public void addFilesToProject_errorWhilePersisting_cleanupFiles() {

        final String filename = "filename";
        final String contentType = "content-type";
        final long size = 1L;

        List<FileEntry> entries = new ArrayList<>();
        entries.add(new FileEntry());
        DiskProjectRepository repository = mock(DiskProjectRepository.class);
        when(repository.addFiles(any())).thenReturn(entries);

        testObject.repositoryFactory = mock(RepositoryFactory.class);
        when(testObject.repositoryFactory.getRepository(any())).thenReturn(repository);

        testObject.projectDAO = mock(ProjectDAO.class);
        when(testObject.projectDAO.persist(any())).thenThrow(new RuntimeException());

        testObject.permissionService = mock(PermissionService.class);
        when(testObject.permissionService.findWritePermission(any(), anyString())).thenReturn(new Permission());

        List<FileItem> items = new ArrayList<>();
        items.add(TestUtils.mockFileItem(filename, contentType, size));

        try {
            testObject.addFilesToProject(items, new Project(), new User());
            fail("exception while persisting project should raise exception and delete written files");
        } catch (Exception e) {
            verify(repository).removeFiles(any());
        }
    }

    @Test
    public void getFileStream_inputStream() {

        Project project = TestUtils.persistProjectWithCreator("test");
        project = addFileEntry(project);
        FileEntry entry = project.getFiles().iterator().next();

        testObject.repositoryFactory = mock(RepositoryFactory.class);
        DiskProjectRepository repository = mock(DiskProjectRepository.class);
        when(repository.getFileStream(any())).thenReturn(mock(FileInputStream.class));
        when(testObject.repositoryFactory.getRepository(any())).thenReturn(repository);

        InputStream result = testObject.getFileStream(project, entry, project.getCreator());
        assertNotNull(result);
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

        testObject.repositoryFactory = mock(RepositoryFactory.class);
        DiskProjectRepository repository = mock(DiskProjectRepository.class);
        doNothing().when(repository).removeFile(any());
        when(testObject.repositoryFactory.getRepository(any())).thenReturn(repository);

        Project updated = testObject.removeFileFromProject(project.getId(), entry.getId(), project.getCreator());

        assertEquals(0, updated.getFiles().size());
        assertEquals(0, projectDAO.find(updated.getId()).getFiles().size());
        verify(repository).removeFile(any());
    }

    private Project addFileEntry(Project project) {
        FileEntry entry = new FileEntry();
        project.addFileEntry(entry);
        return projectDAO.persist(project);
    }
}
