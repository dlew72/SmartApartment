package com.dannylewis.smartapartmentapplication;

public class ActionClass {

    private char weekday; //U M T W R F A X
    private short hour; //0 -- 23
    private short minute; // 0 -- 59
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
        //SAMPLE
        // U0930L050100
        String actionString = "";
        actionString += weekday;
        if (hour < 10)
            actionString += "0";
        actionString += hour;
        if (minute < 10)
            actionString += "0";
        actionString += minute;
        actionString += actionType;
        if (param1 < 10)
            actionString += "0";
        if (param1 < 100)
            actionString += "0";
        actionString += param1;
        if (param2 < 10)
            actionString += "0";
        if (param2 < 100)
            actionString += "0";
        actionString += param2;

        return actionString;
    }
}
