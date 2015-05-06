package edu.uta.ucs;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Owner on 5/5/2015.
 */
public class UserData {
    private static String email;
    private static Boolean militaryTime = false;

    UserData (String email, Boolean militaryTime){
        UserData.setEmail(email);
        UserData.setMilitaryTime(militaryTime);
    }

    UserData(JSONObject userDataJSON) throws JSONException {
        UserData.setEmail(userDataJSON.getString("Email"));
    }

    public static JSONObject toJSON() throws JSONException {
        JSONObject userDataJSON = new JSONObject();

        userDataJSON.put("Email", UserData.getEmail());

        return userDataJSON;
    }


    public static String getEmail() {
        return email;
    }

    public static void setEmail(String email) {
        UserData.email = email;
    }

    public static Boolean isMilitaryTime() {
        return militaryTime;
    }

    public static void setMilitaryTime(Boolean militaryTime) {
        UserData.militaryTime = militaryTime;
    }
}
