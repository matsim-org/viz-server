package org.matsim.webvis.files.visualization;

import org.matsim.webvis.files.communication.AuthenticatedJsonRequestHandler;

public class CreateVisualizationRequestHandler extends AuthenticatedJsonRequestHandler<CreateVisualizationRequest> {

    /*
    VisualizationService visualizationService = new VisualizationService();

    public CreateVisualizationRequestHandler() {
        super(CreateVisualizationRequest.class, GsonFactory.createParserWithExclusionStrategy());
    }

    @Override
    protected Answer process(CreateVisualizationRequest body, Subject subject) {

        if (!isValidRequest(body))
            throw new InvalidInputException("some parameters were not set");

        Visualization visualization = visualizationService.createVisualizationFromRequest(body, subject.getAgent());
        return Answer.ok(visualization);
    }

    private boolean isValidRequest(CreateVisualizationRequest body) {
        return StringUtils.isNotBlank(body.getProjectId()) &&
                StringUtils.isNotBlank(body.getTypeKey()) &&
                body.getInputParameters() != null &&
                body.getInputFiles() != null &&
                body.getInputFiles().size() > 0;
    }
    */
}
