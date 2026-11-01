package com.asmaeel.tazkara.identity.model.entity;

import com.asmaeel.tazkara.identity.common.Roles;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Entity
@Table(name = "USERS")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @NotBlank
    @Column(nullable = false)
    private String password;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String email;

    @NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Roles role;


    @Column(nullable = false)
    private boolean enabled;

    public UserEntity() {
    }

    public UserEntity(String name, String password, String email, Roles role) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.role = role;
        this.enabled = true;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public Roles getRole() {
        return role;
    }

    public void setName(String name) {
        this.name = name;
    }

}
