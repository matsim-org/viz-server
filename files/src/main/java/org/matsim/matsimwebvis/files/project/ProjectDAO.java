package org.matsim.matsimwebvis.files.project;

import org.matsim.matsimwebvis.files.entities.DAO;
import org.matsim.matsimwebvis.files.entities.Project;
import org.matsim.matsimwebvis.files.entities.QProject;
import org.matsim.matsimwebvis.files.entities.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

public class ProjectDAO extends DAO {

    public Project persistNewProject(Project project, String userId) throws Exception {

        EntityManager manager = database.getEntityManager();
        project.setCreator(manager.find(User.class, userId));

        try {
            return database.persistOne(project, manager);
        } catch (PersistenceException e) {
            throw new Exception("could not persist project");
        }
    }

    public Project persist(Project project) {
        return database.updateOne(project);
    }

    public Project find(String projectId) {

        QProject project = QProject.project;
        return database.executeQuery(query -> query.selectFrom(project)
                .where(project.id.eq(projectId))
                .leftJoin(project.files).fetchJoin()
                .fetchOne());
    }

    public void removeAllProjects() {

        QProject project = QProject.project;
        database.executeQuery(query -> query.delete(project).execute());
    }
}
