package edu.uta.ucs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseExpandableListAdapter;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

class BlockoutCoursesAdapter extends BaseExpandableListAdapter {

    private Context context;
    private ArrayList<Course> courseArrayList;
    private Boolean[] checked;

    public BlockoutCoursesAdapter(ArrayList<Course> courseArrayList, Context context) {
        this.courseArrayList = courseArrayList;
        this.context = context;
        if (courseArrayList == null){
            courseArrayList = new ArrayList<>();
        }
        this.checked = new Boolean[courseArrayList.size()];
        for (int index = 0; index < courseArrayList.size(); index++){
            this.checked[index] = false;
        }
    }

    /**
     * Gets the number of groups.
     *
     * @return the number of groups
     */
    @Override
    public int getGroupCount() {
        return courseArrayList.size();
    }

    /**
     * Gets the number of children in a specified group.
     *
     * @param groupPosition the position of the group for which the children
     *                      count should be returned
     * @return the children count in the specified group
     */
    @Override
    public int getChildrenCount(int groupPosition) {
        return courseArrayList.get(groupPosition).getSectionList().size();
    }

    /**
     * Gets the data associated with the given group.
     *
     * @param groupPosition the position of the group
     * @return the data child for the specified group
     */
    @Override
    public Object getGroup(int groupPosition) {
        return courseArrayList.get(groupPosition);
    }

    /**
     * Gets the data associated with the given child within the given group.
     *
     * @param groupPosition the position of the group that the child resides in
     * @param childPosition the position of the child with respect to other
     *                      children in the group
     * @return the data of the child
     */
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return courseArrayList.get(groupPosition).getSectionList().get(childPosition);
    }

    /**
     * Gets the ID for the group at the given position. This group ID must be
     * unique across groups. The combined ID (see
     * {@link #getCombinedGroupId(long)}) must be unique across ALL items
     * (groups and all children).
     *
     * @param groupPosition the position of the group for which the ID is wanted
     * @return the ID associated with the group
     */
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    /**
     * Gets the ID for the given child within the given group. This ID must be
     * unique across all children within the group. The combined ID (see
     * {@link #getCombinedChildId(long, long)}) must be unique across ALL items
     * (groups and all children).
     *
     * @param groupPosition the position of the group that contains the child
     * @param childPosition the position of the child within the group for which
     *                      the ID is wanted
     * @return the ID associated with the child
     */
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    /**
     * Indicates whether the child and group IDs are stable across changes to the
     * underlying data.
     *
     * @return whether or not the same ID always refers to the same object
     * @see Adapter#hasStableIds()
     */
    @Override
    public boolean hasStableIds() {
        return false;
    }

    /**
     * Gets a View that displays the given group. This View is only for the
     * group--the Views for the group's children will be fetched using
     * {@link #getChildView(int, int, boolean, View, ViewGroup)}.
     *
     * @param groupPosition the position of the group for which the View is
     *                      returned
     * @param isExpanded    whether the group is expanded or collapsed
     * @param convertView   the old view to reuse, if possible. You should check
     *                      that this view is non-null and of an appropriate type before
     *                      using. If it is not possible to convert this view to display
     *                      the correct data, this method can create a new view. It is not
     *                      guaranteed that the convertView will have been previously
     *                      created by
     *                      {@link #getGroupView(int, boolean, View, ViewGroup)}.
     * @param parent        the parent that this view will eventually be attached to
     * @return the View corresponding to the group at the specified position
     */
    @SuppressWarnings("JavaDoc")
    @SuppressLint("InflateParams")
    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        String courseName = ((Course) getGroup(groupPosition)).getCourseTitle();
        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_group_course, null);
        }

        TextView courseNameTextView = (TextView) convertView.findViewById(R.id.courseExpandableListViewTitle);
        CheckBox courseCheckbox = (CheckBox) convertView.findViewById(R.id.courseExpandableListViewCheckbox);
        Button courseDelete = (Button) convertView.findViewById(R.id.courseExpandableListViewDeleteButton);

        final Context tempContext = context;

        courseDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    confirmDelete(groupPosition, tempContext);
                }catch (WindowManager.BadTokenException e) {
                    courseArrayList.remove(getGroup(groupPosition));
                    SelectBlockoutTimes.saveBlockoutCoursesToFile(context, courseArrayList);
                    notifyDataSetChanged();
                }
            }
        });

        CheckListener checkListener = new CheckListener(groupPosition);
        courseCheckbox.setFocusable(false);
        courseCheckbox.setOnCheckedChangeListener(checkListener);

        courseNameTextView.setTextColor(Color.BLACK);
        courseNameTextView.setTypeface(null, Typeface.BOLD);
        courseNameTextView.setText(courseName);

        return convertView;
    }

    /**
     * Gets a View that displays the data for the given child within the given
     * group.
     *
     * @param groupPosition the position of the group that contains the child
     * @param childPosition the position of the child (for which the View is
     *                      returned) within the group
     * @param isLastChild   Whether the child is the last child within the group
     * @param convertView   the old view to reuse, if possible. You should check
     *                      that this view is non-null and of an appropriate type before
     *                      using. If it is not possible to convert this view to display
     *                      the correct data, this method can create a new view. It is not
     *                      guaranteed that the convertView will have been previously
     *                      created by
     *                      {@link #getChildView(int, int, boolean, View, ViewGroup)}.
     * @param parent        the parent that this view will eventually be attached to
     * @return the View corresponding to the child at the specified position
     */
    @SuppressWarnings("JavaDoc")
    @SuppressLint("InflateParams")
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.section_list_display, null);

        }

        Section childSection = (Section) getChild(groupPosition, childPosition);

        if (childSection != null) {

            TextView courseText = (TextView) convertView.findViewById(R.id.courseTitle);

            TextView daysText = (TextView) convertView.findViewById(R.id.sectionMeetingDays);
            TextView roomText = (TextView) convertView.findViewById(R.id.sectionRoom);
            TextView instructorsText = (TextView) convertView.findViewById(R.id.sectionInstructors);

            TextView timesText = (TextView) convertView.findViewById(R.id.sectionMeetingTimes);
            TextView sectionIDText = (TextView) convertView.findViewById(R.id.sectionID);
            TextView designationText = (TextView) convertView.findViewById(R.id.sectionDesignation);

            courseText.setTextColor(Color.BLACK);

            daysText.setTextColor(Color.BLACK);
            roomText.setTextColor(Color.BLACK);
            instructorsText.setTextColor(Color.BLACK);

            timesText.setTextColor(Color.BLACK);
            sectionIDText.setTextColor(Color.BLACK);
            designationText.setTextColor(Color.BLACK);

            if(childSection.getSourceCourse()!=null) {
                if ((childSection.getSourceCourse().getCourseTitle() == null && childSection.getInstructors() != null) || (childSection.getSourceCourse().getCourseNumber().equalsIgnoreCase("BLOCKOUT")) ){
                    courseText.setText(childSection.getInstructors());
                    instructorsText.setVisibility(View.GONE);
                }
                else if (childSection.getSourceCourse().getCourseTitle().contains("-"))
                    courseText.setText(childSection.getSourceCourse().getCourseTitle().split("-")[1].substring(1));
                else
                    courseText.setText(childSection.getSourceCourse().getCourseTitle());
            }

            daysText.setText(childSection.getDaysString());

            if (childSection.getRoom().equals(""))
                roomText.setVisibility(View.GONE);
            else
                roomText.setText("Room: "+childSection.getRoom());

            if (childSection.getInstructors().equals(""))
                instructorsText.setVisibility(View.GONE);
            else
                instructorsText.setText(childSection.getInstructors());

            timesText.setText("  " + childSection.getTimeString());

            if (childSection.getSectionID()<0)
                sectionIDText.setVisibility(View.GONE);
            else
                sectionIDText.setText("UTA Class Number: "+((Integer) childSection.getSectionID()).toString());

            if (childSection.getSectionNumber()<0 || childSection.getSectionNumber() == 0)
                designationText.setVisibility(View.GONE);
            else
                designationText.setText(childSection.getSourceCourse().getCourseTitle().split("-")[0] + "- " + String.format("%03d", childSection.getSectionNumber()));

            switch (childSection.getStatus()){
                case OPEN:
                    convertView.setBackgroundColor(Color.rgb(204, 255, 204));
                    break;
                case CLOSED:
                    convertView.setBackgroundColor(Color.rgb(255, 204, 204));
                    break;
                case WAIT_LIST:
                    convertView.setBackgroundColor(Color.rgb(255, 255, 204));
                    break;

            }
        }
        return convertView;
    }

    public void confirmDelete(final int position, Context context) throws WindowManager.BadTokenException{

        AlertDialog.Builder confirmDelete = new AlertDialog.Builder(context);
        confirmDelete.setTitle("Are you sure you want to delete this?");
        confirmDelete.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //noinspection SuspiciousMethodCalls
                courseArrayList.remove(getGroup(position));
                dialog.dismiss();
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
     * Whether the child at the specified position is selectable.
     *
     * @param groupPosition the position of the group that contains the child
     * @param childPosition the position of the child within the group
     * @return whether the child is selectable.
     */
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    public Boolean[] getChecked() {
        return checked;
    }


    class CheckListener implements CompoundButton.OnCheckedChangeListener {

        int position;

        CheckListener(int pos){
            super();
            this.position = pos;

        }

        /**
         * Called when the checked state of a compound button has changed.
         *
         * @param buttonView The compound button view whose state has changed.
         * @param isChecked  The new checked state of buttonView.
         */
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            Log.i("checkListenerChanged", String.valueOf(position)+":"+String.valueOf(isChecked));
            getChecked()[position] = isChecked;
        }
    }
}

/**
 * This Activity allows the user to select times which should not be considered for scheduling
 * <p>Activity should be started for result via startActivityForResult</p>
 */
public class SelectBlockoutTimes extends ActionBarActivity {

    public static final String BLOCKOUT_TIMES = "BLOCKOUT_TIMES";

    EditText nameBlockoutTime;
    ToggleButton mondayToggleButton, tuesdayToggleButton, wednesdayToggleButton, thursdayToggleButton, fridayToggleButton, saturdayToggleButton;
    TimePicker startTimePicker, endTimePicker;
    ListView sectionListView;
    Button toggleTimePickersButton;
    Button addBlockoutTimesButton;
    Button useBlockoutTimesButton;
    HorizontalScrollView timePickerView;
    LinearLayout toggleDaysLayout;

    String blockoutSetName = null;
    SectionArrayAdapter blockoutTimesListAdapter;

    ArrayList<Section> currentBlockoutTimes;
    Course currentBlockoutCourse;
    ArrayList<Course> savedBlockoutCourses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_blockout_times);


        nameBlockoutTime = ((EditText) findViewById(R.id.blockout_time_name_edittext));

        mondayToggleButton = ((ToggleButton) findViewById(R.id.monday_toggleButton));
        tuesdayToggleButton = ((ToggleButton) findViewById(R.id.tuesday_toggleButton));
        wednesdayToggleButton = ((ToggleButton) findViewById(R.id.wednesday_toggleButton));
        thursdayToggleButton = ((ToggleButton) findViewById(R.id.thursday_toggleButton));
        fridayToggleButton = ((ToggleButton) findViewById(R.id.friday_toggleButton));
        saturdayToggleButton = ((ToggleButton) findViewById(R.id.saturday_toggleButton));
        toggleDaysLayout = (LinearLayout) findViewById(R.id.toggle_days_layout);

        addBlockoutTimesButton= (Button) findViewById(R.id.add_blockout_time_button);
        useBlockoutTimesButton = (Button) findViewById(R.id.use_blockout_times_button);

        startTimePicker = ((TimePicker) findViewById(R.id.start_timePicker));
        endTimePicker = ((TimePicker) findViewById(R.id.end_timePicker));

        toggleTimePickersButton = (Button) findViewById(R.id.toggle_timepickers_button);
        timePickerView = (HorizontalScrollView) findViewById(R.id.timepicker_scrollview);

        sectionListView = ((ListView) findViewById(R.id.show_blockout_times_listView));

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
                SettingsActivity.startActivity(SelectBlockoutTimes.this);
                break;
            case R.id.action_logout:
                UserData.logout(SelectBlockoutTimes.this);
                //signOut();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        currentBlockoutTimes = new ArrayList<>();

        blockoutTimesListAdapter = new SectionArrayAdapter(SelectBlockoutTimes.this, R.layout.section_list_display, currentBlockoutTimes);
        blockoutTimesListAdapter.setDeleteButtonVisibility(true);
        blockoutTimesListAdapter.setNotifyOnChange(true);
        sectionListView.setAdapter(blockoutTimesListAdapter);

        // Set buttons to toggle colors. These can all be discarded if a proper style is setup and used for buttons.
        mondayToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    buttonView.setBackgroundColor(getResources().getColor(R.color.utaOrange));
                } else
                    buttonView.setBackgroundColor(getResources().getColor(R.color.button_material_light));
            }
        });

        tuesdayToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    buttonView.setBackgroundColor(getResources().getColor(R.color.utaOrange));
                }
                else
                    buttonView.setBackgroundColor(getResources().getColor(R.color.button_material_light));
            }
        });

        wednesdayToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    buttonView.setBackgroundColor(getResources().getColor(R.color.utaOrange));
                }
                else
                    buttonView.setBackgroundColor(getResources().getColor(R.color.button_material_light));
            }
        });

        thursdayToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    buttonView.setBackgroundColor(getResources().getColor(R.color.utaOrange));
                }
                else
                    buttonView.setBackgroundColor(getResources().getColor(R.color.button_material_light));
            }
        });

        fridayToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    buttonView.setBackgroundColor(getResources().getColor(R.color.utaOrange));
                }
                else
                    buttonView.setBackgroundColor(getResources().getColor(R.color.button_material_light));
            }
        });

        saturdayToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    buttonView.setBackgroundColor(getResources().getColor(R.color.utaOrange));
                }
                else
                    buttonView.setBackgroundColor(getResources().getColor(R.color.button_material_light));
            }
        });


        // Check to see if creating intent had blocout times stored in it.
        Intent intent = getIntent();
        if (intent.hasExtra("BLOCKOUT TIMES")){
            String blockOutTimes = intent.getStringExtra("BLOCKOUT TIMES");
            try {
                JSONObject jsonObject = new JSONObject(blockOutTimes);
                Course course = new Course(jsonObject);
                for (Section section : course.getSectionList()){
                    currentBlockoutTimes.add(section);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            blockoutTimesListAdapter.notifyDataSetChanged();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        // Load blockout times from memory just before activity is shown to user.
        savedBlockoutCourses = SelectBlockoutTimes.loadBlockoutTimesFromFile(SelectBlockoutTimes.this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Save blockout times to memory just after activity pauses.
        saveBlockoutCoursesToFile(SelectBlockoutTimes.this, savedBlockoutCourses);

    }


    /**
     * Adds a block-out time to the ArrayList stored by the activity.
     */
    public void addBlockoutTime(View view){

        TimeShort newStartTime, newEndTime;

        newStartTime = getTime(startTimePicker);
        newEndTime = getTime(endTimePicker);

        ArrayList<Day> newDayList = getDays();
        if(!(newDayList.size()>0)){
            AlertDialog.Builder noDaysErrorDialog = new AlertDialog.Builder(SelectBlockoutTimes.this);
            noDaysErrorDialog.setTitle("Can't block out a time with no days");
            //noDaysErrorDialog.setPositiveButton("OKAY", null);
            noDaysErrorDialog.show();
            return;
        }

        String blockoutTimeName = nameBlockoutTime.getText().toString();
        if(blockoutTimeName.equals("")){
            nameBlockoutTime.setError("Please give this a name");
            nameBlockoutTime.requestFocus();
            return;
        }
        else {
            nameBlockoutTime.setError(null);
            nameBlockoutTime.setText("");
        }

        Course newBlockoutCourse = new Course("BLOCKOUT","BLOCKOUT", "BLOCKOUT");
        Section newBlockoutTime = new Section(-1, blockoutTimeName , "", newStartTime, newEndTime, newDayList, ClassStatus.OPEN, newBlockoutCourse);
        newBlockoutCourse.addSection(newBlockoutTime);

        currentBlockoutTimes.add(newBlockoutTime);
        blockoutTimesListAdapter.notifyDataSetChanged();

    }

    /**
     * Shows a dialogue to enable user to save a plockout time to file
     *
     */
    public void saveBlockoutTimes(View view){

        AlertDialog.Builder saveName = new AlertDialog.Builder(this);

        saveName.setTitle("Save as");
        saveName.setMessage("What do you want to save this set of times as?");

        final EditText blockoutNameEditTextDialog = new EditText(this);
        saveName.setView(blockoutNameEditTextDialog);

        saveName.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {

            /**
             * This method will be invoked when a button in the dialog is clicked.
             *
             * @param dialog The dialog that received the click.
             * @param which  The button that was clicked (e.g.
             *               {@link android.content.DialogInterface#BUTTON1}) or the position
             */
            @Override
            public void onClick(DialogInterface dialog, int which) {
                blockoutSetName = blockoutNameEditTextDialog.getText().toString();
                currentBlockoutCourse = new Course("BLOCKOUT", "BLOCKOUT", blockoutSetName, new ArrayList<>(currentBlockoutTimes));
                savedBlockoutCourses.add(currentBlockoutCourse);
                saveBlockoutCoursesToFile(SelectBlockoutTimes.this, savedBlockoutCourses);
                currentBlockoutTimes.clear();
                blockoutTimesListAdapter.notifyDataSetChanged();
                Log.d("BlockoutTimes", currentBlockoutCourse.toJSON().toString());
            }
        });

        saveName.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {

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

        saveName.show();
    }

    /**
     * Shows a dialog with a listview in it to allow the user to load times from file
     */
    public void loadBlockoutTimes(View view){

        ExpandableListView blockoutListView = new ExpandableListView(this);

        final BlockoutCoursesAdapter blockoutCoursesAdapter = new BlockoutCoursesAdapter(loadBlockoutTimesFromFile(SelectBlockoutTimes.this), SelectBlockoutTimes.this);

        blockoutListView.setAdapter(blockoutCoursesAdapter);

        AlertDialog.Builder saveName = new AlertDialog.Builder(this);
        saveName.setTitle("Load");
        saveName.setView(blockoutListView);
        saveName.setPositiveButton("load", new DialogInterface.OnClickListener() {

            /**
             * This method will be invoked when a button in the dialog is clicked.
             *
             * @param dialog The dialog that received the click.
             * @param which  The button that was clicked (e.g.
             *               {@link android.content.DialogInterface#BUTTON1}) or the position
             */
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Boolean[] blockoutTimesToLoad = blockoutCoursesAdapter.getChecked();
                for (int index = 0; index < savedBlockoutCourses.size(); index++) {
                    if (blockoutTimesToLoad[index] == null) {
                        Log.i("Loading Blockout", "Found null" + index);
                        continue;
                    }
                    if (blockoutTimesToLoad[index]) {
                        Log.i("Loading BlockOut", savedBlockoutCourses.get(index).toJSON().toString());
                        for (Section section : savedBlockoutCourses.get(index).getSectionList()) {
                            Log.i("Adding to Blockout List", section.toJSON().toString());
                            if (!currentBlockoutTimes.contains(section)) {
                                currentBlockoutTimes.add(section);
                            }
                        }
                        blockoutTimesListAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
        saveName.setNegativeButton("cancel", new DialogInterface.OnClickListener() {

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
        saveName.show();
    }

    /**
     * Passes the currently selected blockout times to calling function via setResult and then closes this activity
     */
    public void useBlockoutTimes(View view){

        if (blockoutSetName == null)
            blockoutSetName = "";
        currentBlockoutCourse = new Course("BLOCKOUT", "BLOCKOUT", blockoutSetName, currentBlockoutTimes);
        Log.d("BlockoutTimes", currentBlockoutCourse.toJSON().toString());

        Intent intent = new Intent();
        intent.putExtra("BLOCKOUT", currentBlockoutCourse.toJSON().toString());
        setResult(currentBlockoutTimes.size(), intent);

        finish();
    }


    /**
     * Show or hide the blockout time setup
     */
    public void toggleTimepickers(View view){

        if(toggleDaysLayout.getVisibility() == View.VISIBLE)
            toggleDaysLayout.setVisibility(View.GONE);
        else
            toggleDaysLayout.setVisibility(View.VISIBLE);

        if(timePickerView.getVisibility() == View.VISIBLE)
            timePickerView.setVisibility(View.GONE);
        else
            timePickerView.setVisibility(View.VISIBLE);

        if(addBlockoutTimesButton.getVisibility() == View.VISIBLE)
            addBlockoutTimesButton.setVisibility(View.GONE);
        else
            addBlockoutTimesButton.setVisibility(View.VISIBLE);

        if(nameBlockoutTime.getVisibility() == View.VISIBLE)
            nameBlockoutTime.setVisibility(View.GONE);
        else
            nameBlockoutTime.setVisibility(View.VISIBLE);

    }

    /**
     * Internal function which will create a {@code TimeShort} from a passed TimePicker object.
     * @param timePicker the timepicker from which to get the time from
     * @return TimeShort
     */
    private TimeShort getTime(TimePicker timePicker){
        TimeShort result;
        result = new TimeShort(timePicker.getCurrentHour(), timePicker.getCurrentMinute());
        return result;
    }

    /**
     * Gets status of each day selection button and returns an arraylist of '{@link Day}'s
     *
     * @return ArrayList of type '{@link Day}'
     * @see Day
     */
    private ArrayList<Day> getDays(){
        ArrayList<Day> results = new ArrayList<>();

        if(mondayToggleButton.isChecked())
            results.add(Day.valueOf("M"));
        if(tuesdayToggleButton.isChecked())
            results.add(Day.valueOf("TU"));
        if(wednesdayToggleButton.isChecked())
            results.add(Day.valueOf("W"));
        if(thursdayToggleButton.isChecked())
            results.add(Day.valueOf("TH"));
        if(fridayToggleButton.isChecked())
            results.add(Day.valueOf("F"));
        if(saturdayToggleButton.isChecked())
            results.add(Day.valueOf("SA"));

        return results;
    }


    /**
     * Saves all blockout times in the list of blockout times provided to a sharedPreferance file. This will overwrite all blockout times currently in that file.
     *
     * @param context Context to save with. Usually will be the calling class followed by ".this"
     *                EX: SelectBlockoutTimes.this
     * @param coursesToSave ArrayList of blockout times in course format
     */
    public static void saveBlockoutCoursesToFile(Context context, ArrayList<Course> coursesToSave){

        SharedPreferences.Editor blockoutTimesEditor;
        blockoutTimesEditor = context.getSharedPreferences(BLOCKOUT_TIMES, MODE_PRIVATE).edit();
        blockoutTimesEditor.clear();

        ArrayList<JSONObject> savedBlockoutCourseString = new ArrayList<>(coursesToSave.size());
        for (Course course : coursesToSave){
            savedBlockoutCourseString.add(course.toJSON());
        }
        JSONArray savedBlockoutCourseJSONArray = new JSONArray(savedBlockoutCourseString);
        blockoutTimesEditor.putString(BLOCKOUT_TIMES, savedBlockoutCourseJSONArray.toString());
        blockoutTimesEditor.apply();

    }

    /**
     * Loads all blockout times saved to sharedPreferences file.
     * @param context Context to save with. Usually will be the calling class followed by ".this"
     *                EX: SelectBlockoutTimes.this
     * @return ArrayList of blockout times in course format
     */
    public static ArrayList<Course> loadBlockoutTimesFromFile(Context context){
        ArrayList<Course> blockoutTimes = new ArrayList<>();

        SharedPreferences blockoutTimesSaver = context.getSharedPreferences(BLOCKOUT_TIMES, MODE_PRIVATE);

        String savedBlockoutCourseString = blockoutTimesSaver.getString(BLOCKOUT_TIMES, null);


        if (savedBlockoutCourseString != null){
            Log.i("Blockout Times", savedBlockoutCourseString);
            try {
                JSONArray savedBlockoutCourseJSONArrayString = new JSONArray(savedBlockoutCourseString);
                JSONArray savedBlockoutCourseJSONArray = new JSONArray();
                for(int index = savedBlockoutCourseJSONArrayString.length(); index != 0;index--){
                    JSONObject courseJSONObject = new JSONObject(savedBlockoutCourseJSONArrayString.getString(index-1));
                savedBlockoutCourseJSONArray.put(courseJSONObject);
            }
            blockoutTimes = Course.buildCourseList(savedBlockoutCourseJSONArray);
        } catch (JSONException e) {
        e.printStackTrace();
        }
        }

        return blockoutTimes;
        }

}
