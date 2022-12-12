/* Sensor ultrassonico com Arduino *
 * by Garrido, Diego			   *			  
 * modified by Oliveira, Felipe    */

int echoPin 	= 26;
int triggerPin  = 27;
unsigned int distancia = 0;
unsigned int duracao = 0;

void setup ()
{
  pinMode(echoPin, INPUT);
  pinMode(triggerPin, OUTPUT);
  Serial.begin (9600);
}

void loop ()
{
  digitalWrite(triggerPin, HIGH); //eleva o sinal do trigger... 
  delayMicroseconds(10);          //... por 10 microssegundos (pulso de 10 microssegundos)
  digitalWrite(triggerPin, LOW);  //abaixa o sinal do trigger... (neste momento, sao enviados 8 pulsos de 40 Khz)
  //na linha abaixo, armazena o valor em microssegundos que foi lido pelo retorno do som (echo)
  duracao = pulseIn(echoPin, HIGH);  //tempo no qual o echo permaneceu em nivel alto (tempo de retorno do echo) 
  //---------------------------------
  distancia = duracao * 0.017; //((340*100)/10^6)/2
  /*Entendendo a formula acima:
   *velocidade do som no ar: 340 m/s
   *multiplica-se a velocidade por 100 para transforma-la em cm
   *divide-se a multiplicacao por 1.000.000 (10^6), pois a duracao estah em microssegundos
   *divide-se tudo por 2, pois o sinal ultrassonico viaja ateh o objeto e depois retorna para a origem */
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
  
 
  //Serial.print(distancia);
  //Serial.println (" cm");
  Serial.print (new_volume);
  Serial.println (" L");
  delay (100);
}
