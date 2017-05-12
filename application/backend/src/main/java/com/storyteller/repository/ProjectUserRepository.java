package com.storyteller.repository;

import com.storyteller.model.ProjectUser;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectUserRepository extends JpaRepository<ProjectUser, Long> {
	
	List<ProjectUser> findByUserEmailOrderByProjectName(String email, Pageable pageable);

	ProjectUser findByUserIdAndProjectId(Long userId, Long projectId);
	
	ProjectUser findByUserEmailAndProjectId(String email, Long projectId);
}
