package com.healthapp.config.security;

import com.healthapp.model.entity.Admin;
import com.healthapp.model.entity.Role;
import com.healthapp.repository.AdminRepository;
import com.healthapp.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserLoader implements ApplicationRunner {

    private final AdminRepository adminRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserLoader(AdminRepository adminRepository,
                      PasswordEncoder passwordEncoder,
                      RoleRepository roleRepository) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (adminRepository.count() != 0) {
            return;
        }

        Admin a = new Admin();
        a.setUsername("sa");
        a.setFullName("Phạm Quốc Việt");
        a.setPassword(passwordEncoder.encode("123456"));

        if (roleRepository.findByName(RoleConstants.Roles.MANAGER) == null)
            roleRepository.save(new Role(RoleConstants.Roles.MANAGER));
        if (roleRepository.findByName(RoleConstants.Roles.DOCTOR) == null)
            roleRepository.save(new Role(RoleConstants.Roles.DOCTOR));

        List<Role> roles = roleRepository.findAll();
        roles.forEach(role -> a.getRoles().add(role));
        adminRepository.save(a);

        System.out.println(a.getUsername());
    }
}
