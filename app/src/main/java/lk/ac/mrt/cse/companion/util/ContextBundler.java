package lk.ac.mrt.cse.companion.util;

import com.google.android.gms.awareness.snapshot.HeadphoneStateResult;
import com.google.android.gms.awareness.snapshot.PlacesResult;
import com.google.android.gms.awareness.snapshot.WeatherResult;
import com.google.android.gms.location.ActivityRecognitionResult;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import lk.ac.mrt.cse.companion.Constants;
import lk.ac.mrt.cse.companion.model.ActivityContext;
import lk.ac.mrt.cse.companion.model.BaseContext;
import lk.ac.mrt.cse.companion.model.HeadphoneContext;
import lk.ac.mrt.cse.companion.model.PlacesContext;
import lk.ac.mrt.cse.companion.model.WeatherContext;

/**
 * Created by chamika on 9/13/2016.
 */

public class ContextBundler {

    //
    private Map<String, BaseContext> contextes = new HashMap<>();

    //pattern of supplier and consumer type variable. Service will supply and Activity will consume
    private Set<String> updatedTypes = Collections.synchronizedSet(new HashSet<String>());

    public ContextBundler() {
        contextes.put(Constants.CONTEXT_WEATHER, new WeatherContext());
        contextes.put(Constants.CONTEXT_ACTIVITY, new ActivityContext());
        contextes.put(Constants.CONTEXT_HEADPHONE, new HeadphoneContext());
        contextes.put(Constants.CONTEXT_PLACES, new PlacesContext());
    }

    /**
     * @param result
     * @return result change or not
     */
    public boolean addContext(Object result) {
        String type = null;
        BaseContext baseContext = null;

        if (result instanceof WeatherResult) {
            type = Constants.CONTEXT_WEATHER;
        } else if (result instanceof ActivityRecognitionResult) {
            type = Constants.CONTEXT_ACTIVITY;
        } else if (result instanceof HeadphoneStateResult) {
            type = Constants.CONTEXT_HEADPHONE;
        } else if (result instanceof PlacesResult) {
            type = Constants.CONTEXT_PLACES;
        }

        baseContext = contextes.get(type);

        if (baseContext != null) {
            if (baseContext.getData() == null) {
                //new context type introduced
                baseContext.setData(result);
                contextes.put(type, baseContext);
                updatedTypes.add(type);
                return true;
            } else {
                // existing context value compare with old value. If significant change, replace it
                // Significance will be measured by difference() level and minChangeLevel()
                if (baseContext.difference(result) > baseContext.minChangeLevel()) {
                    baseContext.setData(result);
                    contextes.put(type, baseContext);
                    updatedTypes.add(type);
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
