package lk.ac.mrt.cse.companion;

import java.util.Arrays;
import java.util.List;

/**
 * Created by chamika on 9/12/16.
 */

public class Constants {

    public static final int ACTIVITY_REFRESH_INTERVAL = 5000;
    public static final int SERVICE_REFRESH_INTERVAL = 10000;

    public static final String CONTEXT_ANY = "Any";
    public static final String CONTEXT_ACTIVITY = "Activity";
    public static final String CONTEXT_HEADPHONE = "Headphone";
    public static final String CONTEXT_LOCATION = "Location";
    public static final String CONTEXT_PLACES = "Places";
    public static final String CONTEXT_WEATHER = "Weather";
    public static final String CONTEXT_CALENDAR = "Calendar";

    public static final List<String> LAUNCHER_CONTEXTS = Arrays.asList(CONTEXT_ACTIVITY,
            CONTEXT_HEADPHONE, CONTEXT_PLACES, CONTEXT_WEATHER,CONTEXT_CALENDAR);
}
