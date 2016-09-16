package lk.ac.mrt.cse.companion.store;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.Arrays;
import java.util.List;

/**
 * Created by chamika on 9/11/16.
 */

public class BaseStore {

    private static final String TAG = BaseStore.class.getSimpleName();

    private static SQLiteDatabase dbReadable;
    private static SQLiteDatabase dbWritable;

    private BaseStore() {
    }

    public static class Structure implements BaseColumns {
        public static final String COLUMN_NAME_APP = "app";
        public static final String COLUMN_NAME_TYPE = "type";
        public static final String COLUMN_NAME_STATE = "state";
        public static final String COLUMN_NAME_HITS = "hits";
        public static final String TABLE_NAME_EVENTS = "events";
    }

    public static Cursor getAppsFromState(Context context, String tableName, String type, String state) {
        DBHelper mDbHelper = new DBHelper(context);

        if (dbReadable == null || !dbReadable.isOpen()) {
            dbReadable = mDbHelper.getReadableDatabase();
        }

        String[] projection = {
                Structure._ID,
                Structure.COLUMN_NAME_APP,
                Structure.COLUMN_NAME_TYPE,
                Structure.COLUMN_NAME_STATE,
                Structure.COLUMN_NAME_HITS
        };

        String selection = Structure.COLUMN_NAME_TYPE + " = ? and " + Structure.COLUMN_NAME_STATE + " = ?";
        String[] selectionArgs = {type, state};

        String sortOrder = Structure.COLUMN_NAME_HITS + " DESC";

        return dbReadable.query(
                tableName,      // The table to query
                projection,     // The columns to return
                selection,      // The columns for the WHERE clause
                selectionArgs,  // The values for the WHERE clause
                null,           // don't group the rows
                null,           // don't filter by row groups
                sortOrder       // The sort order
        );
    }

    public static Cursor getAppsFromState(Context context, String tableName, String type, List<String> states) {
        DBHelper mDbHelper = new DBHelper(context);

        if (dbReadable == null || !dbReadable.isOpen()) {
            dbReadable = mDbHelper.getReadableDatabase();
        }

        String[] projection = {
                Structure.COLUMN_NAME_APP
        };

        StringBuilder inBuilder = new StringBuilder(Structure.COLUMN_NAME_STATE + " IN ( ");
        for (int i = 0; i < states.size(); i++) {
            if (i != 0) {
                inBuilder.append(" , ");
            }
            inBuilder.append(" ? ");
        }
        inBuilder.append(" ) and ");
        inBuilder.append(Structure.COLUMN_NAME_TYPE);
        inBuilder.append(" = ? ");
        String selection = inBuilder.toString();

        String[] selectionArgs = Arrays.copyOf(states.toArray(new String[0]), states.size() + 1);
        selectionArgs[selectionArgs.length - 1] = type;

        String sortOrder = Structure.COLUMN_NAME_HITS + " DESC";

        return dbReadable.query(
                true,           //distinct
                tableName,      // The table to query
                projection,     // The columns to return
                selection,      // The columns for the WHERE clause
                selectionArgs,  // The values for the WHERE clause
                null,           // don't group the rows
                null,           // don't filter by row groups
                sortOrder,       // The sort order
                null           // no limit
        );
    }

    public static long saveEvent(Context context, String tableName, String app, String type, String state, int hitIncrement) {
        DBHelper mDbHelper = new DBHelper(context);

        if (dbWritable == null || !dbWritable.isOpen()) {
            dbWritable = mDbHelper.getReadableDatabase();
        }

        dbWritable.execSQL("UPDATE " + tableName + " set " + Structure.COLUMN_NAME_HITS + " = " + Structure.COLUMN_NAME_HITS + " + " + hitIncrement +
                " where " + Structure.COLUMN_NAME_APP + " = ? and " + Structure.COLUMN_NAME_TYPE + " = ? and " + Structure.COLUMN_NAME_STATE + " = ?", new String[]{app, type, state});

        SQLiteStatement statement = dbWritable.compileStatement("SELECT changes()");
        long affectedRows = statement.simpleQueryForLong();

        if (affectedRows > 0) {
            Log.d(TAG, "Updated rows:" + affectedRows + " data=" + app + "," + state + "," + hitIncrement);
            return affectedRows;
        } else {
            ContentValues values = new ContentValues();
            values.put(Structure.COLUMN_NAME_APP, app);
            values.put(Structure.COLUMN_NAME_TYPE, type);
            values.put(Structure.COLUMN_NAME_STATE, state);
            values.put(Structure.COLUMN_NAME_HITS, hitIncrement);
            dbWritable.insert(tableName, null, values);
            Log.d(TAG, "Insert row:" + app + "," + state + "," + hitIncrement);
            return 1;
        }
    }


    public static void closeDBs() {
        if (dbReadable != null && dbReadable.isOpen()) {
            dbReadable.close();
        }
        if (dbWritable != null && dbWritable.isOpen()) {
            dbWritable.close();
        }
    }


}
