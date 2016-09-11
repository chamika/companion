package lk.ac.mrt.cse.companion.model;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import lk.ac.mrt.cse.companion.store.BaseStore;

/**
 * Created by chamika on 9/12/16.
 */

public class DataHandler {

    private static final String TAG = DataHandler.class.getSimpleName();

    private void insert(Context context) {
        BaseStore.saveEvent(context, BaseStore.Structure.TABLE_NAME_EVENTS, "test", "headphone", "plugged_in", 1);
        Cursor cursor = BaseStore.getAppsFromState(context, BaseStore.Structure.TABLE_NAME_EVENTS, "headphone", "plugged_in");
        if (cursor.moveToFirst()) {
            do {
                String app = cursor.getString(cursor.getColumnIndex(BaseStore.Structure.COLUMN_NAME_APP));
                String type = cursor.getString(cursor.getColumnIndex(BaseStore.Structure.COLUMN_NAME_TYPE));
                String state = cursor.getString(cursor.getColumnIndex(BaseStore.Structure.COLUMN_NAME_STATE));
                int hits = cursor.getInt(cursor.getColumnIndex(BaseStore.Structure.COLUMN_NAME_HITS));
                Log.d(TAG, "Value found:" + app + "," + type + "," + state + "," + hits);
            } while (cursor.moveToNext());
        }

        cursor.close();
    }

}
