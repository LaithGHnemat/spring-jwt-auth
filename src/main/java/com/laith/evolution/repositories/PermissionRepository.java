package com.laith.evolution.repositories;

import com.laith.evolution.enums.PermissionName;
import com.laith.evolution.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByName(PermissionName name);
}
