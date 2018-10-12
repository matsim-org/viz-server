package org.matsim.viz.files.project;

import org.matsim.viz.database.PersistenceUnit;
import org.matsim.viz.files.entities.*;

import javax.persistence.EntityManager;
import java.util.List;

public class ProjectDAO extends DAO {

    public ProjectDAO(PersistenceUnit persistenceUnit) {
        super(persistenceUnit);
    }

    public Project persist(Project project) {
        return database.persist(project);
    }

    public Project addPermission(Project project, Permission permission) {

        EntityManager em = database.createEntityManager();
        em.getTransaction().begin();
        project = em.merge(project);
        project.addPermission(permission);
        project.getFiles().forEach(file -> file.addPermission(new Permission(file, permission.getAgent(), permission.getType())));
        project.getVisualizations().forEach(viz -> viz.addPermission(new Permission(viz, permission.getAgent(), permission.getType())));
        em.getTransaction().commit();
        em.close();
        return project;
    }

    Project removePermission(Project project, Agent permissionAgent) {

        EntityManager em = database.createEntityManager();

        em.getTransaction().begin();
        project = em.merge(project);
        project.removePermission(permissionAgent);
        project.getFiles().forEach(file -> file.removePermission(permissionAgent));
        project.getVisualizations().forEach(viz -> viz.removePermission(permissionAgent));
        em.getTransaction().commit();
        em.close();
        return project;
    }

    Project find(String projectId) {

        QProject project = QProject.project;
        QFileEntry fileEntry = QFileEntry.fileEntry;
        QVisualization visualization = QVisualization.visualization;

        return database.executeQuery(query -> query.selectFrom(project)
                .where(project.id.eq(projectId))
                .leftJoin(project.files, fileEntry).fetchJoin()
                .leftJoin(project.visualizations, visualization).fetchJoin()
                .leftJoin(project.permissions).fetchJoin()
                .fetchOne());
    }

    Project findFlat(String projectId) {
        QProject project = QProject.project;
        return database.executeQuery(query -> query.selectFrom(project)
                .where(project.id.eq(projectId))
                .fetchOne()
        );
    }

    List<Project> findAllForUserFlat(Agent agent) {

        QProject project = QProject.project;
        QPermission permission = QPermission.permission;

        return database.executeQuery(query -> query.selectFrom(project)
                .innerJoin(project.permissions, permission).on(permission.agent.eq(agent))
                .distinct()
                .fetch()
        );
    }

    Project findWithFullGraph(String projectId) {

        QProject project = QProject.project;
        QFileEntry fileEntry = QFileEntry.fileEntry;
        QVisualization visualization = QVisualization.visualization;
        QPermission permission = QPermission.permission;

        return database.executeQuery(query -> query.selectFrom(project)
                .where(project.id.eq(projectId))
                .leftJoin(project.files, fileEntry).fetchJoin()
                .leftJoin(fileEntry.permissions, permission).fetchJoin()
                .leftJoin(project.visualizations, visualization).fetchJoin()
                .leftJoin(visualization.permissions, permission).fetchJoin()
                .leftJoin(project.permissions).fetchJoin()
                .fetchOne());
    }

    FileEntry findFileEntry(String projectId, String fileId) {

        QFileEntry fileEntry = QFileEntry.fileEntry;

        return database.executeQuery(query -> query.selectFrom(fileEntry)
                .where(fileEntry.project.id.eq(projectId).and(fileEntry.id.eq(fileId)))
                .fetchOne()
        );
    }

    void removeProject(Project project) {

        EntityManager em = database.createEntityManager();
        em.getTransaction().begin();
        project = em.merge(project);
        project.getCreator().getProjects().remove(project);
        project.setCreator(null);
        em.getTransaction().commit();
        em.close();
    }

    public void removeAllProjects() {

        EntityManager em = database.createEntityManager();

        List<Project> projects = database.createQuery(em).selectFrom(QProject.project).fetch();
        em.getTransaction().begin();

        for (Project project : projects) {
            project.getCreator().getProjects().remove(project);
            project.setCreator(null);
        }

        em.getTransaction().commit();
        em.close();
    }
}
