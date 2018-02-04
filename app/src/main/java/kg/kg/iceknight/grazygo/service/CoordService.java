package kg.kg.iceknight.grazygo.service;

import android.location.Location;
import android.location.LocationManager;

/**
 * Created by bborbuev on 1/20/18.
 */

public class CoordService {

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

}
