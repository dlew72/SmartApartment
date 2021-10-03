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
int out2pin = 14;
int out1pin = 16;
int hardResetButton = 15;

//hardcoded smart device IPs
String hubServerIP = "https://192.168.0.177/";

// function prototypes for HTTP handlers
void handleCredentials();
void handleAction();
void handleRoot();
void handleNotFound();

// misc function prototypes
void hubSetupMode();
void connectToSavedNetwork();
void readEEPROM();
void wipeEEPROM();

// software reset function
void(* resetFunc) (void) = 0;


void setup()
{
  Serial.begin(115200);
  Serial.println("Fresh Run -- lights");
  EEPROM.begin(1024);

  //set pinmodes
  pinMode(greenLEDpin, OUTPUT);
  pinMode(redLEDpin, OUTPUT);
  pinMode(out1pin, OUTPUT);
  pinMode(out2pin, OUTPUT);
  pinMode(hardResetButton, INPUT);

  //set power indicator on
  digitalWrite(redLEDpin, HIGH);

  analogWriteRange(255);

  analogWriteFreq(150);


  //check if setup byte is set
  if (EEPROM.read(0) == char(7)) {
    Serial.println("7 BIT SET");
    //connection settings already configured. attempt to connect to network
    connectToSavedNetwork();
    operationMode = true;
  }
  else {
    //No saved credentials. Enter setup mode
    Serial.println("7 BIT NOT SET");

    shadeSetupMode();
  }

  //HTTP handlers
  server.on("/", handleRoot);                      // Call the 'handleRoot' function when a client requests URI "/"
  server.on("/credentials", handleCredentials);    // Call the 'handleCredentials' function when a client requests URI "/credentials"
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
  server.send(200, "text/plain", "Hello world 789!");   // Send HTTP status 200 (Ok) and send some text to the browser/client
}

void handleCredentials() {
  Serial.println("CREDENTIALS");
  if (server.hasArg("plain") == false) { //Check if body received
    server.send(400, "text/plain", "fail");
    return;
  }

  server.send(200, "text/plain", "success");

  /** the current address in the EEPROM (i.e. which byte we're going to write to next) **/
  addr = 0;
  EEPROM.put(addr, char(7));
  addr++;

  String message = server.arg("plain");

  //credentials are shared as such:
  // "ssid":d@nnyl3w!$@%!$@@:"pass"

  String username = message.substring(0, message.indexOf(":d@nnyl3w!$@%!$@@:"));

  String password = message.substring(message.indexOf(":d@nnyl3w!$@%!$@@:") + 18, message.length());
  Serial.println("ssid: ");
  Serial.println(username);

  Serial.println("password: ");
  Serial.println(password);

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

  //Try to use credentials to connect:
  WiFi.mode(WIFI_STA);
  IPAddress ip(192, 168, 0, 179);
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

void handleNotFound() {
  Serial.println("NOT FOUND");
  server.send(404, "text/plain", "404: Not found"); // Send HTTP status 404 (Not Found) when there's no handler for the URI in the request
}

void wipeEEPROM() {
  Serial.println("WIPE EEPROM");

  EEPROM.write(0, char(0));
  EEPROM.commit();
}

void readEEPROM() {
  Serial.println("READ EEPROM");

  for (int i = 0; i < 100; i++)
    Serial.print(EEPROM.read(i));
}

void shadeSetupMode() {
  Serial.println("SHADE SETUP MODE");

  //autoconnect to hub
  IPAddress ip(192, 168, 4, 179);
  IPAddress dns(192, 168, 4, 1);
  IPAddress gateway(192, 168, 4, 1);
  IPAddress subnet(255, 255, 255, 0);

  //Configure WiFi with above parameters
  WiFi.config(ip, dns, gateway, subnet);

  //Attempt connection
  WiFi.mode(WIFI_STA);
  WiFi.begin("ECE Smart Hub", "seniordesign");

  while (WiFi.status() != WL_CONNECTED) {
    Serial.println("connection failed, trying again");

    //While we aren't connected to hub, flash red indicator light
    digitalWrite(redLEDpin, !digitalRead(redLEDpin));

    //Check to see if hard reset button is pressed
    if (digitalRead(hardResetButton) == HIGH) {
      Serial.println("WIPING EEPROM");
      wipeEEPROM();
      resetFunc();
    }

    delay(500);
  }
  Serial.println("we are connected to hub");

}

void connectToSavedNetwork() {
  Serial.println("CONNECT TO SAVED NETWORK");

  //Supposedly set up already. Try connecting to network
  String ssid = "";
  String password = "";
  int ssidLen = int(EEPROM.read(1));
  int passwordLen = int(EEPROM.read(2 + ssidLen));

  //Serial.println("ssid:");
  for (int i = 0; i < ssidLen; i++) {
    ssid += char(EEPROM.read(2 + i));
    //Serial.print(char(EEPROM.read(2+i)));
  }

  //Serial.println("pass:");
  for (int i = 0; i < passwordLen; i++) {
    password += char(EEPROM.read(3 + i + ssidLen));
    //Serial.print(char(EEPROM.read(3+i+ssidLen)));
  }

  //Serial.println("Password: " + password);

  //Hardcode IP data for now
  IPAddress ip(192, 168, 0, 179);
  IPAddress dns(192, 168, 0, 1);
  IPAddress gateway(192, 168, 0, 1);
  IPAddress subnet(255, 255, 255, 0);

  //Configure WiFi with above parameters
  WiFi.config(ip, dns, gateway, subnet);

  //Attempt connection
  WiFi.mode(WIFI_STA);
  WiFi.begin(ssid, password);

  while (WiFi.status() == WL_CONNECTED) {
    //While we aren't connected, flash green indicator light
    digitalWrite(greenLEDpin, !digitalRead(greenLEDpin));

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

void handleAction(){
    Serial.println("ACTION DETECTED");
    
    if (server.hasArg("plain")== false){ //Check if body received
           server.send(400, "text/plain", "fail");
           Serial.println("400 fail");
           return;
    }
    
    server.send(200, "text/plain", "success");

    String message = server.arg("plain");
        //SAMPLE
        // #95$100

     int brightness = message.substring(1, message.indexOf('$')).toInt();
     int warmth = message.substring(message.indexOf('$')+1).toInt();

      Serial.println("MESSAGE:");
      Serial.println(message);
            
      Serial.println("bright:");
      Serial.println(brightness);
      
      Serial.println("warmth:");
      Serial.println(warmth);

      Serial.println("out1:");
      Serial.println(brightness/100.0*255);

      Serial.println("out2:");
      Serial.println(warmth/100.0*255);
    
     analogWrite(out1pin, brightness/100.0*255);
     analogWrite(out2pin, warmth/100.0*255);
    
}
