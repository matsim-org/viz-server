package org.matsim.viz.files.visualization;

import lombok.val;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.matsim.viz.error.CodedException;
import org.matsim.viz.error.ForbiddenException;
import org.matsim.viz.files.entities.*;
import org.matsim.viz.files.notifications.Notifier;
import org.matsim.viz.files.util.TestUtils;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static junit.framework.TestCase.*;
import static org.mockito.Mockito.mock;

public class VisualizationServiceTest {

    private static String typeKey = "test-key";
    private static VisualizationDAO visualizationDAO = new VisualizationDAO(TestUtils.getPersistenceUnit());
    private VisualizationService testObject;

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
    public void createVisualizationFromRequest_visualization() throws CodedException {

        Project project = TestUtils.persistProjectWithCreator("some-project");
        project = TestUtils.addFileEntry(project, "some-file.txt");
        project = TestUtils.addFileEntry(project, "some-other-file.txt");
        project = TestUtils.addTag(project, "tag-name", "tag-type");
        Tag tag = project.getTags().iterator().next();

        Map<String, String> input = new HashMap<>();
        input.put("network", project.getFiles().iterator().next().getId());
        Map<String, String> parameters = new HashMap<>();
        parameters.put("some", "parameter");
        val title = "some-title";
		Map<String, String> properties = new HashMap<>();
		properties.put("some-key", "some-value");
        val thumbnail = "base64encoded-thumbnail";

        CreateVisualizationRequest request = new CreateVisualizationRequest(
                project.getId(),
                typeKey,
                title,
                input,
                parameters,
                new String[]{tag.getId()},
				properties,
                thumbnail
        );

        Visualization viz = testObject.createVisualizationFromRequest(request, project.getCreator());

        assertNotNull(viz.getId());
        assertEquals(1, viz.getInputFiles().size());
        assertEquals(1, viz.getParameters().size());
        assertEquals(request.getTypeKey(), viz.getType());
        assertEquals(1, viz.getTags().size());
        assertTrue(viz.getTags().contains(tag));
        assertEquals(title, viz.getTitle());
		assertEquals(properties.get("some-key"), viz.getProperties().get("some-key"));
        assertEquals(thumbnail, viz.getThumbnail());
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
        project = TestUtils.addFileEntry(project, "some-file.txt");
        project = TestUtils.addFileEntry(project, "some-other-file.abc");

        FileEntry[] entries = project.getFiles().toArray(new FileEntry[0]);
        Map<String, String> input = new HashMap<>();
        input.put("network", entries[0].getId());
        input.put("other", entries[0].getId());
        Map<String, String> parameters = new HashMap<>();
        parameters.put("some", "parameter");

        CreateVisualizationRequest request = new CreateVisualizationRequest(
                project.getId(),
                typeKey,
                "some-title",
                input,
                parameters,
                new String[0],
				new HashMap<>(), "base64encoded-thumbnail");

        Visualization viz = testObject.createVisualizationFromRequest(request, project.getCreator());

        assertNotNull(viz.getId());
        assertEquals(2, viz.getInputFiles().size());
        assertEquals(1, viz.getParameters().size());
        assertEquals(request.getTypeKey(), viz.getType());

        Project finalProject = project;
        assertTrue(viz.getPermissions().stream().anyMatch(p -> p.getAgent().equals(finalProject.getCreator())));

        for (VisualizationInput vizInput : viz.getInputFiles().values()) {

            assertEquals(entries[0], vizInput.getFileEntry());
            assertEquals(1, vizInput.getFileEntry().getPermissions().size()); //should be one permission service access
        }
    }

    @Test(expected = ForbiddenException.class)
    public void removeVisualization_noPermission_exception() {

        testObject.removeVisualization("anyId", TestUtils.getAgentService().getPublicAgent());
    }

    @Test
    public void removeVisualization_success_ok() {
        Project project = TestUtils.persistProjectWithCreator("bla");

        Visualization viz = new Visualization();
        viz.setType(typeKey);
        project.addVisualization(viz);
        project = TestUtils.getProjectDAO().persist(project);
        viz = project.getVisualizations().iterator().next();

        testObject.removeVisualization(viz.getId(), project.getCreator());

        Visualization shouldBeDeleted = visualizationDAO.find(viz.getId());
        assertNull(shouldBeDeleted);

        Project withoutViz = TestUtils.getProjectService().find(project.getId(), project.getCreator());
        assertEquals(0, withoutViz.getVisualizations().size());
    }

    @Test
    public void find_allGood_visualization() throws CodedException {

        Project project = TestUtils.persistProjectWithCreator("bla");

        Visualization viz = new Visualization();
        viz.setType(typeKey);
        project.addVisualization(viz);
        project = TestUtils.getProjectDAO().persist(project);
        viz = project.getVisualizations().iterator().next();

        Visualization result = testObject.find(viz.getId(), project.getCreator());

        assertEquals(viz.getType(), result.getType());
        assertEquals(viz, result);
    }

    @Test(expected = ForbiddenException.class)
    public void find_userNotAllowed_exception() {
        Project project = TestUtils.persistProjectWithCreator("bla");

        Visualization viz = new Visualization();
        viz.setType(typeKey);
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

        Project project = TestUtils.persistProjectWithCreator("bla");
        Visualization viz = new Visualization();
        viz.setType(typeKey);
        project.addVisualization(viz);
        TestUtils.getProjectDAO().persist(project);

        List<Visualization> result = testObject.findByType("no-such-type", Instant.EPOCH, project.getCreator());

        assertEquals(0, result.size());
    }

    @Test
    public void findByType_agentDoesNotHavePermissionForViz_emtpyList() {

        Project project = TestUtils.persistProjectWithCreator("bla");
        Visualization viz = new Visualization();
        viz.setType(typeKey);
        project.addVisualization(viz);
        TestUtils.getProjectDAO().persist(project);

        User user = TestUtils.persistUser("id");

        List<Visualization> result = testObject.findByType(typeKey, Instant.EPOCH, user);

        assertEquals(0, result.size());
    }

    @Test
    public void findByType_allGood_listOfVisualizations() {

        Project project = TestUtils.persistProjectWithCreator("project");

        CreateVisualizationRequest create = new CreateVisualizationRequest(project.getId(), typeKey, "some-title",
                new HashMap<>(),
                new HashMap<>(), new String[0],
                null,
                "base64encoded-thumbnail");
        Visualization viz = testObject.createVisualizationFromRequest(create, project.getCreator());

        List<Visualization> result = testObject.findByType(typeKey, Instant.EPOCH, project.getCreator());

        assertEquals(1, result.size());
        Visualization resultViz = result.get(0);
        assertEquals(viz, resultViz);
    }

    @Test
    public void findByType_afterInstant_listOfVisualizations() throws InterruptedException {

        Project project = TestUtils.persistProjectWithCreator("first project");

        CreateVisualizationRequest create = new CreateVisualizationRequest(project.getId(), typeKey, "some-title",
                new HashMap<>(),
                new HashMap<>(), new String[0],
                null,
                "base64encoded-thumbnail");
        testObject.createVisualizationFromRequest(create, project.getCreator());

        Instant afterFirst = Instant.now();
        Thread.sleep(100);

        CreateVisualizationRequest secondCreate = new CreateVisualizationRequest(project.getId(), typeKey, "some-title",
                new HashMap<>(),
                new HashMap<>(), new String[0],
                null,
                "base64encoded-thumbnail");
        Visualization secondViz = testObject.createVisualizationFromRequest(secondCreate, project.getCreator());

        List<Visualization> result = testObject.findByType(typeKey, afterFirst, project.getCreator());

        assertEquals(1, result.size());
        Visualization resultViz = result.get(0);
        assertEquals(secondViz, resultViz);
    }

    @Test
    public void findAllForProject_listOfVisualizations() {

        Project project = TestUtils.persistProjectWithCreator("first-project");
        project = TestUtils.addFileEntry(project, "some-file.txt");
        project = addVisualization(project, project.getFiles().iterator().next(), "some-key");

        Project otherProject = new Project();
        otherProject.setName("second-project");
        otherProject.setCreator(project.getCreator());
        otherProject = TestUtils.getProjectDAO().persist(otherProject);
        otherProject = TestUtils.addFileEntry(otherProject, "some-other-file.txt");
        otherProject = addVisualization(otherProject, otherProject.getFiles().iterator().next(), "other-key");

        List<Visualization> result = testObject.findAllForProject(project.getId(), project.getCreator());

        assertEquals(project.getVisualizations().size(), result.size());
        final Set<Visualization> projectVisualizations = project.getVisualizations();
        result.forEach(viz -> assertTrue(projectVisualizations.stream().anyMatch(projectViz -> projectViz.equals(viz))));

        final Set<Visualization> otherProjectVisualizations = otherProject.getVisualizations();
        result.forEach(viz -> assertTrue(otherProjectVisualizations.stream().noneMatch(projectViz -> projectViz.equals(viz))));
    }

    private Project addVisualization(Project project, FileEntry entry, String inputKey) {
        Visualization visualization = new Visualization();
        VisualizationInput input = new VisualizationInput();
        input.setFileEntry(entry);
        input.setInputKey(inputKey);
        visualization.addInput(input);
        project.addVisualization(visualization);
        return TestUtils.getProjectDAO().persist(project);
    }
}
