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
     * Holds a departments' ID, Title, and an arraylist of CourseInfo to hold course information for all courses in that department.
     */
    class DepartmentInfo {


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
                this.courses.add(new CourseInfo(courseJSONArrayRaw.getJSONObject(index - 1), this));
            }

            Log.i("Department Details", "New Department Added:"+ getDepartmentID() + " " + getDepartmentAcronym() + " " + getDepartmentTitle() + " " + courses.size());
        }
    }

    private int semesterNumber;
    private ArrayList<DepartmentInfo> departmentArrayList;

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
        JSONArray departmentJSONArrayRaw = semesterInfoRaw.getJSONArray("Departments");
        this.departmentArrayList = new ArrayList<>(departmentJSONArrayRaw.length());

        for(int index = departmentJSONArrayRaw.length(); index != 0;index--){
            this.departmentArrayList.add(new DepartmentInfo(departmentJSONArrayRaw.getJSONObject(index - 1)));
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

public class SelectCourses extends ActionBarActivity {

    public static final String ACTION_DEPARTMENT_SELECT ="edu.uta.ucs.intent.action.ACTION_DEPARTMENT_SELECT";
    public static final String SPOOFED_DEPARTMENT_COURSES ="";
    public static final String URL_GET_SEMESTER = "http://ucs-scheduler.cloudapp.net/UTA/GetDepartmentClassData";
    public static final String ACTION_GET_SEMESTER ="edu.uta.ucs.intent.action.ACTION_DEPARTMENT_SELECT";
    public static final String SPOOF_SEMESTER ="{\"Success\":true,\"Semesters\":[{\"Id\":5,\"SemesterNumber\":\"2155\",\"Departments\":[{\"Id\":531,\"SemesterId\":5,\"DepartmentAcronym\":\"ADVT\",\"CourseNumbers\":[{\"Id\":8975,\"DepartmentId\":531,\"CourseName\":\"INTEGRATED MARKETING COMMUNICATION (IMC) CASE STUDIES\",\"CourseNumber\":\"4300\"}]},{\"Id\":532,\"SemesterId\":5,\"DepartmentAcronym\":\"AE\",\"CourseNumbers\":[{\"Id\":8976,\"DepartmentId\":532,\"CourseName\":\"PREPARATORY COURSE FOR AEROSPACE ENGINEERING\",\"CourseNumber\":\"5300\"},{\"Id\":8977,\"DepartmentId\":532,\"CourseName\":\"FINITE ELEMENT METHODS\",\"CourseNumber\":\"5310\"},{\"Id\":8978,\"DepartmentId\":532,\"CourseName\":\"ANALYTIC METHODS IN ENGINEERING\",\"CourseNumber\":\"5331\"},{\"Id\":8979,\"DepartmentId\":532,\"CourseName\":\"OPTIMAL CONTROL OF DYNAMIC SYS\",\"CourseNumber\":\"5335\"},{\"Id\":8980,\"DepartmentId\":532,\"CourseName\":\"AEROSPACE ENGINEERING INTERNSHIP\",\"CourseNumber\":\"6196\"},{\"Id\":8981,\"DepartmentId\":532,\"CourseName\":\"RESEARCH IN AEROSPACE ENGINEERING\",\"CourseNumber\":\"6697\"},{\"Id\":8982,\"DepartmentId\":532,\"CourseName\":\"DISSERTATION\",\"CourseNumber\":\"6699\"},{\"Id\":8983,\"DepartmentId\":532,\"CourseName\":\"DISSERTATION\",\"CourseNumber\":\"6999\"}]},{\"Id\":533,\"SemesterId\":5,\"DepartmentAcronym\":\"AS\",\"CourseNumbers\":[]},{\"Id\":534,\"SemesterId\":5,\"DepartmentAcronym\":\"ASA\",\"CourseNumbers\":[{\"Id\":8984,\"DepartmentId\":534,\"CourseName\":\"AFFILIATED STUDY ABROAD\",\"CourseNumber\":\"1191\"},{\"Id\":8985,\"DepartmentId\":534,\"CourseName\":\"AFFILIATED STUDY ABROAD\",\"CourseNumber\":\"1291\"},{\"Id\":8986,\"DepartmentId\":534,\"CourseName\":\"AFFILIATED STUDIES ABROAD\",\"CourseNumber\":\"1391\"},{\"Id\":8987,\"DepartmentId\":534,\"CourseName\":\"AFFILIATED STUDIES ABROAD\",\"CourseNumber\":\"1491\"},{\"Id\":8988,\"DepartmentId\":534,\"CourseName\":\"AFFILIATED STUDY ABROAD\",\"CourseNumber\":\"2191\"},{\"Id\":8989,\"DepartmentId\":534,\"CourseName\":\"AFFILIATED STUDIES ABROAD\",\"CourseNumber\":\"2391\"},{\"Id\":8990,\"DepartmentId\":534,\"CourseName\":\"AFFILIATED STUDY ABROAD\",\"CourseNumber\":\"3191\"},{\"Id\":8991,\"DepartmentId\":534,\"CourseName\":\"AFFILIATED STUDIES ABROAD\",\"CourseNumber\":\"3391\"},{\"Id\":8992,\"DepartmentId\":534,\"CourseName\":\"AFFILIATED STUDIES ABROAD\",\"CourseNumber\":\"4391\"}]},{\"Id\":535,\"SemesterId\":5,\"DepartmentAcronym\":\"AAST\",\"CourseNumbers\":[{\"Id\":8993,\"DepartmentId\":535,\"CourseName\":\"INTRODUCTION TO AFRICAN AMERICAN STUDIES\",\"CourseNumber\":\"2300\"},{\"Id\":8994,\"DepartmentId\":535,\"CourseName\":\"CONFERENCE COURSE\",\"CourseNumber\":\"4391\"}]},{\"Id\":536,\"SemesterId\":5,\"DepartmentAcronym\":\"ANTH\",\"CourseNumbers\":[{\"Id\":8995,\"DepartmentId\":536,\"CourseName\":\"GLOBAL CULTURES\",\"CourseNumber\":\"2322\"},{\"Id\":8996,\"DepartmentId\":536,\"CourseName\":\"ARCHAEOLOGICAL CULTURES\",\"CourseNumber\":\"2358\"}]},{\"Id\":537,\"SemesterId\":5,\"DepartmentAcronym\":\"ARAB\",\"CourseNumbers\":[]},{\"Id\":538,\"SemesterId\":5,\"DepartmentAcronym\":\"ARCH\",\"CourseNumbers\":[{\"Id\":8997,\"DepartmentId\":538,\"CourseName\":\"CONFERENCE COURSE\",\"CourseNumber\":\"1191\"},{\"Id\":8998,\"DepartmentId\":538,\"CourseName\":\"MASTERWORKS OF WESTERN ARCHITECTURE\",\"CourseNumber\":\"2300\"},{\"Id\":8999,\"DepartmentId\":538,\"CourseName\":\"THE CITY OF ROME\",\"CourseNumber\":\"4305\"},{\"Id\":9000,\"DepartmentId\":538,\"CourseName\":\"URBAN DESIGN THEORY\",\"CourseNumber\":\"4306\"},{\"Id\":9001,\"DepartmentId\":538,\"CourseName\":\"TOPICS IN ARCHITECTURAL THEORY\",\"CourseNumber\":\"4311\"},{\"Id\":9002,\"DepartmentId\":538,\"CourseName\":\"NOTATIONAL DRAWING\",\"CourseNumber\":\"4341\"},{\"Id\":9003,\"DepartmentId\":538,\"CourseName\":\"SELECTED TOPICS ARCHITECTURE\",\"CourseNumber\":\"4395\"},{\"Id\":9004,\"DepartmentId\":538,\"CourseName\":\"CITY OF ROME\",\"CourseNumber\":\"5305\"},{\"Id\":9005,\"DepartmentId\":538,\"CourseName\":\"URBAN DESIGN\",\"CourseNumber\":\"5306\"},{\"Id\":9006,\"DepartmentId\":538,\"CourseName\":\"ARCHITECTURAL THEORY\",\"CourseNumber\":\"5311\"},{\"Id\":9007,\"DepartmentId\":538,\"CourseName\":\"PRACTICUM\",\"CourseNumber\":\"5381\"},{\"Id\":9008,\"DepartmentId\":538,\"CourseName\":\"TOPICS IN ARCHITECTURE\",\"CourseNumber\":\"5395\"},{\"Id\":9009,\"DepartmentId\":538,\"CourseName\":\"PRACTICUM\",\"CourseNumber\":\"5681\"},{\"Id\":9010,\"DepartmentId\":538,\"CourseName\":\"CONFERENCE COURSE\",\"CourseNumber\":\"5691\"}]},{\"Id\":539,\"SemesterId\":5,\"DepartmentAcronym\":\"ART\",\"CourseNumbers\":[{\"Id\":9011,\"DepartmentId\":539,\"CourseName\":\"ART APPRECIATION\",\"CourseNumber\":\"1301\"},{\"Id\":9012,\"DepartmentId\":539,\"CourseName\":\"TWO\",\"CourseNumber\":\"1305\"},{\"Id\":9013,\"DepartmentId\":539,\"CourseName\":\"THREE\",\"CourseNumber\":\"1306\"},{\"Id\":9014,\"DepartmentId\":539,\"CourseName\":\"DRAWING FUNDAMENTALS\",\"CourseNumber\":\"1307\"},{\"Id\":9015,\"DepartmentId\":539,\"CourseName\":\"ART OF THE WESTERN WORLD II: BAROQUE TO MODERN\",\"CourseNumber\":\"1310\"},{\"Id\":9016,\"DepartmentId\":539,\"CourseName\":\"DIGITAL DESIGN\",\"CourseNumber\":\"2304\"},{\"Id\":9017,\"DepartmentId\":539,\"CourseName\":\"LIFE DRAWING\",\"CourseNumber\":\"3348\"},{\"Id\":9018,\"DepartmentId\":539,\"CourseName\":\"DIGITAL IMAGING\",\"CourseNumber\":\"3352\"},{\"Id\":9019,\"DepartmentId\":539,\"CourseName\":\"SIGN AND SYMBOL\",\"CourseNumber\":\"3354\"},{\"Id\":9020,\"DepartmentId\":539,\"CourseName\":\"SCRIPT TO SCREEN\",\"CourseNumber\":\"4311\"},{\"Id\":9021,\"DepartmentId\":539,\"CourseName\":\"ADVANCED PRINTMAKING\",\"CourseNumber\":\"4345\"},{\"Id\":9022,\"DepartmentId\":539,\"CourseName\":\"ADVANCED PHOTOGRAPHY\",\"CourseNumber\":\"4359\"},{\"Id\":9023,\"DepartmentId\":539,\"CourseName\":\"INDEPENDENT STUDY\",\"CourseNumber\":\"4391\"},{\"Id\":9024,\"DepartmentId\":539,\"CourseName\":\"SPECIAL STUDIES\",\"CourseNumber\":\"4392\"},{\"Id\":9025,\"DepartmentId\":539,\"CourseName\":\"ART INTERNSHIP\",\"CourseNumber\":\"4395\"},{\"Id\":9026,\"DepartmentId\":539,\"CourseName\":\"SPECIAL STUDIES IN FILM/VIDEO\",\"CourseNumber\":\"4397\"},{\"Id\":9027,\"DepartmentId\":539,\"CourseName\":\"ART INTERNSHIP\",\"CourseNumber\":\"4695\"},{\"Id\":9028,\"DepartmentId\":539,\"CourseName\":\"INDEPENDENT STUDY\",\"CourseNumber\":\"5391\"}]},{\"Id\":540,\"SemesterId\":5,\"DepartmentAcronym\":\"ASTR\",\"CourseNumbers\":[{\"Id\":9029,\"DepartmentId\":540,\"CourseName\":\"INTRODUCTORY ASTRONOMY I\",\"CourseNumber\":\"1345\"},{\"Id\":9030,\"DepartmentId\":540,\"CourseName\":\"INTRODUCTORY ASTRONOMY II\",\"CourseNumber\":\"1346\"}]},{\"Id\":541,\"SemesterId\":5,\"DepartmentAcronym\":\"BEEP\",\"CourseNumbers\":[{\"Id\":9031,\"DepartmentId\":541,\"CourseName\":\"SPANISH FOR TEACHERS IN DUAL LANGUAGE PROGRAMS: AN IMMERSION APPROACH\",\"CourseNumber\":\"4366\"},{\"Id\":9032,\"DepartmentId\":541,\"CourseName\":\"ESL METHODS FOR EC\",\"CourseNumber\":\"5321\"},{\"Id\":9033,\"DepartmentId\":541,\"CourseName\":\"SPANISH FOR SCHOOL ADMINISTRATORS TEACHERS\",\"CourseNumber\":\"5366\"}]},{\"Id\":542,\"SemesterId\":5,\"DepartmentAcronym\":\"BE\",\"CourseNumbers\":[{\"Id\":9034,\"DepartmentId\":542,\"CourseName\":\"LABORATORY PRINCIPLES\",\"CourseNumber\":\"4382\"},{\"Id\":9035,\"DepartmentId\":542,\"CourseName\":\"DIRECTED RESEARCH IN BIOENGINEERING\",\"CourseNumber\":\"4391\"},{\"Id\":9036,\"DepartmentId\":542,\"CourseName\":\"DIRECTED RESEARCH IN BIOENGINEERING\",\"CourseNumber\":\"5191\"},{\"Id\":9037,\"DepartmentId\":542,\"CourseName\":\"DIRECTED RESEARCH IN BIOENGINEERING\",\"CourseNumber\":\"5291\"},{\"Id\":9038,\"DepartmentId\":542,\"CourseName\":\"HUMAN PHYSIOLOGY IN BIOENGINEERING\",\"CourseNumber\":\"5309\"},{\"Id\":9039,\"DepartmentId\":542,\"CourseName\":\"TISSUE ENGINEERING LAB\",\"CourseNumber\":\"5365\"},{\"Id\":9040,\"DepartmentId\":542,\"CourseName\":\"DRUG DELIVERY LAB\",\"CourseNumber\":\"5373\"},{\"Id\":9041,\"DepartmentId\":542,\"CourseName\":\"LABORATORY PRINCIPLES\",\"CourseNumber\":\"5382\"},{\"Id\":9042,\"DepartmentId\":542,\"CourseName\":\"RESEARCH PROJECT\",\"CourseNumber\":\"5390\"},{\"Id\":9043,\"DepartmentId\":542,\"CourseName\":\"DIRECTED RESEARCH IN BIOENGINEERING\",\"CourseNumber\":\"5391\"},{\"Id\":9044,\"DepartmentId\":542,\"CourseName\":\"THESIS\",\"CourseNumber\":\"5398\"},{\"Id\":9045,\"DepartmentId\":542,\"CourseName\":\"THESIS\",\"CourseNumber\":\"5698\"},{\"Id\":9046,\"DepartmentId\":542,\"CourseName\":\"PhD SEMINAR IN BIOENGINEERING\",\"CourseNumber\":\"6103\"},{\"Id\":9047,\"DepartmentId\":542,\"CourseName\":\"DOCTORAL COMPREHENSIVE EXAMINATION\",\"CourseNumber\":\"6195\"},{\"Id\":9048,\"DepartmentId\":542,\"CourseName\":\"RESEARCH IN BIOENGINEERING\",\"CourseNumber\":\"6197\"},{\"Id\":9049,\"DepartmentId\":542,\"CourseName\":\"RESEARCH IN BIOENGINEERING\",\"CourseNumber\":\"6297\"},{\"Id\":9050,\"DepartmentId\":542,\"CourseName\":\"INTERNSHIP IN BIOENGINEERING\",\"CourseNumber\":\"6395\"},{\"Id\":9051,\"DepartmentId\":542,\"CourseName\":\"RESEARCH IN BIOENGINEERING\",\"CourseNumber\":\"6397\"},{\"Id\":9052,\"DepartmentId\":542,\"CourseName\":\"DISSERTATION\",\"CourseNumber\":\"6399\"},{\"Id\":9053,\"DepartmentId\":542,\"CourseName\":\"DISSERTATION\",\"CourseNumber\":\"6499\"},{\"Id\":9054,\"DepartmentId\":542,\"CourseName\":\"INTERNSHIP IN BIOENGINEERING\",\"CourseNumber\":\"6695\"},{\"Id\":9055,\"DepartmentId\":542,\"CourseName\":\"RESEARCH IN BIOENGINEERING\",\"CourseNumber\":\"6697\"},{\"Id\":9056,\"DepartmentId\":542,\"CourseName\":\"DISSERTATION\",\"CourseNumber\":\"6699\"},{\"Id\":9057,\"DepartmentId\":542,\"CourseName\":\"DISSERTATION\",\"CourseNumber\":\"6999\"},{\"Id\":9058,\"DepartmentId\":542,\"CourseName\":\"DOCTORAL DEGREE COMPLETION\",\"CourseNumber\":\"7399\"}]},{\"Id\":543,\"SemesterId\":5,\"DepartmentAcronym\":\"BIOL\",\"CourseNumbers\":[{\"Id\":9059,\"DepartmentId\":543,\"CourseName\":\"INTRODUCTION TO BIOLOGY I\",\"CourseNumber\":\"1333\"},{\"Id\":9060,\"DepartmentId\":543,\"CourseName\":\"INTRODUCTION TO BIOLOGY II\",\"CourseNumber\":\"1334\"},{\"Id\":9061,\"DepartmentId\":543,\"CourseName\":\"CELL AND MOLECULAR BIOLOGY\",\"CourseNumber\":\"1441\"},{\"Id\":9062,\"DepartmentId\":543,\"CourseName\":\"STRUCTURE AND FUNCTION OF ORGANISMS\",\"CourseNumber\":\"1442\"},{\"Id\":9063,\"DepartmentId\":543,\"CourseName\":\"INTRODUCTION TO BIOSTATISTICS\",\"CourseNumber\":\"2300\"},{\"Id\":9064,\"DepartmentId\":543,\"CourseName\":\"EVOLUTION ECOLOGY\",\"CourseNumber\":\"2343\"},{\"Id\":9065,\"DepartmentId\":543,\"CourseName\":\"HUMAN ANATOMY AND PHYSIOLOGY I\",\"CourseNumber\":\"2457\"},{\"Id\":9066,\"DepartmentId\":543,\"CourseName\":\"HUMAN ANATOMY AND PHYSIOLOGY II\",\"CourseNumber\":\"2458\"},{\"Id\":9067,\"DepartmentId\":543,\"CourseName\":\"NURSING MICROBIOLOGY\",\"CourseNumber\":\"2460\"},{\"Id\":9068,\"DepartmentId\":543,\"CourseName\":\"COOPERATIVE PROGRAM IN BIOLOGY\",\"CourseNumber\":\"3149\"},{\"Id\":9069,\"DepartmentId\":543,\"CourseName\":\"COOPERATIVE PROGRAM IN BIOLOGY\",\"CourseNumber\":\"3249\"},{\"Id\":9070,\"DepartmentId\":543,\"CourseName\":\"DRUGS AND BEHAVIOR\",\"CourseNumber\":\"3303\"},{\"Id\":9071,\"DepartmentId\":543,\"CourseName\":\"NON\",\"CourseNumber\":\"3309\"},{\"Id\":9072,\"DepartmentId\":543,\"CourseName\":\"SELECTED TOPICS IN MICROBIOLOGY\",\"CourseNumber\":\"3311\"},{\"Id\":9073,\"DepartmentId\":543,\"CourseName\":\"IMMUNOBIOLOGY\",\"CourseNumber\":\"3312\"},{\"Id\":9074,\"DepartmentId\":543,\"CourseName\":\"GENETICS\",\"CourseNumber\":\"3315\"},{\"Id\":9075,\"DepartmentId\":543,\"CourseName\":\"BRAIN AND BEHAVIOR\",\"CourseNumber\":\"3322\"},{\"Id\":9076,\"DepartmentId\":543,\"CourseName\":\"COOPERATIVE PROGRAM IN BIOLOGY\",\"CourseNumber\":\"3349\"},{\"Id\":9077,\"DepartmentId\":543,\"CourseName\":\"GENERAL MICROBIOLOGY\",\"CourseNumber\":\"3444\"},{\"Id\":9078,\"DepartmentId\":543,\"CourseName\":\"GENERAL ZOOLOGY\",\"CourseNumber\":\"3454\"},{\"Id\":9079,\"DepartmentId\":543,\"CourseName\":\"DIRECTED STUDY\",\"CourseNumber\":\"4179\"},{\"Id\":9080,\"DepartmentId\":543,\"CourseName\":\"RESEARCH IN BIOLOGY\",\"CourseNumber\":\"4189\"},{\"Id\":9081,\"DepartmentId\":543,\"CourseName\":\"DIRECTED STUDY\",\"CourseNumber\":\"4279\"},{\"Id\":9082,\"DepartmentId\":543,\"CourseName\":\"RESEARCH IN BIOLOGY\",\"CourseNumber\":\"4289\"},{\"Id\":9083,\"DepartmentId\":543,\"CourseName\":\"TISSUE ENGINEERING LAB\",\"CourseNumber\":\"4365\"},{\"Id\":9084,\"DepartmentId\":543,\"CourseName\":\"DIRECTED STUDY\",\"CourseNumber\":\"4379\"},{\"Id\":9085,\"DepartmentId\":543,\"CourseName\":\"INSTRUCTIONAL TECHNIQUES IN BIOLOGY\",\"CourseNumber\":\"4388\"},{\"Id\":9086,\"DepartmentId\":543,\"CourseName\":\"HONORS SENIOR PROJECT IN BIOLOGY\",\"CourseNumber\":\"4393\"},{\"Id\":9087,\"DepartmentId\":543,\"CourseName\":\"RESEARCH IN BIOLOGY\",\"CourseNumber\":\"5193\"},{\"Id\":9088,\"DepartmentId\":543,\"CourseName\":\"INDIVIDUAL PROBLEMS IN BIOLOGY\",\"CourseNumber\":\"5291\"},{\"Id\":9089,\"DepartmentId\":543,\"CourseName\":\"RESEARCH\",\"CourseNumber\":\"5293\"},{\"Id\":9090,\"DepartmentId\":543,\"CourseName\":\"INDIVIDUAL PROBLEMS IN BIOLOGY\",\"CourseNumber\":\"5391\"},{\"Id\":9091,\"DepartmentId\":543,\"CourseName\":\"RESEARCH IN BIOLOGY\",\"CourseNumber\":\"5393\"},{\"Id\":9092,\"DepartmentId\":543,\"CourseName\":\"THESIS\",\"CourseNumber\":\"5398\"},{\"Id\":9093,\"DepartmentId\":543,\"CourseName\":\"RESEARCH\",\"CourseNumber\":\"5493\"},{\"Id\":9094,\"DepartmentId\":543,\"CourseName\":\"RESEARCH IN BIOLOGY\",\"CourseNumber\":\"5693\"},{\"Id\":9095,\"DepartmentId\":543,\"CourseName\":\"THESIS\",\"CourseNumber\":\"5698\"},{\"Id\":9096,\"DepartmentId\":543,\"CourseName\":\"ADVANCED RESEARCH\",\"CourseNumber\":\"6191\"},{\"Id\":9097,\"DepartmentId\":543,\"CourseName\":\"ADVANCED RESEARCH\",\"CourseNumber\":\"6391\"},{\"Id\":9098,\"DepartmentId\":543,\"CourseName\":\"DISSERTATION\",\"CourseNumber\":\"6399\"},{\"Id\":9099,\"DepartmentId\":543,\"CourseName\":\"ADVANCED RESEARCH\",\"CourseNumber\":\"6591\"},{\"Id\":9100,\"DepartmentId\":543,\"CourseName\":\"ADVANCED RESEARCH\",\"CourseNumber\":\"6691\"},{\"Id\":9101,\"DepartmentId\":543,\"CourseName\":\"DISSERTATION\",\"CourseNumber\":\"6699\"},{\"Id\":9102,\"DepartmentId\":543,\"CourseName\":\"DISSERTATION\",\"CourseNumber\":\"6999\"},{\"Id\":9103,\"DepartmentId\":543,\"CourseName\":\"DOCTORAL DEGREE COMPLETION\",\"CourseNumber\":\"7399\"}]},{\"Id\":544,\"SemesterId\":5,\"DepartmentAcronym\":\"BCMN\",\"CourseNumbers\":[{\"Id\":9104,\"DepartmentId\":544,\"CourseName\":\"BROADCAST WRITING AND REPORTING\",\"CourseNumber\":\"2347\"},{\"Id\":9105,\"DepartmentId\":544,\"CourseName\":\"RADIO PRODUCTION I\",\"CourseNumber\":\"2357\"},{\"Id\":9106,\"DepartmentId\":544,\"CourseName\":\"TELEVISION PRODUCTION I\",\"CourseNumber\":\"2358\"},{\"Id\":9107,\"DepartmentId\":544,\"CourseName\":\"PROFESSIONAL INTERNSHIP\",\"CourseNumber\":\"4395\"}]},{\"Id\":545,\"SemesterId\":5,\"DepartmentAcronym\":\"BSAD\",\"CourseNumbers\":[{\"Id\":9108,\"DepartmentId\":545,\"CourseName\":\"DISSERTATION\",\"CourseNumber\":\"6399\"},{\"Id\":9109,\"DepartmentId\":545,\"CourseName\":\"DISSERTATION\",\"CourseNumber\":\"6699\"},{\"Id\":9110,\"DepartmentId\":545,\"CourseName\":\"DISSERTATION\",\"CourseNumber\":\"6999\"},{\"Id\":9111,\"DepartmentId\":545,\"CourseName\":\"DOCTORAL DEGREE COMPLETION\",\"CourseNumber\":\"7399\"}]}]}]}";

    private AutoCompleteTextView courseDepartment;
    private AutoCompleteTextView courseNumber;

    private ListView desiredCoursesListView;
    private DesiredCoursesArrayAdapter desiredCoursesArrayAdapter;
    private DepartmentInfoArrayAdapter departmentInfoArrayAdapter;
    private CourseInfoArrayAdapter courseInfoArrayAdapter;

    private Course blockoutTimes = null;
    private ArrayList<DesiredCourse> desiredCoursesArrayList;
    private ArrayList<SemesterInfo.DepartmentInfo> departmentInfoArrayList = new ArrayList<>();
    private ArrayList<SemesterInfo.DepartmentInfo.CourseInfo> courseInfoArrayList = new ArrayList<>();

    private SemesterInfo.DepartmentInfo.CourseInfo tempCourseInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_courses);

        LocalBroadcastManager.getInstance(this).registerReceiver(new DepartmentCoursesReceiver(), new IntentFilter(ACTION_GET_SEMESTER));

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
        String department = courseDepartment.getText().toString().toUpperCase();
        String number = courseNumber.getText().toString();

        if (!department.equals("") && !number.equals("") ) {
            if(tempCourseInfo != null){
                desiredCoursesArrayList.add(new DesiredCourse(department, number, "\t" + tempCourseInfo.getCourseTitle()));
                tempCourseInfo = null;
            }
            else {
                desiredCoursesArrayList.add(new DesiredCourse(department, number, ""));
            }
            desiredCoursesArrayAdapter.notifyDataSetChanged();

            courseDepartment.setText("");
            courseNumber.setText("");

            courseDepartment.requestFocus();
        }

    }

    public void getSemesterCourses(View view){
        String url = null;

        Intent intent = new Intent(this, HTTPGetService.class);
        if(false) {
            intent.putExtra(HTTPGetService.URL_REQUEST, HTTPGetService.SPOOF_SERVER);
            intent.putExtra(HTTPGetService.SPOOFED_RESPONSE, SPOOF_SEMESTER);
        }
        else
            intent.putExtra(HTTPGetService.URL_REQUEST, URL_GET_SEMESTER);

        intent.putExtra(HTTPGetService.SOURCE_INTENT, ACTION_GET_SEMESTER);
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
                ArrayList<String> departmentList = new ArrayList<>(semesterInfo.get(0).getDepartmentArrayList().size());
                for(SemesterInfo.DepartmentInfo departmentInfo : semesterInfo.get(0).getDepartmentArrayList()){
                    departmentList.add(departmentInfo.getDepartmentAcronym());
                }
                updateDepartmentInfoAdapter(semesterInfo.get(0).getDepartmentArrayList());

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
