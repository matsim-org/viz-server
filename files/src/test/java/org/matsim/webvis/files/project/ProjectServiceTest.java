package org.matsim.webvis.files.project;

import org.apache.commons.fileupload.FileItem;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.matsim.webvis.files.config.Configuration;
import org.matsim.webvis.files.entities.FileEntry;
import org.matsim.webvis.files.entities.Project;
import org.matsim.webvis.files.entities.User;
import org.matsim.webvis.files.user.UserDAO;
import org.matsim.webvis.files.util.TestUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

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

    @Test(expected = Exception.class)
    public void createNewProject_projectNameExists_exception() throws Exception {

        String name = "name";
        User user = userDAO.persist(new User());

        testObject.createNewProject(name, user.getId());
        testObject.createNewProject(name, user.getId());

        fail("inserting already present project should throw exception");
    }

    @Test(expected = Exception.class)
    public void createNewProject_userDoesNotExist_exception() throws Exception {

        String name = "name";
        String userId = "some-id";

        testObject.createNewProject(name, userId);

        fail("inserting project with invalid creator should throw an exception");
    }

    @Test
    public void createNewProject_allGood_newProject() throws Exception {

        String name = "name";
        User user = userDAO.persist(new User());

        Project project = testObject.createNewProject(name, user.getId());

        assertNotNull(project);
        assertEquals(name, project.getName());
        assertEquals(user.getId(), project.getCreator().getId());
    }

    @Test(expected = Exception.class)
    public void getProjectIfAllowed_noProject_exception() throws Exception {

        User user = new User();
        userDAO.persist(user);

        testObject.findProjectIfAllowed("invalid-project-id", user.getId());

        fail("invalid project id should throw exception");
    }

    @Test(expected = Exception.class)
    public void getProjectIfAllowed_userNotAuthorized_exception() throws Exception {

        User user = new User();
        userDAO.persist(user);
        Project project = new Project();
        projectDAO.persistNewProject(project, user.getId());

        testObject.findProjectIfAllowed(project.getId(), "some-other-user");

        fail("Should throw exception if user is not creator of the project");
    }

    @Test
    public void getProjectIfAllowed_project() throws Exception {

        User user = new User();
        user = userDAO.persist(user);
        Project project = testObject.createNewProject("name", user.getId());

        Project result = testObject.findProjectIfAllowed(project.getId(), user.getId());

        assertEquals(project.getId(), result.getId());
        assertEquals(user.getId(), result.getCreator().getId());
    }

    @Test
    public void findProjectsForUser_listOfProjects() throws Exception {
        User user = new User();
        user.setAuthId("auth-id");
        user = userDAO.persist(user);
        Project firstProject = testObject.createNewProject("first", user.getId());
        testObject.createNewProject("second", user.getId());
        List<String> projectIds = new ArrayList<>();
        projectIds.add(firstProject.getId());

        List<Project> result = testObject.findProjectsForUser(projectIds, user);

        assertEquals(1, result.size());
        assertEquals(firstProject.getId(), result.get(0).getId());
    }

    @Test
    public void findProjectsForUser_severalProjectsPresent_listOfDistinctProjects() throws Exception {

        Project project1 = persistProjectWithCreator("first");
        project1 = addFileEntry(project1);
        project1 = addFileEntry(project1);

        Project project2 = testObject.createNewProject("second", project1.getCreator().getId());
        project2 = addFileEntry(project2);
        project2 = addFileEntry(project2);

        List<String> ids = new ArrayList<>();
        ids.add(project1.getId());
        ids.add(project2.getId());

        List<Project> result = testObject.findProjectsForUser(ids, project1.getCreator());

        assertEquals(2, result.size());
    }

    @Test
    public void findAllProjectsForUser_listOfProjects() throws Exception {

        User user = new User();
        user.setAuthId("auth-id");
        user = userDAO.persist(user);
        Project firstProject = testObject.createNewProject("first", user.getId());
        Project secondProject = testObject.createNewProject("second", user.getId());

        List<Project> result = testObject.findAllProjectsForUser(user);

        assertEquals(2, result.size());
        assertEquals(firstProject.getName(), result.get(0).getName());
        assertEquals(secondProject.getName(), result.get(1).getName());
    }

    @Test
    public void addFilesToProject() throws Exception {

        final String filename = "filename";
        final String contentType = "content-type";
        final long size = 1L;

        Project project = persistProjectWithCreator("test");
        List<FileItem> items = new ArrayList<>();
        items.add(TestUtils.mockFileItem(filename, contentType, size));

        Project result = testObject.addFilesToProject(items, project);

        assertEquals(project.getId(), result.getId());
        assertEquals(1, project.getFiles().size());
    }

    @Test
    public void addFilesToProject_errorWhilePersisting_cleanupFiles() throws Exception {

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

        List<FileItem> items = new ArrayList<>();
        items.add(TestUtils.mockFileItem(filename, contentType, size));

        try {
            testObject.addFilesToProject(items, new Project());
            fail("exception while persisting project should raise exception and delete written files");
        } catch (Exception e) {
            verify(repository).removeFiles(any());
        }
    }

    @Test
    public void getFileStream_inputStream() throws Exception {

        Project project = persistProjectWithCreator("test");
        project = addFileEntry(project);
        FileEntry entry = project.getFiles().iterator().next();

        testObject.repositoryFactory = mock(RepositoryFactory.class);
        DiskProjectRepository repository = mock(DiskProjectRepository.class);
        when(repository.getFileStream(any())).thenReturn(mock(FileInputStream.class));
        when(testObject.repositoryFactory.getRepository(any())).thenReturn(repository);

        InputStream result = testObject.getFileStream(project, entry);
        assertNotNull(result);
    }

    @Test(expected = Exception.class)
    public void removeFile_fileNotPartOfProject_exception() throws Exception {

        Project project = persistProjectWithCreator("test");
        testObject.removeFileFromProject(project.getId(), "test", project.getCreator());
    }

    @Test(expected = Exception.class)
    public void removeFile_userNotProjectOwner_exception() throws Exception {

        Project project = persistProjectWithCreator("test");
        testObject.removeFileFromProject(project.getId(), "test", new User());
    }

    @Test
    public void removeFile_fileIsRemoved() throws Exception {
        Project project = persistProjectWithCreator("test");
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

    private Project persistProjectWithCreator(String name) {
        User user = new User();
        user.setAuthId("some-auth-id");
        try {
            userDAO.persist(user);
        } catch (Exception e) {
            fail("failed to persist user.");
        }
        try {
            return testObject.createNewProject(name, user.getId());
        } catch (Exception e) {
            fail("Failed to create project with name: " + name);
        }
        return null;
    }

    private Project addFileEntry(Project project) {
        FileEntry entry = new FileEntry();
        project.getFiles().add(entry);
        entry.setProject(project);
        return projectDAO.persist(project);
    }
}
