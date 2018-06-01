package org.matsim.webvis.files.visualization;

import org.apache.commons.lang3.StringUtils;
import org.matsim.webvis.common.communication.Answer;
import org.matsim.webvis.common.service.InvalidInputException;
import org.matsim.webvis.files.communication.AuthenticatedJsonRequestHandler;
import org.matsim.webvis.files.communication.GsonFactory;
import org.matsim.webvis.files.entities.Visualization;
import org.matsim.webvis.files.permission.Subject;

public class VisualizationRequestHandler extends AuthenticatedJsonRequestHandler<VisualizationRequest> {

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
}
