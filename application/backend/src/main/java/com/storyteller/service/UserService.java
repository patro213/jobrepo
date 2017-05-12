package com.storyteller.service;

import com.storyteller.model.Authority;
import com.storyteller.model.Project;
import com.storyteller.model.ProjectUser;
import com.storyteller.model.User;
import com.storyteller.repository.AuthorityRepository;
import com.storyteller.repository.ProjectRepository;
import com.storyteller.repository.ProjectUserRepository;
import com.storyteller.repository.UserRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.sql.Date;
import java.util.*;

@Service
@Transactional
public class UserService {
	
	private static final Logger LOGGER = Logger.getLogger(UserService.class);

	@Inject
	private UserRepository userRepository;
	
	@Inject
    private AuthorityRepository authorityRepository;
	
	@Inject
    private ProjectRepository projectRepository;

	@Inject
    private PasswordEncoder passwordEncoder;
	
	@Inject
	private TokenService tokenService;
	
	@Inject
	private ProjectUserService projectUserService;
	
	@Inject
    private ProjectService projectService;

	@Inject
    private ProjectUserRepository projectUserRepository;

	@Inject
	private NotificationService notificationService;

	@Value("${storyteller.interval.delete-users}")
	private int userDeleteInterval;
	
	/**
	 * Fetches user by activation token
	 * @param token activation token
	 * @return fetched user object
	 */
	public User getUserByActivationToken(String token) {
		return userRepository.findByActivationToken(token);
	}
	
	/**
	 * Fetches user by email
	 * @param email email of user
	 * @return fetched user object
	 */
	public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

	/**
	 * Fetches user by id
	 * @param id id of user
	 * @return fetched user object
	 */
	public User getUserById(Long id) {
		return userRepository.findOne(id);
	}
	
	/**
	 * Persists user object
	 * @param user user object
	 * @return persisted user object
	 */
	public User save(User user) {
		return userRepository.saveAndFlush(user);
	}
	
	/**
	 * Sets user's account active
	 * @param activated sets account activated
	 * @param user an object of user
	 * @return
	 */
	public User activateUser(boolean activated, User user) {
		user.setActivated(activated);
		userRepository.saveAndFlush(user);
		return user;
	}
	
	/**
	 * Deletes user from database
	 * @param user an object of user
	 */
	public void deleteUser(User user) {
		userRepository.delete(user);
	}

	/**
	 * Persists new user
	 * @param user new user object
	 * @return persisted user object
	 */
	public User createUser(User user) {
        Authority authority = authorityRepository.findOne("USER");

        List<Authority> authorities = new LinkedList<Authority>();
        authorities.add(authority);
        
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setActivated(false);
        
        String hashedToken;
		try {
			hashedToken = tokenService.hashActivationToken(UUID.randomUUID().toString() + user.getEmail());
	        user.setActivationToken(hashedToken);
		} catch (Exception e) {
			LOGGER.error("User creation failed: Failed to generate hashed activation token for new user "
					+ user.getEmail(), e);
			return null;
		}
		
		return userRepository.saveAndFlush(user);
    }

	/**
	 * Updates info of existing user
	 * @param email email of existing user
	 * @param newUserInfo User object with updated info
	 * @return updated User object
	 */
	public User updateUser(String email, User newUserInfo) {
		User user = getUserByEmail(email);
		
        user.setName(newUserInfo.getName());
        user.setSurname(newUserInfo.getSurname());
        user.setPhoto(newUserInfo.getPhoto());
        user.setLocalization(newUserInfo.getLocalization()); 
        user.setHomepage(newUserInfo.getHomepage()); 
        
        return userRepository.saveAndFlush(user);
	}
	
	/**
	 * Adding user to existing project
	 * @param project project object 
	 * @param email email of existing user
	 * @return persisted project object
	 */
	public Project addUserToProject(Project project, String email){
		User user = getUserByEmail(email);
		
		ProjectUser projectUser = projectUserService.createProjectUser(user, project);
		
		if (user.getProjectsUsers() == null) {
			user.setProjectsUsers(new LinkedList<>());
		}
		
		user.getProjectsUsers()
			.add(projectUser);
		
		if (project.getProjectsUsers() == null) {
			project.setProjectsUsers(new LinkedList<>());
		}
		
		project.getProjectsUsers()
			   .add(projectUser);
		
		userRepository.saveAndFlush(user);
		projectService.save(project);
		
		return project;
	}
		
	/**
	 * Find users from existing project
	 * @param projectId id of existing project
	 * @return list of project users
	 */
	public List<User> getUsersForProjectId(Long projectId) {

		Project project = projectRepository.findOne(projectId);
		
		if (project == null) {
			return Collections.emptyList();
		}
		
		List<ProjectUser> projectUsers = project.getProjectsUsers();
		List<User> users = new LinkedList<>();
		for (ProjectUser projectUser : projectUsers) {
			users.add(projectUser.getUser());
		}

		users.sort((o1, o2) -> {
			if (o1.getName() == null || o2.getName() == null) {
				return 0;
			}

			return o1.getName()
					 .compareTo(o2.getName());
		});
		
        return users;
	}
	
	/**
	 * Updates activation token of existing user
	 * @param email of user with old activation token
	 * @param activationToken new activation token for user
	 * @return updated User object
	 */
	public User updateUserActivationToken(String email, String activationToken) {
		User user = getUserByEmail(email);
        user.setActivationToken(activationToken);
        return userRepository.saveAndFlush(user);
	}
	
	/**
	 * Removes non-activated users in batch operation every friday at 22:00
	 */
	@Scheduled(cron = "0 0 22 * * fri")
	@Transactional
	public void removeNotActivatedUsers() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR_OF_DAY, -userDeleteInterval);
		
		Date expiryDate = new Date(calendar.getTimeInMillis());
		userRepository.removeNotActivatedUsers(expiryDate);
	}	
	
     /** Changes password of authenticated user
     * @param oldPassword old password
     * @param newPassword new password
     * @return true if changed, else false
     */
    public boolean changePassword(String email, String oldPassword, String newPassword) {
        User user = userRepository.findByEmail(email);

        if (newPassword.length() < 6) {
            LOGGER.error("New password of user '" + user.getEmail() + "' is too short");
            return false;
        }

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            LOGGER.error("Current and given passwords of user '" + user.getEmail() + "' do not match");
            return false;
        }

        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        userRepository.save(user);

        LOGGER.info("Password of user '" + user.getEmail() + "' updated successfully");
        return true;
    }
    
    /**
	 * Generates new user password
	 * @param user User object of authenticated user
	 * @return updated User new password
	 */
	public String generateNewPassword(User user) {
		
		// generate new password
		String newPassword = UUID.randomUUID()
				.toString()
				.substring(0, 10)
				.replace('-', '@');
		user.setPassword(passwordEncoder.encode(newPassword));
		
		if (userRepository.saveAndFlush(user) != null) {
			return newPassword;
		}
		
		return null;
	}

	/**
	 * Find project user by id
	 * @param userId User object of authenticated user
	 * @param projectId email of existing user
	 * @return projectUser an object of projectUser
	 */
	public ProjectUser getProjectUserForUserIdAndProjectId(Long userId, Long projectId) {
		return projectUserRepository.findByUserIdAndProjectId(userId,projectId);
	}

	/**
	 * Persists new user project
	 * @param projectUser project user object
	 * @param authorityName name of new authority given to user
	 * @return true if persisted project user object
	 */
	public boolean updateAuthority(ProjectUser projectUser, String authorityName) {
		Authority authority = authorityRepository.findOne(authorityName);

		if (authority == null) {
			LOGGER.error("Authority '" + authorityName +"' does not exist");
			return false;
		}

		projectUser.setAuthority(authority);

		return projectUserRepository.saveAndFlush(projectUser) != null;
	}
}
