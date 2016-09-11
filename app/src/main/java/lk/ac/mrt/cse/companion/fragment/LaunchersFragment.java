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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.companion.R;
import lk.ac.mrt.cse.companion.adapter.IconAdapter;
import lk.ac.mrt.cse.companion.model.Launcher;

/**
 * Created by chamika on 9/11/16.
 */

public class LaunchersFragment extends Fragment {
    private final static String TAG = LaunchersFragment.class.getSimpleName();
    private GridView gridView;
    private IconAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        super.onCreateView(inflater, container, savedInstanceState)

        View view = inflater.inflate(R.layout.fragment_launcher, container, false);
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

    private void updateData() {


        AsyncTask<String, Integer, List> task = new AsyncTask<String, Integer, List>() {

            @Override
            protected List doInBackground(String... params) {
                List<Launcher> data = new ArrayList<>();

                final Intent main_intent = new Intent(Intent.ACTION_MAIN, null);
                main_intent.addCategory(Intent.CATEGORY_LAUNCHER);
                PackageManager packageManager = getContext().getPackageManager();
                List<ResolveInfo> infoList = packageManager.queryIntentActivities(main_intent, 0);

                for (ResolveInfo ri : infoList) {
                    String activityClass = ri.activityInfo.packageName;
                    Drawable drawable = ri.loadIcon(packageManager);
                    CharSequence title = ri.loadLabel(packageManager);
                    data.add(new Launcher((String) title, drawable, activityClass));
                }

                return data;
            }

            @Override
            protected void onPostExecute(List list) {
                super.onPostExecute(list);
                adapter.addAll(list);
                adapter.notifyDataSetChanged();
            }
        };
        task.execute();
    }
}
