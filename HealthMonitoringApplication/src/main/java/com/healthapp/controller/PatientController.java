package com.healthapp.controller;

import com.healthapp.model.entity.Patient;
import com.healthapp.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.model.IModel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/patient")
public class PatientController {

    @Autowired
    private PatientRepository patientRepository;

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("patient", new Patient());
        return "patient/create";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute Patient patient) {
        patientRepository.save(patient);
        return "redirect:/patient/read";
    }

    @GetMapping
    public String read(Model model) {
        List<Patient> listOfPatients = patientRepository.findAll();
        model.addAttribute("listOfPatients", listOfPatients);
        return "patient/read";
    }

    @GetMapping("update/{id}")
    public String update(@PathVariable String id, Model model) {
        Optional<Patient> patient = patientRepository.findById(UUID.fromString(id));
        if(patient.isEmpty()) {
            return "error/notfound";
        }
        model.addAttribute("patient", patient.get());
        return "patient/update";
    }

//    @PostMapping("update/{id}")
//    public String update() {
//
//    }

    @ModelAttribute
    private Patient newPatient() {
        return new Patient();
    }
}
