package com.ra.repository;

import com.ra.model.RoleName;
import com.ra.model.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IRoleRepository extends JpaRepository<Roles, Long> {
	
	Optional<Roles> findByRoleName(RoleName roleName);
	
}
