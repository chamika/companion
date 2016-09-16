package lk.ac.mrt.cse.companion.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by asksa on 9/16/2016.
 */
public class CalendarContext extends BaseContext<CalendarEventsResult> {

    public static int MAX_STATES = 1;

    @Override
    public int difference(CalendarEventsResult newData) {
        int diff = minChangeLevel() + 1;

        if (data != null && newData != null) {
            List<String> oldEvents = getEventIds(data);
            //MAX_STATES = oldEvents.size();

            List<String> newEvents = getEventIds(newData);
            diff = Math.max(oldEvents.size(), newEvents.size());
            for (String oldPlace : oldEvents) {
                for (String newEvent : newEvents) {
                    if (oldPlace.equals(newEvent)) {
                        --diff;
                    }
                }
            }
        }
        return diff;
    }

    @Override
    public int minChangeLevel() {
        //return getEventIds(data).size()-1;
        return 0;
    }

    @Override
    public List<String> getStates() {
        return getEventIds(data);
    }


    private List<String> getEventIds(CalendarEventsResult result) {
        List<String> eventIds = new ArrayList<>();
        if (result.getCalendarEvents() != null) {
            int max = Math.min(result.getCalendarEvents().size(), MAX_STATES);
            for (int i = 0; i < max; i++) {
                CalendarEvent event = result.getCalendarEvents().get(i);
                eventIds.add(String.valueOf(event.getId()));
            }
        }

        return eventIds;
    }
}
