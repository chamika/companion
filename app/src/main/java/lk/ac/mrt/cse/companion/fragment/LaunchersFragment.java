package lk.ac.mrt.cse.companion.fragment;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import lk.ac.mrt.cse.companion.Constants;
import lk.ac.mrt.cse.companion.R;
import lk.ac.mrt.cse.companion.adapter.IconAdapter;
import lk.ac.mrt.cse.companion.model.BaseContext;
import lk.ac.mrt.cse.companion.model.Launcher;
import lk.ac.mrt.cse.companion.util.DataHandler;

/**
 * Created by chamika on 9/11/16.
 */

public class LaunchersFragment extends Fragment {
    private final static String TAG = LaunchersFragment.class.getSimpleName();

    private static final Map<String, Launcher> lauchersMap = new HashMap<>();
    private static AtomicBoolean cacheLoaded = new AtomicBoolean();

    private GridView gridView;
    private IconAdapter adapter;
    private ImageView imageTitle;
    private TextView textStates;
    private OnAppLaunchListener launchListener;
    private BaseContext baseContext;
    private String type;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        super.onCreateView(inflater, container, savedInstanceState)

        View view = inflater.inflate(R.layout.fragment_launcher, container, false);
        imageTitle = (ImageView) view.findViewById(R.id.image_action);
        textStates = (TextView) view.findViewById(R.id.text_states);
        gridView = (GridView) view.findViewById(R.id.gridview);
        adapter = new IconAdapter(getContext(), R.layout.layout_icon_text, R.id.title, R.id.image);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Launcher item = adapter.getItem(position);
                if (item != null) {
                    try {
                        String packageName = item.getLaunchData();
                        Intent mIntent = getContext().getPackageManager().getLaunchIntentForPackage(packageName);
                        if (mIntent != null) {
                            if (mIntent.getComponent() != null) {
                                String mainActivity = mIntent.getComponent().getClassName();
                                Intent intent = new Intent(Intent.ACTION_MAIN);
                                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                                intent.setComponent(new ComponentName(packageName, mainActivity));
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                getContext().startActivity(intent);
                                if (launchListener != null) {
                                    launchListener.onAppLaunch(item);
                                }
                            }
                        } else {
                            Toast.makeText(getContext(), "Cannot start the activity", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Cannot start the activity", e);
                        Toast.makeText(getContext(), "Cannot start the activity", Toast.LENGTH_LONG).show();
                    }
                }

            }
        });

        updateData();

        return view;
    }

    public void setLaunchListener(OnAppLaunchListener launchListener) {
        this.launchListener = launchListener;
    }

    private void updateData() {

        if (baseContext != null) {
            textStates.setText(formatStates(baseContext.getStates()));
        } else if (Constants.CONTEXT_ANY.equals(type)) {
            textStates.setText("All Applications");
        }

        int resourceId = R.mipmap.ic_launcher;
        switch (type) {
            case Constants.CONTEXT_ANY:
                resourceId = R.drawable.chat_head_any;
                break;
            case Constants.CONTEXT_ACTIVITY:
                resourceId = R.drawable.chat_head_activity;
                break;
            case Constants.CONTEXT_HEADPHONE:
                resourceId = R.drawable.chat_head_headphone;
                break;
            case Constants.CONTEXT_LOCATION:
                resourceId = R.drawable.chat_head_location;
                break;
            case Constants.CONTEXT_PLACES:
                resourceId = R.drawable.chat_head_places;
                break;
            case Constants.CONTEXT_WEATHER:
                resourceId = R.drawable.chat_head_weather;
                break;
        }
        imageTitle.setImageResource(resourceId);

        AsyncTask<String, Integer, List> task = new AsyncTask<String, Integer, List>() {

            @Override
            protected List doInBackground(String... params) {
                initCache();

                List<Launcher> data = new ArrayList<>();

                if (type == null || Constants.CONTEXT_ANY.equals(type)) {
                    data.addAll(new ArrayList<Launcher>(lauchersMap.values()));
                } else {
                    List states = baseContext.getStates();
                    List<String> apps = DataHandler.loadApps(LaunchersFragment.this.getContext(), type, states);
                    if (apps != null) {
                        for (String app : apps) {
                            Launcher launcher = lauchersMap.get(app);
                            if (launcher != null) {
                                data.add(launcher);
                            }
                        }
                    }
                }

                sortLaunchersByName(data);
                return data;
            }

            @Override
            protected void onPostExecute(List list) {
                super.onPostExecute(list);
                updateGrid(list);
            }
        };
        task.execute();
    }

    private synchronized void initCache() {
        if (cacheLoaded.compareAndSet(false, true)) {
            final Intent main_intent = new Intent(Intent.ACTION_MAIN, null);
            main_intent.addCategory(Intent.CATEGORY_LAUNCHER);
            PackageManager packageManager = getContext().getPackageManager();
            List<ResolveInfo> infoList = packageManager.queryIntentActivities(main_intent, 0);

            for (ResolveInfo ri : infoList) {
                String activityClass = ri.activityInfo.packageName;
                Drawable drawable = ri.loadIcon(packageManager);
                CharSequence title = ri.loadLabel(packageManager);
                lauchersMap.put(activityClass, new Launcher((String) title, drawable, activityClass));
            }
        }
    }

    private void updateGrid(List list) {
        adapter.addAll(list);
        adapter.notifyDataSetChanged();
    }

    public void setBaseContext(BaseContext baseContext) {
        this.baseContext = baseContext;
    }

    public void setType(String type) {
        this.type = type;
    }

    private String formatStates(List<String> states) {
        if (states != null) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < states.size(); i++) {
                if (i != 0) {
                    sb.append("\n");
                }
                String state = states.get(i);
                sb.append(state.replace("_", " "));
            }
            return sb.toString();
        }
        return "";
    }

    private final Comparator<Launcher> NAME_COMPARATOR = new Comparator<Launcher>() {
        @Override
        public int compare(Launcher o1, Launcher o2) {
            return o1.getTitle().compareTo(o2.getTitle());
        }
    };

    private void sortLaunchersByName(List<Launcher> launchers) {
        Collections.sort(launchers, NAME_COMPARATOR);
    }
}
