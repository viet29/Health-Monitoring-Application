package com.healthapp.controller;

import com.healthapp.model.api.PredictResult;
import com.healthapp.model.entity.Examination;
import com.healthapp.model.entity.Patient;
import com.healthapp.model.mqtt.MqttSubscribeModel;
import com.healthapp.repository.ExaminationRepository;
import com.healthapp.repository.PatientRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.RestTemplate;

import java.security.Principal;
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
        float bpm = -1.0f;
        float spo2 = -1.0f;
        float temp = -1.0f;
        float result = -1.0f;

        try {
            List<MqttSubscribeModel> dataModel = Arrays.asList(
                    Objects.requireNonNull(restTemplate.getForObject(
                            "http://localhost:8080/api/mqtt/subscribe?topic=/Health/data&wait_millis=2000",
                            MqttSubscribeModel[].class)));

            if (!dataModel.isEmpty()) {
                String res = dataModel.get(dataModel.size() - 1).getMessage();
                System.out.println(res);
                String[] data = res.split("\\|");
                bpm = Float.parseFloat(data[0]);
                spo2 = Float.parseFloat(data[1]);
                temp = Float.parseFloat(data[2]);
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }

        if (bpm > 0 && spo2 > 0 && temp > 0) {
            String apiUrl = "http://127.0.0.1:5000/predict";
            String jsonBody = String.format("{\"bpm\": %s, \"temp\": %s, \"spo2\": %s}",
                    String.valueOf(bpm).replace(',', '.'),
                    String.valueOf(temp).replace(',', '.'),
                    String.valueOf(spo2).replace(',', '.'));
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);
            System.out.println("Entity: " + entity);
            System.out.println("JSON: " + jsonBody);
            ResponseEntity<PredictResult> response = restTemplate.postForEntity(apiUrl, entity, PredictResult.class);

            result = Objects.requireNonNull(response.getBody()).getResult();
        }

        model.addAttribute("bpm", bpm);
        model.addAttribute("spo2",spo2);
        model.addAttribute("temp", temp);
        model.addAttribute("result", result);
    }

    @GetMapping("/{id}")
    public String read(@PathVariable("id") String id, Model model) {
        Optional<Patient> p = patientRepository.findById(id);
        if(p.isEmpty()) {
            return "redirect:notfound";
        }
        Patient patient = p.get();
        patient.setExaminations(examinationRepository.findAllByPatient(patient));
        model.addAttribute("patient", patient);
        return "examinations/detail";
    }

    @GetMapping("/{id}/create")
    public String create(Model model, @PathVariable("id") String id, HttpServletRequest request) {
        Optional<Patient> p = patientRepository.findById(id);
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
                         @RequestParam("temp") String temp,
                         @RequestParam("result") String result) {
        Optional<Patient> p = patientRepository.findById(id);
        if(p.isEmpty()) {
            return "redirect:/notfound";
        }
        Patient patient = p.get();

        Examination examination = new Examination();
        examination.setPatient(patient);
        examination.setTemperature(Float.parseFloat(temp));
        examination.setHeartRate(Float.parseFloat(bpm));
        examination.setSpO2(Float.parseFloat(spo2));
        examination.setResult(Float.parseFloat(result));
        examinationRepository.save(examination);

        return "redirect:/exam/" + patient.getId();
    }

    @GetMapping("/{id}/delete/{id2}")
    public String delete(@PathVariable("id") String id, @PathVariable("id2") String id2) {
        Optional<Patient> p = patientRepository.findById(id);
        if(p.isEmpty()) {
            return "redirect:notfound";
        }
        Optional<Examination> e = examinationRepository.findById(Long.parseLong(id2));
        if(e.isEmpty()) {
            return "redirect:notfound";
        }
        examinationRepository.delete(e.get());
        return "redirect:/exam/" + id;
    }
}
