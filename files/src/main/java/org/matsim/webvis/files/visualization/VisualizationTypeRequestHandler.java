package org.matsim.webvis.files.visualization;

import org.matsim.webvis.common.communication.Answer;
import org.matsim.webvis.files.communication.AuthenticatedJsonRequestHandler;
import org.matsim.webvis.files.communication.GsonFactory;
import org.matsim.webvis.files.permission.Subject;

public class VisualizationTypeRequestHandler extends AuthenticatedJsonRequestHandler<Object> {

    private VisualizationService visualizationService = new VisualizationService();

    public VisualizationTypeRequestHandler() {
        super(Object.class, GsonFactory.createParserWithExclusionStrategy());
    }

    @Override
    protected Answer process(Object body, Subject subject) {
        return Answer.ok(visualizationService.findAllTypes());
    }
}
