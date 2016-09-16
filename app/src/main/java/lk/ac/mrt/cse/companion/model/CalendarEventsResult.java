package lk.ac.mrt.cse.companion.model;

import java.util.ArrayList;

/**
 * Created by asksa on 9/16/2016.
 */
public class CalendarEventsResult {

    private ArrayList<CalendarEvent> calendarEvents;

    public CalendarEventsResult() {

    }

    public ArrayList<CalendarEvent> getCalendarEvents() {
        return calendarEvents;
    }

    public void setCalendarEvents(ArrayList<CalendarEvent> calendarEvents) {
        this.calendarEvents = calendarEvents;
    }

    public void addCalendarEvent(CalendarEvent event){
        if(calendarEvents == null){
            this.calendarEvents = new ArrayList<CalendarEvent>();
        }
        calendarEvents.add(event);
    }
}
