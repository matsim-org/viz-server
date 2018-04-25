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
        return database.executeQuery(query -> query.selectFrom(project)
                .where(project.id.eq(projectId))
                .leftJoin(project.files).fetchJoin()
                .fetchOne());
    }

    Project find(String projectId, User subject) {
        QProject project = QProject.project;
        return database.executeQuery(query -> query.selectFrom(project)
                .where(project.id.eq(projectId)
                        .and(project.creator.eq(subject)))
                .leftJoin(project.files).fetchJoin()
                .fetchOne()
        );
    }

    List<Project> findForUser(List<String> projectIds, User user) {
        QProject project = QProject.project;
        return database.executeQuery(query -> query.selectFrom(project)
                .where(project.creator.authId.eq(user.getAuthId())
                               .and(project.id.in(projectIds)))
                .leftJoin(project.files).fetchJoin()
                .fetch()
        );
    }

    List<Project> findAllForUser(User user) {
        QProject project = QProject.project;
        return database.executeQuery(query -> query.selectFrom(project)
            .where(project.creator.authId.eq(user.getAuthId()))
                .fetch()
        );
    }

    void removeFileEntry(FileEntry entry) {

        EntityManager em = database.getEntityManager();
        em.getTransaction().begin();
        Project project = em.merge(entry.getProject());
        project.getFiles().remove(entry);
        em.getTransaction().commit();
        em.close();
    }

    void remove(Project project) {
        database.removeOne(project);
    }

    void removeAllProjects() {

        QProject project = QProject.project;
        database.executeTransactionalQuery(query -> query.delete(project).execute());
    }
}
