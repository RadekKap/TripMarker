package jawas.tripmarker;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
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

import java.io.IOException;

import jawas.tripmarker.fragments.AddLocationFragment;
import jawas.tripmarker.helpers.FirebaseRef;
import jawas.tripmarker.helpers.LocationPool;
import jawas.tripmarker.helpers.UserId;
import jawas.tripmarker.pojos.LocationMarker;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerDragListener {

    private GoogleMap mMap;
    private Menu menu;
    public static final String FRAGMENT_TAG = "addLocFragment";
    public static double lat, lng;

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
        SearchView search = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.search_location));
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query != null || !query.equals("")) {
                    Address address = null;
                    Geocoder geocoder = new Geocoder(MapsActivity.this);
                    try {
                        address = geocoder.getFromLocationName(query, 1).get(0);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.zoomTo(13.0F));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.add_location:
                item.setEnabled(false);
                AddLocationFragment fragment = AddLocationFragment.newInstance();
                getSupportFragmentManager().beginTransaction().add(R.id.container, fragment, FRAGMENT_TAG).commit();
                LatLng latLng = getCurrentLocation();
                lat = latLng.latitude;
                lng = latLng.longitude;
                return true;
            case R.id.confirm_position:
                item.setVisible(false);
                menu.findItem(R.id.cancel_position).setVisible(false);
                FirebaseRef.getDbContext().child("markers").push().setValue(LocationPool.getLocationPool().getLocation(),
                        new Firebase.CompletionListener() {
                            @Override
                            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                if(firebaseError == null){
                                    LocationMarker location = LocationPool.getLocationPool().getLocation();
                                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                                    mMap.addMarker(new MarkerOptions().position(latLng)
                                            .title(location.getTitle()).snippet(location.getDescription()));
                                    removePoolMarker();
                                }
                                else{
                                    Toast.makeText(getApplicationContext(), "Failure", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                removePoolMarker();
                menu.findItem(R.id.add_location).setEnabled(true);
                menu.findItem(R.id.cancel_position).setVisible(false);
                return true;
            case R.id.cancel_position:
                removePoolMarker();
                item.setVisible(false);
                menu.findItem(R.id.add_location).setEnabled(true);
                menu.findItem(R.id.confirm_position).setVisible(false);
                return true;
            case R.id.search_location:
                return super.onOptionsItemSelected(item);
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
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//                == PackageManager.PERMISSION_GRANTED) {
//            mMap.setMyLocationEnabled(true);
//        }
        mMap.setOnMarkerDragListener(this);
        loadMarkers();
    }

    public void loadMarkers(){
        FirebaseRef.getDbContext().child("markers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                LocationMarker marker;
                LatLng latLng;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    marker = snapshot.getValue(LocationMarker.class);
                    latLng = new LatLng( marker.getLatitude(), marker.getLongitude() );
                    MapsActivity.this.mMap.addMarker(new MarkerOptions()
                            .position(latLng).title(marker.getTitle()).snippet(marker.getDescription()));
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {}
        });
    }

    public void confirmLocDescr(View view){
        menu.findItem(R.id.confirm_position).setVisible(true);
        menu.findItem(R.id.cancel_position).setVisible(true);

        removeFragment();

        LatLng position = new LatLng(lat, lng);
        Marker marker = mMap.addMarker(new MarkerOptions().position(position)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(10.0F));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
        marker.setDraggable(true);

        //*******
        String descr = ((EditText)findViewById(R.id.location_description)).getText().toString();
        String name = ((EditText)findViewById(R.id.location_name)).getText().toString();

        LocationPool.setLocationPool(marker, new LocationMarker(descr,
                marker.getPosition().latitude, marker.getPosition().longitude, name, UserId.getUID()));
        //********
    }

    private void removePoolMarker(){
        LocationPool.getLocationPool().getMarker().remove();
    }

    public void removeAddLocation(View view){
        removeFragment();
        menu.findItem(R.id.add_location).setEnabled(true);
    }

    private void removeFragment(){
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
        if(fragment != null)
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
    }

    private LatLng getCurrentLocation(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            String provider = locationManager.getBestProvider(criteria, true);
            Location location = locationManager.getLastKnownLocation(provider);

            if (location != null) {
                return new LatLng(location.getLatitude(), location.getLongitude());
            }
        }
        return null;
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