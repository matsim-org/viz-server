package org.matsim.viz.files.visualization;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.matsim.viz.error.CodedException;
import org.matsim.viz.error.Error;
import org.matsim.viz.error.InternalException;
import org.matsim.viz.error.InvalidInputException;
import org.matsim.viz.files.entities.*;
import org.matsim.viz.files.notifications.Notification;
import org.matsim.viz.files.notifications.NotificationType;
import org.matsim.viz.files.notifications.Notifier;
import org.matsim.viz.files.permission.PermissionService;
import org.matsim.viz.files.project.ProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.PersistenceException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VisualizationService {

    private static final Logger logger = LoggerFactory.getLogger(VisualizationService.class);

    private final ProjectService projectService;
    private final VisualizationDAO visualizationDAO;
    private final PermissionService permissionService;
    private final Notifier notifier;

    public VisualizationService(VisualizationDAO visualizationDAO, ProjectService projectService, PermissionService permissionService,
                                Notifier notifier) {
        this.visualizationDAO = visualizationDAO;
        this.projectService = projectService;
        this.permissionService = permissionService;
        this.notifier = notifier;
        this.createNotificationTypes();
    }

    private static void validate(Visualization viz) throws CodedException {
        if (viz == null) throw new InvalidInputException("could not find visualization");
    }

    Visualization createVisualizationFromRequest(CreateVisualizationRequest request, Agent user) {

        permissionService.findWritePermission(user, request.getProjectId());

        Project project = projectService.findWithFullChildGraph(request.getProjectId(), user);

        Visualization viz = new Visualization();
        project.addVisualization(viz);
        viz.setType(request.getTypeKey());
        viz.addPermission(permissionService.createServicePermission(viz));
        addInputFilesAndPersistPermissions(viz, project, request);
        addParameters(viz, request);

        try {
            viz = visualizationDAO.persist(viz);
            notifier.dispatchAsync(new VisualizationCreatedNotification(viz.getId()));
            return viz;
        } catch (PersistenceException e) {
            logger.error("Could not persist", e);
            throw new CodedException(409, Error.RESOURCE_EXISTS, "Visualization already exists");
        }
    }

    void removeVisualization(String vizId, Agent user) {

        Permission permission = permissionService.findDeletePermission(user, vizId);

        try {
            Visualization viz = (Visualization) permission.getResource();
            visualizationDAO.removeVisualization(viz);
            notifier.dispatchAsync(new VisualizationDeletedNotification(viz.getId()));
        } catch (Exception e) {
            logger.error("Failed to delete visualization: ", e);
            throw new InternalException("Failed to delete visualization");
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

    List<Visualization> findByType(String vizType, Instant after, Agent agent) {
        return visualizationDAO.findAllByTypeIfHasPermission(vizType, after, agent);
    }

    private void createNotificationTypes() {
        List<NotificationType> types = new ArrayList<>();
        types.add(VisualizationCreatedNotification.getNotificationType());
        types.add(VisualizationDeletedNotification.getNotificationType());
        this.notifier.createNotificationTypes(types);
    }

    @Getter
    @AllArgsConstructor
    private static class VisualizationCreatedNotification implements Notification {

        private static final String type = "visualization_created";
        private String message;

        static NotificationType getNotificationType() {
            return new NotificationType(type);
        }

        @Override
        public String getType() {
            return type;
        }
    }

    @Getter
    @AllArgsConstructor
    public static class VisualizationDeletedNotification implements Notification {

        private static final String type = "visualization_deleted";
        private String message;

        static NotificationType getNotificationType() {
            return new NotificationType(type);
        }

        @Override
        public String getType() {
            return type;
        }
    }
}
