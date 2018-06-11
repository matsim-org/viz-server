package org.matsim.webvis.files.visualization;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.webvis.common.errorHandling.CodedException;
import org.matsim.webvis.common.errorHandling.Error;
import org.matsim.webvis.files.entities.*;
import org.matsim.webvis.files.permission.PermissionService;
import org.matsim.webvis.files.project.ProjectService;

import javax.persistence.PersistenceException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VisualizationService {

    private static final Logger logger = LogManager.getLogger();

    private ProjectService projectService = new ProjectService();
    private VisualizationDAO visualizationDAO = new VisualizationDAO();
    private PermissionService permissionService = PermissionService.Instance;

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

        VisualizationType type = findOrThrow(request.getTypeKey());
        Project project = projectService.findWithFullChildGraph(request.getProjectId(), user);

        Visualization viz = new Visualization();
        project.addVisualization(viz);
        viz.setType(type);
        viz.addPermission(permissionService.createServicePermission(viz));
        addInputFilesAndPersistPermissions(viz, project, request);
        addParameters(viz, request);

        try {
            return visualizationDAO.persist(viz);
        } catch (PersistenceException e) {
            logger.error("Could not perist", e);
            throw new CodedException(Error.RESOURCE_EXISTS, "Visualization already exists");
        }
    }

    Visualization find(String vizId, Agent user) {

        permissionService.findReadPermission(user, vizId);

        Visualization viz = visualizationDAO.find(vizId);
        validate(viz);
        return viz;
    }

    private void addInputFilesAndPersistPermissions(Visualization viz, Project project, CreateVisualizationRequest request) {

        List<Permission> addedPermissions = new ArrayList<>();

        for (Map.Entry<String, String> entry : request.getInputFiles().entrySet()) {
            FileEntry file = project.getFileEntry(entry.getValue());
            Permission permission = permissionService.createServicePermission(file);
            if (file.addPermission(permission))
                addedPermissions.add(permission);

            viz.addInput(new VisualizationInput(entry.getKey(), file, viz));
        }

        /*request.getInputFiles().entrySet().stream().forEach(entry -> {
            FileEntry file = project.getFileEntry(entry.getValue());
            Permission permission = permissionService.createServicePermission(file);
            file.addPermission(permission);
            addedPermissions.add(permission);
            VisualizationInput input = new VisualizationInput(entry.getKey(), file, viz);
            viz.addInput(input);
        });*/

        //the permissions for input files must be persisted here, since they can't be persisted by hibernate's cascade
        //mechanism
        permissionService.persist(addedPermissions);
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
