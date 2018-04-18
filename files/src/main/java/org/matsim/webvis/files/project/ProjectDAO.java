package org.matsim.webvis.files.project;

import org.matsim.webvis.files.entities.DAO;
import org.matsim.webvis.files.entities.Project;
import org.matsim.webvis.files.entities.QProject;
import org.matsim.webvis.files.entities.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import java.util.List;

public class ProjectDAO extends DAO {

    Project persistNewProject(Project project, String userId) throws Exception {

        EntityManager manager = database.getEntityManager();
        project.setCreator(manager.find(User.class, userId));

        try {
            return database.persistOne(project, manager);
        } catch (PersistenceException e) {
            throw new Exception("could not update project");
        }
    }

    public Project persist(Project project) {
        return database.updateOne(project);
    }

    Project find(String projectId) {

        QProject project = QProject.project;
        return database.executeQuery(query -> query.selectFrom(project)
                .where(project.id.eq(projectId))
                .leftJoin(project.files).fetchJoin()
                .fetchOne());
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

    void remove(Project project) {
        database.removeOne(project);
    }

    void removeAllProjects() {

        QProject project = QProject.project;
        database.executeQuery(query -> query.delete(project).execute());
    }
}
