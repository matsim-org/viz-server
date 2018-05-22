package org.matsim.webvis.files.visualization;

import org.apache.commons.lang3.StringUtils;
import org.matsim.webvis.common.communication.Answer;
import org.matsim.webvis.common.communication.RequestError;
import org.matsim.webvis.common.service.CodedException;
import org.matsim.webvis.files.communication.AuthenticatedJsonRequestHandler;
import org.matsim.webvis.files.communication.GsonFactory;
import org.matsim.webvis.files.communication.Subject;
import org.matsim.webvis.files.entities.Visualization;

import java.util.List;

public class VisualizationsRequestHandler extends AuthenticatedJsonRequestHandler<VisualizationsRequest> {

    VisualizationService visualizationService = new VisualizationService();

    public VisualizationsRequestHandler() {
        super(VisualizationsRequest.class, GsonFactory.createParserWithExclusionStrategy());
    }

    @Override
    protected Answer process(VisualizationsRequest body, Subject subject) {
        if (!isValidRequest(body)) return Answer.badRequest(RequestError.INVALID_REQUEST, "visualization type is missing.");

        /*try {
            List<Visualization> visualizations = visualizationService.find
        } catch (CodedException e) {
            return Answer.badRequest(e.getErrorCode(), e.getMessage());
        }*/
        return null;
    }

    private boolean isValidRequest(VisualizationsRequest body) {
        return StringUtils.isNotBlank(body.getVisualizationType());
    }
}
