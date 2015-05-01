package edu.uta.ucs;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Holds all course info for an entire semester for AutoComplete and filtering of courses for validity
 */
class SemesterInfo{

    private int semesterNumber;
    private ArrayList<DepartmentInfo> departmentArrayList;


    public static ArrayList<SemesterInfo> SemesterInfoFactory(JSONObject SemesterRaw) throws JSONException {
        JSONArray semestersArray = SemesterRaw.getJSONArray("Semesters");
        ArrayList<SemesterInfo> results = new ArrayList<>(semestersArray.length());

        for(int index = semestersArray.length(); index != 0;index--){
            SemesterInfo parsedSemester = new SemesterInfo(semestersArray.getJSONObject( index-1 ));
            results.add(parsedSemester);
        }
        Collections.reverse(results);

        return results;
    }

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
        this.semesterNumber = semesterInfoRaw.getInt("SemesterNumber");
        Log.i("Semester Number", ((Integer) semesterNumber).toString());
        JSONArray departmentJSONArrayRaw = semesterInfoRaw.getJSONArray("Departments");
        this.departmentArrayList = new ArrayList<>(departmentJSONArrayRaw.length());

        for(int index = departmentJSONArrayRaw.length(); index != 0;index--){
            this.departmentArrayList.add(new DepartmentInfo(departmentJSONArrayRaw.getJSONObject(index - 1)));
            }

    }

    public JSONObject toJSON() throws JSONException {

        ArrayList<JSONObject> departmentInfoArray = new ArrayList<>(departmentArrayList.size());
        for(DepartmentInfo departmentInfo : departmentArrayList){
            departmentInfoArray.add(departmentInfo.toJSON());
        }
        JSONArray departmentInfoJSONArray = new JSONArray(departmentInfoArray);

        JSONObject semesterInfoJSON = new JSONObject();
        semesterInfoJSON.put("SemesterNumber", semesterNumber);
        semesterInfoJSON.put("Departments", departmentInfoJSONArray);
        return semesterInfoJSON;
    }

    public int getSemesterNumber() {
        return semesterNumber;
    }

    public void setSemesterNumber(int semesterNumber) {
        this.semesterNumber = semesterNumber;
    }

    public ArrayList<DepartmentInfo> getDepartmentArrayList() {
        return departmentArrayList;
    }

    public void setDepartmentArrayList(ArrayList<DepartmentInfo> departmentArrayList) {
        this.departmentArrayList = departmentArrayList;
    }

    /**
     * Holds a departments' ID, Title, and an arraylist of CourseInfo to hold course information for all courses in that department.
     */
    class DepartmentInfo {

        private int departmentID;
        private String departmentAcronym;
        private String departmentTitle;
        private ArrayList<CourseInfo> courses;

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
            this.setDepartmentID(departmentInfoRaw.getInt("Id"));
            this.setDepartmentAcronym(departmentInfoRaw.getString("DepartmentAcronym"));
            this.setDepartmentTitle(null);//departmentInfoRaw.getString("Title");
            JSONArray courseJSONArrayRaw = departmentInfoRaw.getJSONArray("CourseNumbers");
            this.courses = new ArrayList<>(courseJSONArrayRaw.length());

            for(int index = courseJSONArrayRaw.length(); index != 0;index--){
                this.getCourses().add(new CourseInfo(courseJSONArrayRaw.getJSONObject(index - 1), this));
            }

            Log.i("Department Details", "New Department Added:"+ getDepartmentID() + " " + getDepartmentAcronym() + " " + getDepartmentTitle() + " " + getCourses().size());
        }

        public DepartmentInfo(){
            this.setDepartmentID(0);
            this.setDepartmentAcronym(null);
            this.setDepartmentTitle(null);
            this.setCourses(null);
        }

        public JSONObject toJSON() throws JSONException {

            ArrayList<JSONObject> courseInfoArray = new ArrayList<>(courses.size());
            for(CourseInfo courseInfo : courses){
                courseInfoArray.add(courseInfo.toJSON());
            }
            JSONArray courseInfoJSONArray = new JSONArray(courseInfoArray);

            JSONObject departmentInfoJSON = new JSONObject();

            departmentInfoJSON.put("Id", departmentID);
            departmentInfoJSON.put("DepartmentAcronym",departmentAcronym);
            departmentInfoJSON.put("CourseNumbers", courseInfoJSONArray);

            return departmentInfoJSON;
        }

        public int getDepartmentID() {
            return departmentID;
        }

        public void setDepartmentID(int departmentID) {
            this.departmentID = departmentID;
        }

        public String getDepartmentTitle() {
            return departmentTitle;
        }

        public void setDepartmentTitle(String departmentTitle) {
            this.departmentTitle = departmentTitle;
        }

        public ArrayList<CourseInfo> getCourses() {
            return courses;
        }

        public void setCourses(ArrayList<CourseInfo> courses) {
            this.courses = courses;
        }

        public String getDepartmentAcronym() {
            return departmentAcronym;
        }

        public void setDepartmentAcronym(String departmentAcronym) {
            this.departmentAcronym = departmentAcronym;
        }

        /**
         * Holds a course number for autocomplete as well as the tile for that course number.
         */
        class CourseInfo{

            private int courseNumber;
            private String courseTitle;
            private DepartmentInfo departmentInfo;

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
            public CourseInfo(JSONObject courseInfoJSONObject, DepartmentInfo departmentInfo) throws JSONException {
                this.courseNumber = courseInfoJSONObject.getInt("CourseNumber");
                this.courseTitle = courseInfoJSONObject.getString("CourseName");
                Log.i("Course Details", "New Course Added:" + " " + this.courseNumber + " " + this.courseTitle);
                this.departmentInfo = departmentInfo;
            }

            public CourseInfo(JSONObject courseInfoJSONObject) throws JSONException {
                this.courseNumber = courseInfoJSONObject.getInt("CourseNumber");
                this.courseTitle = courseInfoJSONObject.getString("CourseName");
                this.departmentInfo = new DepartmentInfo();
                this.departmentInfo.setDepartmentAcronym(courseInfoJSONObject.getString("DepartmentAcronym"));
            }

            /*public JSONObject toJSONIsolated() throws JSONException {
                JSONObject courseInfo = new JSONObject();
                courseInfo.put("CourseNumber", courseNumber);
                courseInfo.put("CourseTitle", courseTitle);
                courseInfo.put("DepartmentAcronym", getDepartmentAcronym());
                return courseInfo;
            }*/

            public JSONObject toJSON() throws JSONException {
                JSONObject courseInfo = new JSONObject();
                courseInfo.put("CourseNumber", courseNumber);
                courseInfo.put("CourseName", courseTitle);
                return courseInfo;
            }

            public int getCourseNumber() {
                return courseNumber;
            }

            public void setCourseNumber(int courseNumber) {
                this.courseNumber = courseNumber;
            }

            public String getCourseTitle() {
                return courseTitle;
            }

            public void setCourseTitle(String courseTitle) {
                this.courseTitle = courseTitle;
            }

            public DepartmentInfo getDepartmentInfo() {
                return departmentInfo;
            }

            public void setDepartmentInfo(DepartmentInfo departmentInfo) {
                this.departmentInfo = departmentInfo;
            }
        }
    }
}

class DepartmentInfoArrayAdapter extends ArrayAdapter<SemesterInfo.DepartmentInfo> implements Filterable{

    private ArrayList<SemesterInfo.DepartmentInfo> departmentInfoArrayList = new ArrayList<>();
    private ArrayList<SemesterInfo.DepartmentInfo> departmentInfoArrayListAll = new ArrayList<>();
    private Context context;

    /**
     * Constructor
     *
     * @param context  The current context.
     * @param resource The resource ID for a layout file containing a TextView to use when
     *                 instantiating views.
     * @param objects  The objects to represent in the ListView.
     */
    public DepartmentInfoArrayAdapter(Context context, int resource, ArrayList<SemesterInfo.DepartmentInfo> objects) {
        super(context, resource, objects);
        this.departmentInfoArrayList = objects;
        this.departmentInfoArrayListAll = objects;
        this.context = context;
    }

    @Override
    public int getCount() {
        return departmentInfoArrayList.size();
    }

    @Override
    public SemesterInfo.DepartmentInfo getItem(int position) {
        return departmentInfoArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.desired_courses_listview, parent, false);
        }

        TextView departmentID = ((TextView) convertView.findViewById(R.id.desiredCourseDepartment));
        TextView departmentNumber = ((TextView) convertView.findViewById(R.id.desiredCourseNumber));
        TextView departmentTitle = ((TextView) convertView.findViewById(R.id.desiredCourseTitle));

        SemesterInfo.DepartmentInfo departmentInfo = departmentInfoArrayList.get(position);

        departmentID.setText(departmentInfo.getDepartmentAcronym());
        departmentNumber.setText("");
        departmentTitle.setText("\t");// + departmentInfo.getDepartmentTitle());

        return convertView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                departmentInfoArrayList = departmentInfoArrayListAll;
                List<SemesterInfo.DepartmentInfo> results = new ArrayList<>();
                if (constraint != null){
                    for(SemesterInfo.DepartmentInfo departmentInfo : departmentInfoArrayList){
                        if(departmentInfo.getDepartmentAcronym().toUpperCase().startsWith(constraint.toString().toUpperCase())){
                            results.add(departmentInfo);
                        }
                        /*if(departmentInfo.getDepartmentTitle().contains(constraint.toString())){
                            results.add(departmentInfo);
                        }*/
                    }
                }
                filterResults.values = results;
                filterResults.count = results.size();
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0){
                    departmentInfoArrayList = (ArrayList<SemesterInfo.DepartmentInfo>) results.values;
                    notifyDataSetChanged();
                }
                else {
                    notifyDataSetInvalidated();
                }
            }
        };

        return filter;
    }
}

class CourseInfoArrayAdapter extends ArrayAdapter<SemesterInfo.DepartmentInfo.CourseInfo> implements Filterable{

    private ArrayList<SemesterInfo.DepartmentInfo.CourseInfo> courseInfoArrayList = new ArrayList<>();
    private ArrayList<SemesterInfo.DepartmentInfo.CourseInfo> courseInfoArrayListAll = new ArrayList<>();
    private Context context;

    /**
     * Constructor
     *
     * @param context  The current context.
     * @param resource The resource ID for a layout file containing a TextView to use when
     *                 instantiating views.
     * @param objects  The objects to represent in the ListView.
     */
    public CourseInfoArrayAdapter(Context context, int resource, ArrayList<SemesterInfo.DepartmentInfo.CourseInfo> objects) {
        super(context, resource, objects);
        this.courseInfoArrayList = objects;
        this.courseInfoArrayListAll = objects;
        this.context = context;
    }

    @Override
    public int getCount() {
        return courseInfoArrayList.size();
    }

    @Override
    public SemesterInfo.DepartmentInfo.CourseInfo getItem(int position) {
        return courseInfoArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.desired_courses_listview, parent, false);
        }

        TextView departmentID = ((TextView) convertView.findViewById(R.id.desiredCourseDepartment));
        TextView departmentNumber = ((TextView) convertView.findViewById(R.id.desiredCourseNumber));
        TextView departmentTitle = ((TextView) convertView.findViewById(R.id.desiredCourseTitle));

        SemesterInfo.DepartmentInfo.CourseInfo courseInfo = courseInfoArrayList.get(position);

        //departmentID.setText(courseInfo.getDepartmentInfo().getDepartmentAcronym());
        departmentID.setText("");
        departmentNumber.setText(((Integer) courseInfo.getCourseNumber()).toString());
        departmentTitle.setText("\t" + courseInfo.getCourseTitle());

        return convertView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                courseInfoArrayList = courseInfoArrayListAll;
                List<SemesterInfo.DepartmentInfo.CourseInfo> results = new ArrayList<>();
                if (constraint != null){
                    for(SemesterInfo.DepartmentInfo.CourseInfo courseInfo : courseInfoArrayList){
                        if(((Integer) courseInfo.getCourseNumber()).toString().contains(constraint.toString())){
                            results.add(courseInfo);
                        }
                        if(courseInfo.getCourseTitle().toUpperCase().contains(constraint.toString().toUpperCase())){
                            results.add(courseInfo);
                        }
                    }
                }
                filterResults.values = results;
                filterResults.count = results.size();
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0){
                    courseInfoArrayList = (ArrayList<SemesterInfo.DepartmentInfo.CourseInfo>) results.values;
                    notifyDataSetChanged();
                }
                else {
                    notifyDataSetInvalidated();
                }
            }
        };

        return filter;
    }
}

class DesiredCoursesArrayAdapter extends ArrayAdapter<SemesterInfo.DepartmentInfo.CourseInfo>{

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
    public DesiredCoursesArrayAdapter(Context context, int resource, ArrayList<SemesterInfo.DepartmentInfo.CourseInfo> item ) {
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

        SemesterInfo.DepartmentInfo.CourseInfo courseInfo = getItem(position);

        if (courseInfo != null){
            TextView desiredCourseDepartment = ((TextView) view.findViewById(R.id.desiredCourseDepartment));
            TextView desiredCourseNumber = ((TextView) view.findViewById(R.id.desiredCourseNumber));
            TextView desiredCourseTitle = ((TextView) view.findViewById(R.id.desiredCourseTitle));

            String Department = courseInfo.getDepartmentInfo().getDepartmentAcronym();
            String Number = " - " + ((Integer) courseInfo.getCourseNumber()).toString();
            String Title = "\t" + courseInfo.getCourseTitle();

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


public class SelectCourses extends ActionBarActivity {

    private static final boolean spoofServerSwitch = false;

    private static final String SHARED_PREFS = "SHARED_PREFS";

    public static final String URL_GET_DESIRED_COURSE_SECTIONS ="http://ucs-scheduler.cloudapp.net/UTA/ClassStatus?classes=";
    public static final String ACTION_GET_DESIRED_COURSE_SECTIONS ="edu.uta.ucs.intent.action.ACTION_GET_DESIRED_COURSE_SECTIONS";
    private static final String SPOOF_DESIRED_COURSE_SECTIONS = "{\"Results\":[{\"CourseId\":\"ENGL-1301\",\"CourseName\":\"ENGL 1301 - RHETORIC AND COMPOSITION I\",\"CourseResults\":[{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80594\",\"Section\":\"001\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"8:00AM-8:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80595\",\"Section\":\"002\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"8:00AM-8:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80596\",\"Section\":\"003\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"8:00AM-8:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80597\",\"Section\":\"004\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"10:00AM-10:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80598\",\"Section\":\"005\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"8:00AM-8:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80599\",\"Section\":\"006\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"9:00AM-9:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80600\",\"Section\":\"007\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"9:00AM-9:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80601\",\"Section\":\"008\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"9:00AM-9:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80602\",\"Section\":\"009\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"9:00AM-9:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80603\",\"Section\":\"010\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"9:00AM-9:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80604\",\"Section\":\"011\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"9:00AM-9:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80605\",\"Section\":\"012\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"9:00AM-9:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80606\",\"Section\":\"013\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"9:00AM-9:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80607\",\"Section\":\"014\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"9:00AM-9:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80608\",\"Section\":\"015\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"10:00AM-10:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80609\",\"Section\":\"016\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"10:00AM-10:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80610\",\"Section\":\"017\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"10:00AM-10:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80611\",\"Section\":\"018\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"10:00AM-10:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80612\",\"Section\":\"019\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"10:00AM-10:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80613\",\"Section\":\"020\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"10:00AM-10:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80614\",\"Section\":\"021\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"10:00AM-10:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80615\",\"Section\":\"022\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"10:00AM-10:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80616\",\"Section\":\"023\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"11:00AM-11:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80617\",\"Section\":\"024\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"11:00AM-11:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80618\",\"Section\":\"025\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"11:00AM-11:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80619\",\"Section\":\"026\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"11:00AM-11:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80620\",\"Section\":\"027\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"11:00AM-11:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80621\",\"Section\":\"028\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"11:00AM-11:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80622\",\"Section\":\"029\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"11:00AM-11:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80623\",\"Section\":\"030\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"1:00PM-1:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"86701\",\"Section\":\"031\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"8:00AM-9:20AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80624\",\"Section\":\"032\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"1:00PM-1:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80625\",\"Section\":\"033\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"1:00PM-1:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"86702\",\"Section\":\"034\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"8:00AM-9:20AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80626\",\"Section\":\"035\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"2:00PM-2:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"86703\",\"Section\":\"036\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"8:00AM-9:20AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80627\",\"Section\":\"038\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"2:00PM-2:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80628\",\"Section\":\"039\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"4:00PM-5:20PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80629\",\"Section\":\"040\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"5:30PM-6:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"80630\",\"Section\":\"041\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"8:00AM-9:20AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"80631\",\"Section\":\"042\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"8:00AM-9:20AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"80632\",\"Section\":\"043\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"8:00AM-9:20AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"80633\",\"Section\":\"044\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"12:30PM-1:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"80634\",\"Section\":\"045\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"9:30AM-10:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"80635\",\"Section\":\"046\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"9:30AM-10:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"80636\",\"Section\":\"047\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"9:30AM-10:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"80637\",\"Section\":\"048\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"9:30AM-10:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"80638\",\"Section\":\"049\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"11:00AM-12:20PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"80639\",\"Section\":\"050\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"11:00AM-12:20PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"80640\",\"Section\":\"051\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"11:00AM-12:20PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"80641\",\"Section\":\"052\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"11:00AM-12:20PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"80642\",\"Section\":\"053\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"11:00AM-12:20PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"80643\",\"Section\":\"054\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"11:00AM-12:20PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"80644\",\"Section\":\"055\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"12:30PM-1:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"80645\",\"Section\":\"056\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"12:30PM-1:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"80646\",\"Section\":\"057\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"12:30PM-1:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"80647\",\"Section\":\"058\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"12:30PM-1:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"80648\",\"Section\":\"059\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"2:00PM-3:20PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"80649\",\"Section\":\"060\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"2:00PM-3:20PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"80650\",\"Section\":\"061\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"3:30PM-4:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"80651\",\"Section\":\"062\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"7:00PM-8:20PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"80652\",\"Section\":\"066\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"2:00PM-3:20PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80653\",\"Section\":\"067\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"1:00PM-1:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80654\",\"Section\":\"068\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"1:00PM-1:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"80655\",\"Section\":\"069\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"2:00PM-2:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"80656\",\"Section\":\"071\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"12:30PM-1:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"80657\",\"Section\":\"072\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"2:00PM-3:20PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"80658\",\"Section\":\"073\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"5:30PM-6:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"85880\",\"Section\":\"075\",\"Room\":\"OFF WEB\",\"Instructor\":\"Staff\",\"MeetingTime\":\"TBA\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"83333\",\"Section\":\"077\",\"Room\":\"OFF WEB\",\"Instructor\":\"Staff\",\"MeetingTime\":\"TBA\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"84535\",\"Section\":\"079\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"1:00PM-1:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"84537\",\"Section\":\"081\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"9:00AM-9:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"86406\",\"Section\":\"082\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"8:00AM-8:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"86407\",\"Section\":\"083\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"2:00PM-2:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"86408\",\"Section\":\"084\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"1:00PM-1:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"86409\",\"Section\":\"085\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"2:00PM-2:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"86513\",\"Section\":\"086\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"2:00PM-2:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"86515\",\"Section\":\"087\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"3:00PM-4:20PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"86543\",\"Section\":\"089\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"3:00PM-4:20PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"86556\",\"Section\":\"090\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"8:00AM-8:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"87922\",\"Section\":\"091\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"9:00AM-9:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"87923\",\"Section\":\"092\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"9:00AM-9:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"87924\",\"Section\":\"093\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"9:00AM-9:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"87920\",\"Section\":\"094\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"10:00AM-10:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"87921\",\"Section\":\"095\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"10:00AM-10:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"87925\",\"Section\":\"096\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"11:00AM-11:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"87926\",\"Section\":\"097\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"11:00AM-11:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"83165\",\"Section\":\"700\",\"Room\":\"OFF WEB\",\"Instructor\":\"Pamela K Rollins\",\"MeetingTime\":\"TBA\",\"Status\":\"Open\"}]},{\"CourseId\":\"MATH-1426\",\"CourseName\":\"MATH 1426 - CALCULUS I\",\"CourseResults\":[{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"84744\",\"Section\":\"100\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"8:00AM-8:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"84745\",\"Section\":\"101\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"9:00AM-9:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"84746\",\"Section\":\"102\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"10:00AM-10:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"84747\",\"Section\":\"200\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"9:00AM-9:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"84748\",\"Section\":\"201\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"11:00AM-11:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"84749\",\"Section\":\"202\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"1:00PM-1:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"87226\",\"Section\":\"271\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"12:30PM-1:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"87230\",\"Section\":\"273\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"11:00AM-11:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"84750\",\"Section\":\"300\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"4:00PM-5:20PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"84751\",\"Section\":\"301\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"3:00PM-3:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"84752\",\"Section\":\"302\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"5:30PM-6:20PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"85931\",\"Section\":\"400\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"2:00PM-3:20PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"85932\",\"Section\":\"401\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"1:00PM-1:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"85933\",\"Section\":\"402\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"3:30PM-4:20PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"85804\",\"Section\":\"500\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"7:00PM-8:20PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"85805\",\"Section\":\"501\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"6:00PM-6:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"84753\",\"Section\":\"700\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"12:30PM-1:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"84754\",\"Section\":\"701\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"2:00PM-2:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"85029\",\"Section\":\"710\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"12:30PM-1:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"85030\",\"Section\":\"711\",\"Room\":\"TBATBA\",\"Instructor\":\"StaffStaff\",\"MeetingTime\":\"11:00AM-11:50AMWe\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"85031\",\"Section\":\"720\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"12:30PM-1:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"85033\",\"Section\":\"721\",\"Room\":\"TBATBA\",\"Instructor\":\"StaffStaff\",\"MeetingTime\":\"11:00AM-11:50AMWe\",\"Status\":\"Open\"}]},{\"CourseId\":\"PHYS-1441\",\"CourseName\":\"PHYS 1441 - GENERAL COLLEGE PHYSICS I\",\"CourseResults\":[{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"81301\",\"Section\":\"002\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"1:00PM-1:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"81148\",\"Section\":\"003\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"2:00PM-3:20PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"81149\",\"Section\":\"004\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"11:00AM-12:20PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"81150\",\"Section\":\"005\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"5:30PM-6:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"81151\",\"Section\":\"006\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"1:00PM-3:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"81152\",\"Section\":\"007\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"7:00PM-9:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"81153\",\"Section\":\"008\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"11:00AM-1:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"81154\",\"Section\":\"009\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"11:00AM-1:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"81155\",\"Section\":\"010\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"2:00PM-4:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"W\",\"F\"],\"CourseNumber\":\"81156\",\"Section\":\"011\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"1:00PM-3:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"W\",\"F\"],\"CourseNumber\":\"81157\",\"Section\":\"012\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"1:00PM-3:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"W\",\"F\"],\"CourseNumber\":\"81158\",\"Section\":\"013\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"7:00PM-9:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"81159\",\"Section\":\"014\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"8:00AM-10:50AM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"81160\",\"Section\":\"015\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"11:00AM-1:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"81161\",\"Section\":\"016\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"2:00PM-4:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\",\"F\"],\"CourseNumber\":\"81162\",\"Section\":\"017\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"11:00AM-1:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\",\"F\"],\"CourseNumber\":\"81163\",\"Section\":\"018\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"2:00PM-4:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"81164\",\"Section\":\"019\",\"Room\":\"TBA\",\"Instructor\":\"Staff\",\"MeetingTime\":\"1:00PM-3:50PM\",\"Status\":\"Open\"}]},{\"CourseId\":\"CSE-1105\",\"CourseName\":\"CSE 1105 - INTRODUCTION TO COMPUTER SCIENCE AND ENGINEERING\",\"CourseResults\":[{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"84195\",\"Section\":\"001\",\"Room\":\"TBA\",\"Instructor\":\"Eric W Becker\",\"MeetingTime\":\"3:00PM-3:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"M\",\"W\",\"F\"],\"CourseNumber\":\"84196\",\"Section\":\"002\",\"Room\":\"TBA\",\"Instructor\":\"Eric W Becker\",\"MeetingTime\":\"3:00PM-3:50PM\",\"Status\":\"Open\"},{\"MeetingDays\":[\"TU\",\"TH\"],\"CourseNumber\":\"84197\",\"Section\":\"003\",\"Room\":\"TBA\",\"Instructor\":\"Eric W Becker\",\"MeetingTime\":\"10:30AM-11:20AM\",\"Status\":\"Open\"}]}],\"TimeTaken\":29.622961699999998,\"Success\":true}";

    public static final String URL_GET_SEMESTER = "http://ucs-scheduler.cloudapp.net/UTA/GetDepartmentClassData";
    public static final String ACTION_GET_SEMESTER ="edu.uta.ucs.intent.action.ACTION_GET_SEMESTER";
    public static final String SPOOF_SEMESTER ="{\"Success\":true,\"Semesters\":[{\"Id\":5,\"SemesterNumber\":\"2155\",\"Departments\":[{\"Id\":531,\"SemesterId\":5,\"DepartmentAcronym\":\"ADVT\",\"CourseNumbers\":[{\"Id\":8975,\"DepartmentId\":531,\"CourseName\":\"INTEGRATED MARKETING COMMUNICATION (IMC) CASE STUDIES\",\"CourseNumber\":\"4300\"}]},{\"Id\":532,\"SemesterId\":5,\"DepartmentAcronym\":\"AE\",\"CourseNumbers\":[{\"Id\":8976,\"DepartmentId\":532,\"CourseName\":\"PREPARATORY COURSE FOR AEROSPACE ENGINEERING\",\"CourseNumber\":\"5300\"},{\"Id\":8977,\"DepartmentId\":532,\"CourseName\":\"FINITE ELEMENT METHODS\",\"CourseNumber\":\"5310\"},{\"Id\":8978,\"DepartmentId\":532,\"CourseName\":\"ANALYTIC METHODS IN ENGINEERING\",\"CourseNumber\":\"5331\"},{\"Id\":8979,\"DepartmentId\":532,\"CourseName\":\"OPTIMAL CONTROL OF DYNAMIC SYS\",\"CourseNumber\":\"5335\"},{\"Id\":8980,\"DepartmentId\":532,\"CourseName\":\"AEROSPACE ENGINEERING INTERNSHIP\",\"CourseNumber\":\"6196\"},{\"Id\":8981,\"DepartmentId\":532,\"CourseName\":\"RESEARCH IN AEROSPACE ENGINEERING\",\"CourseNumber\":\"6697\"},{\"Id\":8982,\"DepartmentId\":532,\"CourseName\":\"DISSERTATION\",\"CourseNumber\":\"6699\"},{\"Id\":8983,\"DepartmentId\":532,\"CourseName\":\"DISSERTATION\",\"CourseNumber\":\"6999\"}]},{\"Id\":533,\"SemesterId\":5,\"DepartmentAcronym\":\"AS\",\"CourseNumbers\":[]},{\"Id\":534,\"SemesterId\":5,\"DepartmentAcronym\":\"ASA\",\"CourseNumbers\":[{\"Id\":8984,\"DepartmentId\":534,\"CourseName\":\"AFFILIATED STUDY ABROAD\",\"CourseNumber\":\"1191\"},{\"Id\":8985,\"DepartmentId\":534,\"CourseName\":\"AFFILIATED STUDY ABROAD\",\"CourseNumber\":\"1291\"},{\"Id\":8986,\"DepartmentId\":534,\"CourseName\":\"AFFILIATED STUDIES ABROAD\",\"CourseNumber\":\"1391\"},{\"Id\":8987,\"DepartmentId\":534,\"CourseName\":\"AFFILIATED STUDIES ABROAD\",\"CourseNumber\":\"1491\"},{\"Id\":8988,\"DepartmentId\":534,\"CourseName\":\"AFFILIATED STUDY ABROAD\",\"CourseNumber\":\"2191\"},{\"Id\":8989,\"DepartmentId\":534,\"CourseName\":\"AFFILIATED STUDIES ABROAD\",\"CourseNumber\":\"2391\"},{\"Id\":8990,\"DepartmentId\":534,\"CourseName\":\"AFFILIATED STUDY ABROAD\",\"CourseNumber\":\"3191\"},{\"Id\":8991,\"DepartmentId\":534,\"CourseName\":\"AFFILIATED STUDIES ABROAD\",\"CourseNumber\":\"3391\"},{\"Id\":8992,\"DepartmentId\":534,\"CourseName\":\"AFFILIATED STUDIES ABROAD\",\"CourseNumber\":\"4391\"}]},{\"Id\":535,\"SemesterId\":5,\"DepartmentAcronym\":\"AAST\",\"CourseNumbers\":[{\"Id\":8993,\"DepartmentId\":535,\"CourseName\":\"INTRODUCTION TO AFRICAN AMERICAN STUDIES\",\"CourseNumber\":\"2300\"},{\"Id\":8994,\"DepartmentId\":535,\"CourseName\":\"CONFERENCE COURSE\",\"CourseNumber\":\"4391\"}]},{\"Id\":536,\"SemesterId\":5,\"DepartmentAcronym\":\"ANTH\",\"CourseNumbers\":[{\"Id\":8995,\"DepartmentId\":536,\"CourseName\":\"GLOBAL CULTURES\",\"CourseNumber\":\"2322\"},{\"Id\":8996,\"DepartmentId\":536,\"CourseName\":\"ARCHAEOLOGICAL CULTURES\",\"CourseNumber\":\"2358\"}]},{\"Id\":537,\"SemesterId\":5,\"DepartmentAcronym\":\"ARAB\",\"CourseNumbers\":[]},{\"Id\":538,\"SemesterId\":5,\"DepartmentAcronym\":\"ARCH\",\"CourseNumbers\":[{\"Id\":8997,\"DepartmentId\":538,\"CourseName\":\"CONFERENCE COURSE\",\"CourseNumber\":\"1191\"},{\"Id\":8998,\"DepartmentId\":538,\"CourseName\":\"MASTERWORKS OF WESTERN ARCHITECTURE\",\"CourseNumber\":\"2300\"},{\"Id\":8999,\"DepartmentId\":538,\"CourseName\":\"THE CITY OF ROME\",\"CourseNumber\":\"4305\"},{\"Id\":9000,\"DepartmentId\":538,\"CourseName\":\"URBAN DESIGN THEORY\",\"CourseNumber\":\"4306\"},{\"Id\":9001,\"DepartmentId\":538,\"CourseName\":\"TOPICS IN ARCHITECTURAL THEORY\",\"CourseNumber\":\"4311\"},{\"Id\":9002,\"DepartmentId\":538,\"CourseName\":\"NOTATIONAL DRAWING\",\"CourseNumber\":\"4341\"},{\"Id\":9003,\"DepartmentId\":538,\"CourseName\":\"SELECTED TOPICS ARCHITECTURE\",\"CourseNumber\":\"4395\"},{\"Id\":9004,\"DepartmentId\":538,\"CourseName\":\"CITY OF ROME\",\"CourseNumber\":\"5305\"},{\"Id\":9005,\"DepartmentId\":538,\"CourseName\":\"URBAN DESIGN\",\"CourseNumber\":\"5306\"},{\"Id\":9006,\"DepartmentId\":538,\"CourseName\":\"ARCHITECTURAL THEORY\",\"CourseNumber\":\"5311\"},{\"Id\":9007,\"DepartmentId\":538,\"CourseName\":\"PRACTICUM\",\"CourseNumber\":\"5381\"},{\"Id\":9008,\"DepartmentId\":538,\"CourseName\":\"TOPICS IN ARCHITECTURE\",\"CourseNumber\":\"5395\"},{\"Id\":9009,\"DepartmentId\":538,\"CourseName\":\"PRACTICUM\",\"CourseNumber\":\"5681\"},{\"Id\":9010,\"DepartmentId\":538,\"CourseName\":\"CONFERENCE COURSE\",\"CourseNumber\":\"5691\"}]},{\"Id\":539,\"SemesterId\":5,\"DepartmentAcronym\":\"ART\",\"CourseNumbers\":[{\"Id\":9011,\"DepartmentId\":539,\"CourseName\":\"ART APPRECIATION\",\"CourseNumber\":\"1301\"},{\"Id\":9012,\"DepartmentId\":539,\"CourseName\":\"TWO\",\"CourseNumber\":\"1305\"},{\"Id\":9013,\"DepartmentId\":539,\"CourseName\":\"THREE\",\"CourseNumber\":\"1306\"},{\"Id\":9014,\"DepartmentId\":539,\"CourseName\":\"DRAWING FUNDAMENTALS\",\"CourseNumber\":\"1307\"},{\"Id\":9015,\"DepartmentId\":539,\"CourseName\":\"ART OF THE WESTERN WORLD II: BAROQUE TO MODERN\",\"CourseNumber\":\"1310\"},{\"Id\":9016,\"DepartmentId\":539,\"CourseName\":\"DIGITAL DESIGN\",\"CourseNumber\":\"2304\"},{\"Id\":9017,\"DepartmentId\":539,\"CourseName\":\"LIFE DRAWING\",\"CourseNumber\":\"3348\"},{\"Id\":9018,\"DepartmentId\":539,\"CourseName\":\"DIGITAL IMAGING\",\"CourseNumber\":\"3352\"},{\"Id\":9019,\"DepartmentId\":539,\"CourseName\":\"SIGN AND SYMBOL\",\"CourseNumber\":\"3354\"},{\"Id\":9020,\"DepartmentId\":539,\"CourseName\":\"SCRIPT TO SCREEN\",\"CourseNumber\":\"4311\"},{\"Id\":9021,\"DepartmentId\":539,\"CourseName\":\"ADVANCED PRINTMAKING\",\"CourseNumber\":\"4345\"},{\"Id\":9022,\"DepartmentId\":539,\"CourseName\":\"ADVANCED PHOTOGRAPHY\",\"CourseNumber\":\"4359\"},{\"Id\":9023,\"DepartmentId\":539,\"CourseName\":\"INDEPENDENT STUDY\",\"CourseNumber\":\"4391\"},{\"Id\":9024,\"DepartmentId\":539,\"CourseName\":\"SPECIAL STUDIES\",\"CourseNumber\":\"4392\"},{\"Id\":9025,\"DepartmentId\":539,\"CourseName\":\"ART INTERNSHIP\",\"CourseNumber\":\"4395\"},{\"Id\":9026,\"DepartmentId\":539,\"CourseName\":\"SPECIAL STUDIES IN FILM/VIDEO\",\"CourseNumber\":\"4397\"},{\"Id\":9027,\"DepartmentId\":539,\"CourseName\":\"ART INTERNSHIP\",\"CourseNumber\":\"4695\"},{\"Id\":9028,\"DepartmentId\":539,\"CourseName\":\"INDEPENDENT STUDY\",\"CourseNumber\":\"5391\"}]},{\"Id\":540,\"SemesterId\":5,\"DepartmentAcronym\":\"ASTR\",\"CourseNumbers\":[{\"Id\":9029,\"DepartmentId\":540,\"CourseName\":\"INTRODUCTORY ASTRONOMY I\",\"CourseNumber\":\"1345\"},{\"Id\":9030,\"DepartmentId\":540,\"CourseName\":\"INTRODUCTORY ASTRONOMY II\",\"CourseNumber\":\"1346\"}]},{\"Id\":541,\"SemesterId\":5,\"DepartmentAcronym\":\"BEEP\",\"CourseNumbers\":[{\"Id\":9031,\"DepartmentId\":541,\"CourseName\":\"SPANISH FOR TEACHERS IN DUAL LANGUAGE PROGRAMS: AN IMMERSION APPROACH\",\"CourseNumber\":\"4366\"},{\"Id\":9032,\"DepartmentId\":541,\"CourseName\":\"ESL METHODS FOR EC\",\"CourseNumber\":\"5321\"},{\"Id\":9033,\"DepartmentId\":541,\"CourseName\":\"SPANISH FOR SCHOOL ADMINISTRATORS TEACHERS\",\"CourseNumber\":\"5366\"}]},{\"Id\":542,\"SemesterId\":5,\"DepartmentAcronym\":\"BE\",\"CourseNumbers\":[{\"Id\":9034,\"DepartmentId\":542,\"CourseName\":\"LABORATORY PRINCIPLES\",\"CourseNumber\":\"4382\"},{\"Id\":9035,\"DepartmentId\":542,\"CourseName\":\"DIRECTED RESEARCH IN BIOENGINEERING\",\"CourseNumber\":\"4391\"},{\"Id\":9036,\"DepartmentId\":542,\"CourseName\":\"DIRECTED RESEARCH IN BIOENGINEERING\",\"CourseNumber\":\"5191\"},{\"Id\":9037,\"DepartmentId\":542,\"CourseName\":\"DIRECTED RESEARCH IN BIOENGINEERING\",\"CourseNumber\":\"5291\"},{\"Id\":9038,\"DepartmentId\":542,\"CourseName\":\"HUMAN PHYSIOLOGY IN BIOENGINEERING\",\"CourseNumber\":\"5309\"},{\"Id\":9039,\"DepartmentId\":542,\"CourseName\":\"TISSUE ENGINEERING LAB\",\"CourseNumber\":\"5365\"},{\"Id\":9040,\"DepartmentId\":542,\"CourseName\":\"DRUG DELIVERY LAB\",\"CourseNumber\":\"5373\"},{\"Id\":9041,\"DepartmentId\":542,\"CourseName\":\"LABORATORY PRINCIPLES\",\"CourseNumber\":\"5382\"},{\"Id\":9042,\"DepartmentId\":542,\"CourseName\":\"RESEARCH PROJECT\",\"CourseNumber\":\"5390\"},{\"Id\":9043,\"DepartmentId\":542,\"CourseName\":\"DIRECTED RESEARCH IN BIOENGINEERING\",\"CourseNumber\":\"5391\"},{\"Id\":9044,\"DepartmentId\":542,\"CourseName\":\"THESIS\",\"CourseNumber\":\"5398\"},{\"Id\":9045,\"DepartmentId\":542,\"CourseName\":\"THESIS\",\"CourseNumber\":\"5698\"},{\"Id\":9046,\"DepartmentId\":542,\"CourseName\":\"PhD SEMINAR IN BIOENGINEERING\",\"CourseNumber\":\"6103\"},{\"Id\":9047,\"DepartmentId\":542,\"CourseName\":\"DOCTORAL COMPREHENSIVE EXAMINATION\",\"CourseNumber\":\"6195\"},{\"Id\":9048,\"DepartmentId\":542,\"CourseName\":\"RESEARCH IN BIOENGINEERING\",\"CourseNumber\":\"6197\"},{\"Id\":9049,\"DepartmentId\":542,\"CourseName\":\"RESEARCH IN BIOENGINEERING\",\"CourseNumber\":\"6297\"},{\"Id\":9050,\"DepartmentId\":542,\"CourseName\":\"INTERNSHIP IN BIOENGINEERING\",\"CourseNumber\":\"6395\"},{\"Id\":9051,\"DepartmentId\":542,\"CourseName\":\"RESEARCH IN BIOENGINEERING\",\"CourseNumber\":\"6397\"},{\"Id\":9052,\"DepartmentId\":542,\"CourseName\":\"DISSERTATION\",\"CourseNumber\":\"6399\"},{\"Id\":9053,\"DepartmentId\":542,\"CourseName\":\"DISSERTATION\",\"CourseNumber\":\"6499\"},{\"Id\":9054,\"DepartmentId\":542,\"CourseName\":\"INTERNSHIP IN BIOENGINEERING\",\"CourseNumber\":\"6695\"},{\"Id\":9055,\"DepartmentId\":542,\"CourseName\":\"RESEARCH IN BIOENGINEERING\",\"CourseNumber\":\"6697\"},{\"Id\":9056,\"DepartmentId\":542,\"CourseName\":\"DISSERTATION\",\"CourseNumber\":\"6699\"},{\"Id\":9057,\"DepartmentId\":542,\"CourseName\":\"DISSERTATION\",\"CourseNumber\":\"6999\"},{\"Id\":9058,\"DepartmentId\":542,\"CourseName\":\"DOCTORAL DEGREE COMPLETION\",\"CourseNumber\":\"7399\"}]},{\"Id\":543,\"SemesterId\":5,\"DepartmentAcronym\":\"BIOL\",\"CourseNumbers\":[{\"Id\":9059,\"DepartmentId\":543,\"CourseName\":\"INTRODUCTION TO BIOLOGY I\",\"CourseNumber\":\"1333\"},{\"Id\":9060,\"DepartmentId\":543,\"CourseName\":\"INTRODUCTION TO BIOLOGY II\",\"CourseNumber\":\"1334\"},{\"Id\":9061,\"DepartmentId\":543,\"CourseName\":\"CELL AND MOLECULAR BIOLOGY\",\"CourseNumber\":\"1441\"},{\"Id\":9062,\"DepartmentId\":543,\"CourseName\":\"STRUCTURE AND FUNCTION OF ORGANISMS\",\"CourseNumber\":\"1442\"},{\"Id\":9063,\"DepartmentId\":543,\"CourseName\":\"INTRODUCTION TO BIOSTATISTICS\",\"CourseNumber\":\"2300\"},{\"Id\":9064,\"DepartmentId\":543,\"CourseName\":\"EVOLUTION ECOLOGY\",\"CourseNumber\":\"2343\"},{\"Id\":9065,\"DepartmentId\":543,\"CourseName\":\"HUMAN ANATOMY AND PHYSIOLOGY I\",\"CourseNumber\":\"2457\"},{\"Id\":9066,\"DepartmentId\":543,\"CourseName\":\"HUMAN ANATOMY AND PHYSIOLOGY II\",\"CourseNumber\":\"2458\"},{\"Id\":9067,\"DepartmentId\":543,\"CourseName\":\"NURSING MICROBIOLOGY\",\"CourseNumber\":\"2460\"},{\"Id\":9068,\"DepartmentId\":543,\"CourseName\":\"COOPERATIVE PROGRAM IN BIOLOGY\",\"CourseNumber\":\"3149\"},{\"Id\":9069,\"DepartmentId\":543,\"CourseName\":\"COOPERATIVE PROGRAM IN BIOLOGY\",\"CourseNumber\":\"3249\"},{\"Id\":9070,\"DepartmentId\":543,\"CourseName\":\"DRUGS AND BEHAVIOR\",\"CourseNumber\":\"3303\"},{\"Id\":9071,\"DepartmentId\":543,\"CourseName\":\"NON\",\"CourseNumber\":\"3309\"},{\"Id\":9072,\"DepartmentId\":543,\"CourseName\":\"SELECTED TOPICS IN MICROBIOLOGY\",\"CourseNumber\":\"3311\"},{\"Id\":9073,\"DepartmentId\":543,\"CourseName\":\"IMMUNOBIOLOGY\",\"CourseNumber\":\"3312\"},{\"Id\":9074,\"DepartmentId\":543,\"CourseName\":\"GENETICS\",\"CourseNumber\":\"3315\"},{\"Id\":9075,\"DepartmentId\":543,\"CourseName\":\"BRAIN AND BEHAVIOR\",\"CourseNumber\":\"3322\"},{\"Id\":9076,\"DepartmentId\":543,\"CourseName\":\"COOPERATIVE PROGRAM IN BIOLOGY\",\"CourseNumber\":\"3349\"},{\"Id\":9077,\"DepartmentId\":543,\"CourseName\":\"GENERAL MICROBIOLOGY\",\"CourseNumber\":\"3444\"},{\"Id\":9078,\"DepartmentId\":543,\"CourseName\":\"GENERAL ZOOLOGY\",\"CourseNumber\":\"3454\"},{\"Id\":9079,\"DepartmentId\":543,\"CourseName\":\"DIRECTED STUDY\",\"CourseNumber\":\"4179\"},{\"Id\":9080,\"DepartmentId\":543,\"CourseName\":\"RESEARCH IN BIOLOGY\",\"CourseNumber\":\"4189\"},{\"Id\":9081,\"DepartmentId\":543,\"CourseName\":\"DIRECTED STUDY\",\"CourseNumber\":\"4279\"},{\"Id\":9082,\"DepartmentId\":543,\"CourseName\":\"RESEARCH IN BIOLOGY\",\"CourseNumber\":\"4289\"},{\"Id\":9083,\"DepartmentId\":543,\"CourseName\":\"TISSUE ENGINEERING LAB\",\"CourseNumber\":\"4365\"},{\"Id\":9084,\"DepartmentId\":543,\"CourseName\":\"DIRECTED STUDY\",\"CourseNumber\":\"4379\"},{\"Id\":9085,\"DepartmentId\":543,\"CourseName\":\"INSTRUCTIONAL TECHNIQUES IN BIOLOGY\",\"CourseNumber\":\"4388\"},{\"Id\":9086,\"DepartmentId\":543,\"CourseName\":\"HONORS SENIOR PROJECT IN BIOLOGY\",\"CourseNumber\":\"4393\"},{\"Id\":9087,\"DepartmentId\":543,\"CourseName\":\"RESEARCH IN BIOLOGY\",\"CourseNumber\":\"5193\"},{\"Id\":9088,\"DepartmentId\":543,\"CourseName\":\"INDIVIDUAL PROBLEMS IN BIOLOGY\",\"CourseNumber\":\"5291\"},{\"Id\":9089,\"DepartmentId\":543,\"CourseName\":\"RESEARCH\",\"CourseNumber\":\"5293\"},{\"Id\":9090,\"DepartmentId\":543,\"CourseName\":\"INDIVIDUAL PROBLEMS IN BIOLOGY\",\"CourseNumber\":\"5391\"},{\"Id\":9091,\"DepartmentId\":543,\"CourseName\":\"RESEARCH IN BIOLOGY\",\"CourseNumber\":\"5393\"},{\"Id\":9092,\"DepartmentId\":543,\"CourseName\":\"THESIS\",\"CourseNumber\":\"5398\"},{\"Id\":9093,\"DepartmentId\":543,\"CourseName\":\"RESEARCH\",\"CourseNumber\":\"5493\"},{\"Id\":9094,\"DepartmentId\":543,\"CourseName\":\"RESEARCH IN BIOLOGY\",\"CourseNumber\":\"5693\"},{\"Id\":9095,\"DepartmentId\":543,\"CourseName\":\"THESIS\",\"CourseNumber\":\"5698\"},{\"Id\":9096,\"DepartmentId\":543,\"CourseName\":\"ADVANCED RESEARCH\",\"CourseNumber\":\"6191\"},{\"Id\":9097,\"DepartmentId\":543,\"CourseName\":\"ADVANCED RESEARCH\",\"CourseNumber\":\"6391\"},{\"Id\":9098,\"DepartmentId\":543,\"CourseName\":\"DISSERTATION\",\"CourseNumber\":\"6399\"},{\"Id\":9099,\"DepartmentId\":543,\"CourseName\":\"ADVANCED RESEARCH\",\"CourseNumber\":\"6591\"},{\"Id\":9100,\"DepartmentId\":543,\"CourseName\":\"ADVANCED RESEARCH\",\"CourseNumber\":\"6691\"},{\"Id\":9101,\"DepartmentId\":543,\"CourseName\":\"DISSERTATION\",\"CourseNumber\":\"6699\"},{\"Id\":9102,\"DepartmentId\":543,\"CourseName\":\"DISSERTATION\",\"CourseNumber\":\"6999\"},{\"Id\":9103,\"DepartmentId\":543,\"CourseName\":\"DOCTORAL DEGREE COMPLETION\",\"CourseNumber\":\"7399\"}]},{\"Id\":544,\"SemesterId\":5,\"DepartmentAcronym\":\"BCMN\",\"CourseNumbers\":[{\"Id\":9104,\"DepartmentId\":544,\"CourseName\":\"BROADCAST WRITING AND REPORTING\",\"CourseNumber\":\"2347\"},{\"Id\":9105,\"DepartmentId\":544,\"CourseName\":\"RADIO PRODUCTION I\",\"CourseNumber\":\"2357\"},{\"Id\":9106,\"DepartmentId\":544,\"CourseName\":\"TELEVISION PRODUCTION I\",\"CourseNumber\":\"2358\"},{\"Id\":9107,\"DepartmentId\":544,\"CourseName\":\"PROFESSIONAL INTERNSHIP\",\"CourseNumber\":\"4395\"}]},{\"Id\":545,\"SemesterId\":5,\"DepartmentAcronym\":\"BSAD\",\"CourseNumbers\":[{\"Id\":9108,\"DepartmentId\":545,\"CourseName\":\"DISSERTATION\",\"CourseNumber\":\"6399\"},{\"Id\":9109,\"DepartmentId\":545,\"CourseName\":\"DISSERTATION\",\"CourseNumber\":\"6699\"},{\"Id\":9110,\"DepartmentId\":545,\"CourseName\":\"DISSERTATION\",\"CourseNumber\":\"6999\"},{\"Id\":9111,\"DepartmentId\":545,\"CourseName\":\"DOCTORAL DEGREE COMPLETION\",\"CourseNumber\":\"7399\"}]}]}]}";

    private AutoCompleteTextView courseDepartment;
    private AutoCompleteTextView courseNumber;

    private ListView desiredCoursesListView;
    private DesiredCoursesArrayAdapter desiredCoursesArrayAdapter;
    private DepartmentInfoArrayAdapter departmentInfoArrayAdapter;
    private CourseInfoArrayAdapter courseInfoArrayAdapter;

    private Course blockoutTimes = null;
    private ArrayList<SemesterInfo.DepartmentInfo.CourseInfo> desiredCoursesArrayList;
    private ArrayList<SemesterInfo.DepartmentInfo> departmentInfoArrayList;
    private ArrayList<SemesterInfo.DepartmentInfo.CourseInfo> courseInfoArrayList;

    private ArrayList<Course> fetchedCourses;

    private SemesterInfo.DepartmentInfo.CourseInfo tempCourseInfo;
    private SemesterInfo selectedSemester;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_courses);

        departmentInfoArrayList = new ArrayList<>();
        courseInfoArrayList = new ArrayList<>();

        LocalBroadcastManager.getInstance(this).registerReceiver(new DepartmentCoursesReceiver(), new IntentFilter(ACTION_GET_SEMESTER));
        LocalBroadcastManager.getInstance(this).registerReceiver(new DesiredSectionsReceiver(), new IntentFilter(ACTION_GET_DESIRED_COURSE_SECTIONS));

        departmentInfoArrayAdapter = new DepartmentInfoArrayAdapter(this,R.layout.desired_courses_listview, departmentInfoArrayList);
        courseInfoArrayAdapter = new CourseInfoArrayAdapter(this,R.layout.desired_courses_listview, courseInfoArrayList);

        desiredCoursesListView = (ListView) findViewById(R.id.selected_courses_listview);

        courseDepartment = ((AutoCompleteTextView) findViewById(R.id.course_department_edittext));
        courseNumber = ((AutoCompleteTextView) findViewById(R.id.course_number_edittext));
    }

    @Override
    protected void onStart() {
        super.onStart();

        courseDepartment.setThreshold(0);
        courseDepartment.setAdapter(departmentInfoArrayAdapter);
        courseDepartment.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SemesterInfo.DepartmentInfo departmentInfo = (SemesterInfo.DepartmentInfo) parent.getItemAtPosition(position);
                courseDepartment.setText(departmentInfo.getDepartmentAcronym());
                updateCourseInfoAdapter(departmentInfo.getCourses());
            }
        });
        courseDepartment.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    SemesterInfo.DepartmentInfo departmentInfo = getDepartmentInfo(courseDepartment.getText().toString());
                    if (departmentInfo != null){
                        courseDepartment.setText(departmentInfo.getDepartmentAcronym());
                        updateCourseInfoAdapter(departmentInfo.getCourses());
                    }
                }
            }
        });

        courseNumber.setThreshold(0);
        courseNumber.setAdapter(courseInfoArrayAdapter);
        courseNumber.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SemesterInfo.DepartmentInfo.CourseInfo courseInfo = (SemesterInfo.DepartmentInfo.CourseInfo) parent.getItemAtPosition(position);
                courseNumber.setText(((Integer) courseInfo.getCourseNumber()).toString());
                tempCourseInfo = courseInfo;
            }
        });

        desiredCoursesArrayList = new ArrayList<>();
        desiredCoursesArrayAdapter = new DesiredCoursesArrayAdapter(SelectCourses.this, R.layout.desired_courses_listview, desiredCoursesArrayList);
        desiredCoursesListView.setAdapter(desiredCoursesArrayAdapter);
    }

    public void addCourse(View view){

        if(selectedSemester == null){
            Toast.makeText(getApplicationContext(), "Please select a semester first", Toast.LENGTH_LONG).show();
            return;
        }
        String department = courseDepartment.getText().toString().toUpperCase();
        String number = courseNumber.getText().toString();

        SemesterInfo.DepartmentInfo.CourseInfo selectedCourse = getCourseInfo(department, number);

        if (selectedCourse != null) {

            Log.d("Selected Course",selectedCourse.getDepartmentInfo().getDepartmentAcronym() + " - " + ((Integer) selectedCourse.getCourseNumber()).toString() + "\t" + selectedCourse.getCourseTitle());

            if(!desiredCoursesArrayList.contains(selectedCourse))
                desiredCoursesArrayList.add(selectedCourse);
            else
                Toast.makeText(getApplicationContext(), "Class already selected", Toast.LENGTH_LONG).show();

            desiredCoursesArrayAdapter.notifyDataSetChanged();

            courseDepartment.setText("");
            courseNumber.setText("");

            courseDepartment.requestFocus();
        }
        else {
            Toast.makeText(getApplicationContext(), "Class not found", Toast.LENGTH_LONG).show();

            courseDepartment.setText("");
            courseNumber.setText("");

            courseDepartment.requestFocus();
        }

    }

    public SemesterInfo.DepartmentInfo.CourseInfo getCourseInfo(String department, String number){

        SemesterInfo.DepartmentInfo departmentInfo = getDepartmentInfo(department);
                for (SemesterInfo.DepartmentInfo.CourseInfo courseInfo : departmentInfo.getCourses()){
                    if (((Integer) courseInfo.getCourseNumber()).toString().equals(number)){
                        return courseInfo;
                    }
                }
        return null;
    }

    public SemesterInfo.DepartmentInfo getDepartmentInfo(String department){

        for(SemesterInfo.DepartmentInfo departmentInfo : selectedSemester.getDepartmentArrayList()){
            if(departmentInfo.getDepartmentAcronym().toUpperCase().equals(department.toUpperCase())){
                return departmentInfo;
            }
        }
        return null;
    }

    public void getCourseSections(View view){
        StringBuilder urlBuilder = new StringBuilder(URL_GET_DESIRED_COURSE_SECTIONS);
        urlBuilder.append(((Integer) selectedSemester.getSemesterNumber()).toString() + "*");

        for (SemesterInfo.DepartmentInfo.CourseInfo courseInfo : desiredCoursesArrayList){
            urlBuilder.append(courseInfo.getDepartmentInfo().getDepartmentAcronym());
            urlBuilder.append("-" + ((Integer) courseInfo.getCourseNumber()).toString() + ",");
        }

        String urlFinal = urlBuilder.length() > 0 ? urlBuilder.substring( 0, urlBuilder.length() - 1 ): "";

        Intent intent = new Intent(this, HTTPGetService.class);
        if(spoofServerSwitch) {
            intent.putExtra(HTTPGetService.URL_REQUEST, HTTPGetService.SPOOF_SERVER);
            intent.putExtra(HTTPGetService.SPOOFED_RESPONSE, SPOOF_DESIRED_COURSE_SECTIONS);
        }
        else
            intent.putExtra(HTTPGetService.URL_REQUEST, urlFinal);

        intent.putExtra(HTTPGetService.SOURCE_INTENT, ACTION_GET_DESIRED_COURSE_SECTIONS);
        startService(intent);
        showProgressDialog("Getting All Selected Course Data");
    }

    public void getSemesterInfo(View view){
        String url = null;

        Intent intent = new Intent(this, HTTPGetService.class);
        if(spoofServerSwitch) {
            intent.putExtra(HTTPGetService.URL_REQUEST, HTTPGetService.SPOOF_SERVER);
            intent.putExtra(HTTPGetService.SPOOFED_RESPONSE, SPOOF_SEMESTER);
        }
        else
            intent.putExtra(HTTPGetService.URL_REQUEST, URL_GET_SEMESTER);

        intent.putExtra(HTTPGetService.SOURCE_INTENT, ACTION_GET_SEMESTER);
        startService(intent);
        showProgressDialog("Getting Semester Data");
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
        String blockoutTimes;
        if (data.hasExtra("BLOCKOUT"))
            blockoutTimes = data.getStringExtra("BLOCKOUT");
        else
            return;
        try {
            JSONObject jsonBlockoutTimes = new JSONObject(blockoutTimes);
            this.blockoutTimes = new Course(jsonBlockoutTimes);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (this.blockoutTimes != null)
        Log.d("test", this.blockoutTimes.toJSON().toString());
    }

    private void updateDepartmentInfoAdapter(ArrayList<SemesterInfo.DepartmentInfo> departmentInfo){
        departmentInfoArrayAdapter = new DepartmentInfoArrayAdapter(this,R.layout.desired_courses_listview, departmentInfo);
        courseDepartment.setAdapter(departmentInfoArrayAdapter);
        Toast.makeText(getBaseContext(), "Semester Data Updated", Toast.LENGTH_LONG).show();
    }

    private void updateCourseInfoAdapter(ArrayList<SemesterInfo.DepartmentInfo.CourseInfo> courseInfo){
        courseInfoArrayAdapter = new CourseInfoArrayAdapter(this,R.layout.desired_courses_listview, courseInfo);
        courseNumber.setAdapter(courseInfoArrayAdapter);
        Toast.makeText(getBaseContext(), "Department Data Updated", Toast.LENGTH_LONG).show();
    }

    private void showProgressDialog(String title){
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(title);
        progressDialog.setMessage("Please wait while data is fetched...");
        progressDialog.show();
    }

    private class DepartmentCoursesReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            JSONObject response = null;
            boolean success = false;
            ArrayList<SemesterInfo> semesterInfo;
            try {
                response = new JSONObject(intent.getStringExtra(HTTPGetService.SERVER_RESPONSE));
                success = response.getBoolean("Success");
                semesterInfo = SemesterInfo.SemesterInfoFactory(response);
                for (SemesterInfo semester : semesterInfo){
                    Log.i("Semster number", ((Integer) semester.getSemesterNumber()).toString());
                    if (semester.getSemesterNumber() == 2152){
                        Log.i("Semester Fetch", ((Integer) semester.getSemesterNumber()).toString());
                        selectedSemester = semester;
                    }
                }
                ArrayList<String> departmentList = new ArrayList<>(selectedSemester.getDepartmentArrayList().size());
                for(SemesterInfo.DepartmentInfo departmentInfo : selectedSemester.getDepartmentArrayList()){
                    departmentList.add(departmentInfo.getDepartmentAcronym());
                }
                updateDepartmentInfoAdapter(selectedSemester.getDepartmentArrayList());

                if(success){
                    // enable text field
                }
                else {
                    // Try again?
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            progressDialog.dismiss();
        }
    }


    private class DesiredSectionsReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<Section> sectionArrayList = null;
            String response = intent.getStringExtra(HTTPGetService.SERVER_RESPONSE);
            Log.d("Received: ",response);
            int numberOfSectionsTotal = 0;

            try {
                JSONObject rawResult = new JSONObject(response);
                JSONArray jsonCourses = rawResult.getJSONArray("Results");
                float timeTaken = Float.parseFloat(rawResult.getString("TimeTaken"));
                Log.d("New Request Time Taken:", Float.toString(timeTaken));
                fetchedCourses = Course.buildCourseList(jsonCourses);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                ArrayList<Section> blockoutSections;
                if (blockoutTimes != null)
                    blockoutSections = blockoutTimes.getSectionList();
                else
                    blockoutSections = new ArrayList<Section>();


                if (fetchedCourses != null)
                    sectionArrayList = Schedule.scheduleGenerator(0, fetchedCourses, new ArrayList<Section>(), blockoutSections);
                for (Section section : sectionArrayList){
                    Log.i("Built Schedule",section.getSourceCourse().getCourseName() + " " + section.getSourceCourse().getCourseID() + "-" + section.getSectionNumber() + "\t" + section.toJSON().toString());
                }
            } catch (NoSchedulesPossibleException e) {
                e.printStackTrace();
                e.getConflict();
            }


            Log.d("New Section", "ArrayList Built");

            progressDialog.dismiss();

        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        String selectedSemesterString = null;
        SharedPreferences.Editor editor = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE).edit();
        if (selectedSemester != null){
            try {
                selectedSemesterString = selectedSemester.toJSON().toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            editor.putString("selectedSemester", selectedSemesterString);
            editor.apply();
            Log.i("Selected Semester Info", selectedSemesterString );
        }

        if (desiredCoursesArrayList != null)
        if (desiredCoursesArrayList.size() > 0){
            StringBuilder desiredCoursesString = new StringBuilder();
            for (SemesterInfo.DepartmentInfo.CourseInfo courseInfo : desiredCoursesArrayList){
                desiredCoursesString.append(courseInfo.getDepartmentInfo().getDepartmentAcronym() + "-" + courseInfo.getCourseNumber() + ",");
            }
            Log.i("Desired Courses Builder", desiredCoursesString.length() > 0 ? desiredCoursesString.substring( 0, desiredCoursesString.length() - 1 ): null);
            editor.putString("desiredCourses", desiredCoursesString.length() > 0 ? desiredCoursesString.substring( 0, desiredCoursesString.length() - 1 ): null);
            editor.apply();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences preferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String selectedSemesterString = preferences.getString("selectedSemester", null);
        JSONObject selectedSemesterJSON = null;
        if (selectedSemesterString != null) {
            try {
                selectedSemesterJSON = new JSONObject(selectedSemesterString);
                selectedSemester = new SemesterInfo(selectedSemesterJSON);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ArrayList<String> departmentList = new ArrayList<>(selectedSemester.getDepartmentArrayList().size());
            for(SemesterInfo.DepartmentInfo departmentInfo : selectedSemester.getDepartmentArrayList()){
                departmentList.add(departmentInfo.getDepartmentAcronym());
            }
            updateDepartmentInfoAdapter(selectedSemester.getDepartmentArrayList());

            String desiredCoursesString = preferences.getString("desiredCourses", null);
            if (desiredCoursesString != null){
                String[] desiredCoursesArray = desiredCoursesString.split(",");
                for (String string : desiredCoursesArray){
                    String[] courseInfoStrings = string.split("-");
                    SemesterInfo.DepartmentInfo.CourseInfo courseInfo = getCourseInfo(courseInfoStrings[0], courseInfoStrings[1]);
                    desiredCoursesArrayList.add(courseInfo);
                    Log.i("Desired Course", string);
                    try {
                        Log.i("Desired Course", courseInfo.toJSON().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                departmentInfoArrayAdapter.notifyDataSetChanged();
            }
            else
                Log.i("Desired Course", "No Desired Courses Found");
        }
        else
            getSemesterInfo(this.getCurrentFocus());


    }
}
