package edu.uta.ucs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {
    public static final String ACTION_RESP ="edu.uta.ucs.intent.action.MAIN_ACTIVITY";


    String[] desiredCourseList = {};//{"ENGL-1301","MATH-1426","PHYS-1443","CSE-1105"};
    String baseURL = "http://ucs.azurewebsites.net/UTA/ClassStatus?classes=";
    private ListView scheduleListView;

    ArrayList<Schedule> scheduleArrayList;
    ArrayAdapter<String> scheduleNameAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setContentView(R.layout.activity_main);

        setTitle("Saved Schedules");


        scheduleListView = (ListView) findViewById(R.id.schedule_listview);

    }

    @Override
    protected void onStart() {
        super.onStart();

        scheduleArrayList = Schedule.loadSchedulesFromFile(MainActivity.this);
        ArrayList<String> scheduleNameArrayList = new ArrayList<>(scheduleArrayList.size());
        for (Schedule schedule : scheduleArrayList){
            scheduleNameArrayList.add(schedule.getName());
        }
        scheduleNameAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_selectable_list_item, scheduleNameArrayList);

        scheduleListView.setAdapter(scheduleNameAdapter);
        scheduleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("Main Activity", "Showing Schedule Named: " + scheduleArrayList.get(position).getName());
                scheduleArrayList.get(position).showDetailedView(MainActivity.this);
            }
        });

        scheduleNameAdapter.notifyDataSetChanged();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void generateSchedule(View view){
        Log.d("MainActivity", "Opening Generate Schedule");
        Intent startSelectCoursesActivity = new Intent(MainActivity.this, SelectCourses.class);
        MainActivity.this.startActivity(startSelectCoursesActivity);
    }

}
