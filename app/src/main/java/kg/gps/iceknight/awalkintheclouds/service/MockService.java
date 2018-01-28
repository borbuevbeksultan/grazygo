package kg.gps.iceknight.awalkintheclouds.service;

import android.annotation.SuppressLint;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.SystemClock;

/**
 * Created by bborbuev on 1/27/18.
 */

public class MockService {

    @SuppressLint("NewApi")
    public static Location mock(Location newLocation, LocationManager locationManager) {
        Location location = new Location(LocationManager.GPS_PROVIDER);
        locationManager.addTestProvider(LocationManager.GPS_PROVIDER, false, false,
                false, false, true,
                true, true, 0, 1);
        location.setLatitude(newLocation.getLatitude());
        location.setLongitude(newLocation.getLongitude());
        location.setAccuracy(10);
        location.setTime(System.currentTimeMillis());
        location.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
        locationManager.setTestProviderEnabled(LocationManager.GPS_PROVIDER, true);
        locationManager.setTestProviderStatus(
                LocationManager.GPS_PROVIDER,
                LocationProvider.AVAILABLE,
                null,
                System.currentTimeMillis());
        locationManager.setTestProviderLocation(LocationManager.GPS_PROVIDER, location);

        return newLocation;
    }

    @SuppressLint("MissingPermission")
    public static void resetMock(LocationManager locationManager) {
        Location currentLocation;
        currentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        mock(currentLocation, locationManager);
        if (locationManager.getProvider(LocationManager.GPS_PROVIDER) != null) {
            locationManager.removeTestProvider(LocationManager.GPS_PROVIDER);
        }
    }

    @SuppressLint("MissingPermission")
    public static Location getCurrentLocation(LocationManager locationManager) {
        return locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
    }
}
