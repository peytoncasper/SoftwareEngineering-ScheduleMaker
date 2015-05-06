package edu.uta.ucs;

import android.app.Application;
import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Owner on 5/5/2015.
 */
public class UserData extends Application{

    private static Context context;
    private static String email;
    private static Boolean militaryTime = false;

    public static Context getContext(){
        return UserData.context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        UserData.context = getApplicationContext();
    }

    public UserData() {
        super();
    }

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
