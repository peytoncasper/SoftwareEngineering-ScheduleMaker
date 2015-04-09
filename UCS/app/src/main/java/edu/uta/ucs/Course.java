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
    private String courseDepartment;
    private String courseID;
    private String courseName;
    private ArrayList<Section> sectionList;

    Course() {
        this.setCourseDepartment(null);
        this.setCourseID(null);
        this.setCourseName(null);
        this.setSectionList(null);
    }

    Course(String courseDepartment, String courseID, String name, ArrayList<Section> sectionList) {
        this(courseDepartment, courseID, name);
        this.setSectionList(sectionList);
    }

    Course(String courseDepartment, String courseID, String name) {
        this.setCourseDepartment(courseDepartment);
        this.setCourseID(courseID);
        this.setCourseName(name);
    }

    Course(JSONObject jsonObject) throws JSONException {
        this.setCourseName(jsonObject.getString("CourseName"));
        Log.i("New Course Name", getCourseName());
        String[] courseInfo = jsonObject.getString("CourseId").split("-");
        this.setCourseDepartment(courseInfo[0]);
        this.setCourseID(courseInfo[1]);
        Log.i("New Course ID", getCourseDepartment() + " " + getCourseID());
        JSONArray jsonSectionList = jsonObject.getJSONArray("CourseResults");
        sectionList = new ArrayList<Section>(jsonSectionList.length());

        for(int index = jsonSectionList.length(); index != 0;index--){
            Log.i("New Course Section: ",jsonSectionList.getJSONObject( index-1 ).toString());
            this.sectionList.add(new Section(jsonSectionList.getJSONObject(index - 1), this));
            Log.i("New Course Section: ", "Section Added");
        }
        Collections.reverse(sectionList);
    }

    public boolean addSection(Section sectionToAdd) {
        if (!sectionList.contains(sectionToAdd)) {
            sectionList.add(sectionToAdd);
            return false;
        } else return true;
    }

    public String getCourseDepartment() {
        return courseDepartment;
    }

    public void setCourseDepartment(String courseDepartment) {
        this.courseDepartment = courseDepartment;
    }

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
