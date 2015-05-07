package edu.uta.ucs;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Stores some userdata at login and thoughout application lifecycle. Kind of a hack around, but what else can you do?
 *
 * !!Replace with superior implementation if one is thought up!!
 */
public class UserData extends Application {

    private static Context context;
    private static String email;
    private static boolean militaryTime;

    public static final String ACTION_LOGOUT = "ACTION_LOGOUT";

    public static Context getContext() {
        return UserData.context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        UserData.context = getApplicationContext();
        UserData.setEmail(null);

        Context context = UserData.getContext();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        UserData.setMilitaryTime(settings.getBoolean(context.getResources().getString(R.string.pref_key_military_time), false));

    }

    /**
     * Used by android system to initialize UserData.
     *
     * !!Do not remove!!
     */
    public UserData() {
        super();
    }

    UserData(JSONObject userDataJSON) throws JSONException {
        if(userDataJSON.has("Email"))
            UserData.setEmail(userDataJSON.getString("Email"));
        if(userDataJSON.has("MilitaryTime"))
            UserData.setMilitaryTime(userDataJSON.getBoolean("MilitaryTime"));

        if(userDataJSON.has("SCHEDULES")){
            Log.i("UserData Login","Found Schedules in login data");
            JSONArray schedulesJSONArray = userDataJSON.getJSONArray("SCHEDULES");
            ArrayList<Schedule> schedulesFromServer = Schedule.buildScheduleList(schedulesJSONArray);
            Schedule.saveSchedulesToFile(UserData.getContext(), schedulesFromServer);
        }

        if(userDataJSON.has("BLOCKOUTTIMES")){
            Log.i("UserData Login","Found blockout times in login data");
            JSONArray blockoutTimesJSONArray = userDataJSON.getJSONArray("BLOCKOUTTIMES");
            ArrayList<Course> blockoutTimesFromServer = Course.buildCourseList(blockoutTimesJSONArray);
            SelectBlockoutTimes.saveBlockoutCoursesToFile(UserData.getContext(), blockoutTimesFromServer);
        }

    }

    public static JSONObject toJSON() throws JSONException {
        JSONObject userDataJSON = new JSONObject();

        ArrayList<Schedule> schedules = Schedule.loadSchedulesFromFile(UserData.getContext());
        JSONArray schedulesJSON = new JSONArray();
        for(Schedule schedule : schedules){
            schedulesJSON.put(schedule.toJSON());
        }

        ArrayList<Course> blockoutTimes = SelectBlockoutTimes.loadBlockoutTimesFromFile(UserData.getContext());
        JSONArray blockoutJSON = new JSONArray();
        for(Course course : blockoutTimes){
            blockoutJSON.put(course.toJSON());
        }

        userDataJSON.put("Email", UserData.getEmail());
        userDataJSON.put("MilitaryTime", militaryTime);
        userDataJSON.put("SCHEDULES", schedulesJSON);
        userDataJSON.put("BLOCKOUTTIMES", blockoutJSON);

        return userDataJSON;
    }

    public static void logout(Context context) {

        JSONObject logoutJSON = null;

        try {
            logoutJSON = UserData.toJSON();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        SharedPreferences.Editor logoutLog = context.getSharedPreferences("LOGOUT_LOG", Context.MODE_PRIVATE).edit();
        logoutLog.putString(((Long) System.currentTimeMillis()).toString(), logoutJSON.toString());
        logoutLog.apply();

        HTTPService.PostJSON(LoginActivity.getLOGOUT_URL(), logoutJSON,LoginActivity.ACTION_LOGOUT,UserData.getContext());

        Log.i("UserData Logout", "LOGOUT JSON: " + logoutJSON.toString());

    }


    public static String getEmail() {
        return email;
    }

    public static void setEmail(String email) {
        UserData.email = email;
    }

    public static Boolean useMilitaryTime() {
        Context context = UserData.getContext();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getBoolean(context.getResources().getString(R.string.pref_key_military_time), false);
    }

    public static void setMilitaryTime(Boolean militaryTime) {

        UserData.militaryTime = militaryTime;

        SharedPreferences.Editor settings = PreferenceManager.getDefaultSharedPreferences(UserData.getContext()).edit();
        settings.putBoolean(context.getResources().getString(R.string.pref_key_spoof_server), militaryTime);

    }

    public static boolean spoofServer() {

        Context context = UserData.getContext();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        boolean spoofServer = settings.getBoolean(context.getResources().getString(R.string.pref_key_spoof_server), false);

        return spoofServer;
    }

}
