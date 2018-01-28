package kg.gps.iceknight.awalkintheclouds.service;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

/**
 * Created by bborbuev on 1/27/18.
 */

public class MovingService extends Service {
    LocationManager locationManager;

    @Override
    public void onCreate() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        Location newLocation = GpsCoordService.calcNextCoord(MockService.getCurrentLocation(locationManager), 1000L);
        Location newLocation = new Location(LOCATION_SERVICE);
        newLocation.setLatitude(42.875991);
        newLocation.setLatitude(74.614490);
        MockService.mock(newLocation, locationManager);
//        MockService.resetMock(locationManager);
        return START_NOT_STICKY;
    }

    public void mockMoving() {

    }
}
