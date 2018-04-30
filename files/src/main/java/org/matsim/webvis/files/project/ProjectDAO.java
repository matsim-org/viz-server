package org.matsim.webvis.files.project;

import org.matsim.webvis.files.entities.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import java.util.List;

class ProjectDAO extends DAO {

    Project persistNewProject(Project project, String userId) throws Exception {

        EntityManager manager = database.getEntityManager();
        project.setCreator(manager.find(User.class, userId));

        try {
            return database.persistOne(project, manager);
        } catch (PersistenceException e) {
            throw new Exception("could not update project");
        }
    }

    Project persist(Project project) {
        if (project.getId() == null) {
            return database.persistOne(project);
        }
        return database.updateOne(project);
    }

    Project find(String projectId) {

        QProject project = QProject.project;
        QFileEntry fileEntry = QFileEntry.fileEntry;
        QVisualization visualization = QVisualization.visualization;

        return database.executeQuery(query -> query.selectFrom(project)
                .where(project.id.eq(projectId))
                .leftJoin(project.files, fileEntry).fetchJoin()
                .leftJoin(project.visualizations, visualization).fetchJoin()
                .fetchOne());
    }

    Project findFlat(String projectId) {
        QProject project = QProject.project;
        return database.executeQuery(query -> query.selectFrom(project)
                .where(project.id.eq(projectId))
                .fetchOne()
        );
    }

    List<Project> findForUser(List<String> projectIds, User user) {
        QProject project = QProject.project;
        return database.executeQuery(query -> query.selectFrom(project)
                .where(project.creator.eq(user)
                        .and(project.id.in(projectIds)))
                .leftJoin(project.files).fetchJoin()
                .distinct()
                .fetch()
        );
    }

    List<Project> findAllForUser(User user) {
        QProject project = QProject.project;
        return database.executeQuery(query -> query.selectFrom(project)
                .where(project.creator.eq(user))
                .distinct()
                .fetch()
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

    void removeAllProjects() {

        //first we need to remove all related entities
        database.executeTransactionalQuery(query -> query.delete(QFileEntry.fileEntry).execute());
        database.executeTransactionalQuery(query -> query.delete(QProject.project).execute());
    }
}
