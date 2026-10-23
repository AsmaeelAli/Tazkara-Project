package com.asmaeel.tazkara.identity.model.entity;

import com.asmaeel.tazkara.identity.common.Roles;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "USER")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @NotBlank
    @Column(nullable = false)
    String name;

    @Column(name = "organization_name",unique = true)
    String organizationName;

    @NotBlank
    @Column(nullable = false)
    String password;

    @NotBlank
    @Column(nullable = false, unique = true)
    String email;

    @NotBlank
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    Roles role;

    boolean enabled;

    public UserEntity() {
    }

    public UserEntity(UUID id, String name, String organizationName, String password, String email, Roles role) {
        this.id = id;
        this.name = name;
        this.organizationName = organizationName;
        this.password = password;
        this.email = email;
        this.role = role;
        this.enabled = true;
    }

}
