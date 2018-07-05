package org.matsim.webvis.files.file;

public class FileUploadRequestHandlerTest {

    /*
    private FileUploadRequestHandler testObject;
    private User subject;

    @Before
    public void setUp() {

        testObject = new FileUploadRequestHandler();
        subject = TestUtils.persistUser("id");
    }

    @After
    public void tearDown() {
        TestUtils.removeAllEntities();
    }

    @Test(expected = InvalidInputException.class)
    public void process_invalidRequest_invalidInputException() {

        Request request = TestUtils.mockRequestWithRawRequest("Wrong", "request");
        AuthenticationResult authResult = TestUtils.mockAuthResult("user", subject.getAuthId());
        when(request.attribute(anyString())).thenReturn(authResult);
        Response response = mock(Response.class);

        testObject.process(request, response);

        fail("invalid input should raise exception");
    }

    @Test(expected = CodedException.class)
    public void process_projectNotFound_codedException() {

        Request request = TestUtils.mockMultipartRequest();
        AuthenticationResult authResult = TestUtils.mockAuthResult("user", subject.getAuthId());
        when(request.attribute(anyString())).thenReturn(authResult);
        Response response = mock(Response.class);

        FileUploadRequest upload = mock(FileUploadRequest.class);
        testObject.requestFactory = mock(FileUploadRequestHandler.RequestFactory.class);
        when(testObject.requestFactory.createRequest(any())).thenReturn(upload);

        testObject.projectService = mock(ProjectService.class);
        when(testObject.projectService.find(any(), any())).thenThrow(new CodedException("code", "message"));

        Answer answer = testObject.process(request, response);

        assertEquals(HttpStatus.SC_FORBIDDEN, answer.getStatusCode());
        assertTrue(answer.getResponse() instanceof ErrorResponse);
    }

    @Test(expected = CodedException.class)
    public void process_addFilesToProjectFails_internalError() {

        Request request = TestUtils.mockMultipartRequest();
        AuthenticationResult authResult = TestUtils.mockAuthResult("user", subject.getAuthId());
        when(request.attribute(anyString())).thenReturn(authResult);
        Response response = mock(Response.class);

        FileUploadRequest upload = mock(FileUploadRequest.class);
        testObject.requestFactory = mock(FileUploadRequestHandler.RequestFactory.class);
        when(testObject.requestFactory.createRequest(any())).thenReturn(upload);

        testObject.projectService = mock(ProjectService.class);
        when(testObject.projectService.find(any(), any())).thenReturn(new Project());
        when(testObject.projectService.addFilesToProject(any(), any(), any())).thenThrow(new CodedException("bla", "bla"));

        Answer answer = testObject.process(request, response);

        assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, answer.getStatusCode());
        assertTrue(answer.getResponse() instanceof ErrorResponse);
    }

    @Test
    public void process_filesAreAdded_ok() {

        Request request = TestUtils.mockMultipartRequest();
        AuthenticationResult authResult = TestUtils.mockAuthResult("user", subject.getAuthId());
        when(request.attribute(anyString())).thenReturn(authResult);
        Response response = mock(Response.class);

        FileUploadRequest upload = mock(FileUploadRequest.class);
        testObject.requestFactory = mock(FileUploadRequestHandler.RequestFactory.class);
        when(testObject.requestFactory.createRequest(any())).thenReturn(upload);

        testObject.projectService = mock(ProjectService.class);
        when(testObject.projectService.find(any(), any())).thenReturn(new Project());
        when(testObject.projectService.addFilesToProject(any(), any(), any())).thenReturn(new Project());

        Answer answer = testObject.process(request, response);

        assertEquals(HttpStatus.SC_OK, answer.getStatusCode());
        assertTrue(answer.getResponse() instanceof Project);
    }
    */
}
