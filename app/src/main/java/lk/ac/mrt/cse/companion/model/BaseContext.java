package lk.ac.mrt.cse.companion.model;

import java.util.List;

/**
 * Created by chamika on 9/13/2016.
 */

public abstract class BaseContext<T> {

    protected T data;

    /**
     * Measure the difference of the #data and new Data.
     * @param newData
     * @return 0 is equals and higher the value, increase the difference
     */
    public abstract int difference(T newData);

    /**
     * Minimum value of difference level for context change
     * @return
     */
    public abstract int minChangeLevel();

    /**
     * Get the states from the data
     * @return
     */
    public abstract List<String> getStates();

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
