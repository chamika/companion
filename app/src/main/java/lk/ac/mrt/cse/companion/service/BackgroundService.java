package lk.ac.mrt.cse.companion.service;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.snapshot.DetectedActivityResult;
import com.google.android.gms.awareness.snapshot.HeadphoneStateResult;
import com.google.android.gms.awareness.snapshot.LocationResult;
import com.google.android.gms.awareness.snapshot.PlacesResult;
import com.google.android.gms.awareness.snapshot.WeatherResult;
import com.google.android.gms.awareness.state.HeadphoneState;
import com.google.android.gms.awareness.state.Weather;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.txusballesteros.bubbles.BubbleLayout;
import com.txusballesteros.bubbles.BubblesManager;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import lk.ac.mrt.cse.companion.R;
import lk.ac.mrt.cse.companion.activity.CompanionActivity;
import lk.ac.mrt.cse.companion.model.CalendarEventsResult;
import lk.ac.mrt.cse.companion.util.ContextBundler;

/**
 * Created by chamika on 9/11/16.
 */

public class BackgroundService extends Service {

    private static final String TAG = BackgroundService.class.getSimpleName();

    private static Timer timer;

    private boolean started = false;
    private BubblesManager bubblesManager;
    private BubbleLayout bubbleView;
    private GoogleApiClient client;

    private ContextBundler contextBundler = new ContextBundler();
    private final IBinder binder = new LocalBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //init bubble
        bubblesManager = new BubblesManager.Builder(this)
//                .setTrashLayout(R.layout.bubble_trash_layout)
//                .setInitializationCallback(new OnInitializedCallback() {
//                    @Override
//                    public void onInitialized() {
//                        addNewBubble();
//                    }
//                })
                .build();
        bubblesManager.initialize();

        client = new GoogleApiClient.Builder(this)
                .addApi(Awareness.API)
                .build();
        client.connect();


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!started) {
            showBubble();
            started = true;
            if(timer != null){
                timer.cancel();
            }
            timer = new Timer();
            //timer.scheduleAtFixedRate(new SnapshopRetriever(), 0, 10000);
            timer.scheduleAtFixedRate(new CalendarContextRetriever(this), 0, 10000);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        hideBubble();
        super.onDestroy();
        started = false;
        timer.cancel();
    }

    public ContextBundler getContextBundler() {
        return contextBundler;
    }

    private void showBubble() {
        bubbleView = (BubbleLayout) LayoutInflater
                .from(BackgroundService.this).inflate(R.layout.layout_floating, null);
        bindBubbleAction(bubbleView);
        bubblesManager.addBubble(bubbleView, 60, 20);
    }

    private void hideBubble() {
        try {
//            bubblesManager.removeBubble(bubbleView);
            bubblesManager.recycle();
        } catch (Exception e) {
            Log.e(TAG, "Cannot remove bubble");
        }
    }

    private void bindBubbleAction(View view) {
        if (view instanceof BubbleLayout) {
            BubbleLayout bubbleView = (BubbleLayout) view;
            bubbleView.setOnBubbleClickListener(new BubbleLayout.OnBubbleClickListener() {
                @Override
                public void onBubbleClick(BubbleLayout bubble) {
                    Intent intent = new Intent(BackgroundService.this, CompanionActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            });
        }
    }




    private void getSnapshotUpdate() {
        Awareness.SnapshotApi.getDetectedActivity(client)
                .setResultCallback(new ResultCallback<DetectedActivityResult>() {
                    @Override
                    public void onResult(@NonNull DetectedActivityResult detectedActivityResult) {
                        if (!detectedActivityResult.getStatus().isSuccess()) {
                            Log.e("MainActivity", "Could not get the current activity.");
                            Log.d(TAG, "Could not get the current activity.");
                            return;
                        }
                        ActivityRecognitionResult ar = detectedActivityResult.getActivityRecognitionResult();
                        DetectedActivity probableActivity = ar.getMostProbableActivity();
                        Log.i("MainActivity", probableActivity.toString());
                        Log.d(TAG, probableActivity.toString() + " at " + getTimeText());
                        contextBundler.addContext(ar);
                    }
                });

        Awareness.SnapshotApi.getHeadphoneState(client).setResultCallback(new ResultCallback<HeadphoneStateResult>() {
            @Override
            public void onResult(@NonNull HeadphoneStateResult headphoneStateResult) {
                if (!headphoneStateResult.getStatus().isSuccess()) {
                    Log.d(TAG, "Could not get the headphone state.");
                    return;
                }
                HeadphoneState headphoneState = headphoneStateResult.getHeadphoneState();
                String state = "";
                if (headphoneState.getState() == HeadphoneState.PLUGGED_IN) {
                    state = "PLUGGED_IN";
                } else if (headphoneState.getState() == HeadphoneState.UNPLUGGED) {
                    state = "UNPLUGGED";
                }
                Log.d(TAG, state + " at " + getTimeText());
                contextBundler.addContext(headphoneStateResult);
            }
        });

        //Already granted permission at app startup
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Awareness.SnapshotApi.getLocation(client).setResultCallback(new ResultCallback<LocationResult>() {
            @Override
            public void onResult(@NonNull LocationResult locationResult) {
                if (!locationResult.getStatus().isSuccess()) {
                    Log.d(TAG, "Could not get location");
                    return;
                }
                Log.d(TAG, locationResult.getLocation().toString() + " at " + getTimeText());
            }
        });
        Awareness.SnapshotApi.getPlaces(client).setResultCallback(new ResultCallback<PlacesResult>() {
            @Override
            public void onResult(@NonNull PlacesResult placesResult) {
                if (!placesResult.getStatus().isSuccess()) {
                    Log.d(TAG,"Could not get place result");
                    return;
                }
                StringBuilder sb = new StringBuilder();
                if(placesResult.getPlaceLikelihoods() != null) {
                    for (PlaceLikelihood hood : placesResult.getPlaceLikelihoods()) {
                        sb.append(hood.getPlace().getName());
                        sb.append(',');
                    }
                }else{
                    sb.append("No places found");
                }
                Log.d(TAG,sb.toString() + " at " + getTimeText());
                contextBundler.addContext(placesResult);
            }
        });


        Awareness.SnapshotApi.getWeather(client).setResultCallback(new ResultCallback<WeatherResult>() {
            @Override
            public void onResult(@NonNull WeatherResult weatherResult) {
                if (!weatherResult.getStatus().isSuccess()) {
                    Log.d(TAG,"Could not get weather");
                    return;
                }
                Weather weather = weatherResult.getWeather();
                if(weather != null) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Temperature:");
                    sb.append(weather.getTemperature(Weather.CELSIUS));
                    sb.append("`C, Feels like:");
                    sb.append(weather.getFeelsLikeTemperature(Weather.CELSIUS));
                    sb.append("`C, Dew Point:");
                    sb.append(weather.getDewPoint(Weather.CELSIUS));
                    sb.append("`C, Humidiy:");
                    sb.append(weather.getHumidity());
                    sb.append(", Conditions:");

                    int[] conditions = weather.getConditions();
                    for(int val:conditions){
                        try {
                            sb.append(getVariableName("CONDITION_",Weather.class,val));
                            sb.append(", ");
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                    Log.d(TAG,sb.toString() + " at " + getTimeText());
                    contextBundler.addContext(weatherResult);
                }else{
                    Log.d(TAG,"No weather data found at " + getTimeText());
                }
            }
        });


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


    private String getTimeText() {
        return new SimpleDateFormat("hh:mm:ss").format(new Date());
    }

    private class SnapshopRetriever extends TimerTask{

        @Override
        public void run() {
            getSnapshotUpdate();
        }
    }

    private class CalendarContextRetriever extends TimerTask {

        private Context calendarServiceContext;
        CalendarContextRetriever(Context context){
            this.calendarServiceContext = context;
        }
        @Override
        public void run() {
            CalendarEventsResult calendarEventsResult = CalendarReader.readCalendar(calendarServiceContext);
            contextBundler.addContext(calendarEventsResult);
        }
    }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public BackgroundService getService() {
            // Return this instance of BackgroundService so clients can call public methods
            return BackgroundService.this;
        }
    }
}


