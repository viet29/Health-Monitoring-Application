package com.healthapp.controller;

import com.healthapp.model.entity.Examination;
import com.healthapp.model.entity.Patient;
import com.healthapp.model.mqtt.MqttSubscribeModel;
import com.healthapp.repository.ExaminationRepository;
import com.healthapp.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Controller
@RequestMapping("/exam")
public class ExaminationController {
    private final ExaminationRepository examinationRepository;
    private final RestTemplate restTemplate;
    private final PatientRepository patientRepository;

    @Autowired
    public ExaminationController(RestTemplate restTemplate,
                                 ExaminationRepository examinationRepository,
                                 PatientRepository patientRepository) {
        this.examinationRepository = examinationRepository;
        this.restTemplate = restTemplate;
        this.patientRepository = patientRepository;
    }

    private void getData(Model model) {
        List<MqttSubscribeModel> heartRateModel = Arrays.asList(
                Objects.requireNonNull(restTemplate.getForObject(
                        "http://localhost:8080/api/mqtt/subscribe?topic=/Health/bpm&wait_millis=2000",
                        MqttSubscribeModel[].class)));
        List<MqttSubscribeModel> spo2Model = Arrays.asList(
                Objects.requireNonNull(restTemplate.getForObject(
                        "http://localhost:8080/api/mqtt/subscribe?topic=/Health/spo2&wait_millis=2000",
                        MqttSubscribeModel[].class)));
        String bpm = "_";
        String spo2 = "_";
        if (!heartRateModel.isEmpty()) bpm = heartRateModel.get(heartRateModel.size() - 1).getMessage();
        if (!spo2Model.isEmpty()) spo2 = spo2Model.get(spo2Model.size() - 1).getMessage();
        model.addAttribute("bpm", bpm);
        model.addAttribute("spo2",spo2);
        model.addAttribute("temp", "37.1");
    }

    @GetMapping("/{id}")
    public String read(@PathVariable String id, Model model) {
        Optional<Patient> p = patientRepository.findById(UUID.fromString(id));
        if(p.isEmpty()) {
            return "redirect:notfound";
        }
        Patient patient = p.get();
        patient.setExaminations(examinationRepository.findAllByPatient(patient));
        model.addAttribute("patient", patient);
        return "examinations/detail";
    }

    @GetMapping("/{id}/create")
    public String create(Model model, @PathVariable("id") String id) {
        Optional<Patient> p = patientRepository.findById(UUID.fromString(id));
        if(p.isEmpty()) {
            return "redirect:notfound";
        }
        Patient patient = p.get();
        model.addAttribute("patient", patient);
        getData(model);
        return "examinations/create";
    }

    @PostMapping("/{id}/create")
    public String create(@PathVariable("id") String id,
                         @RequestParam("bpm") String bpm,
                         @RequestParam("spo2") String spo2,
                         @RequestParam("temp") String temp) {
        Optional<Patient> p = patientRepository.findById(UUID.fromString(id));
        if(p.isEmpty()) {
            return "redirect:notfound";
        }
        Patient patient = p.get();

        Examination examination = new Examination();
        examination.setPatient(patient);
        examination.setTemperature(Float.parseFloat(temp));
        examination.setHeartRate(Float.parseFloat(bpm));
        examination.setSpO2(Float.parseFloat(spo2));
        examinationRepository.save(examination);

        return "redirect:/exam/" + patient.getId();
    }
}
