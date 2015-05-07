package edu.uta.ucs;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Owner on 5/5/2015.
 */
public class UserData extends Application{

    private static Context context;
    private static String email;
    private static boolean militaryTime;

    public static final String ACTION_LOGOUT = "ACTION_LOGOUT";

    public static Context getContext(){
        return UserData.context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        UserData.context = getApplicationContext();
        UserData.setEmail(null);
        UserData.setMilitaryTime(false);
    }

    public UserData() {
        super();
    }

    UserData (String email, Boolean militaryTime){
    }

    UserData(JSONObject userDataJSON) throws JSONException {
        UserData.setEmail(userDataJSON.getString("Email"));
    }

    public static JSONObject toJSON() throws JSONException {
        JSONObject userDataJSON = new JSONObject();

        userDataJSON.put("Email", UserData.getEmail());

        return userDataJSON;
    }

    public static void logout(Context context){

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(ACTION_LOGOUT);
        Log.i("USER DATA", "Logging Out");
        context.sendBroadcast(broadcastIntent);

        UserData.setEmail(null);
        UserData.setMilitaryTime(false);
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

    public static boolean spoofServer() {

        Context context = UserData.getContext();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        boolean spoofServer = settings.getBoolean(context.getResources().getString(R.string.pref_key_spoof_server), false);

        return spoofServer;
    }

}
