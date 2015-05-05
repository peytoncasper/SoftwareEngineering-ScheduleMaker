package edu.uta.ucs;

import edu.uta.ucs.util.SystemUiHider;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class DetailedSchedule extends Activity {

    public static final String URL_GET_COURSE_SECTIONS ="http://ucs.azurewebsites.net/UTA/GetCourseInfo?";
    public static final String URL_GET_COURSE_SECTIONS_PARAM_SEMESTER ="semester=";
    public static final String URL_GET_COURSE_SECTIONS_PARAM_DEPARTMENT ="&department=";
    public static final String URL_GET_COURSE_SECTIONS_PARAM_COURSENUMBER ="&courseNumber=";

    private static final String ACTION_GET_COURSE_SECTIONS = "ACTION_GET_COURSE_SECTIONS";

    ListView scheduleSections;
    Schedule scheduleToShow;
    MySectionArrayAdapter adapter;
    ProgressDialog progressDialog;

    int selection;
    boolean saveCheck = false;

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

        LocalBroadcastManager.getInstance(this).registerReceiver(new CouresAlternatesReciever(), new IntentFilter(ACTION_GET_COURSE_SECTIONS));
    }

    @Override
    protected void onResume() {
        super.onResume();

        setTitle(scheduleToShow.getName());
        adapter = new MySectionArrayAdapter(this, R.layout.section_list_display, scheduleToShow.getSelectedSections());
        scheduleSections.setAdapter(adapter);
    }

    public void saveSchedule(View view){


        final AlertDialog.Builder saveNameDialog = new AlertDialog.Builder(this);

        saveNameDialog.setTitle("Save as");
        saveNameDialog.setMessage("What do you want to save this set of times as?");

        final EditText blockoutNameEditTextDialog = new EditText(DetailedSchedule.this);
        saveNameDialog.setView(blockoutNameEditTextDialog);

        saveNameDialog.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {

            /**
             * This method will be invoked when a button in the dialog is clicked.
             *
             * @param dialog The dialog that received the click.
             * @param which  The button that was clicked (e.g.
             *               {@link android.content.DialogInterface#BUTTON1}) or the position
             */
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String scheduleSaveName = blockoutNameEditTextDialog.getEditableText().toString();
                setName(scheduleSaveName);
                saveScheduleToFile(scheduleToShow);
            }
        });

        saveNameDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {

            /**
             * This method will be invoked when a button in the dialog is clicked.
             *
             * @param dialog The dialog that received the click.
             * @param which  The button that was clicked (e.g.
             *               {@link android.content.DialogInterface#BUTTON1}) or the position
             */
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        saveNameDialog.show();
    }

    public void verifySchedule(View view){

    }

    public void deleteSchedule(View view){
        this.removeScheduleFromFile(this.scheduleToShow.getName());
        finish();
    }

    public void editSchedule(View view){
        MySectionArrayAdapter arrayAdapter = new MySectionArrayAdapter(DetailedSchedule.this, R.layout.section_list_display, scheduleToShow.getSelectedSections());

        AlertDialog.Builder builder = new AlertDialog.Builder(DetailedSchedule.this);
        builder.setTitle("Select the course for which you want to swap sections");
        builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Section section = scheduleToShow.getSelectedSections().get(which);
                scheduleToShow.getSelectedSections().remove(which);
                setSelection(which);
                getSections( section.getSourceCourse().getCourseName(), section.getSourceCourse().getCourseID());
                progressDialog = new ProgressDialog(DetailedSchedule.this);
                progressDialog.setTitle("");
                progressDialog.setMessage("");
                progressDialog.show();
            }
        });
        builder.show();
    }

    private void setName(String name){
        scheduleToShow.setName(name);
        this.setTitle(name);
    }

    private void saveScheduleToFile(Schedule schedule){
        final ArrayList<String> savedScheduleNames = getSavedScheduleNames();
        final String scheduleName = schedule.getName();
        Log.i("Setting Schedule Name", scheduleName);

        saveCheck = true;

        for(String string : savedScheduleNames) {
            Log.i("Existing Schedules", string);
            if (string.equalsIgnoreCase(scheduleName)) {
                final AlertDialog.Builder confirmOverWrite = new AlertDialog.Builder(DetailedSchedule.this);
                confirmOverWrite.setTitle("A Schedule with this name already exists");
                confirmOverWrite.setMessage("Are you sure you want to overwrite it?");
                confirmOverWrite.setPositiveButton("OVERWRITE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeScheduleFromFile(scheduleName);
                        saveCheck = true;
                        dialog.dismiss();
                    }
                });
                confirmOverWrite.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveCheck = false;
                        dialog.dismiss();
                    }
                });
                confirmOverWrite.show();
            }
        }
        if (saveCheck){
            savedScheduleNames.add(schedule.getName());
            SharedPreferences.Editor editor = getSharedPreferences(Schedule.SCHEDULE_SAVEFILE, MODE_PRIVATE).edit();
            editor.putStringSet(Schedule.SCHEDULE_NAMES, new HashSet<String>(savedScheduleNames));
            try {
                String scheduleJSON = schedule.toJSON().toString();
                Log.i("Schedule to Save", scheduleJSON);
                editor.putString(Schedule.SCHEDULE_NAMES+"_"+schedule.getName(), scheduleJSON);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            editor.apply();
        }
    }

    private void removeScheduleFromFile(String scheduleNameToRemove){
        ArrayList<String> scheduleNamesArrayList = getSavedScheduleNames();
        for(String scheduleName : scheduleNamesArrayList){
            if (scheduleName.equalsIgnoreCase(scheduleNameToRemove)){
                Log.i("Removing Schedule", scheduleName);
                scheduleNamesArrayList.remove(scheduleName);
                SharedPreferences.Editor editor = getSharedPreferences(Schedule.SCHEDULE_SAVEFILE, MODE_PRIVATE).edit();
                HashSet<String> newScheduleStingSet = new HashSet<String>(scheduleNamesArrayList);
                editor.putStringSet(Schedule.SCHEDULE_NAMES, newScheduleStingSet);
                editor.remove(Schedule.SCHEDULE_NAMES + "_" + scheduleNameToRemove);
                editor.apply();
                return;
            }
            else {
                Log.i("Keeping Schedule", scheduleName);
                continue;
            }
        }
    }

    private ArrayList<String> getSavedScheduleNames(){
        File f = new File("/data/data/" + getPackageName() +  "/shared_prefs/" + Schedule.SCHEDULE_SAVEFILE + ".xml");
        if(f.exists()){

            SharedPreferences scheduleNames = getSharedPreferences(Schedule.SCHEDULE_SAVEFILE, MODE_PRIVATE);
            Set<String> scheduleNameSet = scheduleNames.getStringSet(Schedule.SCHEDULE_NAMES, null);
            for (String string : scheduleNameSet){
                Log.i("Schedule Names:", string);
            }
            HashSet<String> result = null;
            if (scheduleNameSet != null) result = new HashSet<>(scheduleNameSet);

            ArrayList<String> scheduleNamesArrayList;
            if(result != null) {
                scheduleNamesArrayList = new ArrayList<>(result);
                for (String string : scheduleNamesArrayList){
                    Log.i("Schedules in file", string);
                }
            }
            else scheduleNamesArrayList = new ArrayList<>();

            return scheduleNamesArrayList;
        }
        else{

            SharedPreferences.Editor editor = getSharedPreferences(Schedule.SCHEDULE_SAVEFILE, MODE_PRIVATE).edit();
            editor.apply();
            return new ArrayList<>();
        }

    }

    private void setSelection(int selection){
        this.selection = selection;
    }

    private void addSection(Section section){
        scheduleToShow.getSelectedSections().add(section);
    }

    private void updateAdapter(){
        adapter.notifyDataSetChanged();
    }

    public void getSections(String department, String courseNumber){
        String url = URL_GET_COURSE_SECTIONS
                + URL_GET_COURSE_SECTIONS_PARAM_SEMESTER + scheduleToShow.getSemesterNumber()
                + URL_GET_COURSE_SECTIONS_PARAM_DEPARTMENT + department
                + URL_GET_COURSE_SECTIONS_PARAM_COURSENUMBER + courseNumber;

        Intent intent = new Intent(this, HTTPGetService.class);

        intent.putExtra(HTTPGetService.URL_REQUEST, url);
        intent.putExtra(HTTPGetService.SOURCE_INTENT, ACTION_GET_COURSE_SECTIONS);
        startService(intent);
    }

    class CouresAlternatesReciever extends BroadcastReceiver{

        /**
         * This method is called when the BroadcastReceiver is receiving an Intent
         * broadcast.  During this time you can use the other methods on
         * BroadcastReceiver to view/modify the current result values.  This method
         * is always called within the main thread of its process, unless you
         * explicitly asked for it to be scheduled on a different thread using
         * {@link Context#registerReceiver(BroadcastReceiver,
         * IntentFilter, String, Handler)}. When it runs on the main
         * thread you should
         * never perform long-running operations in it (there is a timeout of
         * 10 seconds that the system allows before considering the receiver to
         * be blocked and a candidate to be killed). You cannot launch a popup dialog
         * in your implementation of onReceive().
         * <p/>
         * <p><b>If this BroadcastReceiver was launched through a &lt;receiver&gt; tag,
         * then the object is no longer alive after returning from this
         * function.</b>  This means you should not perform any operations that
         * return a result to you asynchronously -- in particular, for interacting
         * with services, you should use
         * {@link Context#startService(Intent)} instead of
         * {@link Context#bindService(Intent, ServiceConnection, int)}.  If you wish
         * to interact with a service that is already running, you can use
         * {@link #peekService}.
         * <p/>
         * <p>The Intent filters used in {@link Context#registerReceiver}
         * and in application manifests are <em>not</em> guaranteed to be exclusive. They
         * are hints to the operating system about how to find suitable recipients. It is
         * possible for senders to force delivery to specific recipients, bypassing filter
         * resolution.  For this reason, {@link #onReceive(Context, Intent) onReceive()}
         * implementations should respond only to known actions, ignoring any unexpected
         * Intents that they may receive.
         *
         * @param context The Context in which the receiver is running.
         * @param intent  The Intent being received.
         */
        @Override
        public void onReceive(Context context, Intent intent) {

            ArrayList<Course> fetchedCourses = null;

            String response = intent.getStringExtra(HTTPGetService.SERVER_RESPONSE);
            Log.d("Received: ", response);

            try {
                JSONObject rawResult = new JSONObject(response);
                if (!rawResult.getBoolean("Success")){
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Couldn't contact server. Please try again in a few minutes", Toast.LENGTH_LONG).show();
                    return;
                }
                JSONArray jsonCourses = rawResult.getJSONArray("Results");
                float timeTaken = Float.parseFloat(rawResult.getString("TimeTaken"));
                Log.d("New Request Time Taken:", Float.toString(timeTaken));
                fetchedCourses = Course.buildCourseList(jsonCourses);
                for(Section section : fetchedCourses.get(0).getSectionList()){
                    if(section.getStatus() == ClassStatus.OPEN && section.conflictsWith(scheduleToShow.getSelectedSections())){
                        section.setStatus(ClassStatus.CONFLICT);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(progressDialog != null)
                progressDialog.dismiss();

            AlertDialog.Builder builder = new AlertDialog.Builder(DetailedSchedule.this);
            final MySectionArrayAdapter arrayAdapter = new MySectionArrayAdapter(DetailedSchedule.this,R.layout.section_list_display,fetchedCourses.get(0).getSectionList());
            builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    addSection(arrayAdapter.getItem(which));
                    updateAdapter();
                }
            });
            builder.show();
        }
    }
}
