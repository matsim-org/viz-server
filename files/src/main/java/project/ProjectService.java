package project;

import entities.Project;

public class ProjectService {

    private ProjectDAO projectDAO = new ProjectDAO();

    public Project createNewProject(String projectName, String userId) throws Exception {

        Project project = new Project();
        project.setName(projectName);
        return projectDAO.persistNewProject(project, userId);

    }
}
