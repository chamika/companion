package lk.ac.mrt.cse.companion.model;

import com.google.android.gms.common.api.Result;

/**
 * Created by chamika on 9/13/2016.
 */

public abstract class BaseContext<T> {

    protected T oldData;

    /**
     * Measure the difference of the #oldData and new Data.
     * @param newData
     * @return 0 is equals and higher the value, increase the difference
     */
    public abstract int difference(T newData);

    public T getOldData() {
        return oldData;
    }

    public void setOldData(T oldData) {
        this.oldData = oldData;
    }
}
