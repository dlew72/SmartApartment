#include <ESP8266WiFi.h>
#include <WiFiClient.h>
#include <ESP8266WiFiMulti.h> 
#include <ESP8266HTTPClient.h>
#include <ESP8266LLMNR.h>
#include <ESP8266WebServer.h>   // Include the WebServer library
#include <EEPROM.h>
#include "RTClib.h"
#include <Wire.h>
//#include <BH1750.h>


//BH1750 lightMeter;

ESP8266WebServer server(80);    // Create a webserver object that listens for HTTP request on port 80

//EEPROM Address index
int addr = 0;

//to ensure schedule is only checked once a minute
int prevMin = -1;

//RTC
RTC_DS3231 rtc;

//RTC comparison helper
char daysOfTheWeek[] = {'U', 'M', 'T', 'W', 'R', 'F', 'A'};

struct action {
  char dow;
  int h;
  int m;
  char type;
  int par1;
  int par2;
};

//setup or operation flag
bool operationMode = false;

//pin assignments
int greenLEDpin = 12;
int redLEDpin = 13;
int hardResetButton = 15;

//hardcoded peripheral IPs
String shadeServerIP = "http://192.168.0.178/";
String shadeServerIPSetup = "http://192.168.4.178/";

String lightServerIP = "http://192.168.0.179/";
String lightServerIPSetup = "http://192.168.4.179/";


// function prototypes for HTTP handlers
void handleRoot();              
void handleNotFound();
void handleSchedule();
void handleAction();
void handleGetState();


// misc function prototypes
void hubSetupMode();
void connectToSavedNetwork();
void readEEPROM();
void wipeEEPROM();
void shareCredentials(String, String);
bool checkSchedValid();
void checkSchedule(DateTime);
String getLightState();
String getShadeState();
int getLightSensorBrightness();

//Interrupt
ICACHE_RAM_ATTR void resetButtonPressed();

// software reset function
void(* resetFunc) (void) = 0;


void setup()
{
  Serial.begin(19200);
  Serial.println("Fresh Run");
  EEPROM.begin(1024);
  Wire.begin(14, 2);
  rtc.begin();



  //set pinmodes
  pinMode(greenLEDpin, OUTPUT);
  pinMode(redLEDpin, OUTPUT);
  pinMode(hardResetButton, INPUT);

  //set up interrupt
  attachInterrupt(digitalPinToInterrupt(hardResetButton), resetButtonPressed, RISING);

  /*Light sensor setup
  
     if (lightMeter.begin()) {
    Serial.println(F("BH1750 initialised"));
  }
  else {
    Serial.println(F("Error initialising BH1750"));
  }
  */

  //set power indicator on
  digitalWrite(redLEDpin, HIGH);


  //check if setup byte is set
  if (EEPROM.read(0) == char(7)) {
    //hub was already configured. attempt to connect to network
    connectToSavedNetwork();    
    operationMode = true;
  }
  else {
    //No saved credentials. Enter setup mode
    hubSetupMode();
  }

  //HTTP handlers
  server.on("/", handleRoot);                      // Call the 'handleRoot' function when a client requests URI "/"
  server.on("/credentials", handleCredentials);    // Call the 'handleCredentials' function when a client requests URI "/credentials"
  server.on("/getStates", handleGetState);
  server.on("/schedule", handleSchedule);       // Call the 'handleSchedule' function when a client requests URI "/schedule"
  server.on("/action", handleAction);         // Call the 'handleAction' function when a client requests URI "/action"
  server.onNotFound(handleNotFound);        // When a client requests an unknown URI (i.e. something other than "/"), call function "handleNotFound"

// Actually start the server
  server.begin();                           
  Serial.println("HTTP server started");
}


void loop()
{
  server.handleClient();
  digitalWrite(redLEDpin, HIGH);
  if (operationMode)
    digitalWrite(greenLEDpin, HIGH);
  else
    digitalWrite(greenLEDpin, LOW);
    
  DateTime now = rtc.now();
  if (prevMin != now.minute()) {
    Serial.print("Prevmin:");
    Serial.println(prevMin);
    Serial.print("nowmin:");
    Serial.println(now.minute());
    prevMin = now.minute();
    checkSchedule(now);
  }

  /*Serial.print("Minute");
  Serial.println(now.minute());
  Serial.print("hour");
  Serial.println(now.hour());

    Serial.print("day");
  Serial.println(now.day());*/
}


bool checkSchedValid() {
  addr = 129;
  if (char(EEPROM.read(addr)) == 'U' || char(EEPROM.read(addr)) == 'M' || char(EEPROM.read(addr)) == 'T' || char(EEPROM.read(addr)) == 'W' || char(EEPROM.read(addr)) == 'R' || char(EEPROM.read(addr)) == 'F' || char(EEPROM.read(addr)) == 'A')
    return true;
  return false;
}

void printAction(action a) {
  Serial.println("ACTION ==========");
  Serial.println(a.dow);
  Serial.println(a.h);
  Serial.println(a.m);
  Serial.println(a.type);
  Serial.println(a.par1);
  Serial.println(a.par2);
}

void checkSchedule(DateTime now) {
  Serial.println("Checking Schedule...");

   if (!checkSchedValid()) {
    Serial.println("SCHEDULE NOT VALID...");
    
    return;

   }
   else {
    Serial.println("Schedule Valid!!");
   }
        
   addr = 129;
   action myAct = {char(EEPROM.read(addr)), int(EEPROM.read(addr+1)), int(EEPROM.read(addr+2)), char(EEPROM.read(addr+3)), int(EEPROM.read(addr+4)), int(EEPROM.read(addr+5))};
      printAction(myAct);
   while (myAct.dow != 'X') {
      //Check day of week, then hour, then minute
      //If all match, send action and add action addr to recent list
      Serial.println("Day Of Week:");
      Serial.println(daysOfTheWeek[now.dayOfTheWeek()]);
      Serial.println("Hour:");
      Serial.println(now.hour());
      Serial.println("minute:");
      Serial.println(now.minute());
      
      if (daysOfTheWeek[now.dayOfTheWeek()] == myAct.dow && now.hour() == myAct.h+1 && now.minute() == myAct.m) {
        Serial.println("SENDING ACTION!");
        //Everything matched! Send the action
        if (myAct.type == 'L')
          sendAction(lightServerIP, myAct.par1, myAct.par2);
        if (myAct.type == 'S')
          sendAction(shadeServerIP, myAct.par1, myAct.par2);
     }  
     //go to next action
     addr += 6;
     myAct = {char(EEPROM.read(addr)), int(EEPROM.read(addr+1)), int(EEPROM.read(addr+2)), char(EEPROM.read(addr+3)), int(EEPROM.read(addr+4)), int(EEPROM.read(addr+5))};
   }
}

void handleRoot() {
  Serial.println("ROOT");
  server.send(200, "text/plain", "Hello world 123!");   // Send HTTP status 200 (Ok) and send some text to the browser/client
}

void handleCredentials() {
  Serial.println("CREDENTIALS");
  if (server.hasArg("plain")== false){ //Check if body received
         server.send(400, "text/plain", "fail");
         return;
      }
      
      server.send(200, "text/plain", "success");

      /** the current address in the EEPROM (i.e. which byte we're going to write to next) **/
      addr = 0;
      EEPROM.put(addr, char(7));
      addr++;
      
      String message = server.arg("plain");
      
      String username = message.substring(9, message.indexOf("\",\"p$$$\":\""));

      String password = message.substring(message.indexOf("p$$$")+7, message.length()-2);

      EEPROM.put(addr, username.length());
      addr++;

      for (int i = 0; i < username.length(); i++) {
         EEPROM.put(addr, username[i]);
         addr++;
      }

      EEPROM.put(addr, password.length());
      addr++;

      for (int i = 0; i < password.length(); i++) {
         EEPROM.put(addr, password[i]);
         addr++;
      }

      EEPROM.commit();

      //Share credentials with lights and shade
      shareCredentials(username, password);

      //Try to use credentials to connect:
      WiFi.mode(WIFI_STA);
      IPAddress ip(192, 168, 0, 177);
      IPAddress dns(192, 168, 0, 1);
      IPAddress gateway(192, 168, 0, 1);
      IPAddress subnet(255, 255, 255, 0);
      WiFi.config(ip, dns, gateway, subnet);
      WiFi.begin(username, password);
      unsigned long timeOfStartConnection = millis();
  
      while (1) {
        yield();
        if (WiFi.status() == WL_CONNECTED) {
          //We connected to the router
          operationMode = true;
          break;
        }
        if (millis() - timeOfStartConnection > 10000) {
          //connection failed (timeout)
          break;
        }
        digitalWrite(redLEDpin, !digitalRead(redLEDpin));
        delay(500);
      }

      if (!operationMode) {
        //credentials failed. wipe EEPROM(0) and reset.
        EEPROM.write(0, 0);
        EEPROM.commit();
        Serial.println("RESETTING...");
        resetFunc();
      }
}

void handleNotFound(){
    Serial.println("NOT FOUND");
  server.send(404, "text/plain", "404: Not found"); // Send HTTP status 404 (Not Found) when there's no handler for the URI in the request
}

void handleSchedule() {
  Serial.println("SCHEDULE DETECTED");
  if (server.hasArg("plain")== false){ //Check if body received
           server.send(400, "text/plain", "fail");
           Serial.println("400 fail");
           return;
    }
    
    server.send(200, "text/plain", "success");

    String message = server.arg("plain");

    message = message.substring(13, message.length()-2);


    Serial.println("MESSAGE:");
    Serial.println(message);


    int numberSchedules = message.length()/12;

//"M0830L100000X"
    addr = 129;
    String temp = "";
    String finalThing = "";
    for (int action = 0; action < numberSchedules; action++) { 
      
      finalThing = message[action*12];
      EEPROM.put(addr, message[action*12]);
      addr++;
      
      temp = message[action*12 + 1];
      temp.concat(message[action*12+2]);
      finalThing += temp.toInt();
      EEPROM.put(addr, temp.toInt());
      addr++;
      
      temp = message[action*12 + 3];
      temp.concat(message[action*12+4]);
      finalThing += temp.toInt();
      EEPROM.put(addr, temp.toInt());
      addr++;
      
      finalThing += message[action*12 + 5];
      EEPROM.put(addr, message[action*12 + 5]);
      addr++;
      
      temp = message[action*12 + 6];
      temp.concat(message[action*12+7]);
      temp.concat(message[action*12+8]);
      finalThing += temp.toInt();
      EEPROM.put(addr, temp.toInt());
      addr++;
      
      temp = message[action*12 + 9];
      temp.concat(message[action*12+10]);
      temp.concat(message[action*12+11]);
      finalThing += temp.toInt();
      EEPROM.put(addr, temp.toInt());
      addr++;
    }

    for (int i = 0; i < 13; i++) {
      EEPROM.put(addr, 'X');
      addr++;
    }
    EEPROM.commit();

    readEEPROM();
}

void handleAction(){
    Serial.println("ACTION DETECTED");
    
    if (server.hasArg("plain")== false){ //Check if body received
           server.send(400, "text/plain", "fail");
           Serial.println("400 fail");
           return;
    }
    
    server.send(200, "text/plain", "success");

    String message = server.arg("plain");

    Serial.println("MESSAGE:");
    Serial.println(message);

    if (message.substring(2, 6) != "a###") {
           server.send(400, "text/plain", "invalid syntax");
           Serial.println("400 syntax fail");
           return;
    }
 
    String actionString = message.substring(9, message.length()-2);
        //SAMPLE
        // N0930L050100
        // X0000L050100
    if (actionString[5] == 'L') {
      //send to lights
      sendAction(lightServerIP, actionString.substring(6, 9).toInt(), actionString.substring(9).toInt());
    }
    if (actionString[5] == 'S') {
      //send to shade
      sendAction(shadeServerIP, actionString.substring(6, 9).toInt(), 0);
    }
}

void sendAction(String ip, int p1, int p2) {
  digitalWrite(greenLEDpin, LOW);
  WiFiClient cli;
  HTTPClient http;
  int httpResponseCode;

  http.begin(cli, ip + "action");

  http.addHeader("Content-Type", "text/plain");

  httpResponseCode = http.POST("#" + String(p1) + "$" + String(p2));      
  Serial.print("HTTP Response code: ");
  Serial.println(httpResponseCode);

  http.end();

  digitalWrite(greenLEDpin, HIGH);

}

void wipeEEPROM() {

  EEPROM.write(0, char(0));
  EEPROM.commit();
}

void readEEPROM() {
  for (int i = 0; i < 256; i++) {
     Serial.print("i = ");
     Serial.print(i);
     Serial.print(": ");
     Serial.print(int(EEPROM.read(i)));
     Serial.print(", ");
     Serial.print(char(EEPROM.read(i)));

     Serial.print("\n");
  }
}

void hubSetupMode() {
    Serial.print("Setting soft-AP ... ");
    boolean result = WiFi.softAP("ECE Smart Hub", "seniordesign");
    if(result == true)
    {
      Serial.println("Ready");
      LLMNR.begin("esp8266");
      Serial.println("LLMNR responder started");
    }
  else
    Serial.println("Failure to set up Soft-AP");
}

void connectToSavedNetwork() {
    //Supposedly set up already. Try connecting to network
    String ssid = "";
    String password = "";
    int ssidLen = int(EEPROM.read(1));
    int passwordLen = int(EEPROM.read(2+ssidLen));

    //Serial.println("ssid:");
    for (int i = 0; i < ssidLen; i++) {
      ssid += char(EEPROM.read(2+i));
      //Serial.print(char(EEPROM.read(2+i)));
    }
    
    //Serial.println("pass:");
    for (int i = 0; i < passwordLen; i++) {
      password += char(EEPROM.read(3+i+ssidLen));
      //Serial.print(char(EEPROM.read(3+i+ssidLen)));
    }

    //Serial.println("Password: " + password);

    //Hardcode IP data for now
    IPAddress ip(192, 168, 0, 177);
    IPAddress dns(192, 168, 0, 1);
    IPAddress gateway(192, 168, 0, 1);
    IPAddress subnet(255, 255, 255, 0);

    //Configure WiFi with above parameters
    WiFi.config(ip, dns, gateway, subnet);

    //Attempt connection
    WiFi.mode(WIFI_STA);
    WiFi.begin(ssid, password);
    
    while (WiFi.status() != WL_CONNECTED) {
      //While we aren't connected, flash red indicator light 
        digitalWrite(redLEDpin, !digitalRead(redLEDpin));

        delay(500);
    }

    //Now we are connected to the saved AP.
}

void handleGetState() {
  Serial.println("Handling GET STATE");
  //App requests current device values to initialize sliders on dashboard

  String lightState = getLightState();
  String shadeState = getShadeState();
  
  Serial.print("We are where we think we are");
  //float lux = lightMeter.readLightLevel();
  //Serial.print("Light: ");
  //Serial.print(lux);

  //respond
  String states = "L:" + String(lightState) + "S:" + String(shadeState);
  server.send(200, "text/plain", states);

  
}

String getLightState() {
  WiFiClient cli;
  HTTPClient http;
  int httpResponseCode;

  http.begin(cli, lightServerIP + "getState");

  http.addHeader("Content-Type", "text/plain");

  httpResponseCode = http.GET();      
  Serial.print("HTTP Response code: ");
  Serial.println(httpResponseCode);


  String message = http.getString();

  Serial.println("MESSAGE:");
  Serial.println(message);

  http.end();

  return message;
}

String getShadeState() {
  WiFiClient cli;
  HTTPClient http;
  int httpResponseCode;

  http.begin(cli, shadeServerIP + "getState");

  http.addHeader("Content-Type", "text/plain");

  httpResponseCode = http.GET();      
  Serial.print("HTTP Response code: ");
  Serial.println(httpResponseCode);
  
  String message = http.getString();
  

  http.end();

  return message;
}

int getLightSensorBrightness() {

}

void shareCredentials(String ssid, String pass) {

  WiFiClient cli;
  HTTPClient http;
  int httpResponseCode;

  http.begin(cli, shadeServerIPSetup + "credentials");

  http.addHeader("Content-Type", "text/plain");

  httpResponseCode = http.POST(ssid + ":d@nnyl3w!$@%!$@@:" + pass);      
  Serial.print("HTTP Response code: ");
  Serial.println(httpResponseCode);

  http.end();

  delay(500);

  http.begin(cli, lightServerIPSetup + "credentials");

  http.addHeader("Content-Type", "text/plain");

  httpResponseCode = http.POST(ssid + ":d@nnyl3w!$@%!$@@:" + pass);      
  Serial.print("HTTP Response code: ");
  Serial.println(httpResponseCode);

  http.end();

   
}

ICACHE_RAM_ATTR void resetButtonPressed() {
    Serial.println("WIPING EEPROM");
    wipeEEPROM();
    resetFunc();
}
