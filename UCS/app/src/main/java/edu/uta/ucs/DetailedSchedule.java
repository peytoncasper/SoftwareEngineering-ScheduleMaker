package edu.uta.ucs;

import edu.uta.ucs.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class DetailedSchedule extends Activity {

    ListView scheduleSections;
    Schedule scheduleToShow;
    MySectionArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_schedule);

        scheduleSections = (ListView) findViewById(R.id.schedule_section_listview);
    }

    @Override
    protected void onStart() {
        super.onStart();


        Intent intent = getIntent();
        if (intent.hasExtra("Schedule Data")) {
            String scheduleString = intent.getStringExtra("Schedule Data");
            try {
                JSONObject scheduleJSON = new JSONObject(scheduleString);
                scheduleToShow = new Schedule(scheduleJSON);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else
            finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        setTitle(scheduleToShow.getName());
        adapter = new MySectionArrayAdapter(this, R.layout.section_list_display, scheduleToShow.getSelectedSections());
        scheduleSections.setAdapter(adapter);
    }
}
