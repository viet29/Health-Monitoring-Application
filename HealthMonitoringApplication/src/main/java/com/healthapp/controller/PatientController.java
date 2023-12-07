package com.healthapp.controller;

import com.healthapp.model.entity.Patient;
import com.healthapp.model.mqtt.MqttSubscribeModel;
import com.healthapp.repository.ExaminationRepository;
import com.healthapp.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/patient")
public class PatientController {

    private final PatientRepository patientRepository;
    private final ExaminationRepository examinationRepository;


    @Autowired
    public PatientController(PatientRepository patientRepository,
                             ExaminationRepository examinationRepository) {
        this.patientRepository = patientRepository;
        this.examinationRepository = examinationRepository;
    }

    @GetMapping("/create")
    public String create() {
        return "patients/create";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute Patient patient) {
        patientRepository.save(patient);
        return "redirect:/patient";
    }

    @GetMapping
    public String read(Model model) {
        List<Patient> listOfPatients = patientRepository.findAll();
        model.addAttribute("listOfPatients", listOfPatients);
        return "patients/read";
    }

    @GetMapping("update/{id}")
    public String update(@PathVariable String id, Model model) {
        Optional<Patient> patient = patientRepository.findById(UUID.fromString(id));
        if(patient.isEmpty()) {
            return "error/notfound";
        }
        model.addAttribute("patient", patient.get());
        return "patients/update";
    }

    @ModelAttribute
    private Patient newPatient() {
        return new Patient();
    }
}
