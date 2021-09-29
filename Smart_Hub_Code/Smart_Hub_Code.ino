#include <ESP8266WiFi.h>
#include <WiFiClient.h>
#include <ESP8266WiFiMulti.h> 
#include <ESP8266HTTPClient.h>
#include <ESP8266LLMNR.h>
#include <ESP8266WebServer.h>   // Include the WebServer library
#include <EEPROM.h>

ESP8266WebServer server(80);    // Create a webserver object that listens for HTTP request on port 80

//EEPROM Address index
int addr = 0;

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

// software reset function
void(* resetFunc) (void) = 0;


void setup()
{
  Serial.begin(115200);
  Serial.println("Fresh Run");
  EEPROM.begin(1024);

  //set pinmodes
  pinMode(greenLEDpin, OUTPUT);
  pinMode(redLEDpin, OUTPUT);
  pinMode(hardResetButton, INPUT);

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

  if (digitalRead(hardResetButton) == HIGH) {
    Serial.println("WIPING EEPROM");
    wipeEEPROM();
    resetFunc();
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
  for (int i = 0; i < 100; i++)
   Serial.print(EEPROM.read(i));
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

        //Check to see if hard reset button is pressed
        if (digitalRead(hardResetButton) == HIGH) {
            Serial.println("WIPING EEPROM");
            wipeEEPROM();
            resetFunc();
          }
          
        delay(500);
    }

    //Now we are connected to the saved AP.
}

void handleGetState() {
  //App requests current device values to initialize sliders on dashboard
  
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

  http.begin(cli, lightServerIPSetup + "credentials");

  http.addHeader("Content-Type", "text/plain");

  httpResponseCode = http.POST(ssid + ":d@nnyl3w!$@%!$@@:" + pass);      
  Serial.print("HTTP Response code: ");
  Serial.println(httpResponseCode);

  http.end();

  

  
}
