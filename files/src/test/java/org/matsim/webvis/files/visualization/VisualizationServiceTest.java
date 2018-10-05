package org.matsim.webvis.files.visualization;

import org.junit.*;
import org.matsim.webvis.error.CodedException;
import org.matsim.webvis.error.Error;
import org.matsim.webvis.error.ForbiddenException;
import org.matsim.webvis.files.entities.*;
import org.matsim.webvis.files.notifications.Notifier;
import org.matsim.webvis.files.util.TestUtils;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.*;
import static org.mockito.Mockito.mock;

@SuppressWarnings("ConstantConditions")
public class VisualizationServiceTest {

    private static String typeKey = "test-key";
    private static VisualizationDAO visualizationDAO = new VisualizationDAO(TestUtils.getPersistenceUnit());
    private VisualizationService testObject;

    @BeforeClass
    public static void setUpFixture() {
        VisualizationType type = new VisualizationType(typeKey, false, null, null, null);
        visualizationDAO.persistType(type);
    }

    @AfterClass
    public static void tearDownFixture() {
        visualizationDAO.removeType(typeKey);
    }

    @Before
    public void setUp() {
        testObject = new VisualizationService(
                visualizationDAO,
                TestUtils.getProjectService(),
                TestUtils.getPermissionService(),
                mock(Notifier.class)
        );
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
            assertEquals(Error.INVALID_REQUEST, e.getInternalErrorCode());
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
        assertEquals(request.getTypeKey(), viz.getType().getTypeName());
        Project finalProject = project;
        assertTrue(viz.getPermissions().stream().anyMatch(p -> p.getAgent().equals(finalProject.getCreator())));

        for (VisualizationInput vizInput : viz.getInputFiles().values()) {

            Permission perm = TestUtils.getPermissionService().findReadPermission(
                    TestUtils.getAgentService().getServiceAgent(),
                    vizInput.getFileEntry().getId());
            assertEquals(Permission.Type.Read, perm.getType());
        }
    }

    @Test
    public void createVisualizationFromRequest_sameInputTwice_correctPermissions() {

        Project project = TestUtils.persistProjectWithCreator("some-project");
        project = TestUtils.addFileEntry(project);
        project = TestUtils.addFileEntry(project);

        FileEntry[] entries = project.getFiles().toArray(new FileEntry[0]);
        Map<String, String> input = new HashMap<>();
        input.put("network", entries[0].getId());
        input.put("other", entries[0].getId());
        Map<String, String> parameters = new HashMap<>();
        parameters.put("some", "parameter");

        CreateVisualizationRequest request = new CreateVisualizationRequest(
                project.getId(),
                typeKey,
                input,
                parameters);

        Visualization viz = testObject.createVisualizationFromRequest(request, project.getCreator());

        assertNotNull(viz.getId());
        assertEquals(2, viz.getInputFiles().size());
        assertEquals(1, viz.getParameters().size());
        assertEquals(request.getTypeKey(), viz.getType().getTypeName());

        Project finalProject = project;
        assertTrue(viz.getPermissions().stream().anyMatch(p -> p.getAgent().equals(finalProject.getCreator())));

        for (VisualizationInput vizInput : viz.getInputFiles().values()) {

            assertEquals(entries[0], vizInput.getFileEntry());
            assertEquals(1, vizInput.getFileEntry().getPermissions().size()); //should be one permission service access
        }
    }

    @Test
    public void find_allGood_visualization() throws CodedException {

        Project project = TestUtils.persistProjectWithCreator("bla");

        Visualization viz = new Visualization();
        viz.setType(visualizationDAO.findType(typeKey));
        project.addVisualization(viz);
        project = TestUtils.getProjectDAO().persist(project);
        viz = project.getVisualizations().iterator().next();

        Visualization result = testObject.find(viz.getId(), project.getCreator());

        assertEquals(viz.getType().getTypeName(), result.getType().getTypeName());
    }

    @Test(expected = ForbiddenException.class)
    public void find_userNotAllowed_exception() {
        Project project = TestUtils.persistProjectWithCreator("bla");

        Visualization viz = new Visualization();
        viz.setType(visualizationDAO.findType(typeKey));
        project.addVisualization(viz);
        project = TestUtils.getProjectDAO().persist(project);
        viz = project.getVisualizations().iterator().next();

        User otherUser = TestUtils.persistUser("other-id");

        testObject.find(viz.getId(), otherUser);
        fail("forbidden user should cause exception");
    }

    @Test(expected = ForbiddenException.class)
    public void find_idNotPresent_exception() {

        User user = TestUtils.persistUser("some-id");

        testObject.find("someid", user);

        fail("should throw forbidden exception");
    }

    @Test
    public void findByType_noSuchType_emtpyList() {

        User user = TestUtils.persistUser("id");
        String type = "some-type";

        List<Visualization> result = testObject.findByType(type, Instant.EPOCH, user);

        assertEquals(0, result.size());
    }

    @Test
    public void findByType_agentDoesNotHavePermissionForViz_emtpyList() {

        VisualizationType type = new VisualizationType();
        type.setTypeName("key");
        testObject.persistType(type);

        User user = TestUtils.persistUser("id");

        List<Visualization> result = testObject.findByType(type.getTypeName(), Instant.EPOCH, user);

        assertEquals(0, result.size());
    }

    @Test
    public void findByType_allGood_listOfVisualizations() {

        Project project = TestUtils.persistProjectWithCreator("project");

        CreateVisualizationRequest create = new CreateVisualizationRequest(project.getId(), typeKey, new HashMap<>(), new HashMap<>());
        Visualization viz = testObject.createVisualizationFromRequest(create, project.getCreator());

        List<Visualization> result = testObject.findByType(typeKey, Instant.EPOCH, project.getCreator());

        assertEquals(1, result.size());
        Visualization resultViz = result.get(0);
        assertEquals(viz, resultViz);
    }

    @Test
    public void findByType_afterInstant_listOfVisualizations() {

        Project project = TestUtils.persistProjectWithCreator("first project");

        CreateVisualizationRequest create = new CreateVisualizationRequest(project.getId(), typeKey, new HashMap<>(), new HashMap<>());
        Visualization viz = testObject.createVisualizationFromRequest(create, project.getCreator());

        Instant afterFirst = Instant.now();

        CreateVisualizationRequest secondCreate = new CreateVisualizationRequest(project.getId(), typeKey, new HashMap<>(), new HashMap<>());
        Visualization secondViz = testObject.createVisualizationFromRequest(secondCreate, project.getCreator());

        List<Visualization> result = testObject.findByType(typeKey, afterFirst, project.getCreator());

        assertEquals(1, result.size());
        Visualization resultViz = result.get(0);
        assertEquals(secondViz, resultViz);
    }
}
