package com.storyteller.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "projects")
public class Project implements Serializable {
	
	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    
	@NotNull
    private String name;

	@Lob
	@Type(type = "org.hibernate.type.TextType")
	@NotNull
    private String description;    
    
    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String logo;

	@ManyToOne
	private User creator;
	
	private Date creationTime;
    	
	@JsonBackReference
	@OneToMany(mappedBy = "project")
    private List<ProjectUser> projectsUsers;

	@OneToMany(mappedBy = "project")
	private List<Sketch> sketches;

	@PrePersist
    public void setCreationDate() {
    	this.creationTime = new Date();
    }
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public User getCreator() {
		return creator;
	}

	public void setCreator(User user) {
		this.creator = user;
	}
	
	public Date getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(Date creationTime) {
		this.creationTime = creationTime;
	}

	public List<ProjectUser> getProjectsUsers() {
		return projectsUsers;
	}

	public void setProjectsUsers(List<ProjectUser> projectsUsers) {
		this.projectsUsers = projectsUsers;
	}

	public List<Sketch> getSketches() {
		return sketches;
	}

	public void setSketches(List<Sketch> sketches) {
		this.sketches = sketches;
	}
}