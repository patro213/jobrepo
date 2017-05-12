package com.storyteller;

import com.storyteller.config.TestConfiguration;
import com.storyteller.model.Project;
import com.storyteller.model.User;
import com.storyteller.service.ProjectService;
import com.storyteller.service.ProjectUserService;
import com.storyteller.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;
import java.util.List;

import static org.junit.Assert.*;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@ContextConfiguration(classes=TestConfiguration.class)
@DataJpaTest
public class ProjectServiceTest {
	
	@Inject
	private ProjectService projectService;

	@Inject
	private UserService userService;
	
	@Inject
	private ProjectUserService projectUserService;
	
	@Test
	public void testCreateProject() {
		User user = new User();
		user.setEmail("test@gmail.com");
		user.setPassword("test123456");
		
		user = userService.createUser(user);
		
		Project project = new Project();
		project.setName("novyProjekt");
		project.setDescription("popis projektu");
		project.setLogo("stringLogo");
		project.setCreator(user);
		assertNull(project.getId());
		
		project = projectService.createProject(project, user.getEmail());
		assertNotNull(project.getId());
	}
	
	@Test
	public void testGetUserProjects() {
		User user = new User();
		user.setEmail("test8@gmail.com");
		user.setPassword("test123456");
		
		user = userService.createUser(user);
		
		User user1 = new User();
		user1.setEmail("test9@gmail.com");
		user1.setPassword("test123456");
		
		user1 = userService.createUser(user1);
		
		Project project1 = new Project();
		project1.setName("b");
		project1.setDescription("popis projektu1");
		project1.setLogo("stringLogo1");
		project1.setCreator(user);
		assertNull(project1.getId());
		project1 = projectService.createProject(project1, user.getEmail());
		assertNotNull(project1.getId());
		assertNotNull(project1.getLogo());
		assertNotNull(project1.getDescription());
		assertNotNull(project1.getCreationTime());
		assertNotNull(project1.getCreator());

		Project project2 = new Project();
		project2.setName("a");
		project2.setDescription("popis projektu2");
		project2.setLogo("stringLogo2");
		project2.setCreator(user);
		assertNull(project2.getId());
		project2 = projectService.createProject(project2, user.getEmail());
		assertNotNull(project2.getId());
		
		Project project3 = new Project();
		project3.setName("m");
		project3.setDescription("popis projektu2");
		project3.setLogo("stringLogo2");
		project3.setCreator(user);
		assertNull(project3.getId());
		project3 = projectService.createProject(project3, user.getEmail());
		assertNotNull(project3.getId());
		
		Project project4 = new Project();
		project4.setName("e");
		project4.setDescription("popis projektu2");
		project4.setLogo("stringLogo2");
		project4.setCreator(user);
		assertNull(project4.getId());
		project4 = projectService.createProject(project4, user.getEmail());
		assertNotNull(project4.getId());

		Project project5 = new Project();
		project5.setName("u2");
		project5.setDescription("popis projektu1");
		project5.setLogo("stringLogo1");
		project5.setCreator(user);
		assertNull(project5.getId());
		project5 = projectService.createProject(project5, user1.getEmail());
		assertNotNull(project5.getId());
		
		List<Project> projects = projectService.getProjectsForUser(user.getEmail(), new PageRequest(0, 4));
		assertNotNull(projects);
		
		for(Project p: projects){
			System.out.println("Nazov projektu: " + p.getName());
		}
	}

	@Test
	public void testGetProjectById() {
		User user = new User();
		user.setEmail("test@gmail.com");
		user.setPassword("test123456");

		user = userService.createUser(user);

		Project project = new Project();
		project.setName("novyProjekt");
		project.setDescription("popis projektu");
		project.setLogo("stringLogo");
		project.setCreator(user);
		assertNull(project.getId());

		project = projectService.createProject(project, user.getEmail());
		assertNotNull(project.getId());

		Project testProject = projectService.getProjectById(project.getId());
		assertNotNull(testProject);
		assertEquals(project.getName(), testProject.getName());
	}
}
