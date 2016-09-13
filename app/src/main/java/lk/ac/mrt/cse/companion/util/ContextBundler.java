package lk.ac.mrt.cse.companion.util;

import com.google.android.gms.awareness.snapshot.WeatherResult;
import com.google.android.gms.common.api.Result;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import lk.ac.mrt.cse.companion.Constants;
import lk.ac.mrt.cse.companion.model.BaseContext;
import lk.ac.mrt.cse.companion.model.WeatherContext;

/**
 * Created by chamika on 9/13/2016.
 */

public class ContextBundler {

    //TODO add other context here
    private WeatherContext weatherContext;

    //pattern of supplier and consumer type variable. Service will supply and Activity will consume
    private Set<String> updatedTypes = Collections.synchronizedSet(new HashSet<String>());

    /**
     * @param result
     * @return result change or not
     */
    public boolean addContext(Object result) {
        if (result instanceof WeatherResult) {
            //TODO write this in more generalize manner so that every result and context updates oldData with less effort
            WeatherResult newWeatherContext = (WeatherResult) result;
            if (weatherContext == null) {
                weatherContext = new WeatherContext();
                weatherContext.setOldData(newWeatherContext);
                updatedTypes.add(Constants.CONTEXT_WEATHER);
                return true;
            } else {
                if (weatherContext.difference(newWeatherContext) > 0) {
                    weatherContext.setOldData(newWeatherContext);
                    updatedTypes.add(Constants.CONTEXT_WEATHER);
                    return true;
                } else {
                    return false;
                }
            }
        }

        return false;
    }

    public Set<String> getUpdatedTypes() {
        return updatedTypes;
    }
}
