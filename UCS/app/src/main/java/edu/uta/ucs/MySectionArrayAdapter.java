package edu.uta.ucs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by arunk_000 on 4/8/2015.
 */
public class MySectionArrayAdapter extends ArrayAdapter<Section> {

    private Context context;
    private boolean showDeleteButton = false;

    /**
     * Constructor
     *
     * @param context  The current context.
     * @param resource The resource ID for a layout file containing a TextView to use when creating view
     * @param items The arraylist of section you want to display
     */
    public MySectionArrayAdapter(Context context, int resource, ArrayList<Section> items) {
        super(context, resource, items);
        this.context = context;
    }

    public void setDeleteButtonVisibility(boolean setVisibility){
        this.showDeleteButton = setVisibility;
    }

    @Override
     public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {

            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.section_list_display, null);

        }

        Section p = getItem(position);

        if (p != null) {

            TextView courseText = (TextView) v.findViewById(R.id.courseName);

            TextView daysText = (TextView) v.findViewById(R.id.sectionMeetingDays);
            TextView roomText = (TextView) v.findViewById(R.id.sectionRoom);
            TextView instructorsText = (TextView) v.findViewById(R.id.sectionInstructors);

            TextView timesText = (TextView) v.findViewById(R.id.sectionMeetingTimes);
            TextView sectionIDText = (TextView) v.findViewById(R.id.sectionID);
            TextView designationText = (TextView) v.findViewById(R.id.sectionDesignation);

            Button deleteSection = (Button) v.findViewById(R.id.section_delete_button);

            courseText.setTextColor(Color.BLACK);

            daysText.setTextColor(Color.BLACK);
            roomText.setTextColor(Color.BLACK);
            instructorsText.setTextColor(Color.BLACK);

            timesText.setTextColor(Color.BLACK);
            sectionIDText.setTextColor(Color.BLACK);
            designationText.setTextColor(Color.BLACK);

            if (courseText != null) {
                if(p.getSourceCourse()!=null) {
                    if ((p.getSourceCourse().getCourseName() == null && p.getInstructors() != null) || (p.getSourceCourse().getCourseNumber().equalsIgnoreCase("BLOCKOUT")) ){
                        courseText.setText(p.getInstructors());
                        instructorsText.setVisibility(View.GONE);
                    }
                    else if (p.getSourceCourse().getCourseName().contains("-"))
                        courseText.setText(p.getSourceCourse().getCourseName().split("-")[1].substring(1));
                    else
                        courseText.setText(p.getSourceCourse().getCourseName());
                }
            }

            if (daysText != null) {
                daysText.setText(p.getDaysString());
            }
            if (roomText != null) {
                if (p.getRoom().equals(""))
                    roomText.setVisibility(View.GONE);
                else
                    roomText.setText("Room: "+p.getRoom());
            }
            if (instructorsText != null){
                if (p.getInstructors().equals(""))
                    instructorsText.setVisibility(View.GONE);
                else
                    instructorsText.setText(p.getInstructors());
            }

            if (timesText != null) {
                timesText.setText("  "+p.getTimeString(Section.h12));
            }
            if (sectionIDText != null) {
                if (p.getSectionID()<0)
                    sectionIDText.setVisibility(View.GONE);
                else
                    sectionIDText.setText("UTA Class Number: "+((Integer) p.getSectionID()).toString());
            }
            if (designationText != null) {
                if (p.getSectionNumber()<0 || p.getSectionNumber() == 0)
                    designationText.setVisibility(View.GONE);
                else
                    designationText.setText(p.getSourceCourse().getCourseName().split("-")[0] + "- " + String.format("%03d", p.getSectionNumber()));
            }

            switch (p.getStatus()){
                case OPEN:
                    v.setBackgroundColor(Color.rgb(204, 255, 204));
                    break;
                case CLOSED:
                    v.setBackgroundColor(Color.rgb(255, 204, 204));
                    break;
                case CONFLICT:
                    v.setBackgroundColor(Color.rgb(255, 204, 204));
                    break;
                case WAIT_LIST:
                    v.setBackgroundColor(Color.rgb(255, 255, 204));
                    break;

            }

            if(showDeleteButton){
                deleteSection.setVisibility(View.VISIBLE);

                final int itemPosition = position;

                deleteSection.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder confirmDelete = new AlertDialog.Builder(context);
                        confirmDelete.setTitle("Are you sure you want to delete this?");
                        confirmDelete.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                remove(getItem(itemPosition));
                            }
                        });
                        confirmDelete.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        confirmDelete.show();
                    }
                });
            }
            else
                deleteSection.setVisibility(View.GONE);
        }
        return v;

    }
}
