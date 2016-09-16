package lk.ac.mrt.cse.companion.service;

/**
 * Created by asksa on 8/7/2016.
 */

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.text.format.DateUtils;
import android.util.Log;

import java.util.Date;
import java.util.HashSet;

import lk.ac.mrt.cse.companion.model.CalendarEvent;
import lk.ac.mrt.cse.companion.model.CalendarEventsResult;

public class CalendarReader {

    private static final String TAG = CalendarReader.class.getSimpleName();

    private static final String[] EVENT_PROJECTION = new String[]{
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.ACCOUNT_NAME,
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
            CalendarContract.Calendars.OWNER_ACCOUNT
    };

    private static final int PROJECTION_ID_INDEX = 0;
    private static final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
    private static final int PROJECTION_DISPLAY_NAME_INDEX = 2;
    private static final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;

    public static CalendarEventsResult readCalendar(Context context) {

        HashSet<String> calendarIds = new HashSet<String>();
        CalendarEventsResult calendarEvents = new CalendarEventsResult();
        String accountType = "com.google";
        String account = getAccount(context, accountType);

        if (account != null) {

            Cursor cursor = null;
            ContentResolver cr = context.getContentResolver();
            Uri uri = CalendarContract.Calendars.CONTENT_URI;
            String selection = "((" + CalendarContract.Calendars.ACCOUNT_NAME + " = ?) AND ("
                    + CalendarContract.Calendars.ACCOUNT_TYPE + " = ?) AND ("
                    + CalendarContract.Calendars.OWNER_ACCOUNT + " = ?))";
            String[] selectionArgs = new String[]{account, accountType,
                    account};

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                return null;
            }
            cursor = cr.query(uri, EVENT_PROJECTION, selection, selectionArgs, null);

            while (cursor.moveToNext()) {

                final String _id = cursor.getString(0);
                final String displayName = cursor.getString(1);
                final Boolean selected = !cursor.getString(2).equals("0");

                Log.i(TAG, "Id: " + _id + " Display Name: " + displayName + " Selected: " + selected);
                calendarIds.add(_id);
            }

            for (String id : calendarIds) {
                Uri.Builder builder = Uri.parse("content://com.android.calendar/instances/when").buildUpon();
                long now = new Date().getTime();
                ContentUris.appendId(builder, now - DateUtils.DAY_IN_MILLIS * 10000);
                ContentUris.appendId(builder, now + DateUtils.DAY_IN_MILLIS * 10000);

                Cursor eventCursor = cr.query(builder.build(),
                        new String[]{"_id", "title", "begin", "end", "allDay", "eventLocation", "description"}, "calendar_id=" + id,
                        null, "startDay ASC, startMinute ASC");

                while (eventCursor.moveToNext()) {
                    final int eventId = eventCursor.getInt(0);
                    final String title = eventCursor.getString(0);
                    final Date begin = new Date(eventCursor.getLong(1));
                    final Date end = new Date(eventCursor.getLong(2));
                    final Boolean allDay = !eventCursor.getString(3).equals("0");
                    final String location= eventCursor.getString(4);
                    final String description= eventCursor.getString(5);


                    Log.d(TAG, "Id: " + id + "Title: " + title + " Begin: " + begin + " End: " + end + " All Day: " + allDay + " Location: " + location + " Decs: " + description);

                    CalendarEvent calendarEvent = new CalendarEvent(eventId, title, begin, end, allDay, location, description);
                    calendarEvents.addCalendarEvent(calendarEvent);
                }
            }
        }
        return calendarEvents;
    }


    private static String getAccount(Context context, String type) {
        AccountManager manager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        Account[] list = manager.getAccounts();
        String acount = null;

        for (Account account : list) {
            if (account.type.equalsIgnoreCase(type)) {
                acount = account.name;
                break;
            }
        }

        return acount;
    }

}
