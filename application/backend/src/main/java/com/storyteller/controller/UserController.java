package com.storyteller.controller;

import com.storyteller.model.Notification;
import com.storyteller.model.Project;
import com.storyteller.model.Token;
import com.storyteller.model.User;
import com.storyteller.security.SecurityUtil;
import com.storyteller.service.NotificationService;
import com.storyteller.service.ProjectService;
import com.storyteller.service.TokenService;
import com.storyteller.service.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.log4j.Logger;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {
	
	private static final Logger LOGGER = Logger.getLogger(UserController.class);

	@Inject
	private TokenService tokenService;
	
	@Inject
	private UserService userService;

	@Inject
	private ProjectService projectService;

	@Inject
	private NotificationService notificationService;

	@ApiOperation(value = "getUserProfile", notes = "Fetches currently authenticated user profile")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "User profile retrieved successfully.", response = User.class),
			@ApiResponse(code = 400, message = "User profile not found.")
	})
	@RequestMapping(value = "/current", method = RequestMethod.GET)
	public ResponseEntity<?> getUserProfile() {
		String email = SecurityUtil.getCurrentLogin();
		User user = userService.getUserByEmail(email);
			
		if (user != null) {
			Notification notification = new Notification();
			notification.setType("info");
			notification.setRecipient(user);
			notification.setBody("test");
			notification.setBody("Boli ste prihlaseny");
			notificationService.createNotification(notification);
			notificationService.sendNotification(notification);

			List<Long> projectIds = projectService.getProjectsForUser(user.getEmail(),
																	  new PageRequest(0, Integer.MAX_VALUE))
												  .parallelStream()
												  .map(Project::getId)
												  .collect(Collectors.toList());
			user.setProjectIds(projectIds);
			return new ResponseEntity<>(user, HttpStatus.OK);
		}
		
		return new ResponseEntity<>("Could not find user profile.", HttpStatus.BAD_REQUEST);
	}


	@ApiOperation(value = "updateUserProfile", notes = "Updates currently authenticated user profile")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "User profile updated successfully.", response = User.class),
			@ApiResponse(code = 400, message = "Failed to update user profile.")
	})
	@RequestMapping(value = "/current", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> updateUserProfile(@Valid @RequestBody @ApiParam(value = "new user object") User newUserInfo) {
		String email = SecurityUtil.getCurrentLogin();
		User user = userService.updateUser(email, newUserInfo);
		
		if (user != null) {
			LOGGER.info("User " + email + " profile has been updated.");
			return new ResponseEntity<>(user, HttpStatus.OK);
		}
		
		return new ResponseEntity<>("Could not update user profile.", HttpStatus.BAD_REQUEST);
	}


	@ApiOperation(value = "getUsersInProject", notes = "Finds project users")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Users of project fetched successfully.", response = User.class, responseContainer = "List"),
			@ApiResponse(code = 400, message = "Users of project not found.")
	})
	@RequestMapping(value = "/{projectId}", method = RequestMethod.GET)
	public ResponseEntity<?> getUsersInProject(@PathVariable @ApiParam(value = "project id") Long projectId) {
		
		List<User> users = userService.getUsersForProjectId(projectId);
		
		if (users != null) {
			LOGGER.info("Users of project  " + projectId + " have been found.");
			return new ResponseEntity<>(users, HttpStatus.OK);
		}
		
		return new ResponseEntity<>("Could not find project users.", HttpStatus.BAD_REQUEST);
	}


	@ApiOperation(value = "changePassword", notes = "Changes password of current user")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Password changed successfully."),
			@ApiResponse(code = 400, message = "Failed to change password.")
	})
    @RequestMapping(value = "/current/password", method = RequestMethod.POST)
    public ResponseEntity<?> changePassword(@RequestParam("oldPassword") @ApiParam(value = "user old password") String oldPassword,
                                            @RequestParam("newPassword") @ApiParam(value = "user new password") String newPassword) {
		String email = SecurityUtil.getCurrentLogin();

        if (!userService.changePassword(email, oldPassword, newPassword)) {
            LOGGER.error("Password of user '" + SecurityUtil.getCurrentLogin() + "' could not be changed");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        LOGGER.info("Password of user '" + SecurityUtil.getCurrentLogin() + "' changed successfully");
        return new ResponseEntity<>(HttpStatus.OK);
    }	


	@ApiOperation(value = "getAllUserTokens", notes = "Fetches currently authenticated user profile and all tokens")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Tokens of user fetched successfully.", response = Token.class, responseContainer = "List"),
			@ApiResponse(code = 400, message = "Currently authenticated user profile not found.")
	})
	@RequestMapping(value = "/current/tokens", method = RequestMethod.GET)
    public ResponseEntity<?> getAllUserTokens() {
		String email = SecurityUtil.getCurrentLogin();
		User user = userService.getUserByEmail(email);
		
		if (user == null) {
			LOGGER.error("Could not find currently authenticated user profile with email " + email);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		List<Token> userTokens = tokenService.findAllUserTokens(user);
		LOGGER.info("Tokens of user with email " + email + " have been successfully found");
		return new ResponseEntity<>(userTokens, HttpStatus.OK);
	}
}
