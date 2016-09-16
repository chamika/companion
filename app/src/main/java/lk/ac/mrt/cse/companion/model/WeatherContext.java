package lk.ac.mrt.cse.companion.model;

import com.google.android.gms.awareness.snapshot.WeatherResult;
import com.google.android.gms.awareness.state.Weather;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chamika on 9/13/2016.
 */

public class WeatherContext extends BaseContext<WeatherResult> {

    @Override
    public int difference(WeatherResult newData) {
        int diff = minChangeLevel() + 1;
        if (data != null && newData != null) {
            int[] conditions = newData.getWeather().getConditions();
            diff = Math.max(conditions.length,data.getWeather().getConditions().length);
            for (int conNew : conditions) {
                for (int conOld : data.getWeather().getConditions()) {
                    if (conOld == conNew) {
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
        if(data != null){
            List<String> states = new ArrayList<>();
            Weather weather = data.getWeather();

            int[] conditions = weather.getConditions();
            for(int val:conditions){
                try {
                    states.add(getVariableName("CONDITION_",Weather.class,val));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            states.add("Temperature:" + weather.getTemperature(Weather.CELSIUS) + "°C");
            states.add("Feels:" + weather.getFeelsLikeTemperature(Weather.CELSIUS) + "°C");
            states.add("Humidity:" + weather.getHumidity() + "%");

            return states;

        }
        return null;
    }

    private String getVariableName(String prefix, Class clazz, Object value) throws IllegalAccessException {
        Field[] declaredFields = clazz.getDeclaredFields();
        for(Field field:declaredFields){
            if(field.getName().startsWith(prefix) && value.equals(field.get(null))){
                return field.getName().replace(prefix,"");
            }
        }
        return "";
    }
}
