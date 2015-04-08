package edu.uta.ucs;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

enum ClassStatus {
    OPEN("OPEN"), CLOSED("CLOSED"), WAIT_LIST("WAIT_LIST");

    private String value;

    ClassStatus(final String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}

enum Day {
    M("M"), TU("TU"), W("W"), TH("TH"), F("F"), SA("SA");

    private String value;

    Day(final String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}

class TimeShort {
    private byte hour;
    private byte minute;

    public TimeShort() {
        this.hour = 0;
        this.minute = 0;
    }

    public TimeShort(byte hour, byte minute) {
        this.hour = hour;
        this.minute = minute;
    }

    public TimeShort(int hour, int minute) {
        this.hour = (byte) hour;
        this.minute = (byte) minute;
    }

    public TimeShort(String timeAsString) {
        String[] times = timeAsString.split(":");
        this.hour = Byte.parseByte(times[0]);
        this.minute = Byte.parseByte(times[1].substring(0, 2));
        if (times[1].substring(2).equalsIgnoreCase("PM")) {
            this.hour += 12;
        }

    }

    public boolean after(TimeShort other) {
        return this.getMinAfterMidnight() > other.getMinAfterMidnight();
    }

    public boolean before(TimeShort other) {
        return this.getMinAfterMidnight() < other.getMinAfterMidnight();
    }

    public boolean equals(TimeShort other) {
        return this.getMinAfterMidnight() == other.getMinAfterMidnight();
    }

    public String toString24h() {
        String result = String.format("%d", this.hour) + ":" + String.format("%02d", this.minute);
        return result;
    }

    public String toString12h() {
        String result = String.format("%d", this.hour % 12) + ":" + String.format("%02d", this.minute) + (this.hour > 12 ? "PM" : "AM");
        return result;
    }

    public int getMinAfterMidnight() {
        return ((this.hour * 60) + this.minute);
    }
}

/**
 * Created by arunk_000 on 4/5/2015.
 */
public class Section {
    private int sectionID;                                                                          // Class Number in UTA system
    private int sectionNumber;                                                                      // Section number as part of class
    private String instructors;
    private String room;
    private TimeShort startTime;
    private TimeShort endTime;
    private ArrayList<Day> days;
    private ClassStatus status;
    private Course sourceCourse;

    /**
     * Constructor
     *
     * Generates a new Section will all fields set to null
     * @returns Section
     */
    Section() {
        this.setSectionID(0);
        this.setInstructors(null);
        this.setRoom(null);
        this.setStartTime(null);
        this.setEndTime(null);
        this.setDays(new ArrayList<Day>(1));
        this.setStatus(null);
        this.setSourceCourse(null);
    }

    /**
     * Constructor which takes custom schedule information
     * @param number UTA Section ID number
     * @param instructors list of instructors for section
     * @param room room the section will meet in
     * @param startTime time the lecture period will begin
     * @param endTime time the lecture period will end
     * @param days arrayList of days class will be meeting
     * @param status current class status (OPEN, CLOSED, WAIT_LIST)
     * @param sourceCourse the course the section is of
     */
    Section(int number, String instructors, String room, TimeShort startTime, TimeShort endTime, ArrayList<Day> days, ClassStatus status, Course sourceCourse) {
        this.setSectionID(number);
        this.setInstructors(instructors);
        this.setRoom(room);
        this.setStartTime(startTime);
        this.setEndTime(endTime);
        this.setDays(days);
        this.setStatus(status);
        this.setSourceCourse(sourceCourse);
    }

    /**
     * Constructor to build Section from JSON
     * @param jsonObject JSON Object must have the following keys present:
     *                   <ul>
     *                   <li>"CourseNumber" - integer, unique to all classes, UTA Class ID Number
     *                   <li>"Section" - integer, unique within course, UTA Section Number
     *                   <li>"Room" - String, room the section will meet in
     *                   <li>"Instructor" - String, list of instructors for section
     *                   <li>"MeetingTime" - String, time the lecture period will begin to time the lecture period will end
     *                   <li>"MeetingDays" - String, JSON Array of days class will be meeting
     *                   <li>"Status" - String, current class status (OPEN, CLOSED, WAIT_LIST)
     *                   <ul/>
     * @param sourceCourse
     * @throws JSONException
     */
    Section(JSONObject jsonObject, Course sourceCourse) throws JSONException {

        this.setSectionID(Integer.parseInt(jsonObject.getString("CourseNumber")));
        Log.d("New Section ID", ((Integer)getSectionID()).toString());

        this.setSectionNumber(Integer.parseInt(jsonObject.getString("Section")));
        Log.d("New Section Number", ((Integer)getSectionNumber()).toString());

        this.setRoom(jsonObject.getString("Room"));
        Log.d("New Section Room", getRoom());

        this.setInstructors(jsonObject.getString("Instructor"));
        Log.d("New Section Instructor", getInstructors());

        String times[] = jsonObject.getString("MeetingTime").split("-");
        Log.d("New Start Time", times[0]);
        if (!times[0].equalsIgnoreCase("TBA")) {
            this.setStartTime(new TimeShort(times[0]));
            this.setEndTime(new TimeShort(times[1]));
            Log.d("New Section MeetingTime", getStartTime().toString24h() + "-" + getEndTime().toString24h());
        }
        else{
            this.setStartTime(new TimeShort(0,0));
            this.setEndTime(new TimeShort(0,0));
        }

        JSONArray jsonDaysArray = jsonObject.getJSONArray("MeetingDays");
        Log.d("New Section Days List:", jsonDaysArray.toString());

        days = new ArrayList<Day>(jsonDaysArray.length());

        for(int index = jsonDaysArray.length(); index != 0;index--){
            Day temp = Day.valueOf(jsonDaysArray.getString(index -1));
            days.add(temp);
            Log.d("New Section Day: ", ((Day)days.get(days.size()-1)).toString());
            Day temp2 = (Day)days.get(0);
        }
        Collections.reverse(days);
        Log.d("New Section #of Days: ", ((Integer) days.size()).toString());

        setStatus(ClassStatus.valueOf(jsonObject.getString("Status").toUpperCase()));
        Log.d("New Section Status: ", getStatus().toString());

        this.setSourceCourse(sourceCourse);
    }

    public String getInstructors() {
        return instructors;
    }

    public void setInstructors(String instructors) {
        this.instructors = instructors;
    }

    public TimeShort getStartTime() {
        return startTime;
    }

    public void setStartTime(TimeShort startTime) {
        this.startTime = startTime;
    }

    public TimeShort getEndTime() {
        return endTime;
    }

    public void setEndTime(TimeShort endTime) {
        this.endTime = endTime;
    }

    public boolean hasTimes(){
        return (startTime.getMinAfterMidnight() - endTime.getMinAfterMidnight()) != 0;
    }

    public String getTimeString(){
        if(this.hasTimes()){
            return startTime.toString24h() + "-" + endTime.toString24h();
        }
        return "UNKNOWN/TBA";
    }

    public ArrayList<Day> getDays() {
        return days;
    }

    public void setDays(ArrayList<Day> days) {
        this.days = days;
    }

    public ClassStatus getStatus() {
        return status;
    }

    public void setStatus(ClassStatus status) {
        this.status = status;
    }

    public int getSectionID() {
        return sectionID;
    }

    public void setSectionID(int sectionID) {
        this.sectionID = sectionID;
    }

    public int getSectionNumber() {
        return sectionNumber;
    }

    public void setSectionNumber(int sectionNumber) {
        this.sectionNumber = sectionNumber;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public Course getSourceCourse() {
        return sourceCourse;
    }

    public void setSourceCourse(Course sourceCourse) {
        this.sourceCourse = sourceCourse;
    }

    /**
     * Compares this section against another section for conflicts.
     *
     * @param section  The section to compare this section against.
     * @return boolean
     *          <ul>
     *          true - conflict detected
     *          <p>false - no conflict detected
     *          <ul/>
     */
    public boolean conflictsWith(Section section) {
        if (!Collections.disjoint(this.getDays(), section.getDays())) {     // If there is overlap between the two sets of days conflict is possible, run checks

            if(this.getEndTime().equals(section.getEndTime()))                  // If this section's end time equals the other section's end time
                return true;

            if (this.getStartTime().before(section.getStartTime()))             // If this section starts before the other section
                return section.getStartTime().before(this.getEndTime());            //  check to see if this section ends before other section begins

            if (section.getStartTime().before(this.getStartTime()))             // If this section starts after the other section
                return this.getStartTime().before(section.getEndTime());            //  check to see if other section ends before this section begins

            return true;                                                        // In this section starts neither before nor after the other section it starts at the same time, or there's some other issue

        } else return false;                                                // Days are disjoint, no conflict possible
    }

    /**
     * Compares this section against all the sections in a course's arraylist of sections.
     *
     * @param course  The course to compare this section against.
     * @return boolean
     *          <ul>
     *          true - conflict detected
     *          <p>false - no conflict detected
     *          <ul/>
     */
    public boolean conflictsWith(Course course){
        for(Section section : course.getSectionList()){
            if (this.conflictsWith(section))
                return true;
        }
        return false;
    }

    /**
     * Compares this section against all the sections in a provided arraylist of sections.
     * Gets each course in the arraylist and runs a conflictsWith against it.
     *
     * @param sectionArrayList  The arraylist of courses to compare this section against.
     * @return boolean
     *          <ul>
     *          true - conflict detected
     *          <p>false - no conflict detected
     *          <ul/>
     */
    public boolean conflictsWith(ArrayList<Section> sectionArrayList) {
        for(Section section : sectionArrayList){
            if(this.conflictsWith(section))
                return true;
        }
        return false;
    }

}
