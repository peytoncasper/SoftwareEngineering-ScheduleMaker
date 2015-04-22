package edu.uta.ucs;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class SelectBlockoutTimes extends ActionBarActivity {

    EditText nameBlockoutTime;
    ToggleButton mondayToggleButton, tuesdayToggleButton, wednesdayToggleButton, thursdayToggleButton, fridayToggleButton, saturdayToggleButton;
    TimePicker startTimePicker, endTimePicker;
    ListView sectionListView;

    String blockoutSetName = null;
    MySectionArrayAdapter blockoutTimesListAdapter;

    ArrayList<Section> currentBlockoutTimes;

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

        currentBlockoutTimes = new ArrayList<>();

    }

    @Override
    protected void onStart() {
        super.onStart();

        blockoutTimesListAdapter = new MySectionArrayAdapter(SelectBlockoutTimes.this, R.layout.section_list_display, currentBlockoutTimes);
        blockoutTimesListAdapter.setNotifyOnChange(true);
        sectionListView.setAdapter(blockoutTimesListAdapter);

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

    public void addBlockoutTime(View view){

        TimeShort newStartTime = null, newEndTime = null;

        newStartTime = getTime(startTimePicker);
        newEndTime = getTime(endTimePicker);
        ArrayList<Day> newDayList = getDays();
        Course newBlockoutCourse = new Course();
        // newBlockoutCourse.setCourseName(nameBlockoutTime.getText().toString());

        Section newBlockoutTime = new Section(-1, nameBlockoutTime.getText().toString(), "", newStartTime, newEndTime, newDayList, ClassStatus.OPEN, newBlockoutCourse);
        nameBlockoutTime.setText("");
        currentBlockoutTimes.add(newBlockoutTime);
        blockoutTimesListAdapter.notifyDataSetChanged();

    }

    public void useBlockoutTimes(View view){

        if (blockoutSetName == null)
            blockoutSetName = "";
        Course course = new Course("BLOCKOUT", blockoutSetName, currentBlockoutTimes);
        Log.d("BlockoutTimes", course.toJSON().toString());

        Intent intent = new Intent();
        intent.putExtra("BLOCKOUT", course.toJSON().toString());
        setResult(0, intent);

        finish();
    }

    public void saveBlockoutTimes(View view){
        /*
        for(Section section : currentBlockoutTimes){
            Log.d("TestCourse Out", section.getSourceCourse().toJSON(section).toString());
        }*/

        AlertDialog.Builder saveName = new AlertDialog.Builder(this);

        saveName.setTitle("Save as");
        saveName.setMessage("What do you want to save this set of times as?");

        final EditText blockoutNameEditTextDialog = new EditText(this);
        saveName.setView(blockoutNameEditTextDialog);

        saveName.setPositiveButton("ok", new DialogInterface.OnClickListener(){

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
                Course course = new Course("BLOCKOUT", blockoutSetName, currentBlockoutTimes);
                Log.d("BlockoutTimes", course.toJSON().toString() );
            }
        });

        saveName.setNegativeButton("cancel", new DialogInterface.OnClickListener(){

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
