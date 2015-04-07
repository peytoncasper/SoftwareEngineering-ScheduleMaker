package edu.uta.ucs;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.Date;
import java.util.Set;

class TimeShort {
    private byte hour;
    private byte minute;

     public TimeShort(){
         this.hour = 0;
         this.minute = 0;
     }

    public TimeShort(byte hour, byte minute){
        this.hour = hour;
        this.minute = minute;
    }

    public TimeShort(int hour, int minute){
        this.hour = (byte) hour;
        this.minute = (byte) minute;
    }

    public TimeShort(String timeAsString){
        String[] times = timeAsString.split(":");
        this.hour = Byte.parseByte(times[0]);
        this.minute = Byte.parseByte(times[1].substring(0,2));
        if (times[1].substring(2).equalsIgnoreCase("PM")){
            this.hour += 12;
        }

    }

    public boolean after(TimeShort other){
        return this.getMinAfterMidnight() > other.getMinAfterMidnight();
    }

    public boolean before(TimeShort other){
        return this.getMinAfterMidnight() < other.getMinAfterMidnight();
    }

    public boolean equals(TimeShort other){
        return this.getMinAfterMidnight() == other.getMinAfterMidnight();
    }

    public String toString24h() {
        String result = String.format("%d", this.hour) + ":" + String.format("%02d", this.minute);
        return result;
    }

    public String toString12h() {
        String result = String.format("%d", this.hour%12) + ":" + String.format("%02d", this.minute) + (this.hour > 12 ? "PM": "AM");
        return result;
    }

    public int getMinAfterMidnight(){
        return ((this.hour * 60) + this.minute);
    }
}

enum ClassStatus {
    OPEN("Open"), CLOSE("Closed");

    private String value;

    ClassStatus(String setValue) {
        this.value = setValue;
    }

    String getValue() {
        return value;
    }
}

enum Day {
    MONDAY("M"),
    TUESDAY("TU"),
    WEDNESDAY("W"),
    THURSDAY("TH"),
    FRIDAY("F"),
    SATURDAY("S");

    private String abbreviation;

    Day(String abbrev) {
        this.abbreviation = abbrev;
    }

    String getAbbreviation() {
        return abbreviation;
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
    private Set<Day> days;
    private ClassStatus status;
    Section() {
        this.setSectionID(0);
        this.setInstructors(null);
        this.setRoom(null);
        this.setStartTime(null);
        this.setEndTime(null);
        this.setDays(null);
        this.setStatus(null);
    }

    Section(int number, String instructors, String room, TimeShort startTime, TimeShort endTime, Set<Day> days, ClassStatus status) {
        this.setSectionID(number);
        this.setInstructors(instructors);
        this.setRoom(room);
        this.setStartTime(startTime);
        this.setEndTime(endTime);
        this.setDays(days);
        this.setStatus(status);
    }

    Section(JSONObject jsonObject) throws JSONException {
        this.setSectionID(Integer.parseInt(jsonObject.getString("CourseNumber")));
        Log.d("New Section ID", ((Integer)getSectionID()).toString());
        this.setSectionNumber(Integer.parseInt(jsonObject.getString("Section")));
        Log.d("New Section Number", ((Integer)getSectionNumber()).toString());
        this.setRoom(jsonObject.getString("Room"));
        Log.d("New Section Room", getRoom());
        this.setInstructors(jsonObject.getString("Instructor"));
        Log.d("New Section Instructor", getInstructors());
        String times[] = jsonObject.getString("MeetingTime").split("-");
        this.setStartTime(new TimeShort(times[0]));
        this.setEndTime(new TimeShort(times[1]));
        Log.d("New Section MeetingTime", getStartTime().toString24h()+ "-" + getEndTime().toString24h());
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

    public Set<Day> getDays() {
        return days;
    }

    public void setDays(Set<Day> days) {
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

    public boolean conflictsWith(Section Other) {
        if (!Collections.disjoint(this.getDays(), Other.getDays()))                                 //If there is overlap between the two sets of days
            return (
                    (this.getEndTime().after(Other.getStartTime()))
                            &&
                            (this.getStartTime().before(Other.getEndTime())))                       // this section intersects the end of other section
                    ||
                    ((Other.getEndTime().after(this.getStartTime()))
                            &&
                            (Other.getStartTime().before(this.getEndTime()))                        // this section intersects the beginning of other section
                            ||
                            this.getStartTime().equals(Other.getStartTime())                        // start times match
                            ||
                            this.getEndTime().equals(Other.getEndTime())                            // end times match
                    );
        else return false;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }
}
