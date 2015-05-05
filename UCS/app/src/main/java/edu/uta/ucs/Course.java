package edu.uta.ucs;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by arunk_000 on 4/5/2015.
 */
public class Course {
    //private String courseDepartment;
    private String courseID;
    private String courseName;
    private ArrayList<Section> sectionList;

    Course() {
        this.setCourseID(null);
        this.setCourseName(null);
        this.setSectionList(null);
    }

    Course(String courseID, String name, ArrayList<Section> sectionList) {
        this(courseID, name);
        this.setSectionList(sectionList);
        for (Section section : sectionList){
            section.setSourceCourse(this);
        }
    }

    Course(String courseID, String name) {
        this.setCourseID(courseID);
        this.setCourseName(name);
    }

    Course(JSONObject jsonObject) throws JSONException {
        this.setCourseName(jsonObject.getString("CourseName"));
        Log.i("New Course Name", getCourseName());
        this.setCourseID(jsonObject.getString("CourseId"));
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
            course.put("CourseId", getCourseID());
            course.put("CourseName", getCourseName());
            ArrayList<JSONObject> courseResults = new ArrayList<>();
            for(Section section : sectionList){
                courseResults.add(section.toJSON());
            }
            JSONArray sectionJSON = new JSONArray(courseResults);
            course.put("CourseResults",sectionJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return course;
    }


    public JSONObject toJSON(Section section) {
        JSONObject course = new JSONObject();
        try {
            course.put("CourseId", getCourseID());
            course.put("CourseName", getCourseName());
            ArrayList<JSONObject> courseResults = new ArrayList<>();
            courseResults.add(section.toJSON());
            JSONArray sectionJSON = new JSONArray(courseResults);
            course.put("CourseResults",sectionJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }

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
                e.printStackTrace();
                String courseString = jsonCourses.getString(index - 1);
                courseJSON = new JSONObject(courseString);
            }

            Log.i("New Course JSON test: ", courseJSON.toString() );
            courseList.add( new Course(courseJSON));
        }
        Collections.reverse(courseList);

        return courseList;
    }

    /*
    public String getCourseDepartment() {
        return courseDepartment;
    }

    public void setCourseDepartment(String courseDepartment) {
        this.courseDepartment = courseDepartment;
    }
    */

    public String getCourseID() {
        return courseID;
    }

    public void setCourseID(String courseID) {
        this.courseID = courseID;
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
}
