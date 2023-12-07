package com.healthapp.config;

import org.eclipse.paho.client.mqttv3.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqttConfig {
    private static final String MQTT_PUBLISHER_ID = "spring-server";
    private static final String MQTT_SERVER_ADDRESS = "tcp://broker.hivemq.com:1883";

    @Bean
    public IMqttClient getInstance() {
        IMqttClient mqttClient = null;
        try {
            mqttClient = new MqttClient(MQTT_SERVER_ADDRESS, MQTT_PUBLISHER_ID);
            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            options.setConnectionTimeout(10);

            if (!mqttClient.isConnected()) {
                mqttClient.connect(options);
                System.out.println("Initialize success!");
            }
        } catch (MqttException e) {
            System.err.println(e.getMessage());
        }

        return mqttClient;
    }
}
