package lk.ac.mrt.cse.companion.model;

import com.google.android.gms.awareness.snapshot.PlacesResult;
import com.google.android.gms.location.places.PlaceLikelihood;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chamika on 9/16/16.
 */

public class PlacesContext extends BaseContext<PlacesResult> {

    public static final int MAX_STATES = 3;

    @Override
    public int difference(PlacesResult newData) {
        int diff = minChangeLevel() + 1;
        if (data != null && newData != null) {
            List<String> oldPlaces = getPlaceNames(data);
            List<String> newPlaces = getPlaceNames(newData);
            diff = Math.max(oldPlaces.size(), newPlaces.size());
            for (String oldPlace : oldPlaces) {
                for (String newPlace : newPlaces) {
                    if (oldPlace.equals(newPlace)) {
                        --diff;
                    }
                }
            }
        }
        return diff;
    }

    @Override
    public int minChangeLevel() {
        return 2;
    }

    @Override
    public List<String> getStates() {
        return getPlaceNames(data);
    }


    private List<String> getPlaceNames(PlacesResult result) {
        List<String> places = new ArrayList<>();
        if (result.getPlaceLikelihoods() != null) {
            int max = Math.min(result.getPlaceLikelihoods().size(), MAX_STATES);
            for (int i = 0; i < max; i++) {
                PlaceLikelihood hood = result.getPlaceLikelihoods().get(i);
                places.add(String.valueOf(hood.getPlace().getName()));
            }
        }

        return places;
    }
}
