package edu.uta.ucs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;


public class SelectCourses extends ActionBarActivity {

    public static final String ACTION_DEPARTMENT_SELECT ="edu.uta.ucs.intent.action.ACTION_DEPARTMENT_SELECT";
    public static final String SPOOFED_DEPARTMENT_COURSES ="edu.uta.ucs.intent.action.ACTION_DEPARTMENT_SELECT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_courses);


        LocalBroadcastManager.getInstance(this).registerReceiver(new DepartmentCoursesReceiver(), new IntentFilter(ACTION_DEPARTMENT_SELECT));
    }

    private void getDepartmentCourses(View view){
        String url = null;
        Intent intent = new Intent(this, HTTPGetService.class);
        if(true) {

            intent.putExtra(HTTPGetService.URL_REQUEST, HTTPGetService.SPOOF_SERVER);
            intent.putExtra(HTTPGetService.SPOOFED_RESPONSE, SPOOFED_DEPARTMENT_COURSES);
        }
        else
            intent.putExtra(HTTPGetService.URL_REQUEST, url);
        startService(intent);
    }

    public void SelectBlockoutTimes(View view){
        Intent startSelectCoursesActivity = new Intent(SelectCourses.this, SelectBlockoutTimes.class);
        SelectCourses.this.startActivity(startSelectCoursesActivity);
    }

    private class DepartmentCoursesReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            JSONObject response = null;
            boolean success = false;
            try {
                response = new JSONObject(intent.getStringExtra(HTTPGetService.SERVER_RESPONSE));
                success = response.getBoolean("Success");
                if(success){
                    // enable text field
                }
                else {
                    // Try again?
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
