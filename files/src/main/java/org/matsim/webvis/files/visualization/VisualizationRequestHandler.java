package org.matsim.webvis.files.visualization;

import org.apache.commons.lang3.StringUtils;
import org.matsim.webvis.common.communication.Answer;
import org.matsim.webvis.common.communication.RequestError;
import org.matsim.webvis.common.service.CodedException;
import org.matsim.webvis.files.communication.AuthenticatedJsonRequestHandler;
import org.matsim.webvis.files.communication.GsonFactory;
import org.matsim.webvis.files.communication.Subject;
import org.matsim.webvis.files.entities.Visualization;

public class VisualizationRequestHandler extends AuthenticatedJsonRequestHandler<VisualizationRequest> {

    VisualizationService visualizationService = new VisualizationService();

    public VisualizationRequestHandler() {
        super(VisualizationRequest.class, GsonFactory.createParserWithExclusionStrategy());
    }

    @Override
    protected Answer process(VisualizationRequest body, Subject subject) {

        if (!isValidRequest(body)) return Answer.badRequest(RequestError.INVALID_REQUEST, "parameters missing");

        try {
            Visualization viz = visualizationService.find(body.getVisualizationId(), subject.getUser());
            return Answer.ok(viz);
        } catch (CodedException e) {
            return Answer.badRequest(RequestError.INVALID_REQUEST, "user not allowed or project- or visualization id invalid");
        }
    }

    private boolean isValidRequest(VisualizationRequest body) {
        return StringUtils.isNotBlank(body.getVisualizationId());
    }
}
