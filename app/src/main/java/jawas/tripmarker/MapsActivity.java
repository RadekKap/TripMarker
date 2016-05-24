package jawas.tripmarker;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
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
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
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
import jawas.tripmarker.listeners.OnQueyReceiveListener;
import jawas.tripmarker.pojos.LocationMarker;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerDragListener,
        GoogleApiClient.ConnectionCallbacks
{
    private GoogleMap mMap;
    private Menu menu;
    public static final String FRAGMENT_TAG = "addLocFragment";
    public static double lat, lng;
    private GoogleApiClient apiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Firebase.setAndroidContext(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        apiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
                .addApi(LocationServices.API).build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_actions, menu);
        this.menu = menu;
        SearchView search = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.search_location));
        search.setOnQueryTextListener(new OnQueyReceiveListener(this, mMap));
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
                FirebaseRef.getDbContext().child("markers").push().setValue(LocationPool.getLocationPool().getLocation(),
                        new Firebase.CompletionListener() {
                            @Override
                            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                if(firebaseError == null){
                                    LocationMarker location = LocationPool.getLocationPool().getLocation();
                                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                                    addMark(latLng, location.getTitle(), location.getDescription());
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
        loadMarkers();
    }

    public void loadMarkers(){
        FirebaseRef.getDbContext().child("markers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                LocationMarker marker;
                LatLng latLng;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    marker = snapshot.getValue(LocationMarker.class);
                    latLng = new LatLng(marker.getLatitude(), marker.getLongitude());
                    addMark(latLng, marker.getTitle(), marker.getDescription());
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    private Marker addMark(LatLng latLng, String title, String description){
        return mMap.addMarker(new MarkerOptions().position(latLng).title(title).snippet(description));
    }

    public void confirmLocDescr(View view){
        menu.findItem(R.id.confirm_position).setVisible(true);
        menu.findItem(R.id.cancel_position).setVisible(true);

        removeFragment();

        LatLng position = new LatLng(lat, lng);
        Marker marker = mMap.addMarker(new MarkerOptions().position(position)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(12.0F));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
        marker.setDraggable(true);

        String descr = ((EditText)findViewById(R.id.location_description)).getText().toString();
        String name = ((EditText)findViewById(R.id.location_name)).getText().toString();

        LocationPool.setLocationPool(marker, new LocationMarker(descr,
                marker.getPosition().latitude, marker.getPosition().longitude, name, UserId.getUID()));
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

    @Override
    public void onMarkerDragStart(Marker marker) {}

    @Override
    public void onMarkerDrag(Marker marker) {}

    @Override
    public void onMarkerDragEnd(Marker marker) {
        LocationPool.getLocationPool().setCurrentPosition( marker.getPosition().latitude, marker.getPosition().longitude);
    }

    @Override
    public void onConnected(Bundle bundle) {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
            Location location = LocationServices.FusedLocationApi.getLastLocation(apiClient);
            if(location != null){
                lat = location.getLatitude();
                lng = location.getLongitude();

                AdView banner = (AdView) findViewById(R.id.mapBanner);
                AdRequest request = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                        .addTestDevice("0D5BB761E332D5A158D5C5AABBB949A2")
                        .setLocation(location).build();
                banner.loadAd(request);
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {}

    protected void onStart() {
        apiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        apiClient.disconnect();
        super.onStop();
    }
}