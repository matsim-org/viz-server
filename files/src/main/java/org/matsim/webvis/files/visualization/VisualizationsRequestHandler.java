package org.matsim.webvis.files.visualization;

import org.matsim.webvis.files.communication.AuthenticatedJsonRequestHandler;

public class VisualizationsRequestHandler extends AuthenticatedJsonRequestHandler<VisualizationsRequest> {

   /* private VisualizationService visualizationService = new VisualizationService();

    public VisualizationsRequestHandler() {
        super(VisualizationsRequest.class, GsonFactory.createParserWithExclusionStrategy());
    }

    @Override
    protected Answer process(VisualizationsRequest body, Subject subject) {

        if (!isValidRequest(body)) throw new InvalidInputException("parameter visualizationType is missing");

        List<Visualization> result = visualizationService.findByType(body.getVisualizationType(), subject.getAgent());
        return Answer.ok(result);
    }

    private boolean isValidRequest(VisualizationsRequest body) {
        return StringUtils.isNotBlank(body.getVisualizationType());
    }
    */
}
