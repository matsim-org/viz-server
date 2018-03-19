package project;

import entities.DAO;
import entities.Project;
import entities.QProject;
import entities.User;

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

    public void removeAllProjects() {

        QProject project = QProject.project;
        database.executeQuery(query -> query.delete(project).execute());
    }
}
