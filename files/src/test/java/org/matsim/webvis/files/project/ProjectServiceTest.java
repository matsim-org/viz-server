package org.matsim.webvis.files.project;

import org.apache.commons.fileupload.FileItem;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.matsim.webvis.common.service.CodedException;
import org.matsim.webvis.common.service.Error;
import org.matsim.webvis.files.config.Configuration;
import org.matsim.webvis.files.entities.*;
import org.matsim.webvis.files.user.UserDAO;
import org.matsim.webvis.files.util.TestUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
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
    public void createNewProject_projectNameExists_exception() {

        String name = "name";
        User user = userDAO.persist(new User());

        testObject.createNewProject(name, user);
        testObject.createNewProject(name, user);

        fail("inserting already present project should throw exception");
    }

    @Test(expected = Exception.class)
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
    }

    @Test
    public void findFlat_noProject_exception() {

        try {
            testObject.findFlat("some-id", null);
        } catch (CodedException e) {
            assertEquals(Error.RESOURCE_NOT_FOUND, e.getErrorCode());
            return;
        }
        fail("invalid project id should throw exception");
    }

    @Test
    public void findFlat_userNotAllowed_exception() {

        Project project = persistProjectWithCreator("project-name");
        try {
            testObject.findFlat(project.getId(), new User());
        } catch (CodedException e) {
            assertEquals(Error.FORBIDDEN, e.getErrorCode());
            return;
        }
        fail("invalid user should throw exception");
    }

    @Test
    public void findFlat_allGood_project() throws CodedException {

        Project project = persistProjectWithCreator("project-name");

        Project result = testObject.findFlat(project.getId(), project.getCreator());

        assertEquals(project.getId(), result.getId());
        assertEquals(project.getCreator().getId(), result.getCreator().getId());
    }

    @Test
    public void find_noProject_exception() {

        try {
            testObject.find("some-id", null);
        } catch (CodedException e) {
            assertEquals(Error.RESOURCE_NOT_FOUND, e.getErrorCode());
            return;
        }
        fail("invalid project id should throw exception");
    }

    @Test
    public void find_userNotAllowed_exception() {

        Project project = persistProjectWithCreator("project-name");
        try {
            testObject.find(project.getId(), new User());
        } catch (CodedException e) {
            assertEquals(Error.FORBIDDEN, e.getErrorCode());
            return;
        }
        fail("invalid user should throw exception");
    }

    @Test
    public void find_allGood_project() throws CodedException {

        Project project = persistProjectWithCreator("project-name");

        Project result = testObject.find(project.getId(), project.getCreator());

        assertEquals(project.getId(), result.getId());
        assertEquals(project.getCreator().getId(), result.getCreator().getId());

        //testing the size of relational collections as proof of loaded object graph
        assertEquals(0, result.getFiles().size());
        assertEquals(0, result.getVisualizations().size());
    }

    @Test
    public void find_noProjectForUser_emptyList() {

        Project project = persistProjectWithCreator("project-name");
        List<String> ids = new ArrayList<>();
        ids.add(project.getId());
        User otherUser = userDAO.persist(new User());

        List<Project> result = testObject.find(ids, otherUser);

        assertEquals(0, result.size());
    }

    @Test
    public void find_listOfOneProject() {

        Project first = persistProjectWithCreator("first-project");
        List<String> projectIds = new ArrayList<>();
        projectIds.add(first.getId());

        List<Project> result = testObject.find(projectIds, first.getCreator());

        assertEquals(1, result.size());
        assertEquals(first.getId(), result.get(0).getId());
    }

    @Test
    public void find_severalProjectsPresent_listOfProjects() {

        Project project1 = persistProjectWithCreator("first");
        project1 = addFileEntry(project1);
        project1 = addFileEntry(project1);

        Project project2 = testObject.createNewProject("second", project1.getCreator());
        project2 = addFileEntry(project2);
        project2 = addFileEntry(project2);

        List<String> ids = new ArrayList<>();
        ids.add(project1.getId());
        ids.add(project2.getId());

        List<Project> result = testObject.find(ids, project1.getCreator());

        assertEquals(2, result.size());
        Project firstResult = result.get(0);
        assertEquals(2, firstResult.getFiles().size());
        assertEquals(0, firstResult.getVisualizations().size());
    }

    @Test
    public void findAllProjectsForUser_listOfProjects() {

        User user = new User();
        user.setAuthId("auth-id");
        user = userDAO.persist(user);
        Project firstProject = testObject.createNewProject("first", user);
        Project secondProject = testObject.createNewProject("second", user);

        List<Project> result = testObject.findAllForUserFlat(user);

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(e -> e.getName().equals(firstProject.getName())));
        assertTrue(result.stream().anyMatch(e -> e.getName().equals(secondProject.getName())));
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

    @Test
    public void removeFile_fileNotPartOfProject_exception() {

        Project project = persistProjectWithCreator("test");
        try {
            testObject.removeFileFromProject(project.getId(), "test", project.getCreator());
        } catch (CodedException e) {
            assertEquals(Error.RESOURCE_NOT_FOUND, e.getErrorCode());
            return;
        }
        fail("should have thrown exception");
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

    @Test
    public void addVisualization() throws CodedException {
        Project project = persistProjectWithCreator("test");
        project = addFileEntry(project);
        VisualizationInput input = new VisualizationInput();
        input.setKey("network");
        input.setFileEntry(project.getFiles().iterator().next());

        Visualization viz = new Visualization();
        viz.setBackendService(URI.create("http://bla.com"));
        viz.addInput(input);

        VisualizationParameter parameter = new VisualizationParameter();
        parameter.setKey("some");
        parameter.setValue("value");
        viz.addParameter(parameter);
        viz.setName("some name");

        Project persisted = testObject.addVisualization(project.getId(), viz, project.getCreator());

        assertEquals(1, persisted.getVisualizations().size());
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
            return testObject.createNewProject(name, user);
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
