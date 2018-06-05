package org.matsim.webvis.files.visualization;

import org.apache.commons.lang3.StringUtils;
import org.matsim.webvis.common.communication.Answer;
import org.matsim.webvis.common.errorHandling.InvalidInputException;
import org.matsim.webvis.files.communication.AuthenticatedJsonRequestHandler;
import org.matsim.webvis.files.communication.GsonFactory;
import org.matsim.webvis.files.entities.Visualization;
import org.matsim.webvis.files.permission.Subject;

import java.util.List;

public class VisualizationsRequestHandler extends AuthenticatedJsonRequestHandler<VisualizationsRequest> {

    private VisualizationService visualizationService = new VisualizationService();

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
}
