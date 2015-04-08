package edu.uta.ucs;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

enum ClassStatus {
    OPEN("OPEN"), CLOSED("CLOSED");

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
    Section() {
        this.setSectionID(0);
        this.setInstructors(null);
        this.setRoom(null);
        this.setStartTime(null);
        this.setEndTime(null);
        this.setDays(null);
        this.setStatus(null);
    }

    Section(int number, String instructors, String room, TimeShort startTime, TimeShort endTime, ArrayList<Day> days, ClassStatus status) {
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

        JSONArray jsonDaysArray = jsonObject.getJSONArray("MeetingDays");
        Log.d("New Section Days List:", jsonDaysArray.toString());

        days = new ArrayList<Day>(jsonDaysArray.length());

        for(int index = jsonDaysArray.length(); index != 0;index--){
            Day temp = Day.valueOf(jsonDaysArray.getString(index -1));
            days.add(temp);
            Log.d("New Section Day: ", ((Day)days.get(days.size()-1)).toString());
            Day temp2 = (Day)days.get(0);
        }
        Log.d("New Section #of Days: ", ((Integer) days.size()).toString());

        setStatus(ClassStatus.valueOf(jsonObject.getString("Status")));
        Log.d("New Section Status: ", getStatus().toString());
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

    public boolean conflictsWith(Section otherSection) {
        if (!Collections.disjoint(this.getDays(), otherSection.getDays())) {        // If there is overlap between the two sets of days conflict is possible, run checks

            if (this.getStartTime().before(otherSection.getStartTime())) {          // If this section starts before the other section
                return this.getEndTime().before(otherSection.getEndTime());         //  check to see if this section ends before other section begins
            } else if (this.getStartTime().after(otherSection.getStartTime())) {      // If this section starts after the other section
                return this.getEndTime().after(otherSection.getEndTime());          //  check to see if other section ends before this section begins
            } else
                return true;                                                     // In this section starts neither before nor after the other section it starts at the same time, or there's some other issue
        } else return false;  // Days are disjoint, no conflict possible
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }
}
