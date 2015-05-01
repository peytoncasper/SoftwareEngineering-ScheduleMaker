package edu.uta.ucs;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by arunk_000 on 4/5/2015.
 */
public class Schedule {
    private String name;
    private ArrayList<Section> selectedSections;

    Schedule() {
        this.setName(null);
        this.setSelectedSections(null);
    }

    Schedule(String name) {
        this.setName(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Section> getSelectedSections() {
        return selectedSections;
    }

    public void setSelectedSections(ArrayList<Section> selectedSections) {
        this.selectedSections = selectedSections;
    }

    public static ArrayList<Section> scheduleGenerator(int index, ArrayList<Course> courseArrayList, ArrayList<Section> sectionArrayList, ArrayList<Section> blockOutTimesList) throws NoSchedulesPossibleException{

        Log.i("schedule Factory", "Loop Counter:" + ((Integer) index).toString());
        if (index == courseArrayList.size()){
            return sectionArrayList;
        }
        Course course = courseArrayList.get(index);
        ArrayList<Section> possibleSections = course.getSectionList();
        // Shuffle sectionArrayList
        checkPossibleSections:
        for (Section section : possibleSections){
            for (Section sectionToCompare : sectionArrayList){
                if (section.conflictsWith(sectionToCompare)){
                    StringBuilder errorBuilder = new StringBuilder("Conflict between " + section.getSourceCourse().getCourseName() + " " + section.getSourceCourse().getCourseID() + "-" + section.getSectionNumber());
                    errorBuilder.append(" and " + sectionToCompare.getSourceCourse().getCourseName() + " " + sectionToCompare.getSourceCourse().getCourseID() + "-" + sectionToCompare.getSectionNumber());
                    Log.e("Schedule Conflict Error", errorBuilder.toString());
                    continue checkPossibleSections;
                }
            }
            for (Section sectionToCompare : blockOutTimesList){
                if (section.conflictsWith(sectionToCompare)){
                    StringBuilder errorBuilder = new StringBuilder("Conflict between " + section.getSourceCourse().getCourseName() + " " + section.getSourceCourse().getCourseID() + "-" + section.getSectionNumber());
                    errorBuilder.append(" and " + sectionToCompare.getSourceCourse().getCourseID() + " "  + sectionToCompare.getInstructors());
                    Log.e("Schedule Conflict Error", errorBuilder.toString());
                    continue checkPossibleSections;
                }
            }
            sectionArrayList.add(section);
            try{
                return scheduleGenerator(index + 1, courseArrayList, sectionArrayList, blockOutTimesList);
            } catch (NoSchedulesPossibleException exception){
                exception.printStackTrace();
                sectionArrayList.remove(index);
            }

        }throw new NoSchedulesPossibleException(course, sectionArrayList);

    }
}

class NoSchedulesPossibleException extends Exception {

    String message;

    public NoSchedulesPossibleException() {}

    public NoSchedulesPossibleException(String message) {
        super(message);
    }

    public NoSchedulesPossibleException(Course course, ArrayList<Section> sectionArrayList){
        super();
        StringBuilder message = new StringBuilder("Could not build a schedule from this combination of courses:\n" + course.getCourseName() + "\n");
        for (Section section : sectionArrayList){
            if (section.getSourceCourse() != null)
                message.append("\t" + section.getSourceCourse().getCourseName() + " - " + section.getSourceCourse().getCourseID());
            else
                message.append("\nError - Unrecognized Course");
        }
        this.message = message.toString();
    }

    public NoSchedulesPossibleException(Course course, Section section){
        super();
        StringBuilder message = new StringBuilder("Could not build a schedule from this combination of courses:\n" + course.getCourseName() + "\n");

        message.append("\t" + section.getSourceCourse().getCourseName() + " - " + section.getSourceCourse().getCourseID());

        message.append("\nError - Unrecognized Course");

        this.message = message.toString();
    }

    public String getConflict(){
        Log.e("Cannot Generate", message);
        return message;
    }

}
