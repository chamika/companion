package lk.ac.mrt.cse.companion.model;

import android.graphics.drawable.Drawable;

/**
 * Created by chamika on 9/11/16.
 */

public class Launcher {

    private String title;
    private Drawable icon;
    private String launchData;

    public Launcher(String title, Drawable icon, String launchData) {
        this.title = title;
        this.icon = icon;
        this.launchData = launchData;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getLaunchData() {
        return launchData;
    }
}
