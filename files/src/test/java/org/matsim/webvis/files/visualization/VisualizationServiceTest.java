package org.matsim.webvis.files.visualization;

import org.junit.*;
import org.matsim.webvis.common.service.CodedException;
import org.matsim.webvis.common.service.Error;
import org.matsim.webvis.files.entities.Project;
import org.matsim.webvis.files.entities.User;
import org.matsim.webvis.files.entities.Visualization;
import org.matsim.webvis.files.entities.VisualizationType;
import org.matsim.webvis.files.project.ProjectDAO;
import org.matsim.webvis.files.util.TestUtils;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.*;

@SuppressWarnings("ConstantConditions")
public class VisualizationServiceTest {

    private static String typeKey = "test-key";
    private static VisualizationDAO visualizationDAO = new VisualizationDAO();
    private static ProjectDAO projectDAO = new ProjectDAO();
    private VisualizationService testObject;

    @BeforeClass
    public static void setUpFixture() {
        VisualizationType type = new VisualizationType(typeKey, false, null);
        visualizationDAO.persistType(type);
    }

    @AfterClass
    public static void tearDownFixture() {
        visualizationDAO.removeType(typeKey);
    }

    @Before
    public void setUp() {
        testObject = new VisualizationService();
    }

    @After
    public void tearDown() {
        TestUtils.removeAllEntities();
    }

    @Test
    public void createVisualizationFromRequest_invalidType_exception() {

        Project project = TestUtils.persistProjectWithCreator("some-project");

        Map<String, String> input = new HashMap<>();
        Map<String, String> parameters = new HashMap<>();
        parameters.put("some", "parameter");

        CreateVisualizationRequest request = new CreateVisualizationRequest(
                project.getId(),
                "invalid-type",
                input,
                parameters);

        try {
            testObject.createVisualizationFromRequest(request, project.getCreator());
            fail("invalid viz type should cause exception");
        } catch (CodedException e) {
            assertEquals(Error.RESOURCE_NOT_FOUND, e.getErrorCode());
        }
    }

    @Test
    public void createVisualizationFromRequest_visualization() throws CodedException {

        Project project = TestUtils.persistProjectWithCreator("some-project");
        project = TestUtils.addFileEntry(project);
        project = TestUtils.addFileEntry(project);

        Map<String, String> input = new HashMap<>();
        input.put("network", project.getFiles().iterator().next().getId());
        Map<String, String> parameters = new HashMap<>();
        parameters.put("some", "parameter");

        CreateVisualizationRequest request = new CreateVisualizationRequest(
                project.getId(),
                typeKey,
                input,
                parameters);

        Visualization viz = testObject.createVisualizationFromRequest(request, project.getCreator());

        assertNotNull(viz.getId());
        assertEquals(1, viz.getInputFiles().size());
        assertEquals(1, viz.getParameters().size());
        assertEquals(request.getTypeKey(), viz.getType().getKey());
    }

    @Test
    public void find_allGood_visualization() throws CodedException {

        Project project = TestUtils.persistProjectWithCreator("bla");

        Visualization viz = new Visualization();
        viz.setType(visualizationDAO.findType(typeKey));
        project.addVisualization(viz);
        project = projectDAO.persist(project);
        viz = project.getVisualizations().iterator().next();

        Visualization result = testObject.find(viz.getId(), project.getCreator());

        assertEquals(viz.getType().getKey(), result.getType().getKey());
    }

    @Test
    public void find_userNotAllowed_exception() {
        Project project = TestUtils.persistProjectWithCreator("bla");

        Visualization viz = new Visualization();
        viz.setType(visualizationDAO.findType(typeKey));
        project.addVisualization(viz);
        project = projectDAO.persist(project);
        viz = project.getVisualizations().iterator().next();

        try {
            testObject.find(viz.getId(), new User());
            fail("forbidden user should cause exception");
        } catch (CodedException e) {
            assertEquals(Error.FORBIDDEN, e.getErrorCode());
        }
    }

    @Test
    public void find_idNotPresent_exception() {
        try {
            testObject.find("someid", new User());
        } catch (CodedException e) {
            assertEquals(Error.RESOURCE_NOT_FOUND, e.getErrorCode());
        }
    }
}
