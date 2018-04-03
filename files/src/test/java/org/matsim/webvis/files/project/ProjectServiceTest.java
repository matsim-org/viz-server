package org.matsim.webvis.files.project;

import org.apache.commons.fileupload.FileItem;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.matsim.webvis.files.entities.FileEntry;
import org.matsim.webvis.files.entities.Project;
import org.matsim.webvis.files.entities.User;
import org.matsim.webvis.files.user.UserDAO;
import org.matsim.webvis.files.util.TestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
        userDAO.removeAllUser();
        projectDAO.removeAllProjects();
    }

    @Test(expected = Exception.class)
    public void createNewProject_projectNameExists_exception() throws Exception {

        String name = "name";
        User user = userDAO.persistNewUser(new User());

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
        User user = userDAO.persistNewUser(new User());

        Project project = testObject.createNewProject(name, user.getId());

        assertNotNull(project);
        assertEquals(name, project.getName());
        assertEquals(user.getId(), project.getCreator().getId());
    }

    @Test(expected = Exception.class)
    public void getProjectIfAllowed_noProject_exception() throws Exception {

        User user = new User();
        userDAO.update(user);

        testObject.getProjectIfAllowed("invalid-project-id", user.getId());

        fail("invalid project id should throw exception");
    }

    @Test(expected = Exception.class)
    public void getProjectIfAllowed_userNotAuthorized_exception() throws Exception {

        User user = new User();
        userDAO.update(user);
        Project project = new Project();
        projectDAO.persist(project);

        testObject.getProjectIfAllowed(project.getId(), user.getId());

        fail("Should throw exception if user is not creator of the project");
    }

    @Test
    public void getProjectIfAllowed_project() throws Exception {

        User user = new User();
        user = userDAO.update(user);
        Project project = testObject.createNewProject("name", user.getId());

        Project result = testObject.getProjectIfAllowed(project.getId(), user.getId());

        assertEquals(project.getId(), result.getId());
        assertEquals(user.getId(), result.getCreator().getId());
    }

    @Test
    public void addFilesToProject() throws Exception {

        final String filename = "filename";
        final String contentType = "content-type";
        final long size = 1L;

        User user = new User();
        user = userDAO.persistNewUser(user);
        Project project = testObject.createNewProject("name", user.getId());
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
}
