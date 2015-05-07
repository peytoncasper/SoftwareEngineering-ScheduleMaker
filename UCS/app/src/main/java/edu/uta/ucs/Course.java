package edu.uta.ucs;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by arunk_000 on 4/5/2015.
 *
 * <p>Contains 4 objects</p>
 *
 * <li>String Course Department, a 3 or more character string which represents the department. <br>EX: ENGL in "ENGL 1301"</li>
 * <li>String Course Number, 4 digit number which represents the course identifier <br>EX: 1301 in "ENGL 1301"</li>
 * <li>String Course Name, University designation for a given course <br>EX: "RHETORIC AND COMPOSITION I" for "ENGL 1301"</li>
 * <li>Arraylist of {@link Section} which are of this type of course</li>
 */
public class Course {
    private String courseNumber;
    private String courseName;
    private String courseDepartment;
    private ArrayList<Section> sectionList;

    /**
     *
     * @param courseNumber String course number EX: 1301 in "ENGL 1301"
     * @param department String course department EX: ENGL in "ENGL 1301"
     * @param name  String course name. EX: "RHETORIC AND COMPOSITION I" for "ENGL 1301"
     * @param sectionList Arraylist of {@link Section} which are of this type of course
     */
    Course(String courseNumber, String department, String name, ArrayList<Section> sectionList) {
        this.setCourseNumber(courseNumber);
        this.setCourseName(name);
        this.setCourseDepartment(department);
        this.setSectionList(sectionList);
        for (Section section : sectionList){
            section.setSourceCourse(this);
        }
    }

    Course(String courseNumber, String department, String name) {
        this.setCourseNumber(courseNumber);
        this.setCourseName(name);
        this.setCourseDepartment(department);
        this.setSectionList(new ArrayList<Section>());
    }

    Course(JSONObject jsonObject) throws JSONException {
        this.setCourseName(jsonObject.getString("CourseName"));
        Log.i("New Course Name", getCourseName());
        this.setCourseNumber(jsonObject.getString("CourseId"));
        Log.i("New Course Department", jsonObject.getString("Department"));
        this.setCourseDepartment(jsonObject.getString("Department"));
        JSONArray jsonSectionList = jsonObject.getJSONArray("CourseResults");
        sectionList = new ArrayList<Section>(jsonSectionList.length());

        for(int index = jsonSectionList.length(); index != 0;index--){
            Log.i("New Course Section: ",jsonSectionList.getJSONObject( index-1 ).toString());
            this.sectionList.add(new Section(jsonSectionList.getJSONObject(index - 1), this));
            Log.i("New Course Section: ", "Section Added");
        }
        Collections.reverse(sectionList);
    }

    public JSONObject toJSON() {
        JSONObject course = new JSONObject();
        try {
            course.put("CourseId", getCourseNumber());
            course.put("CourseName", getCourseName());
            course.put("Department", getCourseDepartment());
            ArrayList<JSONObject> courseResults = new ArrayList<>();
            for(Section section : sectionList){
                courseResults.add(section.toJSON());
            }
            JSONArray sectionJSON = new JSONArray(courseResults);
            course.put("CourseResults",sectionJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.i("New Course JSON", "Course JSON built: " + course.toString());

        return course;
    }

    public JSONObject toJSON(Section section) {
        JSONObject course = new JSONObject();
        try {
            course.put("CourseId", getCourseNumber());
            course.put("CourseName", getCourseName());
            course.put("Department", getCourseDepartment());
            ArrayList<JSONObject> courseResults = new ArrayList<>();
            courseResults.add(section.toJSON());
            JSONArray sectionJSON = new JSONArray(courseResults);
            course.put("CourseResults",sectionJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.i("New Course JSON", "Course JSON built: " + course.toString());

        return course;
    }

    public boolean addSection(Section sectionToAdd) {
        if (!sectionList.contains(sectionToAdd)) {
            sectionList.add(sectionToAdd);
            return false;
        } else return true;
    }

    /**
     * Builds an ArrayList of Courses based on a JSONArray
     *
     * @param jsonCourses JSONArray of courses
     * @return ArrayList<Course>
     * @throws JSONException
     */
    public static ArrayList<Course> buildCourseList(JSONArray jsonCourses) throws JSONException {

        ArrayList<Course> courseList = new ArrayList<>(jsonCourses.length());

        for(int index = jsonCourses.length(); index != 0;index--){
            JSONObject courseJSON;
            try {
                courseJSON = jsonCourses.getJSONObject(index - 1);
            }
            catch (JSONException e){
                Log.i("New Course JSON", "JSON Construction failed. Attempting to construct JSON from String");
                String courseString = jsonCourses.getString(index - 1);
                courseJSON = new JSONObject(courseString);
            }

            Log.i("New Course JSON", "test: " + courseJSON.toString() );
            courseList.add(new Course(courseJSON));
        }
        Collections.reverse(courseList);

        return courseList;
    }

    public String getCourseNumber() {
        return courseNumber;
    }

    public void setCourseNumber(String courseNumber) {
        this.courseNumber = courseNumber;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public ArrayList<Section> getSectionList() {
        return sectionList;
    }

    public void setSectionList(ArrayList<Section> sectionList) {
        this.sectionList = sectionList;
    }

    public String getCourseDepartment() {
        return courseDepartment;
    }

    public void setCourseDepartment(String courseDepartment) {
        this.courseDepartment = courseDepartment;
    }
}
