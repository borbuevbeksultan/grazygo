package kg.kg.iceknight.awalkintheclouds.core;

import android.app.Service;
import android.location.Location;

/**
 * Created by Iceknight on 28.01.2018.
 */

public class Distance {

    private Location from;
    private Location to;

    public Distance() { }

    public Distance(Double latFrom, Double lonFrom, Double latTo, Double lonTo) {
        Location from = new Location(Service.LOCATION_SERVICE);
        Location to = new Location(Service.LOCATION_SERVICE);
        from.setLatitude(latFrom);
        from.setLongitude(lonFrom);
        to.setLatitude(latTo);
        to.setLongitude(lonTo);
    }
}
