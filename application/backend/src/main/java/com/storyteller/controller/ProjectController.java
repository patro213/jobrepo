package com.storyteller.controller;

import com.storyteller.model.Project;
import com.storyteller.model.ProjectInvitation;
import com.storyteller.model.User;
import com.storyteller.security.SecurityUtil;
import com.storyteller.service.MailService;
import com.storyteller.service.ProjectInvitationService;
import com.storyteller.service.ProjectService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import com.storyteller.service.UserService;
import org.apache.log4j.Logger;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@RestController
@RequestMapping(value = "/projects", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProjectController {
    
	private static final Logger LOGGER = Logger.getLogger(ProjectController.class);

	@Inject
	private ProjectService projectService;

	@Inject
	private MailService mailService;

	@Inject
	private UserService userService;

	@Inject
	private ProjectInvitationService projectInvitationService;

	@ApiOperation(value = "createProject", notes = "Saves new project")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Project created successfully.", response = Project.class),
			@ApiResponse(code = 400, message = "Failed to create project.")
	})
	@RequestMapping(value = "", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> createProject(@Valid @RequestBody @ApiParam(value = "project object") Project project) {
		String email = SecurityUtil.getCurrentLogin();
		
		project = projectService.createProject(project, email);
		
		if (project != null) {
	        LOGGER.info("Created new project with name " + project.getName() + " by user " + email);
			return new ResponseEntity<>(project, HttpStatus.OK);
		}
		
		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	}


	@ApiOperation(value = "updateProject", notes = "Updates existing project")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Project updated successfully."),
			@ApiResponse(code = 400, message = "Failed to update project.")
	})
	@PreAuthorize("@roleSecurityService.hasPermissionToProject(#projectId)")
	@RequestMapping(value = "/{projectId}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> updateProject(@PathVariable @ApiParam(value = "project id") Long projectId,
										   @Valid @RequestBody @ApiParam(value = "project object") Project project) {
		project = projectService.updateProject(project);

		if (project != null) {
			LOGGER.info("Updated project with name " + project.getName() );
			return new ResponseEntity<>(HttpStatus.OK);
		}

		LOGGER.info("Failed to update project.");
		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	}


	@ApiOperation(value = "getProjectById", notes = "Fetches project object specified by id")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Project retrieved successfully.", response = Project.class),
			@ApiResponse(code = 400, message = "Project not found.")
	})
	@RequestMapping(value = "/{projectId}", method = RequestMethod.GET)
	@PreAuthorize("@roleSecurityService.hasPermissionToProject(#projectId)")
	public ResponseEntity<?> getProjectById(@PathVariable @ApiParam(value = "project id") Long projectId) {
		Project project = projectService.getProjectById(projectId);

		if (project != null) {
			LOGGER.info("Fetched project with id " + projectId);
			return new ResponseEntity<>(project, HttpStatus.OK);
		}

		LOGGER.info("Project with id " + projectId + " not found");
		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	}


	@ApiOperation(value = "getUserProjects", notes = "Finds user projects")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Projects retrieved successfully.", response = Project.class, responseContainer = "List"),
			@ApiResponse(code = 400, message = "Project not found.")
	})
	@RequestMapping(value = "/{pageIndex}/{pageSize}", method = RequestMethod.GET)
	public ResponseEntity<?> getUserProjects(@PathVariable @ApiParam(value = "page index") int pageIndex,
											 @PathVariable @ApiParam(value = "page size") int pageSize) {
		String email = SecurityUtil.getCurrentLogin();
		List<Project> projects = projectService.getProjectsForUser(email, new PageRequest(pageIndex, pageSize));
		
		if (projects != null) {
			LOGGER.info("Get user's " + email + " projects.");
			return new ResponseEntity<>(projects, HttpStatus.OK);
		}
		
		return new ResponseEntity<>("Could not find user's projects.", HttpStatus.BAD_REQUEST);
	}


	@ApiOperation(value = "projectInvitationEmail", notes = "Saves new registered user and sends activation email to his email address")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "User saved successfully."),
			@ApiResponse(code = 400, message = "User not found.")
	})
	@RequestMapping(value = "/{projectId}/invitation", method = RequestMethod.POST)
	public ResponseEntity<?> projectInvitationEmail(@PathVariable("projectId") @ApiParam(value = "project id") Long projectId,
													@RequestParam("email")@ApiParam(value = "recipient email") String recipientEmail,
													HttpServletRequest request) throws NoSuchAlgorithmException {
		String sender = SecurityUtil.getCurrentLogin();
		String localization = request.getHeader("LOCALIZATION");

		Project project = projectService.getProjectById(projectId);
		User user = userService.getUserByEmail(recipientEmail);

		if (user == null) {
			LOGGER.info("User with email " + recipientEmail + " does not exist.");
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		if (project != null) {
			// send invitation email
			mailService.sendProjectInvitationEmail(sender, recipientEmail, project, localization);
			LOGGER.info("Project invitation email has been sent successfully.");
			return new ResponseEntity<>(HttpStatus.OK);
		}

		LOGGER.info("Project with id " + projectId + " not found");
		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	}


	@ApiOperation(value = "getProjectInvitation", notes = "Finds project invitation for invitation token")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Project invitation fetched successfully.", response = ProjectInvitation.class),
			@ApiResponse(code = 400, message = "Project invitation not found.")
	})
	@RequestMapping(value = "/invitation/{invitationToken}", method = RequestMethod.GET)
	public ResponseEntity<?> getProjectInvitation(@PathVariable @ApiParam(value = "invitation token") String invitationToken) {

		ProjectInvitation projectInvitation = projectInvitationService.findProjectInvitationForToken(invitationToken);

		if (projectInvitation != null) {
			LOGGER.info("Fetched project invitation for invitation token " + invitationToken);
			return new ResponseEntity<>(projectInvitation, HttpStatus.OK);
		}

		LOGGER.info("Project invitation for invitation token " + invitationToken + " not found");
		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	}
}
