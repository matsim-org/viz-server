package org.matsim.webvis.files.project;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.matsim.webvis.files.entities.Project;
import org.matsim.webvis.files.entities.User;
import org.matsim.webvis.files.user.UserDAO;

import static org.junit.Assert.*;

public class ProjectServiceTest {

    private ProjectService testObject;
    private UserDAO userDAO = new UserDAO();

    @Before
    public void setUp() {
        testObject = new ProjectService();
    }

    @After
    public void tearDown() {
        userDAO.removeAllUser();
        new ProjectDAO().removeAllProjects();
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
}
