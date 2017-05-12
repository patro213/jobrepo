package com.storyteller.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "projects_users")
public class ProjectUser implements Serializable {

	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
	
	@ManyToOne
    @JoinColumn(name = "user_id")
	private User user;
	
	@ManyToOne
    @JoinColumn(name = "project_id")
	private Project project;
	
	private Date lastAccessTime;
	
	@ManyToOne
	private Authority authority;
	
	@PrePersist
    public void setLastAccessDate() {
    	this.lastAccessTime = null;
    }

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public Date getLastAccessTime() {
		return lastAccessTime;
	}

	public void setLastAccessTime(Date lastAccessTime) {
		this.lastAccessTime = lastAccessTime;
	}

	public Authority getAuthority() {
		return authority;
	}

	public void setAuthority(Authority authority) {
		this.authority = authority;
	}
}
