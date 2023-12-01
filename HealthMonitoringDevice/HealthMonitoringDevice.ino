#include <Wire.h>
#include <Adafruit_Sensor.h>
#include <Adafruit_MAX30100.h>
#include <OneWire.h>
#include <DallasTemperature.h>
#include <WiFi.h>
#include <PubSubClient.h>

// WiFi credentials
const char *ssid = "VIET";
const char *password = "29122002";

// MQTT broker details
const char *mqtt_server = "mqtt-broker-IP";
const char *mqtt_username = "viet29";
const char *mqtt_password = "12345";
const char *client_id = "ESP32_Client";

// Create instances for sensors
Adafruit_MAX30100 pulseOxSensor;
OneWire oneWire(2);  // Pin 2 for DS18B20
DallasTemperature sensors(&oneWire);

WiFiClient espClient;
PubSubClient client(espClient);

void setup() {
  Serial.begin(115200);

  // Connect to Wi-Fi
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) {
    delay(1000);
    Serial.println("Connecting to WiFi...");
  }
  Serial.println("Connected to WiFi");

  // Connect to MQTT broker
  client.setServer(mqtt_server, 1883);
  while (!client.connected()) {
    Serial.println("Connecting to MQTT...");
    if (client.connect(client_id, mqtt_username, mqtt_password)) {
      Serial.println("Connected to MQTT");
    } else {
      Serial.print("Failed, rc=");
      Serial.println(client.state());
      delay(2000);
    }
  }

  // Initialize sensors
  if (!pulseOxSensor.begin()) {
    Serial.println("MAX30100 not found. Please check wiring/power.");
    while (1);
  }

  sensors.begin();
}

void loop() {
  // Read sensor values
  // float spo2 = pulseOxSensor.getSpO2();
  // float bloodPressure = pulseOxSensor.getBloodPressure();
  
  // sensors.requestTemperatures();
  // float temperature = sensors.getTempCByIndex(0);

  float spo2 = random(10);
  float bloodPressure = random(100);
  float temperature = random(100);

  // Publish values to MQTT
  char topic[50];
  snprintf(topic, sizeof(topic), "esp32/%s/spo2", client_id);
  client.publish(topic, String(spo2).c_str());

  snprintf(topic, sizeof(topic), "esp32/%s/blood_pressure", client_id);
  client.publish(topic, String(bloodPressure).c_str());

  snprintf(topic, sizeof(topic), "esp32/%s/temperature", client_id);
  client.publish(topic, String(temperature).c_str());

  delay(5000);  // Adjust delay as needed
}
