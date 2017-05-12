package com.storyteller.repository;

import com.storyteller.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Date;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	
    User findByEmail(String email);
    
    User findByActivationToken(String token);
    
    @Modifying
    @Query("delete from User u where u.creationTime < :expiryDate and u.activated = false")
    void removeNotActivatedUsers(@Param("expiryDate") Date activationExpiryDate);
    
}
