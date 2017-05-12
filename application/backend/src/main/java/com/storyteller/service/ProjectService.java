package com.storyteller.service;

import com.storyteller.model.Project;
import com.storyteller.model.ProjectUser;
import com.storyteller.model.User;
import com.storyteller.repository.ProjectRepository;
import com.storyteller.repository.ProjectUserRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.LinkedList;
import java.util.List;

@Service
@Transactional
public class ProjectService {
	
	@Inject
	private ProjectRepository projectRepository;
	
	@Inject
	private ProjectUserRepository projectUserRepository;
	
	@Inject
	private UserService userService;
	
	@Inject
	private ProjectUserService projectUserService;
	
	/**
	 * Persists project object
	 * @param project project object
	 * @return persisted project object
	 */
	public Project save(Project project) {
		return projectRepository.saveAndFlush(project);
	}

	/**
	 * Fetches project by its id
	 * @param projectId id of project
	 * @return fetched projectobject
     */
	public Project getProjectById(Long projectId) {
		Project project = projectRepository.findOne(projectId);
		if (project.getSketches() != null) {
			project.getSketches().size(); // force thumbnail load via Lob stream
		}
		return project;
	}

	/**
	 * Save project to database and created ProjectUser entity
	 * @param project project to be saved
	 * @param email email of user creator
     * @return newly persisted project object
     */
	public Project createProject(Project project, String email) {
		User user = userService.getUserByEmail(email);
        project.setCreator(user);
        project = projectRepository.saveAndFlush(project);
        
        ProjectUser projectUser = projectUserService.createProjectUser(user, project);
        
		if (user.getProjectsUsers() == null) {
			user.setProjectsUsers(new LinkedList<>());
		}
		
		user.getProjectsUsers()
			.add(projectUser);
		project.setProjectsUsers(new LinkedList<>());
		project.getProjectsUsers()
			   .add(projectUser);
		
		userService.save(user);
		
		return projectRepository.saveAndFlush(project);
	}

	/**
	 * Updates project in database
	 * @param updatedProject project to be updated
	 * @return updated project object
	 */
	public Project updateProject(Project updatedProject) {
		Project project = getProjectById(updatedProject.getId());

		if (project == null) {
			return null;
		}

		project.setLogo(updatedProject.getLogo());
		project.setName(updatedProject.getName());
		project.setDescription(updatedProject.getDescription());

		projectRepository.saveAndFlush(project);

		return project;
	}
	
	/**
	 * find user projects by user email
	 * @param email
	 * @param pageable new pageable object
	 * @return list of user projects
	 */
	public List<Project> getProjectsForUser(String email, Pageable pageable) {
				
		List<ProjectUser> userProjects = projectUserRepository.findByUserEmailOrderByProjectName(email, pageable);
		
		List<Project> projects = new LinkedList<>();
		for (ProjectUser userProject : userProjects) {
			Project project = userProject.getProject();
			if (project.getSketches() != null) {
				project.getSketches().size(); // force thumbnail load via Lob stream
			}
			projects.add(project);
		}
		
		return projects;
	}
}
