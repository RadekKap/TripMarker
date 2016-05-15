package jawas.tripmarker;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import jawas.tripmarker.fragments.AddLocationFragment;
import jawas.tripmarker.helpers.FirebaseRef;
import jawas.tripmarker.helpers.LocationPool;
import jawas.tripmarker.helpers.UserId;
import jawas.tripmarker.pojos.Location;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerDragListener {

    private GoogleMap mMap;
    private Menu menu;
    public static final String FRAGMENT_TAG = "addLocFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        AdView banner = (AdView) findViewById(R.id.mapBanner);
        AdRequest request = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("0D5BB761E332D5A158D5C5AABBB949A2").build();
        banner.loadAd(request);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_actions, menu);
        this.menu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.add_location:
                item.setEnabled(false);
                AddLocationFragment fragment = AddLocationFragment.newInstance();
                getSupportFragmentManager().beginTransaction().add(R.id.container, fragment, FRAGMENT_TAG).commit();
                return true;
            case R.id.confirm_position:
                item.setVisible(false);
                FirebaseRef.getDbContext().child("markers").push().setValue(LocationPool.getLocationPool().getLocation());
                menu.findItem(R.id.add_location).setEnabled(true);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        UiSettings mapStructure = mMap.getUiSettings();
        mapStructure.setCompassEnabled(true);
        mapStructure.setZoomControlsEnabled(true);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }
        mMap.setOnMarkerDragListener(this);
    }

    public void confirmLocDescr(View view){
        menu.findItem(R.id.confirm_position).setVisible(true);

        removeFragment();

        LatLng position = new LatLng(-34, 151);
        Marker marker = mMap.addMarker(new MarkerOptions().position(position)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
        marker.setDraggable(true);

        //*******
        String descr = ((EditText)findViewById(R.id.location_description)).getText().toString();
        String name = ((EditText)findViewById(R.id.location_name)).getText().toString();

        LocationPool.setLocationPool(marker, new Location(descr,
                marker.getPosition().latitude, marker.getPosition().longitude, name, UserId.getUID()));
        //********
    }

    public void removeAddLocation(View view){
        removeFragment();
        menu.findItem(R.id.add_location).setEnabled(true);
    }

    public void removeFragment(){
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
        if(fragment != null)
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
    }

    @Override
    public void onMarkerDragStart(Marker marker) {}

    @Override
    public void onMarkerDrag(Marker marker) {}

    @Override
    public void onMarkerDragEnd(Marker marker) {
        LocationPool.getLocationPool().setCurrentPosition( marker.getPosition().latitude, marker.getPosition().longitude);
    }
}