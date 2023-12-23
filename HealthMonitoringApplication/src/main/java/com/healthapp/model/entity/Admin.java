package com.healthapp.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Table(name = "admin")
    @Entity
    @Getter
    @Setter
    public class Admin {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        public Long id;

        @Column(name = "username", unique = true, nullable = false)
        public String username;

        @Column(name = "password", nullable = false)
        public String password;

        @Column(name = "full_name", nullable = false)
        public String fullName;

        @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
        @JoinTable(
                name = "admins_roles",
                joinColumns = @JoinColumn(name = "admin_id", referencedColumnName = "id"),
                inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
        private Set<Role> roles = new HashSet<>();
    }
