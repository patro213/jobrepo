package com.storyteller.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Email;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "users")
public class User implements Serializable {
	
	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotNull
    @Email
    @Size(min = 1, max = 50)
    @Column(unique = true, nullable = false)
    private String email;

    @JsonIgnore
    @NotNull
    @Size(min = 6, max = 100)
    @Column(length = 100, nullable = false)
    private String password;
    
    private String name;
    
    private String surname;    
    
    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String photo;

    private String localization;
    
    private String homepage;
    
    private Date creationTime;
    
    private boolean activated;
    
    private String activationToken;

    @Transient
    private List<Long> projectIds;

    @OneToMany(mappedBy = "user")
    private List<Token> tokens;
    
    @JsonBackReference
    @OneToMany(mappedBy = "user")
    private List<ProjectUser> projectsUsers;
    
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	@JsonProperty
	public void setPassword(String password) {
		this.password = password;
	}
	
	public Date getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(Date creationTime) {
		this.creationTime = creationTime;
	}

	public boolean isActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	public String getActivationToken() {
		return activationToken;
	}

	public void setActivationToken(String activationToken) {
		this.activationToken = activationToken;
	}

	public List<Long> getProjectIds() {
		return projectIds;
	}

	public void setProjectIds(List<Long> projectIds) {
		this.projectIds = projectIds;
	}

	public List<Token> getTokens() {
		return tokens;
	}

	public void setTokens(List<Token> tokens) {
		this.tokens = tokens;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public String getLocalization() {
		return localization;
	}

	public void setLocalization(String localization) {
		this.localization = localization;
	}

	public String getHomepage() {
		return homepage;
	}

	public void setHomepage(String homepage) {
		this.homepage = homepage;
	}

	public List<ProjectUser> getProjectsUsers() {
		return projectsUsers;
	}

	public void setProjectsUsers(List<ProjectUser> projectsUsers) {
		this.projectsUsers = projectsUsers;
	}

}