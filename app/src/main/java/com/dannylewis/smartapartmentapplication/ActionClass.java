package com.dannylewis.smartapartmentapplication;

public class ActionClass {

    private char weekday;
    private short hour;
    private short minute;
    private char actionType; //S or L
    private short param1; //0 -- 100
    private short param2; //0 -- 100

    public ActionClass(char wd, short h, short m, char aT, short p1, short p2) {
        weekday = wd;
        hour = h;
        minute = m;
        actionType = aT;
        param1 = p1;
        param2 = p2;
    }

    public String convertActionToString() {
        return ("" + weekday + hour + minute + actionType + param1 + param2);
    }
}
