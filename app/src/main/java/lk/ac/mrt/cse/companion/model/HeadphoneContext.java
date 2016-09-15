package lk.ac.mrt.cse.companion.model;

import com.google.android.gms.awareness.snapshot.HeadphoneStateResult;
import com.google.android.gms.awareness.state.HeadphoneState;

import java.util.Arrays;
import java.util.List;

/**
 * Created by chamika on 9/16/16.
 */

public class HeadphoneContext extends BaseContext<HeadphoneStateResult> {
    @Override
    public int difference(HeadphoneStateResult newData) {
        if (data != null && newData != null) {
            return Math.abs(data.getHeadphoneState().getState() - newData.getHeadphoneState().getState());
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
            HeadphoneState headphoneState = data.getHeadphoneState();
            String state = "";
            if (headphoneState.getState() == HeadphoneState.PLUGGED_IN) {
                state = "PLUGGED_IN";
            } else if (headphoneState.getState() == HeadphoneState.UNPLUGGED) {
                state = "UNPLUGGED";
            }
            return Arrays.asList(state);
        }
        return null;
    }
}
