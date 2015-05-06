package edu.uta.ucs;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    private ListView scheduleListView;

    ArrayList<Schedule> scheduleArrayList;
    ArrayAdapter<String> scheduleNameAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        scheduleNameAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_selectable_list_item, scheduleNameArrayList);

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id){
            case R.id.action_settings:
                SettingsActivity.startActivity(MainActivity.this);
                break;
        }

        return super.onOptionsItemSelected(item);
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
