package jawas.tripmarker.helpers;

import com.google.android.gms.maps.model.Marker;

import jawas.tripmarker.pojos.LocationMarker;

public class LocationPool
{
    private static LocationPool pool = new LocationPool();
    private Marker marker;
    private LocationMarker location;

    private LocationPool(){}

    public LocationMarker getLocation() {
        return location;
    }

    public void setLocation(LocationMarker location) {
        this.location = location;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public static void setLocationPool(Marker markerArg, LocationMarker locationArg){
        pool.setLocation(locationArg);
        pool.setMarker(markerArg);
    }

    public void setCurrentPosition(double latitude, double longitude){
        location.setLatitude(latitude);
        location.setLongtitude(longitude);
    }

    public static LocationPool getLocationPool(){
        return pool;
    }
}
