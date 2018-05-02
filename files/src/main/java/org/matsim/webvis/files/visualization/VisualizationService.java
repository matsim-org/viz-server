package org.matsim.webvis.files.visualization;

import org.matsim.webvis.common.service.CodedException;
import org.matsim.webvis.common.service.Error;
import org.matsim.webvis.files.entities.*;
import org.matsim.webvis.files.project.ProjectService;

import javax.persistence.PersistenceException;

class VisualizationService {

    private ProjectService projectService = new ProjectService();
    private VisualizationDAO visualizationDAO = new VisualizationDAO();

    private static void addInput(Visualization viz, Project project, CreateVisualizationRequest request) {
        request.getInputFiles().forEach((key, value) -> {
            FileEntry file = project.getFileEntry(value);
            VisualizationInput input = new VisualizationInput(key, file, viz);
            viz.addInput(input);
        });
    }

    private static void addParameters(Visualization viz, CreateVisualizationRequest request) {
        request.getInputParameters().forEach((key, value) -> {
            VisualizationParameter parameter = new VisualizationParameter(key, value);
            viz.addParameter(parameter);
        });
    }

    private static void validate(Visualization viz, User user) throws CodedException {
        if (viz == null) throw new CodedException(Error.RESOURCE_NOT_FOUND, "could not find visualization");
        if (!viz.getProject().getCreator().getId().equals(user.getId()))
            throw new CodedException(Error.FORBIDDEN, "user is not allowed to access visualization");
    }

    Visualization createVisualizationFromRequest(CreateVisualizationRequest request, User user) throws CodedException {

        Project project = projectService.find(request.getProjectId(), user);
        VisualizationType type = findOrThrow(request.getTypeKey());

        Visualization viz = new Visualization();
        viz.setProject(project);
        viz.setType(type);
        addInput(viz, project, request);
        addParameters(viz, request);

        try {
            return visualizationDAO.persist(viz);
        } catch (PersistenceException e) {
            throw new CodedException(Error.RESOURCE_EXISTS, "visualization already exists");
        }
    }

    Visualization find(String vizId, User user) throws CodedException {

        Visualization viz = visualizationDAO.find(vizId);
        validate(viz, user);
        return viz;
    }

    private VisualizationType findOrThrow(String visualizationType) throws CodedException {
        VisualizationType type = visualizationDAO.findType(visualizationType);
        if (type == null) throw new CodedException(Error.RESOURCE_NOT_FOUND, "could not find supplied type");
        return type;
    }

    private void throwIfNotAllowed(String vizId, String projectId, User user) throws CodedException {
        Project project = projectService.find(projectId, user);
        if (project.getVisualizations().stream().noneMatch(v -> v.getId().equals(vizId)))
            throw new CodedException(Error.FORBIDDEN, "wrong project id or user not allowed or visualization not part of project");

    }

}