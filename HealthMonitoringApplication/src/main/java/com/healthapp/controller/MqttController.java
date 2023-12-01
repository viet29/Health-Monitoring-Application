package com.healthapp.controller;

import com.healthapp.config.Mqtt;
import com.healthapp.model.Examination;
import lombok.Value;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@RestController
@RequestMapping("/api/mqtt")
public class MqttController {

    @Value("${mqtt.broker}")
    private String broker;


}
