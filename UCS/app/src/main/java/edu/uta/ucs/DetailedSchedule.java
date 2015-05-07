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
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


/**
 * This Activity is designed to show a schedule. It expects the intent with which it is started to have the propper string extras.
 */
public class DetailedSchedule extends Activity {

    public static final String URL_GET_COURSE_SECTIONS =UserData.getContext().getString(R.string.get_course_info_base);
    public static final String URL_VALIDATE_COURSES =UserData.getContext().getString(R.string.validate_courses_base);
    public static final String URL_GET_COURSE_SECTIONS_PARAM_SEMESTER =UserData.getContext().getString(R.string.validate_courses_param_semester);
    public static final String URL_GET_COURSE_SECTIONS_PARAM_DEPARTMENT =UserData.getContext().getString(R.string.validate_courses_param_department);
    public static final String URL_GET_COURSE_SECTIONS_PARAM_CLASSNUMBER =UserData.getContext().getString(R.string.validate_courses_param_class_number);

    private static final String ACTION_GET_COURSE_SECTIONS = "ACTION_GET_COURSE_SECTIONS";
    private static final String ACTION_VERIFY_SCHEDULE = "ACTION_VERIFY_SCHEDULE";

    private ListView scheduleSections;
    private Schedule scheduleToShow;
    private Section sectionToSwap;
    private SectionArrayAdapter adapter;
    private ProgressDialog progressDialog;

    int selection;
    boolean saveCheck = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_schedule);

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

    @Override
    protected void onPause() {
        super.onPause();

        if(progressDialog != null)
            progressDialog.dismiss();
    }

    public void verifySchedule(View view){
        getVerifySchedule();
    }

    public void deleteSchedule(View view){
        removeScheduleFromFile(this.scheduleToShow.getName());
        finish();
    }

    public void editSchedule(View view){
        SectionArrayAdapter arrayAdapter = new SectionArrayAdapter(DetailedSchedule.this, R.layout.section_list_display, scheduleToShow.getSelectedSections());

        AlertDialog.Builder builder = new AlertDialog.Builder(DetailedSchedule.this);
        builder.setTitle("Select the course for which you want to swap sections");
        builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sectionToSwap = scheduleToShow.getSelectedSections().get(which);
                //removeSection(sectionToSwap);
                setSelection(which);
                getAlternativeSections(sectionToSwap.getSourceCourse().getCourseDepartment(), sectionToSwap.getSourceCourse().getCourseNumber());
            }
        });
        builder.show();
    }

    private void getVerifySchedule(){

        Log.i("Verify Schedule", "About to attempt verify schedule");
        ArrayList<Section> sectionsToUpdate = this.scheduleToShow.getSelectedSections();

        StringBuilder semesterParam = new StringBuilder(URL_GET_COURSE_SECTIONS_PARAM_SEMESTER);
        StringBuilder departmentParam = new StringBuilder(URL_GET_COURSE_SECTIONS_PARAM_DEPARTMENT);
        StringBuilder classNumberParam = new StringBuilder(URL_GET_COURSE_SECTIONS_PARAM_CLASSNUMBER);

        for (Section section : sectionsToUpdate){

            semesterParam.append(this.scheduleToShow.getSemesterNumber()).append(",");
            departmentParam.append(section.getSourceCourse().getCourseDepartment()).append(",");
            classNumberParam.append(section.getSectionID()).append(",");
        }

        String semesterParamFinal = semesterParam.length() > 0 ? semesterParam.substring( 0, semesterParam.length() - 1 ): "";
        String departmentParamFinal = departmentParam.length() > 0 ? departmentParam.substring( 0, departmentParam.length() - 1 ): "";
        String courseNumberParamFinal = classNumberParam.length() > 0 ? classNumberParam.substring( 0, classNumberParam.length() - 1 ): "";

        String urlFinal = URL_VALIDATE_COURSES + semesterParamFinal + departmentParamFinal + courseNumberParamFinal;

        HTTPService.FetchURL(urlFinal, ACTION_VERIFY_SCHEDULE,DetailedSchedule.this);

        showProgressDialog("Verifying Class Statuses");
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
            editor.putStringSet(Schedule.SCHEDULE_NAMES, new HashSet<>(savedScheduleNames));
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
                HashSet<String> newScheduleStingSet = new HashSet<>(scheduleNamesArrayList);
                editor.putStringSet(Schedule.SCHEDULE_NAMES, newScheduleStingSet);
                editor.remove(Schedule.SCHEDULE_NAMES + "_" + scheduleNameToRemove);
                editor.apply();
                return;
            }
            else {
                Log.i("Keeping Schedule", scheduleName);
            }
        }
    }

    private ArrayList<String> getSavedScheduleNames(){
        File f = new File(UserData.getContext().getFilesDir().getParentFile().getPath() +  "/shared_prefs/" + Schedule.SCHEDULE_SAVEFILE + ".xml");
        Log.i("DetailedSchedule", "getSavedScheduleNames attempting load from file: " + f.getAbsolutePath());
        if(f.exists()){
            Log.i("DetailedSchedule", "getSavedScheduleNames found file");

            SharedPreferences scheduleNames = getSharedPreferences(Schedule.SCHEDULE_SAVEFILE, MODE_PRIVATE);
            Set<String> scheduleNameSet = scheduleNames.getStringSet(Schedule.SCHEDULE_NAMES, null);
            HashSet<String> result = null;
            if (scheduleNameSet != null){
                for (String string : scheduleNameSet){
                    Log.i("Schedule Names:", string);
                }
                result = new HashSet<>(scheduleNameSet);
            }

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
            Log.i("DetailedSchedule", "getSavedScheduleNames file not found");

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

    private void removeSection(Section section){
        scheduleToShow.getSelectedSections().remove(section);
    }

    private void updateAdapter(){
        adapter.notifyDataSetChanged();
    }

    public void getAlternativeSections(String department, String classNumber){
        String url = URL_GET_COURSE_SECTIONS
                + URL_GET_COURSE_SECTIONS_PARAM_SEMESTER + scheduleToShow.getSemesterNumber()
                + URL_GET_COURSE_SECTIONS_PARAM_DEPARTMENT + department
                + URL_GET_COURSE_SECTIONS_PARAM_CLASSNUMBER + classNumber;

        HTTPService.FetchURL(url, ACTION_GET_COURSE_SECTIONS, this);

        showProgressDialog("Getting alternate sections");
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
                if(success) {
                    JSONArray jsonCourses = response.getJSONArray("Results");
                    fetchedCourses = Course.buildCourseList(jsonCourses);
                    for (Section section : fetchedCourses.get(0).getSectionList()) {
                        if (section.getStatus() == ClassStatus.OPEN && section.conflictsWith(scheduleToShow.getSelectedSections()) && !section.equals(sectionToSwap)) {
                            section.setStatus(ClassStatus.CONFLICT);
                        }
                    }
                    showAlternateChoices(fetchedCourses.get(0).getSectionList());

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(progressDialog != null)
                progressDialog.dismiss();

        }
    }

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
                                    String notification = section.getSourceCourse().getCourseDepartment() + " "
                                            + section.getSourceCourse().getCourseNumber() + "-" + section.getSectionNumber() + " status has changed to: " + fetchedSection.getStatus().toString().replace("_", " ");
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

    private void showAlternateChoices(ArrayList<Section> alternates){

        final SectionArrayAdapter arrayAdapter = new SectionArrayAdapter(DetailedSchedule.this,R.layout.section_list_display,alternates);
        final AlertDialog.Builder showAlternatesBuilder = new AlertDialog.Builder(this);
        showAlternatesBuilder.setTitle("Select a replacement course");
        showAlternatesBuilder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                removeSection(sectionToSwap);
                addSection(arrayAdapter.getItem(which));
                dialog.dismiss();
                updateAdapter();
            }
        });
        if(!DetailedSchedule.this.isFinishing()) {
            showAlternatesBuilder.show();
        }
    }

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
}
