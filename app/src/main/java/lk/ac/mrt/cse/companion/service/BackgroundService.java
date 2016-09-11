package lk.ac.mrt.cse.companion.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.txusballesteros.bubbles.BubbleLayout;
import com.txusballesteros.bubbles.BubblesManager;

import lk.ac.mrt.cse.companion.R;
import lk.ac.mrt.cse.companion.activity.CompanionActivity;

/**
 * Created by chamika on 9/11/16.
 */

public class BackgroundService extends Service {

    private static final String TAG = BackgroundService.class.getSimpleName();

    private boolean started = false;
    private BubblesManager bubblesManager;
    private BubbleLayout bubbleView;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
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


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!started) {
            showBubble();
            started = true;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        hideBubble();
        super.onDestroy();
        started = false;
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
        }catch (Exception e)
        {
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
}


