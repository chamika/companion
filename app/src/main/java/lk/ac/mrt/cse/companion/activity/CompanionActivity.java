package lk.ac.mrt.cse.companion.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.flipkart.chatheads.ui.ChatHead;
import com.flipkart.chatheads.ui.ChatHeadArrangement;
import com.flipkart.chatheads.ui.ChatHeadContainer;
import com.flipkart.chatheads.ui.ChatHeadListener;
import com.flipkart.chatheads.ui.ChatHeadViewAdapter;
import com.flipkart.chatheads.ui.MaximizedArrangement;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import lk.ac.mrt.cse.companion.Constants;
import lk.ac.mrt.cse.companion.R;
import lk.ac.mrt.cse.companion.fragment.LaunchersFragment;
import lk.ac.mrt.cse.companion.fragment.OnAppLaunchListener;
import lk.ac.mrt.cse.companion.model.BaseContext;
import lk.ac.mrt.cse.companion.model.Event;
import lk.ac.mrt.cse.companion.model.Launcher;
import lk.ac.mrt.cse.companion.service.BackgroundService;
import lk.ac.mrt.cse.companion.util.ContextBundler;
import lk.ac.mrt.cse.companion.util.DataHandler;

public class CompanionActivity extends AppCompatActivity implements OnAppLaunchListener {

    private static Timer timer;
    private ChatHeadContainer chatContainer;
    private BackgroundService backgroundService;
    boolean bound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_companion);

        chatContainer = (ChatHeadContainer) findViewById(R.id.chat_head_container);
        chatContainer.setViewAdapter(new ChatHeadViewAdapter<String>() {
            @Override
            public FragmentManager getFragmentManager() {
                return getSupportFragmentManager();
            }

            @Override
            public Fragment instantiateFragment(String key, ChatHead chatHead) {
                // return the fragment which should be shown when the arrangment switches to maximized (on clicking a chat head)
                // you can use the key parameter to get back the object you passed in the addChatHead method.
                // this key should be used to decide which fragment to show.
                if (Constants.CONTEXT_ANY.equals(key) || Constants.LAUNCHER_CONTEXTS.contains(key)) {
                    LaunchersFragment launchersFragment = new LaunchersFragment();
                    launchersFragment.setLaunchListener(CompanionActivity.this);
                    launchersFragment.setType(key);
                    launchersFragment.setBaseContext(getServiceBaseContext(key));
                    return launchersFragment;
                } else {
                    //returns empty fragment
                    return new Fragment();
                }
            }

            @Override
            public Drawable getChatHeadDrawable(String key) {
                // this is where you return a drawable for the chat head itself based on the key. Typically you return a circular shape
                // you may want to checkout circular image library https://github.com/flipkart-incubator/circular-image
                int resource = R.mipmap.ic_launcher;
                switch (key) {
                    case Constants.CONTEXT_ANY:
                        resource = R.drawable.chat_head_icon_any;
                        break;
                    case Constants.CONTEXT_ACTIVITY:
                        resource = R.drawable.chat_head_icon_activity;
                        break;
                    case Constants.CONTEXT_HEADPHONE:
                        resource = R.drawable.chat_head_icon_headphone;
                        break;
                    case Constants.CONTEXT_LOCATION:
                        resource = R.drawable.chat_head_location;
                        break;
                    case Constants.CONTEXT_PLACES:
                        resource = R.drawable.chat_head_icon_places;
                        break;
                    case Constants.CONTEXT_WEATHER:
                        resource = R.drawable.chat_head_icon_weather;
                        break;
                    case Constants.CONTEXT_CALENDAR:
                        resource = R.drawable.chat_head_icon_calendar;
                        break;
                }
                return getResources().getDrawable(resource);
            }

            @Override
            public Drawable getPointerDrawable() {
                return null;
            }

            @Override
            public View getTitleView(String key, ChatHead chatHead) {
                return null;
            }
        });

        chatContainer.addChatHead(Constants.CONTEXT_ANY, true, true);

        chatContainer.setArrangement(MaximizedArrangement.class, null);
        chatContainer.setListener(new ChatHeadListener<String>() {
            @Override
            public void onChatHeadAdded(String key) {

            }

            @Override
            public void onChatHeadRemoved(String key, boolean userTriggered) {
                if (chatContainer.getChatHeads().size() == 0) {
                    finish();
                }
            }

            @Override
            public void onChatHeadArrangementChanged(ChatHeadArrangement oldArrangement, ChatHeadArrangement newArrangement) {

            }

            @Override
            public <T extends Serializable> void onChatHeadAnimateEnd(ChatHead<T> chatHead) {

            }

            @Override
            public <T extends Serializable> void onChatHeadAnimateStart(ChatHead chatHead) {

            }
        });

        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        timer.scheduleAtFixedRate(new UpdateChecker(), 100, Constants.ACTIVITY_REFRESH_INTERVAL);
        new UpdateChecker().run();
    }

    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            BackgroundService.LocalBinder binder = (BackgroundService.LocalBinder) service;
            CompanionActivity.this.backgroundService = binder.getService();
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            bound = false;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        Intent intent = new Intent(this, BackgroundService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (bound) {
            unbindService(connection);
            bound = false;
        }
    }

    @Override
    public void onAppLaunch(final Launcher launcher) {
        //save to database with current context and launcher.package
        if (backgroundService != null) {

            new AsyncTask<Launcher, Integer, Boolean>() {

                @Override
                protected Boolean doInBackground(Launcher... params) {
                    ContextBundler contextBundler = backgroundService.getContextBundler();
                    if (contextBundler != null) {
                        String app = launcher.getLaunchData();
                        for (Map.Entry<String, BaseContext> entry : contextBundler.getContextes().entrySet()) {
                            String type = entry.getKey();
                            if (Constants.LAUNCHER_CONTEXTS.contains(type)) {
                                BaseContext baseContext = entry.getValue();
                                if (baseContext != null && baseContext.getStates() != null) {
                                    List<String> states = new ArrayList<String>(baseContext.getStates());
                                    for (String state : states) {
                                        DataHandler.saveEvent(getApplicationContext(), new Event(app, type, state));
                                    }
                                }
                            }
                        }
                    }
                    return true;
                }

                @Override
                protected void onPostExecute(Boolean aBoolean) {
                    super.onPostExecute(aBoolean);
                    finish();
                }
            }.execute(launcher);

        }
    }

    private BaseContext getServiceBaseContext(String type) {
        if (backgroundService != null && backgroundService.getContextBundler() != null) {
            return backgroundService.getContextBundler().getContextes().get(type);
        }
        return null;
    }

    private class UpdateChecker extends TimerTask {

        @Override
        public void run() {
            if (backgroundService != null) {
                checkForUpdate(backgroundService.getContextBundler());
            }
        }
    }

    private void checkForUpdate(ContextBundler bundler) {
        Set<String> updatedTypes = bundler.getUpdatedTypes();
        if (updatedTypes.size() > 0) {
            HashSet<String> clone = new HashSet<>(updatedTypes);
            for (final String key : clone) {
                updatedTypes.remove(key);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        chatContainer.removeChatHead(key, false);
                        chatContainer.addChatHead(key, false, true);
                    }
                });

            }
        }

    }
}
