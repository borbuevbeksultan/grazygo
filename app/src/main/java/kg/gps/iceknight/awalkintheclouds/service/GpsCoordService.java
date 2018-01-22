package kg.gps.iceknight.awalkintheclouds.service;

import android.location.Location;
import android.location.LocationManager;

/**
 * Created by bborbuev on 1/20/18.
 */

public class GpsCoordService {

    public static Location calcNextCoord(Location location, Long offset) {
        Double latitude = location.getLatitude();
        Double longitute = location.getLongitude();

        Double delta = offset / 13.7D;

        latitude += (delta/10000D);
        longitute += (delta/10000D);

        Location convertedLocation = new Location(LocationManager.GPS_PROVIDER);

        convertedLocation.setLatitude(latitude);
        convertedLocation.setLongitude(longitute);

        return convertedLocation;
    }

    public static void imitateMoving(LocationManager locationManager, Location from, Location to, Long speed) {
        Double deltaLatitude = 0D;
        deltaLatitude = (from.getLatitude() > to.getLatitude()) ?
                from.getLatitude() - to.getLatitude() : to.getLatitude() - from.getLatitude();

        Double deltaLongitude = 0D;
        deltaLongitude = (from.getLongitude() > to.getLongitude()) ?
                from.getLongitude() - to.getLongitude() : to.getLongitude() - from.getLongitude();


    }
}
