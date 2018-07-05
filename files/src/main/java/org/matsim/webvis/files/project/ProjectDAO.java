package org.matsim.webvis.files.project;

import org.matsim.webvis.files.entities.*;

import javax.persistence.EntityManager;
import java.util.List;

public class ProjectDAO extends DAO {

    public Project persist(Project project) {
        return database.persist(project);
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

    List<Project> find(List<String> projectIds, User creator) {
        QProject project = QProject.project;
        QFileEntry fileEntry = QFileEntry.fileEntry;
        QVisualization visualization = QVisualization.visualization;

        return database.executeQuery(query -> query.selectFrom(project)
                .where(project.creator.eq(creator)
                               .and(project.id.in(projectIds)))
                .leftJoin(project.files, fileEntry).fetchJoin()
                .leftJoin(project.visualizations, visualization).fetchJoin()
                .distinct()
                .fetch()
        );
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

    void remove(Project project) {

        //all child entities must be removed first. We do it this way until it becomes too many child relations
        //we would have to resort to jpa's cascading functionality then.
        QProject projectTable = QProject.project;
        QFileEntry fileEntry = QFileEntry.fileEntry;
        database.executeTransactionalQuery(query -> query.delete(fileEntry).where(fileEntry.project.eq(project)).execute());
        database.executeTransactionalQuery(query -> query.delete(projectTable).where(projectTable.eq(project)).execute());


    }

    public void removeAllProjects() {

        EntityManager em = database.createEntityManager();
        List<Project> projects = database.executeQuery(query -> query.selectFrom(QProject.project).fetch(), em);
        em.getTransaction().begin();

        for (Project project : projects) {
            project.getCreator().getProjects().remove(project);
            project.setCreator(null);
        }

        em.getTransaction().commit();
        em.close();
    }
}
