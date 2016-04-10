package fishman.lazyfisher;

import android.Manifest;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.List;
import java.util.Vector;

public class PointsLocationActivity extends FragmentActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener,
        OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationRequest mLocationRequest;
    private double  lat=0, lon=0;
    private Location mLastLocation;
    private Marker mLastMarker=null;
    private Vector<Marker> mSavedLocation= new Vector<>(3);

    private GoogleApiClient mGoogleApiClient = null;

    /*private GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(Location location) {
            LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.addMarker(new MarkerOptions().position(loc));
            if (mMap != null) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 16.0f));
            }
        }
    };*/

    @Override
    public void onClick(View v) {
        mSavedLocation.add(mMap.addMarker(new MarkerOptions().
                position(new LatLng(lat,lon)).
                draggable(false).
                title("Point " + String.valueOf(mSavedLocation.size()+1)).
                icon(BitmapDescriptorFactory.fromResource(R.drawable.down))));
    }
    synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();


    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        buildGoogleApiClient();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (lat == 0 && lon == 0
                ) {
            lat = location.getLatitude();
            lon = location.getLongitude();
        }
        else {
            lat +=0.100;
            lon +=0.100;
        }
        SetNewLocation(lat, lon);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        // Toast.makeText(mContext, "Connection suspended", Toast.LENGTH_LONG).show();
    }


    @Override
    public void onConnected(Bundle arg0) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(3000); // Update location every second

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);


        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            lat = mLastLocation.getLatitude();
            lon = mLastLocation.getLongitude();

        }
        SetNewLocation(lat,lon);
    }


    public void SetNewLocation(double lat, double lng) {
        LatLng myPosition = new LatLng(lat,lng);//(-34, 151);
        if(mLastMarker!=null)
            mLastMarker.remove();

        mLastMarker = mMap.addMarker(new MarkerOptions().
                position(myPosition).
                draggable(false).
                title("Your position").
                icon(BitmapDescriptorFactory.fromResource(R.drawable.boat)));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myPosition, 16.0f));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_points_location);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        buildGoogleApiClient();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    //GoogleMap.OnMyLocationChangeListener(){
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        /*if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);*/

        // Add a marker in Sydney and move the camera
        //double latitude = mMap.getMyLocation().getLatitude();
        //double langitude = mMap.getMyLocation().getLongitude();


        /*LatLng myPosition = new LatLng(latitude, langitude);
        LatLng sydney = new LatLng(0,0);//(-34, 151);
        //mMap.getMyLocation()
        mMap.addMarker(new MarkerOptions().position(myPosition).title("Your position"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myPosition, 16.0f));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        */
        //SetNewLocation(-34,151);
        //SetNewLocation(-36,151);
    }
}
