package lk.ac.mrt.cse.companion.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.companion.R;
import lk.ac.mrt.cse.companion.adapter.IconAdapter;
import lk.ac.mrt.cse.companion.model.Launcher;

/**
 * Created by chamika on 9/11/16.
 */

public class LaunchersFragment extends Fragment {

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

        updateData();

        return view;
    }

    private void updateData() {
        List<Launcher> data = new ArrayList<>();
        data.add(new Launcher("App1", getResources().getDrawable(R.mipmap.ic_launcher)));
        data.add(new Launcher("App2", getResources().getDrawable(R.mipmap.ic_launcher)));
        data.add(new Launcher("App3", getResources().getDrawable(R.mipmap.ic_launcher)));
        data.add(new Launcher("App4", getResources().getDrawable(R.mipmap.ic_launcher)));

        adapter.addAll(data);
        adapter.notifyDataSetChanged();
    }
}
