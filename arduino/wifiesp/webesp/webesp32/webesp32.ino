#include <WiFi.h>
#include <HTTPClient.h>

const char* ssid = "Residencial TV";
const char* password = "rmb952186";

const char* serverName = "http://192.168.1.16:5000/post_volume";

unsigned long lastTime = 0;
//unsigned long timerDelay = 90000;
unsigned long timerDelay = 90000;

/* Sensor */
int echoPin   = 25;
int triggerPin  = 26;
unsigned int distancia = 0;
unsigned int duracao = 0;


void setup() {
  WiFi.begin(ssid, password);
  Serial.println("Connecting");
  
  while(WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("");
  Serial.print("Connected to WiFi network with IP Address: ");
  Serial.println(WiFi.localIP());
 
  Serial.println("Timer set to 5 seconds (timerDelay variable), it will take 5 seconds before publishing the first reading.");

  /* Sensor */
  pinMode(echoPin, INPUT);
  pinMode(triggerPin, OUTPUT);
  Serial.begin (9600);
}

void loop() {
  //Send an HTTP POST request every 10 minutes
  if ((millis() - lastTime) > timerDelay) {
    //Check WiFi connection status
    if(WiFi.status()== WL_CONNECTED){
      HTTPClient http;
      http.begin(serverName);
      http.addHeader("Content-Type", "application/x-www-form-urlencoded");

      digitalWrite(triggerPin, HIGH); //eleva o sinal do trigger... 
      delayMicroseconds(10);          //... por 10 microssegundos (pulso de 10 microssegundos)
      digitalWrite(triggerPin, LOW);  //abaixa o sinal do trigger... (neste momento, sao enviados 8 pulsos de 40 Khz)
      //na linha abaixo, armazena o valor em microssegundos que foi lido pelo retorno do som (echo)
      duracao = pulseIn(echoPin, HIGH);  //tempo no qual o echo permaneceu em nivel alto (tempo de retorno do echo) 
      //---------------------------------
      distancia = duracao * 0.017; //((340*100)/10^6)/2
      const float pi = 3.14; 
      float h = 25 - distancia;
      Serial.println(distancia);
      float volume = (pi * 81 * h) / 1000;
      float new_volume;
      Serial.println(volume);
      Serial.println(new_volume);
      if(h <= 16){
          new_volume = volume - ((h / 4 ) * 0.27);
      }
      else{
          new_volume = volume - ((h / 5) * 0.27);
      }

      String httpRequestData = "volume=" + String(new_volume) + "&id=078.657.534-46&capacidade=5";           
     
      Serial.println(httpRequestData);     
      int httpResponseCode = http.POST(httpRequestData);
        
      // Free resources
      http.end();
    }
    else {
      Serial.println("WiFi Disconnected");
    }
    lastTime = millis();
  }
}
