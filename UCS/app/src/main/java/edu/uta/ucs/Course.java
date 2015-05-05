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
 * Contains 4 objects
 *
 * String Course Department, a 3 or more character string which represents the department
 * String Course ID, 4 digit number which represents the course identifier
 * String Course Name, University designation for a given course
 */
public class Course {
    //private String courseDepartment;
    private String courseNumber;
    private String courseName;
    private String courseDepartment;
    private ArrayList<Section> sectionList;

    Course() {
        this.setCourseNumber(null);
        this.setCourseName(null);
        this.setSectionList(null);
    }

    Course(String courseNumber, String name, ArrayList<Section> sectionList) {
        this(courseNumber, name);
        this.setSectionList(sectionList);
        for (Section section : sectionList){
            section.setSourceCourse(this);
        }
    }

    Course(String courseNumber, String name) {
        this.setCourseNumber(courseNumber);
        this.setCourseName(name);
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

    public static ArrayList<Course> buildCourseList(JSONArray jsonCourses) throws JSONException {

        ArrayList<Course> courseList = new ArrayList<Course>(jsonCourses.length());

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
