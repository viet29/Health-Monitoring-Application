package com.healthapp.controller;

import com.healthapp.model.entity.Patient;
import com.healthapp.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/patient")
public class PatientController {

    @Autowired
    private PatientRepository patientRepository;

    @GetMapping("/create")
    public String create() {
        return "patient/create";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute Patient patient) {
        patientRepository.save(patient);
        return "patient";
    }

    @GetMapping
    public String read(Model model) {
        List<Patient> listOfPatients = patientRepository.findAll();
        model.addAttribute("listOfPatients", listOfPatients);
        return "read";
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

    @PostMapping("update/{id}")
    public String update() {

    }

    @ModelAttribute
    private Patient newPatient() {
        return new Patient();
    }
}
