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
public class Schedule {
    private String name;
    private int semesterNumber;
    private ArrayList<Section> selectedSections;

    Schedule() {
        this.setName(null);
        this.setSelectedSections(null);
    }

    Schedule(String name) {
        this.setName(name);
    }

    Schedule(String name, int semesterNumber, ArrayList<Section> sectionArrayList){
        this.name = name;
        this.semesterNumber = semesterNumber;
        this.selectedSections = sectionArrayList;
    }

    Schedule(JSONObject scheduleJSON) throws JSONException {
        name = scheduleJSON.getString("ScheduleName");
        semesterNumber = scheduleJSON.getInt("ScheduleSemester");
        JSONArray scheduleCoursesJSONArray = scheduleJSON.getJSONArray("ScheduleCourses");
        ArrayList<JSONObject> scheduleCoursesString = new ArrayList<>(scheduleCoursesJSONArray.length());
        selectedSections = new ArrayList<>();
        for (int index = 0; index < scheduleCoursesJSONArray.length(); index++){
            JSONObject courseJSON = new JSONObject(scheduleCoursesJSONArray.getString(index));
            Course course = new Course(courseJSON);
            selectedSections.addAll(course.getSectionList());
        }
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

    public JSONObject toJSON() throws JSONException {
        JSONObject result = new JSONObject();

        result.put("ScheduleName", name);
        result.put("ScheduleSemester", semesterNumber);

        ArrayList<String> selectedSectionsString = new ArrayList<>(selectedSections.size());

        for (Section section : selectedSections){
            selectedSectionsString.add(section.getSourceCourse().toJSON(section).toString());
        }
        JSONArray selectedSectionsJSONArray = new JSONArray(selectedSectionsString);

        result.put("ScheduleCourses",selectedSectionsJSONArray);

        return result;
    }

    public static ArrayList<Section> scheduleGenerator(int index, ArrayList<Course> courseArrayList, ArrayList<Section> sectionArrayList, ArrayList<Section> blockOutTimesList) throws NoSchedulesPossibleException{

        Log.i("schedule Factory", "Loop Counter:" + ((Integer) index).toString());
        if (index == courseArrayList.size()){
            return sectionArrayList;
        }
        Course course = courseArrayList.get(index);
        ArrayList<Section> possibleSections = new ArrayList<>(course.getSectionList().size());
        possibleSections.addAll(course.getSectionList());
        Collections.shuffle(possibleSections);
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
            Log.i("Adding Section to List", section.toJSON().toString());
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
        StringBuilder message = new StringBuilder("Could not build a schedule from this combination of courses\n");
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
