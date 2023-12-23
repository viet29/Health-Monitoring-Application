package com.healthapp.model.entity;


import com.healthapp.config.security.RoleConstants;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Table(name = "role")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToMany(mappedBy = "roles", cascade = CascadeType.MERGE)
    private Set<Admin> admins = new HashSet<>();

    public Role(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        if (name.equals(RoleConstants.Roles.DOCTOR))
            return "Bác sĩ";
        if (name.equals(RoleConstants.Roles.MANAGER))
            return "Quản lý";
        return name;
    }
}
