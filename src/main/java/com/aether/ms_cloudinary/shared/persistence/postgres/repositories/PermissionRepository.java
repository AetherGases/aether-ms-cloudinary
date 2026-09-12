package com.aether.ms_cloudinary.shared.persistence.postgres.repositories;

import com.aether.ms_cloudinary.shared.persistence.postgres.entities.PermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends JpaRepository<PermissionEntity, Integer> {
}
