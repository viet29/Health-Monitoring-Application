package com.healthapp.controller;

import com.healthapp.exception.ExceptionMessage;
import com.healthapp.exception.MqttException;
import com.healthapp.model.mqtt.MqttPublishModel;
import com.healthapp.model.mqtt.MqttSubscribeModel;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/mqtt")
public class MqttController {

    private final IMqttClient mqttClient;

    @Autowired
    public MqttController(IMqttClient mqttClient) {
        this.mqttClient = mqttClient;
    }

    @PostMapping("publish")
    public void publishMessage(@RequestBody @Valid MqttPublishModel messagePublishModel,
                               BindingResult bindingResult) throws org.eclipse.paho.client.mqttv3.MqttException {
        if (bindingResult.hasErrors()) {
            throw new MqttException(ExceptionMessage.SOME_PARAMETERS_INVALID);
        }

        MqttMessage mqttMessage = new MqttMessage(messagePublishModel.getMessage().getBytes());
        mqttMessage.setQos(messagePublishModel.getQos());
        mqttMessage.setRetained(messagePublishModel.getRetained());

        mqttClient.publish(messagePublishModel.getTopic(), mqttMessage);
    }

    @GetMapping("subscribe")
    public List<MqttSubscribeModel> subscribeChannel(@RequestParam(value = "topic") String topic,
                                                     @RequestParam(value = "wait_millis") Integer waitMillis) {
        List<MqttSubscribeModel> messages = new ArrayList<>();
        try {
            CountDownLatch countDownLatch = new CountDownLatch(10);
            mqttClient.subscribeWithResponse(topic, (s, mqttMessage) -> {
                MqttSubscribeModel mqttSubscribeModel = new MqttSubscribeModel();
                mqttSubscribeModel.setId(mqttMessage.getId());
                mqttSubscribeModel.setMessage(new String(mqttMessage.getPayload()));
                mqttSubscribeModel.setQos(mqttMessage.getQos());
                messages.add(mqttSubscribeModel);
                countDownLatch.countDown();
            });
            countDownLatch.await(waitMillis, TimeUnit.MILLISECONDS);
        } catch (org.eclipse.paho.client.mqttv3.MqttException e) {
            System.err.println("Error 1: " + e.getMessage());
        } catch (InterruptedException e) {
            System.err.println("Error 2: " + e.getMessage());
        }
        return messages;
    }


}
