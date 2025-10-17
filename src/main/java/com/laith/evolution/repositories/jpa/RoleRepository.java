package com.laith.evolution.repositories.jpa;

import com.laith.evolution.enums.RoleName;
import com.laith.evolution.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}
