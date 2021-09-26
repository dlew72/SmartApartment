#include <ESP8266WiFi.h>
#include <WiFiClient.h>
#include <ESP8266WiFiMulti.h> 
#include <ESP8266mDNS.h>
#include <ESP8266WebServer.h>   // Include the WebServer library
#include <EEPROM.h>

ESP8266WebServer server(80);    // Create a webserver object that listens for HTTP request on port 80

int addr = 0;
bool operationMode = false;

void handleRoot();              // function prototypes for HTTP handlers
void handleNotFound();
void(* resetFunc) (void) = 0;


void setup()
{
  Serial.begin(115200);
  Serial.println();

  if (EEPROM.read(0) == char(7)) {
    //Supposedly set up. Try connecting
    String ssid = "";
    String password = "";
    int ssidLen = EEPROM.read(1);
    int passwordLen = EEPROM.read(2+ssidLen);

    for (int i = 0; i < ssidLen; i++) {
      ssid += EEPROM.read(2+i);
    }

    for (int i = 0; i < passwordLen; i++) {
      password += EEPROM.read(2+i);
    }

    WiFi.mode(WIFI_STA);
    WiFi.begin(ssid, password);
    unsigned long timeOfStartConnection = millis();

    
    while (1) {
      if (WiFi.status() == WL_CONNECTED) {
        //We connected to the router
        operationMode = true;
        break;
      }
      if (millis() - timeOfStartConnection > 10000) {
        //connection failed (timeout)
        break;
      }
      //TODO: blink red light
    }

    if (!operationMode) {
      Serial.println("Connection failed. Trying again...");
      loopAttemptConnection();
    }

  }
  else {
    //No saved credentials. Enter setup mode
    hubSetupMode();
  }

  server.on("/", handleRoot);                      // Call the 'handleRoot' function when a client requests URI "/"
  server.on("/credentials", handleCredentials);    // Call the 'handleRoot' function when a client requests URI "/credentials"
  server.on("/schedule", handleCredentials);       // Call the 'handleRoot' function when a client requests URI "/schedule"
  server.on("/action", handleCredentials);         // Call the 'handleRoot' function when a client requests URI "/action"


  server.onNotFound(handleNotFound);        // When a client requests an unknown URI (i.e. something other than "/"), call function "handleNotFound"

  server.begin();                           // Actually start the server
  Serial.println("HTTP server started");
}

void hubSetupMode() {
    Serial.print("Setting soft-AP ... ");
    boolean result = WiFi.softAP("ECE Smart Hub", "seniordesign");
    if(result == true)
    {
      Serial.println("Ready");
      if (MDNS.begin("esp8266")) {              // Start the mDNS responder for esp8266.local
      Serial.println("mDNS responder started");
    } else {
      Serial.println("Error setting up MDNS responder!");
    }
  }
  else
    Serial.println("Failure to set up Soft-AP");
}

void loop()
{
  server.handleClient();
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
      EEPROM.write(addr, char(7));
      addr++;
      
      String message = server.arg("plain");
      
      String username = message.substring(9, message.indexOf("\",\"p$$$\":\""));

      String password = message.substring(message.indexOf("p$$$")+7, message.length()-2);

      EEPROM.write(addr, username.length());
      addr++;

      for (int i = 0; i < username.length(); i++) {
         EEPROM.write(addr, username[i]);
         addr++;
      }

      EEPROM.write(addr, password.length());
      addr++;

      for (int i = 0; i < password.length(); i++) {
         EEPROM.write(addr, password[i]);
         addr++;
      }

      //Try to use credentials to connect:
      WiFi.mode(WIFI_STA);
      WiFi.begin(username, password);
      unsigned long timeOfStartConnection = millis();
  
      while (1) {
        if (WiFi.status() == WL_CONNECTED) {
          //We connected to the router
          operationMode = true;
          break;
        }
        if (millis() - timeOfStartConnection > 10000) {
          //connection failed (timeout)
          break;
        }
        //TODO: blink red light
      }

      if (!operationMode) {
        //credentials failed. wipe EEPROM(0) and reset.
        EEPROM.write(0, 0);
        Serial.println("RESETTING...");
        resetFunc();
      }
      
 
}

void handleNotFound(){
    Serial.println("NOT FOUND");
  server.send(404, "text/plain", "404: Not found"); // Send HTTP status 404 (Not Found) when there's no handler for the URI in the request
}

void loopAttemptConnection() {
   while(1) {
      if (WiFi.status() == WL_CONNECTED) {
        //We connected to the router
        operationMode = true;
        break;
      }
      else {
        delay(500);
        Serial.println("Connection failed. Trying again...");
      }
   }
}

void clearSavedCredentials() {
  for (int i = 0; i < 100; i++)
    EEPROM.write(i, 0);
}
