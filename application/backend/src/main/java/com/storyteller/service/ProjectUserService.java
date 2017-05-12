package com.storyteller.service;

import com.storyteller.model.Authority;
import com.storyteller.model.Project;
import com.storyteller.model.ProjectUser;
import com.storyteller.model.User;
import com.storyteller.repository.AuthorityRepository;
import com.storyteller.repository.ProjectUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

@Service
@Transactional
public class ProjectUserService {

	@Inject
	private ProjectUserRepository projectUserRepository;
	
	@Inject
	private AuthorityRepository authorityRepository;
	
	/**
	 * Persists projectUser object
	 * @param projectUser projectUser object
	 * @return persisted projectUser object
	 */
	public ProjectUser save(ProjectUser projectUser) {
		return projectUserRepository.saveAndFlush(projectUser);
	}
	
	/**
	 * Persists new projectUser object
	 * @param user
	 * @param project
	 * @return persisted projectUser object
	 */
	public ProjectUser createProjectUser(User user, Project project) {
		ProjectUser projectUser = new ProjectUser();
		
		Authority authority = authorityRepository.findByName("USER");
		
		projectUser.setUser(user);
		projectUser.setProject(project);
		projectUser.setAuthority(authority);
		return save(projectUser);
	}
}
