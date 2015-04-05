package edu.uta.ucs;

import java.util.ArrayList;

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
