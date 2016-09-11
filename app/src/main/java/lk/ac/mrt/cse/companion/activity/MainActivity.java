package lk.ac.mrt.cse.companion.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.txusballesteros.bubbles.BubbleLayout;
import com.txusballesteros.bubbles.BubblesManager;

import lk.ac.mrt.cse.companion.R;
import lk.ac.mrt.cse.companion.service.FloatingViewService;

public class MainActivity extends AppCompatActivity {

    private boolean started = false;

    private BubblesManager bubblesManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Bubble will " + ((started) ? "hide" : "show") + " now.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
//                if(started){
//                    stopService(new Intent(getApplicationContext(), FloatingViewService.class));
//                }else {
//                    startService(new Intent(getApplicationContext(), FloatingViewService.class));
//                }
//                started = !started;
                if (started) {
                    hideBubble();
                } else {
                    showBubble();
                }
                started = !started;

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        bubblesManager.recycle();
    }

    private void showBubble() {
        BubbleLayout bubbleView = (BubbleLayout) LayoutInflater
                .from(MainActivity.this).inflate(R.layout.layout_floating, null);
        bindBubbleAction(bubbleView);
        bubblesManager.addBubble(bubbleView, 60, 20);
    }


    private void hideBubble() {
        bubblesManager.recycle();
    }

    private void bindBubbleAction(View view){
        if(view instanceof BubbleLayout) {
            BubbleLayout bubbleView = (BubbleLayout) view;
            bubbleView.setOnBubbleClickListener(new BubbleLayout.OnBubbleClickListener() {
                @Override
                public void onBubbleClick(BubbleLayout bubble) {
                    Intent intent = new Intent(MainActivity.this, CompanionActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            });
        }
    }
}
