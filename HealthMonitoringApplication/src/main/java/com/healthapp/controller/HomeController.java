package com.healthapp.controller;

import com.healthapp.model.entity.Patient;
import com.healthapp.repository.ExaminationRepository;
import com.healthapp.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
@RequestMapping("")
public class HomeController {

    private final PatientRepository patientRepository;
    private final ExaminationRepository examinationRepository;

    @Autowired HomeController(PatientRepository patientRepository,
                              ExaminationRepository examinationRepository) {
        this.patientRepository = patientRepository;
        this.examinationRepository = examinationRepository;
    }

    @GetMapping({"", "/index"})
    public String home() {
        return "index";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }

    @GetMapping("/login")
    public String login(Model model) {
        return "login";
    }

    @GetMapping("/find")
    public String find(@RequestParam(value = "id", required = false) String id, Model model) {
        if(id != null) {
            Optional<Patient> p = patientRepository.findById(id);
            if(p.isEmpty()) {
                return "redirect:/find?error";
            }
            Patient patient = p.get();
            patient.setExaminations(examinationRepository.findAllByPatient(patient));
            model.addAttribute("patient", patient);
        }

        return "find";
    }
}
