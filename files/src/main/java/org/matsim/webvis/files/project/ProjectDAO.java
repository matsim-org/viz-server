package org.matsim.webvis.files.project;

import org.matsim.webvis.files.entities.*;

import javax.persistence.EntityManager;
import java.util.List;

public class ProjectDAO extends DAO {

    public Project persist(Project project) {
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

    List<Project> findAllForUserFlat(User user) {
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

    public void removeAllProjects() {

        EntityManager em = database.getEntityManager();
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
