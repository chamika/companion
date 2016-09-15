package lk.ac.mrt.cse.companion.util;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.companion.model.Event;
import lk.ac.mrt.cse.companion.store.BaseStore;

/**
 * Created by chamika on 9/12/16.
 */

public class DataHandler {

    private static final String TAG = DataHandler.class.getSimpleName();

    public static void saveEvent(Context context, Event event) {
        BaseStore.saveEvent(context, BaseStore.Structure.TABLE_NAME_EVENTS, event.getApp(), event.getType(), event.getState(), 1);
        Log.d(TAG, "Event saved:" + event.toString());
    }

    public static List<Event> loadEvents(Context context, String type, String state) {
        List<Event> events = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = BaseStore.getAppsFromState(context, BaseStore.Structure.TABLE_NAME_EVENTS, type, state);
            if (cursor.moveToFirst()) {
                do {
                    Event event = new Event(cursor.getString(cursor.getColumnIndex(BaseStore.Structure.COLUMN_NAME_APP)),
                            cursor.getString(cursor.getColumnIndex(BaseStore.Structure.COLUMN_NAME_TYPE)),
                            cursor.getString(cursor.getColumnIndex(BaseStore.Structure.COLUMN_NAME_STATE)));
                    event.setHitCount(cursor.getInt(cursor.getColumnIndex(BaseStore.Structure.COLUMN_NAME_HITS)));
                    Log.d(TAG, "Value found:" + event.toString());
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return events;
    }

}
