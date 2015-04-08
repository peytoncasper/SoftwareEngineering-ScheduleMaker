package edu.uta.ucs;

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

    public ArrayList<Section> scheduleFactory(int index, ArrayList<Course> courseArrayList, ArrayList<Section> sectionArrayList) throws NoSchedulesPossibleException{

        Course course = courseArrayList.get(index);
        ArrayList<Section> possibleSections = course.getSectionList();
        // Shuffle sectionArrayList
        for (Section section : possibleSections){
            if (!section.conflictsWith(sectionArrayList)){
                sectionArrayList.add(section);
                try{
                    return scheduleFactory(index+1, courseArrayList, sectionArrayList);
                } catch (NoSchedulesPossibleException exception){
                    exception.printStackTrace();
                    sectionArrayList.remove(index);
                }
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
        StringBuilder message = new StringBuilder("Could not pick a section from " + course.getCourseName() + " which does not conflict with at least one of these courses: ");
        for (Section section : sectionArrayList){
            message.append("/n" + section.getSourceCourse().getCourseName());
        }
        this.message = message.toString();
    }

    public String getConflict(){
        return message;
    }

}
