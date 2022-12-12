#include <WiFi.h>
 
const char* ssid = "Residencial TV";
const char* password = "rmb952186";
AsyncWebServer server(80);

int echoPin   = 26;
int triggerPin  = 27;
unsigned int distancia = 0;
unsigned int duracao = 0;
 
void setup() {
  Serial.begin(9600);
 
  Serial.println();
  Serial.print("Conectando-se a ");
  Serial.println(ssid);
  WiFi.begin(ssid, password);
 
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
 
  Serial.println("");
  Serial.println("WiFi conectada.");
  Serial.println("Endereço de IP: ");
  Serial.println(WiFi.localIP());

   /* Sensor */
  pinMode(echoPin, INPUT);
  pinMode(triggerPin, OUTPUT);

  server.begin();
}
 
void loop() {
  WiFiClient client = server.available();
  if (client) {
    Serial.println("New Client.");
    String currentLine = "";
    while (client.connected()) {
      if (client.available()) {
        if (currentLine.endsWith("GET /ARDUINO")) {
          digitalWrite(triggerPin, HIGH); //eleva o sinal do trigger... 
          delayMicroseconds(10);          //... por 10 microssegundos (pulso de 10 microssegundos)
          digitalWrite(triggerPin, LOW);  //abaixa o sinal do trigger... (neste momento, sao enviados 8 pulsos de 40 Khz)
          //na linha abaixo, armazena o valor em microssegundos que foi lido pelo retorno do som (echo)
          duracao = pulseIn(echoPin, HIGH);  //tempo no qual o echo permaneceu em nivel alto (tempo de retorno do echo) 
          //---------------------------------
          distancia = duracao * 0.017; //((340*100)/10^6)/2
          const float pi = 3.14; 
          float h = 25 - distancia;
          float volume = (pi * 81 * h) / 1000;
          float new_volume;
          if(h <= 16){
              new_volume = volume - ((h / 4 ) * 0.27);
          }
          else{
              new_volume = volume - ((h / 5) * 0.27);
          }

          //client.send(200, "text/plain", String(new_volume);

          
        }
      }
    }
    client.stop();
    Serial.println("Client Disconnected.");
  }
}
