package edu.uta.ucs;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by arunk_000 on 4/8/2015.
 */
public class MySectionArrayAdapter extends ArrayAdapter<Section> {
    /**
     * Constructor
     *
     * @param context  The current context.
     * @param textViewResourceId The resource ID for a layout file containing a TextView to use when creating view
     */
    public MySectionArrayAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    /**
     * Constructor
     *
     * @param context  The current context.
     * @param resource The resource ID for a layout file containing a TextView to use when creating view
     * @param items The arraylist of section you want to display
     */
    public MySectionArrayAdapter(Context context, int resource, ArrayList<Section> items) {
        super(context, resource, items);
    }@Override
     public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {

            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.list_item, null);

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

            if (courseText != null) {
                if(p.getSourceCourse()!=null)
                courseText.setText(p.getSourceCourse().getCourseName().split("-")[1].substring(1));
            }

            if (daysText != null) {
                daysText.setText(p.getDays().toString().substring(1,p.getDays().toString().length()-1));
            }
            if (roomText != null) {
                roomText.setText("Room: "+p.getRoom());
            }
            if (instructorsText != null) {
                instructorsText.setText(p.getInstructors());
            }

            if (timesText != null) {
                timesText.setText("  "+p.getTimeString());
            }
            if (sectionIDText != null) {
                sectionIDText.setText("UTA Class Number: "+((Integer) p.getSectionID()).toString());
            }
            if (designationText != null) {
                designationText.setText(p.getSourceCourse().getCourseName().split("-")[0] + "- " + String.format("%03d", p.getSectionNumber()));
            }

            switch (p.getStatus()){
                case OPEN:
                    v.setBackgroundColor(Color.rgb(204, 255, 204));
                    break;
                case CLOSED:
                    v.setBackgroundColor(Color.rgb(255, 204, 204));
                    break;
                case WAIT_LIST:
                    v.setBackgroundColor(Color.rgb(255, 255, 204));
                    break;

            }
        }
        return v;

    }
}
