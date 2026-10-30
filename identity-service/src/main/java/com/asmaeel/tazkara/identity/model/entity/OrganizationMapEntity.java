package com.asmaeel.tazkara.identity.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Table
@Entity
public class OrganizationMapEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @Column(name = "user-id", nullable = false)
    private UUID userId;

    @NotBlank
    @Column(name = "original_name", nullable = false)
    private String originalName;

    @NotBlank
    @Column(name = "normalized-name", nullable = false, unique = true)
    private String normalizedName;

    public OrganizationMapEntity() {
    }

    public OrganizationMapEntity(UUID userId, String originalName, String normalizedName) {
        this.userId = userId;
        this.originalName = originalName;
        this.normalizedName = normalizedName;
    }

    public UUID getId() {
        return id;
    }

    public String getOriginalName() {
        return originalName;
    }


    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public void setNormalizedName(String normalizedName) {
        this.normalizedName = normalizedName;
    }
}
