package org.matsim.webvis.files.project;

import org.apache.commons.fileupload.FileItem;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.matsim.webvis.files.entities.FileEntry;
import org.matsim.webvis.files.entities.Project;
import org.matsim.webvis.files.entities.User;
import org.matsim.webvis.files.user.UserDAO;
import org.matsim.webvis.files.util.TestUtils;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class DiskProjectRepositoryTest {

    private Project project;
    private ProjectDAO projectDAO = new ProjectDAO();
    private UserDAO userDAO = new UserDAO();

    @BeforeClass
    public static void setUpFixture() throws UnsupportedEncodingException, FileNotFoundException {
        TestUtils.loadConfig();
    }

    @Before
    public void setUp() {
        User user = userDAO.persist(new User());
        Project toPersist = new Project();
        toPersist.setCreator(user);
        project = projectDAO.persist(toPersist);
    }

    @After
    public void tearDown() {
        userDAO.removeAllUser();
    }

    @Test
    public void constructor_object() throws Exception {

        DiskProjectRepository repository = new DiskProjectRepository(project);

        assertNotNull(repository);
    }

    @Test
    public void addFile() throws Exception {

        final String filename = "filename.file";
        final String contentType = "content-type";
        final long size = 1L;
        FileItem item = mock(FileItem.class);
        when(item.getName()).thenReturn(filename);
        when(item.getContentType()).thenReturn(contentType);
        when(item.getSize()).thenReturn(size);
        List<FileItem> items = new ArrayList<>();
        items.add(item);

        DiskProjectRepository repository = new DiskProjectRepository(project);

        List<FileEntry> entries = repository.addFiles(items);

        verify(item).write(any());
        assertEquals(1, entries.size());
        FileEntry entry = entries.get(0);
        assertEquals(filename, entry.getUserFileName());
        assertEquals(contentType, entry.getContentType());
        assertEquals(size, entry.getSizeInBytes());
        String diskFilename = entry.getPersistedFileName().split("\\.")[0];
        UUID uuid = UUID.fromString(diskFilename);
        assertNotNull(uuid);
    }

    private Project createProjectWithCreator() throws Exception {

        User user = new UserDAO().persist(new User());
        return new ProjectService().createNewProject("name", user.getId());
    }
}
