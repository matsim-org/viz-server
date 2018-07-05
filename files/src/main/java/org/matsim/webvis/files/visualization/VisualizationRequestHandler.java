package org.matsim.webvis.files.visualization;

import org.matsim.webvis.files.communication.AuthenticatedJsonRequestHandler;

public class VisualizationRequestHandler extends AuthenticatedJsonRequestHandler<VisualizationRequest> {

    /*
    VisualizationService visualizationService = new VisualizationService();

    public VisualizationRequestHandler() {
        super(VisualizationRequest.class, GsonFactory.createParserWithExclusionStrategy());
    }

    @Override
    protected Answer process(VisualizationRequest body, Subject subject) {

        if (!isValidRequest(body)) throw new InvalidInputException("parameter 'visualizationId' is missing");

        Visualization viz = visualizationService.find(body.getVisualizationId(), subject.getAgent());
        return Answer.ok(viz);
    }

    private boolean isValidRequest(VisualizationRequest body) {
        return StringUtils.isNotBlank(body.getVisualizationId());
    }
    */
}
