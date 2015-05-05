package edu.uta.ucs;

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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListView;
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
    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        String courseName = ((Course) getGroup(groupPosition)).getCourseName();
        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_group_course, null);
        }

        TextView courseNameTextView = (TextView) convertView.findViewById(R.id.courseExpandableListViewTitle);
        CheckBox courseCheckbox = (CheckBox) convertView.findViewById(R.id.courseExpandableListViewCheckbox);
        Button courseDelete = (Button) convertView.findViewById(R.id.courseExpandableListViewDeleteButton);


        courseDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                courseArrayList.remove(getGroup(groupPosition));
                notifyDataSetChanged();
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
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.section_list_display, null);

        }

        Section childSection = (Section) getChild(groupPosition, childPosition);

        if (childSection != null) {

            TextView courseText = (TextView) convertView.findViewById(R.id.courseName);

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

            if (courseText != null) {
                if(childSection.getSourceCourse()!=null) {
                    if ((childSection.getSourceCourse().getCourseName() == null && childSection.getInstructors() != null) || (childSection.getSourceCourse().getCourseNumber().equalsIgnoreCase("BLOCKOUT")) ){
                        courseText.setText(childSection.getInstructors());
                        instructorsText.setVisibility(View.GONE);
                    }
                    else if (childSection.getSourceCourse().getCourseName().contains("-"))
                        courseText.setText(childSection.getSourceCourse().getCourseName().split("-")[1].substring(1));
                    else
                        courseText.setText(childSection.getSourceCourse().getCourseName());
                }
            }

            if (daysText != null) {
                daysText.setText(childSection.getDaysString());
            }
            if (roomText != null) {
                if (childSection.getRoom().equals(""))
                    roomText.setVisibility(View.GONE);
                else
                    roomText.setText("Room: "+childSection.getRoom());
            }
            if (instructorsText != null){
                if (childSection.getInstructors().equals(""))
                    instructorsText.setVisibility(View.GONE);
                else
                    instructorsText.setText(childSection.getInstructors());
            }

            if (timesText != null) {
                timesText.setText("  " + childSection.getTimeString(Section.h12));
            }
            if (sectionIDText != null) {
                if (childSection.getSectionID()<0)
                    sectionIDText.setVisibility(View.GONE);
                else
                    sectionIDText.setText("UTA Class Number: "+((Integer) childSection.getSectionID()).toString());
            }
            if (designationText != null) {
                if (childSection.getSectionNumber()<0 || childSection.getSectionNumber() == 0)
                    designationText.setVisibility(View.GONE);
                else
                    designationText.setText(childSection.getSourceCourse().getCourseName().split("-")[0] + "- " + String.format("%03d", childSection.getSectionNumber()));
            }

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

    public void setChecked(Boolean[] checked) {
        this.checked = checked;
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

public class SelectBlockoutTimes extends ActionBarActivity {

    private static final String BLOCKOUT_NAME = "BLOCKOUT_NAME";
    private static final String BLOCKOUT_TIMES = "BLOCKOUT_TIMES";

    EditText nameBlockoutTime;
    ToggleButton mondayToggleButton, tuesdayToggleButton, wednesdayToggleButton, thursdayToggleButton, fridayToggleButton, saturdayToggleButton;
    TimePicker startTimePicker, endTimePicker;
    ListView sectionListView;

    String blockoutSetName = null;
    MySectionArrayAdapter blockoutTimesListAdapter;

    ArrayList<Section> currentBlockoutTimes;
    Course currentBlockoutCourse;
    ArrayList<Course> savedBlockoutCourses;

    SharedPreferences blockoutTimesSaver;
    SharedPreferences.Editor blockoutTimesEditor;

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

        startTimePicker = ((TimePicker) findViewById(R.id.start_timePicker));
        endTimePicker = ((TimePicker) findViewById(R.id.end_timePicker));

        sectionListView = ((ListView) findViewById(R.id.show_blockout_times_listView));


    }

    @Override
    protected void onStart() {
        super.onStart();

        currentBlockoutTimes = new ArrayList<>();

        blockoutTimesListAdapter = new MySectionArrayAdapter(SelectBlockoutTimes.this, R.layout.section_list_display, currentBlockoutTimes);
        blockoutTimesListAdapter.setNotifyOnChange(true);
        sectionListView.setAdapter(blockoutTimesListAdapter);

        blockoutTimesSaver = getSharedPreferences(BLOCKOUT_TIMES, MODE_PRIVATE);
        blockoutTimesEditor = getSharedPreferences(BLOCKOUT_TIMES, MODE_PRIVATE).edit();

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

        /**
         * Loads all blockout times from memory to make runtime tasks faster
         */
        String savedBlockoutCourseString = blockoutTimesSaver.getString(BLOCKOUT_TIMES, "");
        Log.i("Blockout Times", savedBlockoutCourseString);
        if (!savedBlockoutCourseString.equals("")){
            try {
                JSONArray savedBlockoutCourseJSONArrayString = new JSONArray(savedBlockoutCourseString);
                JSONArray savedBlockoutCourseJSONArray = new JSONArray();
                for(int index = savedBlockoutCourseJSONArrayString.length(); index != 0;index--){
                    JSONObject courseJSONObject = new JSONObject(savedBlockoutCourseJSONArrayString.getString(index-1));
                    savedBlockoutCourseJSONArray.put(courseJSONObject);
                }
                savedBlockoutCourses = Course.buildCourseList(savedBlockoutCourseJSONArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else
            savedBlockoutCourses = new ArrayList<>();
    }

    @Override
    protected void onPause() {
        super.onPause();

        saveBlockoutCoursesToFile();

    }

    /**
     * Saves all blockout times in the list of times user wants saved to file
     *
     */public void saveBlockoutCoursesToFile(){
        ArrayList<String> savedBlockoutCourseString = new ArrayList<>(savedBlockoutCourses.size());
        for (Course course : savedBlockoutCourses){
            savedBlockoutCourseString.add(course.toJSON().toString());
        }
        JSONArray savedBlockoutCourseJSONArray = new JSONArray(savedBlockoutCourseString);
        blockoutTimesEditor.putString(BLOCKOUT_TIMES, savedBlockoutCourseJSONArray.toString());
        blockoutTimesEditor.apply();
    }

    public void addBlockoutTime(View view){

        TimeShort newStartTime = null, newEndTime = null;

        newStartTime = getTime(startTimePicker);
        newEndTime = getTime(endTimePicker);
        ArrayList<Day> newDayList = getDays();
        Course newBlockoutCourse = new Course();

        Section newBlockoutTime = new Section(-1, nameBlockoutTime.getText().toString(), "", newStartTime, newEndTime, newDayList, ClassStatus.OPEN, newBlockoutCourse);
        nameBlockoutTime.setText("");
        currentBlockoutTimes.add(newBlockoutTime);
        blockoutTimesListAdapter.notifyDataSetChanged();

    }

    public void useBlockoutTimes(View view){

        if (blockoutSetName == null)
            blockoutSetName = "";
        currentBlockoutCourse = new Course("BLOCKOUT", blockoutSetName, currentBlockoutTimes);
        Log.d("BlockoutTimes", currentBlockoutCourse.toJSON().toString());

        Intent intent = new Intent(this, SelectBlockoutTimes.class);
        intent.putExtra("BLOCKOUT", currentBlockoutCourse.toJSON().toString());
        setResult(0, intent);

        finish();
    }

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
                currentBlockoutCourse = new Course("BLOCKOUT", blockoutSetName, new ArrayList<Section>(currentBlockoutTimes));
                savedBlockoutCourses.add(currentBlockoutCourse);
                saveBlockoutCoursesToFile();
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

    public void loadBlockoutTimes(View view){

        AlertDialog.Builder saveName = new AlertDialog.Builder(this);
        saveName.setTitle("Load");

        ExpandableListView blockoutListView = new ExpandableListView(this);
        final BlockoutCoursesAdapter blockoutCoursesAdapter = new BlockoutCoursesAdapter(savedBlockoutCourses, getApplicationContext());
        blockoutListView.setAdapter(blockoutCoursesAdapter);

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
                for (int index = 0; index < savedBlockoutCourses.size(); index++){
                    if (blockoutTimesToLoad[index] == null){
                        Log.i("Loading Blockout", "Found null" + index);
                        continue;
                    }
                    if (blockoutTimesToLoad[index]){
                        Log.i("Loading BlockOut", savedBlockoutCourses.get(index).toJSON().toString());
                        for(Section section : savedBlockoutCourses.get(index).getSectionList()){
                            Log.i("Adding to Blockout List",section.toJSON().toString());
                            if (!currentBlockoutTimes.contains(section)){
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
        AlertDialog dialog = saveName.create();

        dialog.show();
    }

    public void removeBlockoutTimes(View view){
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(SelectBlockoutTimes.this);
        //builderSingle.setIcon(R.drawable.ic_launcher);

        builderSingle.setTitle("Select Time to Remove:-");
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(SelectBlockoutTimes.this,android.R.layout.select_dialog_singlechoice);
        for(Section section : currentBlockoutTimes){
            arrayAdapter.add(section.getInstructors()+"\t"+section.getDaysString()+" "+section.getTimeString(Section.h12));
        }
        builderSingle.setNegativeButton("CANCEL",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builderSingle.setAdapter(arrayAdapter,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strName = arrayAdapter.getItem(which);
                        final Section selectedSection = currentBlockoutTimes.get(which);
                        AlertDialog.Builder builderInner = new AlertDialog.Builder(SelectBlockoutTimes.this);
                        builderInner.setMessage(strName);
                        builderInner.setTitle("Are you sure you want to remove this?\nIt can't be undone");
                        builderInner.setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        currentBlockoutTimes.remove(selectedSection);
                                        blockoutTimesListAdapter.notifyDataSetChanged();
                                        dialog.dismiss();
                                    }
                                });
                        builderInner.setNegativeButton("CANCEL",
                                new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                        });
                        builderInner.show();
                    }
                });
        builderSingle.show();
    }

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


}
