package org.matsim.webvis.files.visualization;

public class VisualizationRequestHandlerTest {

    /*
    private VisualizationRequestHandler testObject;

    @Before
    public void setUp() {
        testObject = new VisualizationRequestHandler();
    }

    @Test(expected = InvalidInputException.class)
    public void process_vizIdMissing_badRequest() {
        VisualizationRequest request = new VisualizationRequest("");

        testObject.process(request, TestUtils.createSubject(new User()));

        fail("invalid input should yield exception");
    }

    @Test(expected = CodedException.class)
    public void process_serviceThrowsException_badRequest() throws CodedException {

        VisualizationRequest request = new VisualizationRequest("id");
        testObject.visualizationService = mock(VisualizationService.class);
        when(testObject.visualizationService.find(anyString(), any())).thenThrow(new CodedException("bla", "bla"));

        testObject.process(request, TestUtils.createSubject(new User()));

        fail("exception in service should be passed upstream");
    }

    @Test
    public void process_allGood_answerOk() throws CodedException {

        VisualizationRequest request = new VisualizationRequest("id");
        Visualization result = new Visualization();
        testObject.visualizationService = mock(VisualizationService.class);
        when(testObject.visualizationService.find(anyString(), any())).thenReturn(result);

        Answer answer = testObject.process(request, TestUtils.createSubject(new User()));

        assertEquals(HttpStatus.SC_OK, answer.getStatusCode());
        assertEquals(result, answer.getResponse());
    }
    */
}
