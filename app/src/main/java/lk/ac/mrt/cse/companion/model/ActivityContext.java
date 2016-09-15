package lk.ac.mrt.cse.companion.model;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.Arrays;
import java.util.List;

/**
 * Created by chamika on 9/16/16.
 */

public class ActivityContext extends BaseContext<ActivityRecognitionResult> {
    @Override
    public int difference(ActivityRecognitionResult newData) {
        if (data != null && newData != null) {
            return Math.abs(data.getMostProbableActivity().getType() - newData.getMostProbableActivity().getType());
        }
        return 0;
    }

    @Override
    public int minChangeLevel() {
        return 0;
    }

    @Override
    public List<String> getStates() {
        if (data != null) {
            DetectedActivity probableActivity = data.getMostProbableActivity();
            String activity = DetectedActivity.zzsy(probableActivity.getType());
            return Arrays.asList(activity);
        }
        return null;
    }
}
