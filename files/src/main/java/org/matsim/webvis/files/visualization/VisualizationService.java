package org.matsim.webvis.files.visualization;

import org.matsim.webvis.common.service.CodedException;
import org.matsim.webvis.common.service.Error;
import org.matsim.webvis.common.service.ForbiddenException;
import org.matsim.webvis.files.entities.*;
import org.matsim.webvis.files.permission.PermissionService;
import org.matsim.webvis.files.project.ProjectService;

import javax.persistence.PersistenceException;
import java.security.Permissions;
import java.util.List;

public class VisualizationService {

    private ProjectService projectService = new ProjectService();
    private VisualizationDAO visualizationDAO = new VisualizationDAO();
    private PermissionService permissionService = new PermissionService();

    public VisualizationType persistType(VisualizationType type) {
        return visualizationDAO.persistType(type);
    }

    List<VisualizationType> findAllTypes() {
        return visualizationDAO.findAllTypes();
    }

    private VisualizationType findOrThrow(String visualizationType) throws CodedException {
        VisualizationType type = visualizationDAO.findType(visualizationType);
        if (type == null)
            throw new CodedException(Error.RESOURCE_NOT_FOUND, "Could not find visualization type: " + visualizationType);
        return type;
    }

    private static void validate(Visualization viz) throws CodedException {
        if (viz == null) throw new CodedException(Error.RESOURCE_NOT_FOUND, "could not find visualization");
    }

    Visualization createVisualizationFromRequest(CreateVisualizationRequest request, Agent user) {

        permissionService.findWritePermission(user, request.getProjectId());

        Project project = projectService.find(request.getProjectId(), user);
        VisualizationType type = findOrThrow(request.getTypeKey());


        Visualization viz = new Visualization();
        project.addVisualization(viz);
        viz.setType(type);
        viz.addPermission(PermissionService.createServicePermission(viz));
        addInput(viz, project, request);
        addParameters(viz, request);

        try {
            return visualizationDAO.persist(viz);
        } catch (PersistenceException e) {
            throw new CodedException(Error.RESOURCE_EXISTS, "Visualization already exists");
        }
    }

    Visualization find(String vizId, Agent user) {

        permissionService.findReadPermission(user, vizId);

        Visualization viz = visualizationDAO.find(vizId);
        validate(viz);
        return viz;
    }

    private static void addInput(Visualization viz, Project project, CreateVisualizationRequest request) {

        request.getInputFiles().forEach((key, value) -> {
            FileEntry file = project.getFileEntry(value);
            file.addPermission(PermissionService.createServicePermission(file));
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

    List<Visualization> findByType(String vizType, Agent agent) {
        return visualizationDAO.findAllByTypeIfHasPermission(vizType, agent);
    }
}
