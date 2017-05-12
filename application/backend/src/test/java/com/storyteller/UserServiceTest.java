package com.storyteller;

import com.storyteller.config.TestConfiguration;
import com.storyteller.model.Authority;
import com.storyteller.model.Project;
import com.storyteller.model.ProjectUser;
import com.storyteller.model.User;
import com.storyteller.repository.AuthorityRepository;
import com.storyteller.repository.UserRepository;
import com.storyteller.service.ProjectService;
import com.storyteller.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@ContextConfiguration(classes=TestConfiguration.class)
@DataJpaTest
public class UserServiceTest {

	@Inject
	private UserService userService;
	
	@Inject
	private ProjectService projectService;
	
	@Inject
	private UserRepository userRepository;
	
	@Inject
	private PasswordEncoder passwordEncoder;

	@Inject
	private AuthorityRepository authorityRepository;

	@Test
	public void testCreateUser() {
		User user = new User();
		user.setEmail("test@gmail.com");
		user.setPassword("test123456");
		
		assertNull(user.getId());
		assertNull(user.getCreationTime());
		assertNull(user.getActivationToken());
		user = userService.createUser(user);
		assertNotNull(user.getId());
		assertNotNull(user.getCreationTime());
		assertNotNull(user.getActivationToken());
	}
	
	@Test
	public void testRemoveNotActivatedUsers() {
		User user = new User();
		user.setEmail("test@gmail.com");
		user.setPassword("test123456");
		
		assertNull(user.getId());
		assertNull(user.getCreationTime());
		assertNull(user.getActivationToken());
		user = userService.createUser(user);
		assertNotNull(user.getId());
		assertNotNull(user.getCreationTime());
		assertNotNull(user.getActivationToken());
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR_OF_DAY, -999);
		
		user.setCreationTime(calendar.getTime());
		userService.save(user);
		assertEquals(userRepository.count(), 1);
		
		userService.removeNotActivatedUsers();
		assertEquals(userRepository.count(), 0);
	}
	
	@Test
	public void testUpdateUser() {
		
		testCreateUser();
		
		User user = new User();
		
		user.setName("menoUsera");
		user.setSurname("priezviskoUsera");
		user.setPassword("hesloUsera21");
		user.setEmail("test1@gmail.com");
		user.setPhoto("linkKFotke");
		user.setLocalization("lokalizacia");
		user.setHomepage("url");
		
		user = userService.updateUser("test@gmail.com",user);

		assertEquals("menoUsera", user.getName());
		assertEquals("priezviskoUsera", user.getSurname());
		assertEquals("linkKFotke", user.getPhoto());
		assertEquals("lokalizacia", user.getLocalization());
		assertEquals("url", user.getHomepage());
	}
	
	@Test
	public void testGetProjectUsers() {
		User user1 = new User();
		user1.setEmail("user1@gmail.com");
		user1.setName("u");
		user1.setPassword("test123456");
		
		user1 = userService.createUser(user1);
		
		User user2 = new User();
		user2.setEmail("user2@gmail.com");
		user2.setName("j");
		user2.setPassword("test123456");
		
		user2 = userService.createUser(user2);
		
		User user3 = new User();
		user3.setEmail("user3@gmail.com");
		user3.setName("c");
		user3.setPassword("test123456");
		
		user3 = userService.createUser(user3);
		
		Project project = new Project();
		project.setName("novyProjekt");
		project.setDescription("popis projektu");
		project.setLogo("stringLogo");
		project.setCreator(user1);
		assertNull(project.getId());
		project = projectService.createProject(project, user1.getEmail());
		assertNotNull(project.getId());
		
		Project project1 = new Project();
		project1.setName("novyProjekt");
		project1.setDescription("popis projektu");
		project1.setLogo("stringLogo");
		project1.setCreator(user1);
		assertNull(project1.getId());
		project1 = projectService.createProject(project1, user1.getEmail());
		assertNotNull(project.getId());
		
		project = userService.addUserToProject(project, "user1@gmail.com");
		project1 = userService.addUserToProject(project1, "user2@gmail.com");
		project = userService.addUserToProject(project, "user3@gmail.com");
		
		List<User> users = userService.getUsersForProjectId(project.getId());
		assertNotNull(users);
		
		for (User u : users){
			System.out.println("Email usera: " + u.getEmail());
		}

		users = userService.getUsersForProjectId(999L);
		assertEquals(Collections.EMPTY_LIST, users);
	}

	@Test
	public void testGenerateNewPassword() {
		
		User user = new User();
		
		user.setName("menoUsera");
		user.setSurname("priezviskoUsera");
		user.setPassword("hesloUsera21");
		user.setEmail("test1@gmail.com");
		user.setPhoto("linkKFotke");
		user.setLocalization("lokalizacia");
		user.setHomepage("url");
		
		assertNull(user.getId());
		user = userService.createUser(user);
		assertNotNull(user.getId());

		userService.generateNewPassword(user);
		assertFalse(passwordEncoder.matches("hesloUsera21", user.getPassword()));
	}

	@Test
	public void testChangePassword() {
		User user = new User();

		user.setName("menoUsera");
		user.setSurname("priezviskoUsera");
		user.setPassword("hesloUsera21");
		user.setEmail("test1@gmail.com");
		user.setPhoto("linkKFotke");
		user.setLocalization("lokalizacia");
		user.setHomepage("url");

		assertNull(user.getId());
		user = userService.createUser(user);
		assertNotNull(user.getId());

		boolean result = userService.changePassword("test1@gmail.com","hesloUsera2541", "hesloUsera12");
		assertFalse(result);

		result = userService.changePassword("test1@gmail.com","hesloUsera21", "123");
		assertFalse(result);

		result = userService.changePassword("test1@gmail.com","hesloUsera21", "hesloUsera12");
		assertTrue(result);
		assertFalse(passwordEncoder.matches("hesloUsera21", user.getPassword()));
		assertTrue(passwordEncoder.matches("hesloUsera12", user.getPassword()));
	}
	
	@Test
	public void testUpdateUserActivationToken() {
		
		testCreateUser();
		
		User user = userService.updateUserActivationToken("test@gmail.com", "novyToken");
		
		assertEquals("novyToken", user.getActivationToken());
	}
	
	@Test
	public void testUpdateUserAuthority() {
		Authority authority = new Authority();
		authority.setName("ADMIN");
		authorityRepository.save(authority);

		User user = new User();
		user.setEmail("user1@gmail.com");
		user.setName("u");
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
		
		ProjectUser projectUser = userService.getProjectUserForUserIdAndProjectId(user.getId(), project.getId());
		boolean updatedAuthority = userService.updateAuthority(projectUser, "ADMIN");
		assertTrue(updatedAuthority);
		assertNull(projectUser.getLastAccessTime());

		updatedAuthority = userService.updateAuthority(projectUser, "NULL");
		assertFalse(updatedAuthority);
	}
}
