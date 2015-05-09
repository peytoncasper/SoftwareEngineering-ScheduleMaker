package edu.uta.ucs;

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
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;


/**
 * This Activity is designed to show a schedule. It expects the intent with which it is started to have the propper string extras.
 */
public class DetailedSchedule extends Activity {

    public static final String URL_GET_COURSE_SECTIONS =UserData.getContext().getString(R.string.get_course_info_base);
    //public static final String URL_VALIDATE_COURSES =UserData.getContext().getString(R.string.validate_courses_base); // No longer used
    public static final String URL_GET_COURSE_SECTIONS_PARAM_SEMESTER =UserData.getContext().getString(R.string.validate_courses_param_semester);
    public static final String URL_GET_COURSE_SECTIONS_PARAM_DEPARTMENT =UserData.getContext().getString(R.string.validate_courses_param_department);
    public static final String URL_GET_COURSE_SECTIONS_PARAM_CLASSNUMBER =UserData.getContext().getString(R.string.validate_courses_param_course_number);

    private static final String ACTION_GET_COURSE_SECTIONS = "ACTION_GET_COURSE_SECTIONS";
    private static final String ACTION_VERIFY_SCHEDULE = "ACTION_VERIFY_SCHEDULE";

    private ListView scheduleSections;
    private Schedule scheduleToShow;
    private Section sectionToSwap;
    private SectionArrayAdapter adapter;
    private ProgressDialog progressDialog;

    boolean saveCheck = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_schedule);

        // Find list view ID so that it can be populated.
        scheduleSections = (ListView) findViewById(R.id.schedule_section_listview);

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
                SettingsActivity.startActivity(DetailedSchedule.this);
                break;
            case R.id.action_logout:
                UserData.logout(DetailedSchedule.this);
                //signOut();
                break;
        }

        return super.onOptionsItemSelected(item);
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
        LocalBroadcastManager.getInstance(this).registerReceiver(new VerifyScheduleReciever(), new IntentFilter(ACTION_VERIFY_SCHEDULE));
    }

    @Override
    protected void onResume() {
        super.onResume();

        setTitle(scheduleToShow.getName());
        adapter = new SectionArrayAdapter(this, R.layout.section_list_display, scheduleToShow.getSelectedSections());
        scheduleSections.setAdapter(adapter);
    }


    /**
     * Saves the schedule being displayed to the sharedPrefs file.
     * @param view view that button is launched from
     */
    public void saveSchedule(View view){


        // Presents the user with an AlertDialog to enter a name for the schedule.
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
                // Get text from dialog
                String scheduleSaveName = blockoutNameEditTextDialog.getEditableText().toString();
                // Set schedule name
                setName(scheduleSaveName);
                // Save schedule
                saveScheduleToFile();
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

    @Override
    protected void onPause() {
        super.onPause();

        // progressDialog Safety
        if(progressDialog != null)
            progressDialog.dismiss();
    }

    /**
     * Initializes verify schedule sequence
     * @param view View this function is called from
     */
    public void verifySchedule(View view){
        scheduleToShow.verifySchedule(DetailedSchedule.this);
        showProgressDialog("Verifying Class Statuses");
    }

    /**
     * Removes this schedule from memory and then destroys the activity.
     * @param view View this function is called from
     */
    public void deleteSchedule(View view) {
        AlertDialog.Builder confirmDelete = new AlertDialog.Builder(DetailedSchedule.this);
        confirmDelete.setTitle("Are you sure you want to delete this?");
        confirmDelete.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Schedule.removeScheduleFromFile(scheduleToShow);
                dialog.dismiss();
                finish();
            }
        });
        confirmDelete.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        confirmDelete.create().show();
    }

    /**
     * Initial dialog to edit schedule.
     * @param view View this function is called from
     */
    public void editSchedule(View view){
        // Build ArrayAdapter to show possible sections to show.
        SectionArrayAdapter arrayAdapter = new SectionArrayAdapter(DetailedSchedule.this, R.layout.section_list_display, scheduleToShow.getSelectedSections());

        AlertDialog.Builder builder = new AlertDialog.Builder(DetailedSchedule.this);
        builder.setTitle("Select the course for which you want to swap sections");
        builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Store section to swap so it can be removed if the user selects a replacement.
                sectionToSwap = scheduleToShow.getSelectedSections().get(which);
                // Start server call to get alternate sections.
                getAlternativeSections(sectionToSwap);
            }
        });
        builder.show();
    }

    private void setName(String name) {
        scheduleToShow.setName(name);
        this.setTitle(name);
    }

    /**
     * Saves the currently displayed schedule to file
     */
    private void saveScheduleToFile() {

        saveCheck = true;

        SharedPreferences reader = getSharedPreferences(Schedule.SCHEDULE_SAVEFILE, MODE_PRIVATE);
        Map<String, ?> schedules = reader.getAll();

        String scheduleName = scheduleToShow.fileName();

        if (schedules.containsKey(scheduleName)) {
            final AlertDialog.Builder confirmOverWrite = new AlertDialog.Builder(DetailedSchedule.this);
            confirmOverWrite.setTitle("A Schedule with this name already exists");
            confirmOverWrite.setMessage("Are you sure you want to overwrite it?");
            confirmOverWrite.setPositiveButton("OVERWRITE", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
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

        if (saveCheck) {
            Schedule.saveScheduleToFile(scheduleToShow);
        }
    }


    private void addSection(Section section){
        scheduleToShow.getSelectedSections().add(section);
    }

    private void removeSection(Section section){
        scheduleToShow.getSelectedSections().remove(section);
    }

    private void updateAdapter(){
        adapter.notifyDataSetChanged();
    }

    /**
     * Create a server request for all sections of a selected course
     */
    public void getAlternativeSections(Section section){

        String department = section.getSourceCourse().getDepartmentAcronym();
        String classNumber = section.getSourceCourse().getCourseNumber();

        String url = URL_GET_COURSE_SECTIONS
                + URL_GET_COURSE_SECTIONS_PARAM_SEMESTER + scheduleToShow.getSemesterNumber()
                + URL_GET_COURSE_SECTIONS_PARAM_DEPARTMENT + department
                + URL_GET_COURSE_SECTIONS_PARAM_CLASSNUMBER + classNumber;

        HTTPService.FetchURL(url, ACTION_GET_COURSE_SECTIONS, this);

        showProgressDialog("Fetching Class Details", "Getting alternate sections for class:\n" + section.getDescription());

    }

    /**
     * Receiver class for BroadcastManager.
     */
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
        @SuppressWarnings("JavaDoc")
        @Override
        public void onReceive(Context context, Intent intent) {
            JSONObject response;
            boolean success;
            String message;

            ArrayList<Course> fetchedCourses;

            try {
                // Standard server response info
                response = new JSONObject(intent.getStringExtra(HTTPService.SERVER_RESPONSE));
                success = response.getBoolean("Success");
                if(response.has("Message")) {
                    if(success)
                        message = response.getString("Message");
                    else message = "Error: " + response.getString("Message");
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                }
                if(response.has("TimeTaken")){
                    float timeTaken = Float.parseFloat(response.getString("TimeTaken"));
                    Log.d("New Request Time Taken:", Float.toString(timeTaken));
                }
                // End of standard server response info

                if(success) {
                    JSONArray jsonCourses = response.getJSONArray("Results");
                    fetchedCourses = Course.buildCourseList(jsonCourses);
                    ArrayList<Section> fetchedSections = new ArrayList<>();
                    for(Course course : fetchedCourses){
                        fetchedSections.addAll(course.getSectionList());
                    }
                    for (Section section : fetchedSections) {
                        if (section.getStatus() == ClassStatus.OPEN && section.conflictsWith(scheduleToShow.getSelectedSections()) && !section.equals(sectionToSwap)) {
                            section.setStatus(ClassStatus.CONFLICT);
                        }
                    }
                    showAlternateChoices(fetchedSections);

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(progressDialog != null)
                progressDialog.dismiss();

        }
    }

    /**
     * Receiver class for BroadcastManager.
     */
    class VerifyScheduleReciever extends BroadcastReceiver{

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
        @SuppressWarnings("JavaDoc")
        @Override
        public void onReceive(Context context, Intent intent) {
            JSONObject response;
            boolean success;
            String message;

            ArrayList<Course> fetchedCourses;


            try {

                response = new JSONObject(intent.getStringExtra(HTTPService.SERVER_RESPONSE));
                success = response.getBoolean("Success");
                if(response.has("Message")) {
                    if(success)
                        message = response.getString("Message");
                    else message = "Error: " + response.getString("Message");
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                }
                if(response.has("TimeTaken")){
                    float timeTaken = Float.parseFloat(response.getString("TimeTaken"));
                    Log.d("New Request Time Taken:", Float.toString(timeTaken));
                }
                if(success){
                    JSONArray jsonCourses = response.getJSONArray("Results");
                    fetchedCourses = Course.buildCourseList(jsonCourses);

                    ArrayList<Section> fetchedSections = new ArrayList<>(fetchedCourses.size());
                    for (Course course : fetchedCourses){
                        fetchedSections.addAll(course.getSectionList());
                    }

                    ArrayList<String> notifications = new ArrayList<>();
                    for(Section section : scheduleToShow.getSelectedSections()){
                        for(Section fetchedSection : fetchedSections){
                            if (section.getSectionID() == fetchedSection.getSectionID()){
                                if(section.getStatus() != fetchedSection.getStatus()){
                                    String notification = section.getDescription() + " status has changed to: " + fetchedSection.getStatus().toString().replace("_", " ");
                                    Log.i("DetailedSchedule", "Verify Schedule detected status change: " + notification);
                                    notifications.add(notification);
                                    section.setStatus(fetchedSection.getStatus());
                                }
                            }

                        }
                    }

                    //scheduleToShow.setSelectedSections(fetchedSections);
                    showStatusChanges(notifications);
                    adapter.notifyDataSetChanged();
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(progressDialog != null)
                progressDialog.dismiss();
        }
    }

    /**
     * Will generate a DetailedSchedule activity to display a supplied schedule
     *
     * @param scheduleToShow Schedule to show
     * @param context context to create intent with. Usually will be the calling class followed by ".this"
     *                <br>EX: MainActivity.this
     */
    public static void ShowSchedule(Schedule scheduleToShow, Context context){
        Intent scheduleIntent = new Intent(context, DetailedSchedule.class);
        try {
            scheduleIntent.putExtra("Schedule Data", scheduleToShow.toJSON().toString());
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("DetailedSchedule", "Could not parse schedule to JSON");
        }
        context.startActivity(scheduleIntent);
    }

    /**
     * Display alternate choices for a particular section
     * @param alternates ArrayList of Section from which user will select a schedule.
     */
    private void showAlternateChoices(ArrayList<Section> alternates){

        final SectionArrayAdapter arrayAdapter = new SectionArrayAdapter(DetailedSchedule.this,R.layout.section_list_display,alternates);
        final AlertDialog.Builder showAlternatesBuilder = new AlertDialog.Builder(this);
        showAlternatesBuilder.setTitle("Select a replacement course");
        showAlternatesBuilder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                removeSection(sectionToSwap);
                Section selection = arrayAdapter.getItem(which);
                selection.setStatus(ClassStatus.OPEN);
                addSection(selection);
                dialog.dismiss();
                updateAdapter();
            }
        });
        if(!DetailedSchedule.this.isFinishing()) {
            showAlternatesBuilder.show();
        }
    }

    /**
     * Show the user a list of strings as an AlertDialog with a listview.
     *
     * @param listToShow List of strings to show.
     */
    private void showStatusChanges(ArrayList<String> listToShow){

        AlertDialog.Builder showStatusBuilder = new AlertDialog.Builder(this);
        if(listToShow.size() > 0){
            showStatusBuilder.setTitle("Section statuses have changed");
            ArrayAdapter<String> listArrayAdapter = new ArrayAdapter<String>(DetailedSchedule.this, android.R.layout.simple_list_item_1, listToShow) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                    text1.setTextColor(Color.BLACK);
                    return view;
                }
            };
            showStatusBuilder.setAdapter( listArrayAdapter, null);
        }
        else {
            showStatusBuilder.setTitle("Section statuses have not changed");
        }
        showStatusBuilder.setNeutralButton("OKAY", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        if(!DetailedSchedule.this.isFinishing()) {
            showStatusBuilder.show();
        }
    }

    private void showProgressDialog(String title){
        progressDialog = new ProgressDialog(DetailedSchedule.this);
        progressDialog.setTitle(title);
        progressDialog.setMessage("Please wait while data is fetched...");
        progressDialog.show();
    }

    private void showProgressDialog(String title, String message){
        progressDialog = new ProgressDialog(DetailedSchedule.this);
        progressDialog.setTitle(title);
        progressDialog.setMessage(message + "\nPlease wait while data is fetched...");
        progressDialog.show();
    }
}
