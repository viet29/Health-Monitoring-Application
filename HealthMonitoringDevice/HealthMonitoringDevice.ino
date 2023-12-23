#include <Wire.h>
#include <OneWire.h>
#include <DallasTemperature.h>
#include "MAX30100_PulseOximeter.h"
#include <PubSubClient.h>
#include <WiFi.h>

#define REPORTING_PERIOD_MS     1000
#define ONE_WIRE_BUS 25 // DS18B20 on arduino pin25 corresponds to D25 on ESP32

OneWire oneWire(ONE_WIRE_BUS);
DallasTemperature DS18B20(&oneWire);

const char* ssid = "cunco";
const char* password = "0919468995";
const char* mqtt_server = "broker.hivemq.com";

WiFiClient espClient;
PubSubClient client(espClient);

// Create a PulseOximeter object
PulseOximeter pox;

// Time at which the last beat occurred
uint32_t tsLastReport = 0;

// Callback routine is executed when a pulse is detected
void onBeatDetected() {
    Serial.println("♥ Beat!");
}

void setup_wifi() { 
  delay(10);
  // We start by connecting to a WiFi network
  Serial.println();
  Serial.print("Connecting to ");
  Serial.println(ssid);

  WiFi.mode(WIFI_STA); 
  WiFi.begin(ssid, password); 

  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }

  randomSeed(micros());

  Serial.println("");
  Serial.println("WiFi connected");
  Serial.println("IP address: ");
  Serial.println(WiFi.localIP());
}

void callback(char* topic, byte* payload, unsigned int length) {
  String temp; 
  Serial.print("Message arrived [");
  Serial.print(topic);
  Serial.print("] ");
  for (int i = 0; i < length; i++) { 
    temp += (char)payload[i];
  }
  Serial.println(temp);
}

void reconnect() { 
  // Loop until we're reconnected
  while (!client.connected()) {
    Serial.print("Attempting MQTT connection...");
    String clientId = "ESP32Client-";
    clientId += String(random(0xffff), HEX);
    // Attempt to connect
    if (client.connect(clientId.c_str())) {
      Serial.println("Connected to " + clientId);
      client.publish("/PTIT_Test/p/mqtt", "PTIT_Test"); 
    } else {
      Serial.print("failed, rc=");
      Serial.print(client.state());
      Serial.println(" try again in 5 seconds");
      delay(2000);
    }
  }
}

void setup_sensor() {
  // Initialize sensor
  if (!pox.begin()) {
      Serial.println("MAX30100 INITIALIZED FAILED");
      for(;;);
  } else {
      Serial.println("MAX30100 INITIALIZED SUCCESS");
      pox.setOnBeatDetectedCallback(onBeatDetected);
  }
  pox.setIRLedCurrent(MAX30100_LED_CURR_7_6MA);
  DS18B20.begin();
}

void setup() {
    Serial.begin(115200);
    Serial.print("Initializing pulse oximeter..");
    setup_wifi(); 
    client.setServer(mqtt_server, 1883); 
    client.setCallback(callback); 
    setup_sensor();
}

void loop() {
  if (!client.connected()) {
    reconnect();
  }
  client.loop();

  // Read from the sensor
  pox.update();
  float BPM = pox.getHeartRate();
  float SpO2 = pox.getSpO2();
  DS18B20.requestTemperatures(); 
  float tempC = DS18B20.getTempCByIndex(0);

  int randomInt = random(1001);
  tempC = map(randomInt, 0, 1000, 360, 405) / 10.0;
  BPM = map(randomInt, 0, 1000, 600, 1350) / 10.0;
  SpO2 = map(randomInt, 0, 1000, 750, 990) / 10.0;
  
  if (millis() - tsLastReport > REPORTING_PERIOD_MS) {
      Serial.print("Heart rate:");
      Serial.print(BPM);
      Serial.print("bpm / SpO2:");
      Serial.print(SpO2);
      Serial.print("% / Temp:");
      Serial.print(tempC);
      Serial.println("C");
      Serial.println("*******************************");
      Serial.println();

      String bpm = String(BPM, 2);
      String spo2 = String(SpO2, 2);
      String temp = String(tempC, 2);

      String res = bpm + "|" + spo2 + "|" + temp;
      client.publish("/Health/data", res.c_str());
      tsLastReport = millis();
  }
}