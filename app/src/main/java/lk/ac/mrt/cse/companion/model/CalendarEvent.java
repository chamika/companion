package lk.ac.mrt.cse.companion.model;

import java.util.Date;

/**
 * Created by asksa on 9/16/2016.
 */
public class CalendarEvent {

    private int id;


    private String title;
    private Date begin;
    private Date end;
    private Boolean allDay;
    private String location;
    private String description;

    public CalendarEvent(int id, String title, Date begin, Date end, Boolean allDay, String location, String description) {
        this.id = id;
        this.title = title;
        this.begin = begin;
        this.end = end;
        this.location = location;
        this.description = description;
        this.allDay = allDay;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getBegin() {
        return begin;
    }

    public void setBegin(Date begin) {
        this.begin = begin;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public Boolean getAllDay() {
        return allDay;
    }

    public void setAllDay(Boolean allDay) {
        this.allDay = allDay;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

}
