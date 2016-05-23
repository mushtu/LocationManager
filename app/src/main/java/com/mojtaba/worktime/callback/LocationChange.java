package com.mojtaba.worktime.callback;

import android.location.Location;

/**
 * Created by mojtaba on 6/4/15.
 */
public interface LocationChange {
    void onLocationChanged(Location location);
}
