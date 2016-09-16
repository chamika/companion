package lk.ac.mrt.cse.companion.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by asksa on 9/16/2016.
 */
public class CalendarContext extends BaseContext<CalendarEventsResult> {


    @Override
    public int difference(CalendarEventsResult newData) {
        int diff = minChangeLevel() + getEventIds(data).size();

        if (data != null && newData != null) {
            List<String> oldEvents = getEventIds(data);

            List<String> newEvents = getEventIds(newData);
            diff = Math.max(oldEvents.size(), newEvents.size());
            for (String oldEvent : oldEvents) {
                for (String newEvent : newEvents) {
                    if (oldEvent.equals(newEvent)) {
                        --diff;
                    }
                }
            }
        }
        return diff;
    }

    @Override
    public int minChangeLevel() {
        return 0;
    }

    @Override
    public List<String> getStates() {
        return getEventIds(data);
    }


    private List<String> getEventIds(CalendarEventsResult result) {
        List<String> eventIds = new ArrayList<>();
        if (result.getCalendarEvents() != null) {
            for (int i = 0; i < result.getCalendarEvents().size(); i++) {
                CalendarEvent event = result.getCalendarEvents().get(i);
                eventIds.add(String.valueOf(event.getId()));
            }
        }

        return eventIds;
    }
}
