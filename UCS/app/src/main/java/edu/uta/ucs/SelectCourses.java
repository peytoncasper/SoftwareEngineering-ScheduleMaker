package edu.uta.ucs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Holds Info to be shown in listview
 */
class DesiredCourse {
    private String courseDepartment;
    private String courseNumber;
    private String courseTitle;

    DesiredCourse(){
        setCourseDepartment(null);
        setCourseNumber(null);
        setCourseTitle(null);
    }

    /**
     * Constructor
     *
     * @param department
     * @param number
     * @param title
     */
    DesiredCourse(String department, String number, String title){
        setCourseDepartment(department);
        setCourseNumber(number);
        setCourseTitle(title);
    }

    public String getCourseDepartment() {
        return courseDepartment;
    }

    public void setCourseDepartment(String courseDepartment) {
        this.courseDepartment = courseDepartment;
    }

    public String getCourseNumber() {
        return courseNumber;
    }

    public void setCourseNumber(String courseNumber) {
        this.courseNumber = courseNumber;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }
}

class DesiredCoursesArrayAdapter extends ArrayAdapter<DesiredCourse>{

    /**
     * Constructor
     *
     * @param context  The current context.
     * @param resource The resource ID for a layout file containing a TextView to use when
     */
    DesiredCoursesArrayAdapter(Context context, int resource) {
        super(context, resource);
    }

    /**
     * Constructor
     *
     * @param context  The current context.
     * @param resource The resource ID for a layout file containing a TextView to use when
     */
    public DesiredCoursesArrayAdapter(Context context, int resource, ArrayList<DesiredCourse> item ) {
        super(context, resource, item);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if (view == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            view = vi.inflate(R.layout.desired_courses_listview, null);
        }

        DesiredCourse desiredCourse = getItem(position);

        if (desiredCourse != null){
            TextView desiredCourseDepartment = ((TextView) view.findViewById(R.id.desiredCourseDepartment));
            TextView desiredCourseNumber = ((TextView) view.findViewById(R.id.desiredCourseNumber));
            TextView desiredCourseTitle = ((TextView) view.findViewById(R.id.desiredCourseTitle));

            String Department = desiredCourse.getCourseDepartment() + " ";
            String Number = "- " + desiredCourse.getCourseNumber();
            String Title = desiredCourse.getCourseTitle();

            if (Department != null){
                desiredCourseDepartment.setText(Department);
            }

            if (Number != null){
                desiredCourseNumber.setText(Number);
            }

            if (Title != null){
                desiredCourseTitle.setText(Title);
            }
        }

        return view;
    }
}

/**
 * Holds all course info for an entire semester for AutoComplete and filtering of courses for validity
 */
class SemesterInfo{

    /**
     * Holds a departments' ID, Title, and an arraylist of CourseInfo to hold course information for all courses in that department.
     */
    class DepartmentInfo {


        /**
         * Holds a course number for autocomplete as well as the tile for that course number.
         */
        class CourseInfo{
            int courseNumber;
            String courseTitle;

            /**
             * Constructor
             * @param courseInfoJSONObject JSON Object must have the following keys present:
             *                   <ul>
             *                   <li>"CourseNumber" - integer, represents the course number in the course info
             *                   <li><t>In an example course, such as "ENGL-1301", this would the the "1301" part
             *                   <li>"CourseTitle" - string, course title. Example: "RHETORIC AND COMPOSITION I" for ENGL-1301
             *                   <ul/>
             * @throws JSONException
             */
            public CourseInfo(JSONObject courseInfoJSONObject) throws JSONException {
                this.courseNumber = courseInfoJSONObject.getInt("CourseNumber");
                this.courseTitle = courseInfoJSONObject.getString("CourseTitle");
            }
        }

        String departmentID;
        String departmentTitle;
        ArrayList<CourseInfo> courses;

        /**
         * Constructor
         *
         * @param departmentInfoRaw JSON Object must have the following keys present:
         *                   <ul>
         *                   <li>"ID" - string, abbreviated department title as used in MyMav.
         *                          EX: "ENGL" for "English" department
         *                              "ECED" for "Early Childhood Education" department
         *                   <li>"Title" - string, full name of department. EX: "English" or "Early Childhood Education"
         *                   <li>"Courses" - JSONArray, Course Information. See CourseInfo constructor for requirements
         *                   <ul/>
         * @throws JSONException
         */
        public DepartmentInfo(JSONObject departmentInfoRaw) throws JSONException {
            this.departmentID = departmentInfoRaw.getString("ID");
            this.departmentTitle = departmentInfoRaw.getString("Title");
            JSONArray courseJSONArrayRaw = departmentInfoRaw.getJSONArray("Courses");

            for(int index = courseJSONArrayRaw.length(); index != 0;index--){
                this.courses.add(new CourseInfo(courseJSONArrayRaw.getJSONObject(index - 1)));
            }
        }
    }

    int semesterNumber;
    ArrayList<DepartmentInfo> departmentArrayList;

    /**
     * Constructor
     *
     * @param semesterInfoRaw JSON Object must have the following keys present:
     *                   <ul>
     *                   <li>"Success" - boolean, represents good server response
     *                   <li>"SemesterNumber" - integer, UTA semster number. EX: 2155 for Summer 2015, 2158 for Spring 2015
     *                   <li>"Departments" - JSONArray, Departments information. See DepartmentInfo constructor for requirements
     *                   <ul/>
     * @throws JSONException
     */
    SemesterInfo(JSONObject semesterInfoRaw) throws JSONException {
        boolean success;
        success = semesterInfoRaw.getBoolean("Success");
        if(success){
            this.semesterNumber = semesterInfoRaw.getInt("SemesterNumber");
            JSONArray departmentJSONArrayRaw = semesterInfoRaw.getJSONArray("Departments");
            this.departmentArrayList = new ArrayList<>(departmentJSONArrayRaw.length());

            for(int index = departmentJSONArrayRaw.length(); index != 0;index--){
                this.departmentArrayList.add(new DepartmentInfo(departmentJSONArrayRaw.getJSONObject( index-1 )));
            }
        }
        else {
            semesterNumber = 0;
            departmentArrayList = null;
        }
    }
}

public class SelectCourses extends ActionBarActivity {

    public static final String ACTION_DEPARTMENT_SELECT ="edu.uta.ucs.intent.action.ACTION_DEPARTMENT_SELECT";
    public static final String SPOOFED_DEPARTMENT_COURSES ="edu.uta.ucs.intent.action.ACTION_DEPARTMENT_SELECT";

    private EditText courseDepartment;
    private EditText courseNumber;

    private ListView desiredCoursesListView;
    private DesiredCoursesArrayAdapter desiredCoursesArrayAdapter;

    private Course blockoutTimes = null;
    private ArrayList<DesiredCourse> desiredCoursesArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_courses);

        LocalBroadcastManager.getInstance(this).registerReceiver(new DepartmentCoursesReceiver(), new IntentFilter(ACTION_DEPARTMENT_SELECT));

        courseDepartment = ((EditText) findViewById(R.id.course_department_edittext));
        courseNumber = ((EditText) findViewById(R.id.course_number_edittext));

        desiredCoursesListView = (ListView) findViewById(R.id.selected_courses_listview);

        desiredCoursesArrayList = new ArrayList<>();
    }

    @Override
    protected void onStart() {
        super.onStart();

        desiredCoursesArrayAdapter = new DesiredCoursesArrayAdapter(SelectCourses.this, R.layout.section_list_display, desiredCoursesArrayList);

        desiredCoursesListView.setAdapter(desiredCoursesArrayAdapter);
    }

    public void addCourse(View view){
        String department = courseDepartment.getText().toString().toUpperCase();
        String number = courseNumber.getText().toString();

        if (!department.equals("") && !number.equals("") ) {
            desiredCoursesArrayList.add(new DesiredCourse(department, number, ""));
            desiredCoursesArrayAdapter.notifyDataSetChanged();

            courseDepartment.setText("");
            courseNumber.setText("");

            courseDepartment.requestFocus();
        }

    }

    public void getDepartmentCourses(View view){
        String url = null;

        Intent intent = new Intent(this, HTTPGetService.class);
        if(true) {
            intent.putExtra(HTTPGetService.URL_REQUEST, HTTPGetService.SPOOF_SERVER);
            intent.putExtra(HTTPGetService.SPOOFED_RESPONSE, SPOOFED_DEPARTMENT_COURSES);
        }
        else
            intent.putExtra(HTTPGetService.URL_REQUEST, url);

        startService(intent);
    }

    public void selectBlockoutTimes(View view){
        Intent startSelectCoursesActivity = new Intent(SelectCourses.this, SelectBlockoutTimes.class);
        if (blockoutTimes != null) {
            Log.d("BLOCKOUT TIMES", blockoutTimes.toJSON().toString());
            startSelectCoursesActivity.putExtra("BLOCKOUT TIMES", blockoutTimes.toJSON().toString());
        }
        SelectCourses.this.startActivityForResult(startSelectCoursesActivity, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String blockoutTimes = data.getStringExtra("BLOCKOUT");
        try {
            JSONObject jsonBlockoutTimes = new JSONObject(blockoutTimes);
            this.blockoutTimes = new Course(jsonBlockoutTimes);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (this.blockoutTimes != null)
        Log.d("test", this.blockoutTimes.toJSON().toString());
    }

    private class DepartmentCoursesReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            JSONObject response = null;
            boolean success = false;
            try {
                response = new JSONObject(intent.getStringExtra(HTTPGetService.SERVER_RESPONSE));
                success = response.getBoolean("Success");
                if(success){
                    // enable text field
                }
                else {
                    // Try again?
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
