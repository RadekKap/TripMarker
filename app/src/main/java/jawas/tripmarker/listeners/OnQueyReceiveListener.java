package jawas.tripmarker.listeners;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.widget.SearchView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;

public class OnQueyReceiveListener implements SearchView.OnQueryTextListener {

    Activity activity;
    GoogleMap map;

    public OnQueyReceiveListener(Activity activity, GoogleMap map){
        this.activity = activity;
        this.map = map;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (query != null || !query.equals("")) {
            Address address = null;
            Geocoder geocoder = new Geocoder(activity);
            try {
                address = geocoder.getFromLocationName(query, 1).get(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            map.moveCamera(CameraUpdateFactory.zoomTo(13.0F));
            map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
