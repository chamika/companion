package lk.ac.mrt.cse.companion.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import lk.ac.mrt.cse.companion.model.Launcher;

/**
 * Created by chamika on 9/11/16.
 */

public class IconAdapter extends ArrayAdapter<Launcher> {

    private int resourceId;
    private int resourceIdText;
    private int resourceIdImage;

    public IconAdapter(Context context, int resource, int resourceIdText, int resourceIdImage) {
        super(context, resource, resourceIdText);
        this.resourceId = resource;
        this.resourceIdText = resourceIdText;
        this.resourceIdImage = resourceIdImage;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
        }

        TextView textView = (TextView) convertView.findViewById(resourceIdText);
        ImageView imageView = (ImageView) convertView.findViewById(resourceIdImage);

        Launcher item = getItem(position);
        if (item != null) {
            textView.setText(item.getTitle());
            imageView.setImageDrawable(item.getIcon());
        }


        return convertView;
    }
}
