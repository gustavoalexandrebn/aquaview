#include <WiFi.h>

const char* ssid = "Residencial TV";
const char* password = "rmb952186";

void setup() {
  Serial.begin(115200);
  WiFi.begin(ssid, password);

  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.println("Connecting to wifi...");

  }
  Serial.println("Connected to the wifi network");
}

void loop() {
  
}
