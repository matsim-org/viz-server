package org.matsim.webvis.files.visualization;

import org.apache.commons.lang3.StringUtils;
import org.matsim.webvis.common.communication.Answer;
import org.matsim.webvis.common.service.InvalidInputException;
import org.matsim.webvis.files.communication.AuthenticatedJsonRequestHandler;
import org.matsim.webvis.files.communication.GsonFactory;
import org.matsim.webvis.files.communication.Subject;
import org.matsim.webvis.files.entities.Visualization;

public class CreateVisualizationRequestHandler extends AuthenticatedJsonRequestHandler<CreateVisualizationRequest> {

    VisualizationService visualizationService = new VisualizationService();

    public CreateVisualizationRequestHandler() {
        super(CreateVisualizationRequest.class, GsonFactory.createParserWithExclusionStrategy());
    }

    @Override
    protected Answer process(CreateVisualizationRequest body, Subject subject) {

        if (!isValidRequest(body))
            throw new InvalidInputException("some parameters were not set");

        Visualization visualization = visualizationService.createVisualizationFromRequest(body, subject.getUser());
        return Answer.ok(visualization);
    }

    private boolean isValidRequest(CreateVisualizationRequest body) {
        return StringUtils.isNotBlank(body.getProjectId()) &&
                StringUtils.isNotBlank(body.getTypeKey()) &&
                body.getInputParameters() != null &&
                body.getInputFiles() != null &&
                body.getInputFiles().size() > 0;
    }
}
