package lk.ac.mrt.cse.companion.model;

/**
 * Created by chamika on 9/16/16.
 */

public class Event {

    private String app;
    private String type;
    private String state;
    private int hitCount;

    public Event(String app, String type, String state) {
        this.app = app;
        this.type = type;
        this.state = state;
    }

    public void setHitCount(int hitCount) {
        this.hitCount = hitCount;
    }

    public String getApp() {
        return app;
    }

    public String getType() {
        return type;
    }

    public String getState() {
        return state;
    }

    public int getHitCount() {
        return hitCount;
    }

    @Override
    public String toString() {
        return "Event{" +
                "app='" + app + '\'' +
                ", type='" + type + '\'' +
                ", state='" + state + '\'' +
                ", hitCount=" + hitCount +
                '}';
    }
}
