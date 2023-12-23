package com.healthapp.controller;

import com.healthapp.config.security.RoleConstants;
import com.healthapp.model.entity.Admin;
import com.healthapp.model.entity.Role;
import com.healthapp.repository.AdminRepository;
import com.healthapp.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminRepository adminRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AdminController(final AdminRepository adminRepository,
                           final RoleRepository roleRepository,
                           final PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("")
    public String read(Model model) {
        List<Admin> admins = adminRepository.findAll();
        model.addAttribute("admins", admins);
        return "admins/read";
    }

    @GetMapping("/create")
    public String create() {
        return "admins/create";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute Admin admin) {
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        Role role = roleRepository.findByName(RoleConstants.Roles.DOCTOR);
        admin.setRoles(Set.of(role));
        adminRepository.save(admin);
        return "redirect:/admin";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable String id) {
        Optional<Admin> a = adminRepository.findById(Long.parseLong(id));
        if(a.isEmpty()) {
            return "errors/notfound";
        }
        Admin admin = a.get();
        adminRepository.delete(admin);
        return "redirect:/admin";
    }

    @ModelAttribute
    private Admin newAdmin() {
        return new Admin();
    }
}
