package lk.ac.mrt.cse.companion.model;

import com.google.android.gms.awareness.snapshot.WeatherResult;

/**
 * Created by chamika on 9/13/2016.
 */

public class WeatherContext extends BaseContext<WeatherResult> {

    @Override
    public int difference(WeatherResult newData) {
        int diff = 100;
        if (oldData != null && newData != null) {
            int[] conditions = newData.getWeather().getConditions();
            diff = conditions.length;
            for (int conNew : conditions) {
                for (int conOld : oldData.getWeather().getConditions()) {
                    if (conOld == conNew) {
                        --diff;
                    }
                }
            }
        }
        return diff;
    }
}
