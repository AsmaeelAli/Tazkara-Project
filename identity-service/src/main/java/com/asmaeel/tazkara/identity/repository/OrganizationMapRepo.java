package com.asmaeel.tazkara.identity.repository;

import com.asmaeel.tazkara.identity.model.entity.OrganizationMapEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrganizationMapRepo extends JpaRepository<OrganizationMapEntity, UUID>, JpaSpecificationExecutor<OrganizationMapEntity> {
    boolean existsByNormalizedName(String normalizedName);
    boolean existsByNormalizedNameAndUserIdNot(String normalizedName, UUID userId);
    OrganizationMapEntity findByUserId(UUID userId);
}
