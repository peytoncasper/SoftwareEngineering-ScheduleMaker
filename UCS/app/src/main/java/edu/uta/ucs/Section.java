package edu.uta.ucs;

import java.util.Collections;
import java.util.Date;
import java.util.Set;

enum ClassStatus {
    OPEN, CLOSE
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
    private int sectionID;
    private String instructors;
    private Date startTime;
    private Date endTime;
    private Set<Day> days;
    private ClassStatus status;

    Section() {
        this.setSectionNumber(0);
        this.setInstructors(null);
        this.setStartTime(null);
        this.setEndTime(null);
        this.setDays(null);
        this.setStatus(null);
    }

    Section(int number, String instructors, Date startTime, Date endTime, Set<Day> days, ClassStatus status) {
        this.setSectionNumber(number);
        this.setInstructors(instructors);
        this.setStartTime(startTime);
        this.setEndTime(endTime);
        this.setDays(days);
        this.setStatus(status);
    }

    public void setSectionNumber(int newSectionNumber) {
        this.setSectionID(newSectionNumber);
    }

    public String getInstructors() {
        return instructors;
    }

    public void setInstructors(String instructors) {
        this.instructors = instructors;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
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

    public boolean conflictsWith(Section Other) {
        if (!Collections.disjoint(this.getDays(), Other.getDays()))
            return (
                    (this.getEndTime().after(Other.getStartTime()))
                            &&
                            (this.getStartTime().before(Other.getEndTime())))
                    ||
                    ((Other.getEndTime().after(this.getStartTime()))
                            &&
                            (Other.getStartTime().before(this.getEndTime()))
                            ||
                            this.getStartTime().equals(Other.getStartTime())
                            ||
                            this.getEndTime().equals(Other.getEndTime())
                    );
        else return false;
    }

}
