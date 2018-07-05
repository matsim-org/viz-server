package org.matsim.webvis.files.file;

public class FileDownloadRequestHandlerTest {

    /*
    private FileDownloadRequestHandler testObject;

    @Before
    public void setUp() {
        testObject = new FileDownloadRequestHandler();
    }

    @After
    public void tearDown() {
        TestUtils.removeAllEntities();
    }

    @Test(expected = InvalidInputException.class)
    public void handle_wrongContentType_invalidRequest() {
        Request req = mock(Request.class);
        when(req.contentType()).thenReturn("something-wrong");

        Response res = mock(Response.class);

        testObject.handle(req, res);

        fail("invalid input should yield invalid input exception.");
    }

    @Test(expected = InvalidInputException.class)
    public void handle_parsingError_invalidRequest() {
        Request req = mock(Request.class);
        when(req.contentType()).thenReturn(ContentType.APPLICATION_JSON);
        when(req.body()).thenReturn("{not: json");

        Response res = mock(Response.class);

        testObject.handle(req, res);

        fail("invalid input should yield invalid input exception.");
    }

    @Test
    public void handle_fileNotFound_codedException() {

        Project project = new Project();
        testObject.projectService = mock(ProjectService.class);
        when(testObject.projectService.getFileStream(any(), any(), any())).thenThrow(new CodedException("e", "error"));
        when(testObject.projectService.find(any(), any())).thenReturn(project);

        User user = TestUtils.persistUser("id");
        Request req = TestUtils.mockRequestWithRawRequest("POST", ContentType.APPLICATION_JSON);
        AuthenticationResult authResult = TestUtils.mockAuthResult("user", user.getAuthId());
        when(req.attribute(anyString())).thenReturn(authResult);

        FileRequest body = new FileRequest("id", "pid");
        when(req.body()).thenReturn(new Gson().toJson(body));

        Response res = mock(Response.class);

        try {
            testObject.handle(req, res);
            fail("file not found should yield exception");
        } catch (CodedException e) {
            assertEquals(Error.RESOURCE_NOT_FOUND, e.getErrorCode());
        }
    }

    @Test(expected = CodedException.class)
    public void handle_errorWhileCreatingInputStream_internalError() {

        testObject.projectService = mock(ProjectService.class);
        when(testObject.projectService.getFileStream(any(), any(), any())).thenThrow(new CodedException("e", "error"));

        User user = TestUtils.persistUser("id");
        Request req = TestUtils.mockRequestWithRawRequest("POST", ContentType.APPLICATION_JSON);
        AuthenticationResult authResult = TestUtils.mockAuthResult("user", user.getAuthId());
        when(req.attribute(anyString())).thenReturn(authResult);

        FileRequest body = new FileRequest("id", "pid");
        when(req.body()).thenReturn(new Gson().toJson(body));

        FileEntry entry = new FileEntry();
        entry.setId(body.getFileId());
        Project project = new Project();
        project.setId(body.getProjectId());
        project.getFiles().add(entry);
        when(testObject.projectService.find(any(), any())).thenReturn(project);

        Response res = mock(Response.class);

        testObject.handle(req, res);

        verify(res).status(HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void handle_allRight_outputStream() throws Exception {

        InputStream inStream = new InputStream() {
            @Override
            public int read() {
                return -1;
            }
        };
        testObject.projectService = mock(ProjectService.class);
        when(testObject.projectService.getFileStream(any(), any(), any())).thenReturn(inStream);

        User user = TestUtils.persistUser("id");
        Request req = TestUtils.mockRequestWithRawRequest("POST", ContentType.APPLICATION_JSON);
        AuthenticationResult authResult = TestUtils.mockAuthResult("user", user.getAuthId());
        when(req.attribute(anyString())).thenReturn(authResult);

        FileRequest body = new FileRequest("id", "pId");
        when(req.body()).thenReturn(new Gson().toJson(body));

        FileEntry entry = new FileEntry();
        entry.setId(body.getFileId());
        Project project = new Project();
        project.setId(body.getProjectId());
        project.getFiles().add(entry);
        when(testObject.projectService.find(any(), any())).thenReturn(project);

        HttpServletResponse rawResponse = mock(HttpServletResponse.class);
        when(rawResponse.getOutputStream()).thenReturn(new ServletOutputStream() {
            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setWriteListener(WriteListener writeListener) {

            }

            @Override
            public void write(int b) {

            }
        });
        Response res = mock(Response.class);
        when(res.raw()).thenReturn(rawResponse);

        Object result = testObject.handle(req, res);

        assertTrue(result instanceof Integer);
        assertEquals(HttpStatus.SC_OK, (int) result);
    }
    */
}
