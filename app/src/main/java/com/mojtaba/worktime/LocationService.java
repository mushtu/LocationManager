package com.mojtaba.worktime;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;


public class LocationService extends Service{

    private static final int TWO_MINUTES = 1000 * 60 * 2;
    static final public String LOCATION_RESULT = "com.moj.location.LOCATION_SERVICE_RESULT";
    static final public String LOCATION_MESSAGE = "com.moj.location.LOCATION_SERVICE_MESSAGE";

    private boolean isStarted;
	private LocationManager mlocManager;
    private Location currentBestLocation ;
    private LocalBroadcastManager broadcaster;


	private LocationListener listener = new LocationListener() {
		@Override
		public void onLocationChanged(Location location) {
            if(isBetterLocation(location,currentBestLocation))
            {
                currentBestLocation = location;
                sendLocation();
            }
		}

		@Override
		public void onStatusChanged(String s, int i, Bundle bundle) {

		}

		@Override
		public void onProviderEnabled(String s) {

		}

		@Override
		public void onProviderDisabled(String s) {

		}
	};

    @Override
    public void onCreate() {
        Log.i(getClass().getSimpleName(),"in on create!");
        isStarted =false;
        mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        broadcaster = LocalBroadcastManager.getInstance(this);
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        if (mlocManager != null) {
            mlocManager.removeUpdates(listener);
        }
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(!isStarted)
        {
            mlocManager.requestLocationUpdates( "gps", 0, 0, listener);
            mlocManager.requestLocationUpdates( "network", 0, 0, listener);
            currentBestLocation= mlocManager.getLastKnownLocation("gps");
            if(currentBestLocation == null)
                currentBestLocation = mlocManager.getLastKnownLocation("network");
            sendLocation();
            isStarted = true;
            Log.i(getClass().getSimpleName(),"Location Service started!");
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }




    /** Determines whether one Location reading is better than the current Location fix
     * @param location  The new Location that you want to evaluate
     * @param currentBestLocation  The current Location fix, to which you want to compare the new one
     */
    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }



    public void sendLocation() {
        Log.i(getClass().getSimpleName(),"Sending Location...");
        Intent intent = new Intent(LOCATION_RESULT);
        if(currentBestLocation != null)
        {

            intent.putExtra(LOCATION_MESSAGE,currentBestLocation);
        }else{
            Log.i(getClass().getSimpleName(),"Location is null!");
        }

        broadcaster.sendBroadcast(intent);
    }
}