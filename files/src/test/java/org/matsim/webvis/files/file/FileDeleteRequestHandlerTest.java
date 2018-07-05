package org.matsim.webvis.files.file;

public class FileDeleteRequestHandlerTest {
    /*

    private FileDeleteRequestHandler testObject;

    @Before
    public void setUp() {
        testObject = new FileDeleteRequestHandler();
        testObject.projectService = mock(ProjectService.class);
    }

    @Test
    public void process_projectIdMising_badRequest() {

        FileRequest request = new FileRequest("id", "");
        Subject subject = TestUtils.createSubject(new User());

        Answer answer = testObject.process(request, subject);

        assertEquals(HttpStatus.SC_BAD_REQUEST, answer.getStatusCode());
    }

    @Test
    public void process_fileIdMissing_badRequest() {

        FileRequest request = new FileRequest("", "id");
        Subject subject = TestUtils.createSubject(new User());

        Answer answer = testObject.process(request, subject);

        assertEquals(HttpStatus.SC_BAD_REQUEST, answer.getStatusCode());
    }

    @Test(expected = CodedException.class)
    public void process_removeInServiceThrowsException_internalError() throws CodedException {

        FileRequest request = new FileRequest("id", "id");
        Subject subject = TestUtils.createSubject(new User());
        when(testObject.projectService.removeFileFromProject(any(), any(), any())).thenThrow(new CodedException("error", "error"));

        Answer answer = testObject.process(request, subject);

        fail("failing service call should result in exception");
    }

    @Test
    public void process_allGood_ok() throws CodedException {

        FileRequest request = new FileRequest("id", "id");
        Subject subject = TestUtils.createSubject(new User());
        Project result = new Project();
        when(testObject.projectService.removeFileFromProject(any(), any(), any())).thenReturn(result);

        Answer answer = testObject.process(request, subject);

        assertEquals(HttpStatus.SC_OK, answer.getStatusCode());
        assertEquals(result, answer.getResponse());
    }
    */
}
