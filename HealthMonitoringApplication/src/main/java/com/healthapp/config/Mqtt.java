package com.healthapp.config;

import org.eclipse.paho.client.mqttv3.*;
import org.springframework.context.annotation.Bean;

public class Mqtt {
    private static final String MQTT_PUBLISHER_ID = "spring-server";
    private static final String MQTT_SERVER_ADDRESS = "tcp://broker.hivemq.com:1883";
    private static IMqttClient instance;

    @Bean
    public static IMqttClient getInstance() {
        try {
            if (instance == null) {
                instance = new MqttClient(MQTT_SERVER_ADDRESS, MQTT_PUBLISHER_ID);
            }

            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            options.setConnectionTimeout(10);

            if (!instance.isConnected()) {
                instance.connect(options);
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }

        return instance;
    }

    private Mqtt() {

    }
}
