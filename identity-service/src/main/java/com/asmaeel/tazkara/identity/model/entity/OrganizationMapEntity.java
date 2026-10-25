package com.asmaeel.tazkara.identity.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Table
@Entity
public class OrganizationMapEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @NotNull
    @Column(name = "user-id",nullable = false)
    UUID userId;

    @NotBlank
    @Column(name = "original_name",nullable = false)
    String originalName;

    @NotBlank
    @Column(name = "normalized-name",nullable = false,unique = true)
    String normalizedName;

    public OrganizationMapEntity() {
    }

    public OrganizationMapEntity(UUID userId, String originalName, String normalizedName) {
        this.userId = userId;
        this.originalName = originalName;
        this.normalizedName = normalizedName;
    }
}
