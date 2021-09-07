package com.dannylewis.smartapartmentapplication.ui.main;

public class ActionClass {

    private char weekday;
    private short hour;
    private short minute;
    private char actionType;
    private short intensity;

    public ActionClass(char wd, short h, short m, char aT, short i) {
        weekday = wd;
        hour = h;
        minute = m;
        actionType = aT;
        intensity = i;
    }

    public String convertActionToString() {
        return ("" + weekday + hour + minute + actionType + intensity);
    }
}
