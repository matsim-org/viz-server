package org.matsim.viz.files.project;

import lombok.val;
import org.junit.*;
import org.matsim.viz.error.CodedException;
import org.matsim.viz.error.ForbiddenException;
import org.matsim.viz.error.InternalException;
import org.matsim.viz.error.InvalidInputException;
import org.matsim.viz.files.entities.*;
import org.matsim.viz.files.file.FileDownload;
import org.matsim.viz.files.file.FileUpload;
import org.matsim.viz.files.file.LocalRepository;
import org.matsim.viz.files.file.Repository;
import org.matsim.viz.files.notifications.Notifier;
import org.matsim.viz.files.util.TestUtils;

import java.io.InputStream;
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

        Project project = TestUtils.persistProjectWithCreator("test name");
        FileEntry fileEntry = new FileEntry();
        fileEntry.setUserFileName("bla.txt");
        fileEntry.setPersistedFileName("blup.txt");
        project.addFileEntry(fileEntry);
        Visualization viz = new Visualization();
        viz.setType("some-type");
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
    public void addFileToProject() {

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
        final String tagName = "some-tag-name";

        final String secondName = "second.txt";
        val upload1 = new FileUpload("first.txt", "plain/text", mock(InputStream.class), new String[]{tagName});
        val upload2 = new FileUpload(secondName, "plain/text", mock(InputStream.class), new String[]{tagName});

        testObject.addFileToProject(upload1, project.getId(), project.getCreator());
        FileEntry result = testObject.addFileToProject(upload2, project.getId(), project.getCreator());

        assertEquals(secondName, result.getUserFileName());
        assertNotNull(result.getId());
    }

    @Test(expected = InternalException.class)
    public void addFileToProject_duplicateFileName_exception() {

        LocalRepository repository = mock(LocalRepository.class);
        doAnswer(args -> {
            FileUpload upload = args.getArgument(0);
            FileEntry result = new FileEntry();
            result.setPersistedFileName(upload.getFileName());
            result.setUserFileName(upload.getFileName());
            return result;
        }).when(repository).addFile(any(FileUpload.class));
        //when(repository.removeFile(any())).thenReturn(Void);
        testObject = new ProjectService(new ProjectDAO(TestUtils.getPersistenceUnit()),
                TestUtils.getPermissionService(), repository, mock(Notifier.class));
        Project project = TestUtils.persistProjectWithCreator("test");
        final String tagName = "some-tag-name";

        final String fileName = "some-name.txt";
        val upload1 = new FileUpload(fileName, "plain/text", mock(InputStream.class), new String[]{tagName});
        val upload2 = new FileUpload(fileName, "plain/text", mock(InputStream.class), new String[]{tagName});

        testObject.addFileToProject(upload1, project.getId(), project.getCreator());
        FileEntry result = testObject.addFileToProject(upload2, project.getId(), project.getCreator());

        fail("duplicate file entry should cause exception");
    }

    @Test(expected = ForbiddenException.class)
    public void addFileToProject_noPermission_exception() {

        User user = TestUtils.persistUser("some-id");
        Project project = TestUtils.persistProjectWithCreator("project", "auth-id");

        testObject.addFileToProject(null, project.getId(), user);

        fail("user without permission should raise forbidden exception");
    }

    @Test
    public void addFileToProject_errorWhilePersisting_cleanupFiles() {

        Project project = TestUtils.persistProjectWithCreator("test");
        val upload1 = new FileUpload("same-name.txt", "plain/text", mock(InputStream.class), new String[0]);
        val upload2 = new FileUpload("same-name.txt", "plain/text", mock(InputStream.class), new String[0]);

        LocalRepository repository = spy(new LocalRepository("some-directory"));
        doReturn(new FileEntry()).when(repository).addFile(any(FileUpload.class));
        doNothing().when(repository).removeFile(any(FileEntry.class));
        ProjectDAO mockedDao = mock(ProjectDAO.class);
        when(mockedDao.findWithFullGraph(anyString())).thenReturn(project);
        when(mockedDao.persist(any(Project.class))).thenThrow(new RuntimeException("persisting error"));

        testObject = new ProjectService(mockedDao, TestUtils.getPermissionService(), repository, mock(Notifier.class));
        try {
            testObject.addFileToProject(upload1, project.getId(), project.getCreator());
            testObject.addFileToProject(upload2, project.getId(), project.getCreator());
            fail("exception while persisting project should raise exception and delete written files");
        } catch (RuntimeException e) {
            verify(repository).removeFile(any());
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

    @Test(expected = InternalException.class)
    public void removeFile_fileUsedByVisualization_exception() {

        Project project = TestUtils.persistProjectWithCreator("test");
        project = addFileEntry(project);
        FileEntry entry = project.getFiles().iterator().next();

        Visualization visualization = new Visualization();
        VisualizationInput input = new VisualizationInput();
        input.setFileEntry(entry);
        input.setInputKey("test-input");
        visualization.addInput(input);
        project.addVisualization(visualization);
        TestUtils.getProjectDAO().persist(project);

        LocalRepository repository = mock(LocalRepository.class);
        doNothing().when(repository).removeFile(any());

        testObject = new ProjectService(
                new ProjectDAO(TestUtils.getPersistenceUnit()),
                TestUtils.getPermissionService(),
                repository, mock(Notifier.class)
        );

        testObject.removeFileFromProject(project.getId(), entry.getId(), project.getCreator());
    }

    @Test
    public void removeFile_fileIsRemoved() {
        Project project = TestUtils.persistProjectWithCreator("test");
        project = addFileEntry(project);

        // ensure file is added to project
        assertEquals(1, project.getFiles().size());
        FileEntry entry = project.getFiles().iterator().next();

        LocalRepository repository = mock(LocalRepository.class);
        doNothing().when(repository).removeFile(any());

        testObject = new ProjectService(
                new ProjectDAO(TestUtils.getPersistenceUnit()),
                TestUtils.getPermissionService(),
                repository, mock(Notifier.class)
        );

        testObject.removeFileFromProject(project.getId(), entry.getId(), project.getCreator());

        Project updatedProject = TestUtils.getProjectDAO().find(project.getId());
        assertEquals(0, updatedProject.getFiles().size());
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

        Visualization visualization = new Visualization();
        visualization.setType("some-type");
        project.addVisualization(visualization);
        TestUtils.getProjectDAO().persist(project);

        User otherUser = TestUtils.getAgentService().createUser("some-other-auth-id");
        Permission.Type permissionType = Permission.Type.Owner;

        Permission result = testObject.addPermission(project.getId(), otherUser, permissionType, project.getCreator());

        assertEquals(otherUser, result.getAgent());
        assertEquals(permissionType, result.getType());
        assertEquals(project, result.getResource());

        Project updatedProject = TestUtils.getProjectDAO().find(project.getId());

        TestUtils.getPermissionService().findOwnerPermission(otherUser, updatedProject.getId());
        updatedProject.getFiles().forEach(file -> TestUtils.getPermissionService().findOwnerPermission(otherUser, file.getId()));
        updatedProject.getVisualizations().forEach(viz -> TestUtils.getPermissionService().findOwnerPermission(otherUser, viz.getId()));
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

    @Test(expected = CodedException.class)
    public void addTag_duplicateTagName_exception() {

        final Project project = TestUtils.persistProjectWithCreator("some-name");
        final String tagName = "some-tag";

        testObject.addTag(project.getId(), tagName, "some-type", project.getCreator());
        testObject.addTag(project.getId(), tagName, "some-type", project.getCreator());

        fail("second addTag should cause exception");
    }

    @Test(expected = ForbiddenException.class)
    public void addTag_noPermission_exception() {

        final Project project = TestUtils.persistProjectWithCreator("some-name");
        final String tagName = "some-tag";
        final User otherUser = TestUtils.persistUser("other-user");

        testObject.addTag(project.getId(), tagName, "some-type", otherUser);

        fail("unauthorized user should cause exception");
    }

    @Test
    public void addTag_tagIsAdded() {

        final Project project = TestUtils.persistProjectWithCreator("some-name");
        final String tagName = "tag-name";

        Tag persisted = testObject.addTag(project.getId(), tagName, "some-type", project.getCreator());

        assertEquals(tagName, persisted.getName());
        assertNotNull(persisted.getId());
        assertEquals(project, persisted.getProject());
    }

    @Test(expected = ForbiddenException.class)
    public void removeTag_noPermission_exception() {

        Project project = TestUtils.persistProjectWithCreator("some-name");
        final User otherUser = TestUtils.persistUser("other-user");
        final String tagName = "tag-name";

        Tag persisted = testObject.addTag(project.getId(), tagName, "some-type", project.getCreator());

        testObject.removeTag(project.getId(), persisted.getId(), otherUser);

        fail("unauthorized user should cause exception");
    }

    @Test(expected = InvalidInputException.class)
    public void removeTag_noSuchTag_exception() {

        Project project = TestUtils.persistProjectWithCreator("some-name");
        final String tagName = "tag-name";

        testObject.addTag(project.getId(), tagName, "some-type", project.getCreator());

        testObject.removeTag(project.getId(), "some-other-tag-id", project.getCreator());

        fail("invalid tag id should cause exception");
    }

    @Test
    public void removeTag_tagIsRemoved() {

        Project project = TestUtils.persistProjectWithCreator("some-name");
        final String firstTagName = "first-tag-name";
        final String secondTagName = "second-tag-name";

        Tag firstTag = testObject.addTag(project.getId(), firstTagName, "some-type", project.getCreator());
        testObject.addTag(project.getId(), secondTagName, "some-type", project.getCreator());

        project = testObject.removeTag(project.getId(), firstTag.getId(), project.getCreator());

        assertEquals(1, project.getTags().size());
        assertTrue(project.getTags().stream().noneMatch(tag -> tag.getName().equals(firstTagName)));
    }

    private Project addFileEntry(Project project) {
        FileEntry entry = new FileEntry();
        entry.setUserFileName("some-user-filename.txt");
        entry.setPersistedFileName("some-persisted-filename.txt");
        project.addFileEntry(entry);
        return TestUtils.getProjectDAO().persist(project);
    }
}
