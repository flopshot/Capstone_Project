package com.sean.golfranger;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sean.golfranger.sync.ElevationJobInfo;
import com.sean.golfranger.sync.WindJobInfo;
import com.sean.golfranger.utils.GolfMarker;
import com.sean.golfranger.utils.PermissionUtils;
import com.sean.golfranger.utils.SharedPrefUtils;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Set;

import timber.log.Timber;

/**
 * Main Activity when round is being played. This activity will hold 3 tabs
 * Scorecard
 * Hole Detail
 * Map
 */
public class RoundActivity extends FragmentActivity
      implements OnMapReadyCallback,
      GoogleApiClient.ConnectionCallbacks,
      GoogleApiClient.OnConnectionFailedListener,
      LocationListener {

    public static final String FRAG_VISIBILITY_STATE = "fragVisibilityState";
    private static final String ACTION_WIND_UPDATED = "com.sean.golfranger.ACTION_WIND_UPDATED";
    private static final String ACTION_ELEVATION_RETRIEVED = "com.sean.golfranger.ACTION_ELEVATION_RETRIEVED";
    private static final String EXTRA_ELEVATION_HASH = "com.sean.golfranger.EXTRA_ELEVATION_HASH";
    public static final int MAP_STATE = 2;
    public static final int HOLE_STATE = 1;
    public static final int SCORECARD_STATE = 0;

//    Button mScorecardViewButton, mHoleViewButton, mMapViewButton;
    Boolean mLocationEnabled;
    FrameLayout mFragmentContainer;
    View mMarkerStats;
    MapFragment mMapFragment;
    Fragment mScorecardFragment, mHoleFragment;

    //Location member vaiables
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    GoogleMap gMap;
    Boolean mapReady;
    HashMap<String, GolfMarker> golfMarkersMap = new HashMap<>();

    WindReceiver windReceiver = new WindReceiver();
    ElevationReceiver elevationReceiver = new ElevationReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_round);

        //Check Location Status
        LocationManager manager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            mLocationEnabled = false;
            Timber.d("Location DISABLED");
        } else {
            Timber.d("Location ENABLED");
            mLocationEnabled = true;
        }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
              .addApi(LocationServices.API)
              .addConnectionCallbacks(this)
              .addOnConnectionFailedListener(this)
              .build();

        mFragmentContainer = (FrameLayout) findViewById(R.id.fragment_container);
        mMarkerStats = findViewById(R.id.markerStats);
        mMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mMapFragment.setRetainInstance(true);
        mHoleFragment = getFragmentManager().findFragmentById(R.id.holeFrag);
        mHoleFragment.setRetainInstance(true);
        mScorecardFragment = getFragmentManager().findFragmentById(R.id.scorecardFrag);
        mScorecardFragment.setRetainInstance(true);
        if (savedInstanceState == null) {
            setFragmentViewState(SCORECARD_STATE);
        }
        mMapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapReady = true;
        googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        gMap = googleMap;
        gMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                String hash = new BigInteger(35, new SecureRandom()).toString(32);
                Marker golferMarker = gMap.addMarker(new MarkerOptions()
                      .position(latLng));
                golferMarker.setTag(hash);
                Double[] markerLatLng = new Double[] {latLng.latitude, latLng.longitude};
                golfMarkersMap.put(hash, new GolfMarker(markerLatLng));

                SharedPrefUtils.setPendingMarkerLatLon(getApplicationContext(), hash, markerLatLng);
                SharedPrefUtils.addPendingMarkerHash(getApplicationContext(), hash);
            }
        });

        gMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                return false;
            }
        });

        gMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                Set<String> hashes = SharedPrefUtils.getEstablishedMarkerHashes(getApplicationContext());
                for (String hash : hashes) {
                    SharedPrefUtils.removeEstablishedMarkerHash(getApplicationContext(), hash);
                    Timber.d("Marker " + hash + "Removed");
                }
                gMap.clear();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (mMapFragment.isVisible()) {
            outState.putInt(FRAG_VISIBILITY_STATE, MAP_STATE);
        } else if (mHoleFragment.isVisible()){
            outState.putInt(FRAG_VISIBILITY_STATE, HOLE_STATE);
        } else {
            outState.putInt(FRAG_VISIBILITY_STATE, SCORECARD_STATE);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState != null) {
            setFragmentViewState(savedInstanceState.getInt(FRAG_VISIBILITY_STATE));
        }
    }

    @Override
    protected void onResume() {
        PermissionUtils.checkLocationPermission(getApplicationContext());
        super.onResume();
    }

    @Override
    protected void onStart() {
        IntentFilter windFilter = new IntentFilter();
        windFilter.addAction(ACTION_WIND_UPDATED);
        registerReceiver(windReceiver, windFilter);

        IntentFilter elevationFilter = new IntentFilter();
        elevationFilter.addAction(ACTION_ELEVATION_RETRIEVED);
        registerReceiver(elevationReceiver, elevationFilter);

        mGoogleApiClient.connect();
        WindJobInfo.initialize(getApplicationContext());
        ElevationJobInfo.initialize(getApplicationContext());
        super.onStart();
    }

    @Override
    protected void onStop() {
        JobScheduler jobScheduler
              = (JobScheduler)this.getSystemService(Context.JOB_SCHEDULER_SERVICE );
        jobScheduler.cancelAll();
        mGoogleApiClient.disconnect();

        unregisterReceiver(elevationReceiver);
        unregisterReceiver(windReceiver);
        super.onStop();
    }

    public void getScorecardView(View view) {
        setFragmentViewState(SCORECARD_STATE);
    }

    public void getHoleView(View view) {
        setFragmentViewState(HOLE_STATE);
    }

    public void getMapView(View view) {
        setFragmentViewState(MAP_STATE);
    }

    private void setFragmentViewState(int fragViewState) {
        FragmentManager fm = getFragmentManager();
        switch (fragViewState) {
            case SCORECARD_STATE:
                fm.beginTransaction()
                      //.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                      .show(mScorecardFragment)
                      .hide(mHoleFragment)
                      .hide(mMapFragment)
                      .commit();
                SharedPrefUtils.setIsOnMapScreen(getApplicationContext(), false);
                mMarkerStats.setVisibility(View.INVISIBLE);
                break;
            case HOLE_STATE:
                fm.beginTransaction()
                      //.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                      .show(mHoleFragment)
                      .hide(mScorecardFragment)
                      .hide(mMapFragment)
                      .commit();
                SharedPrefUtils.setIsOnMapScreen(getApplicationContext(), false);
                mMarkerStats.setVisibility(View.INVISIBLE);
                break;
            case MAP_STATE:
                fm.beginTransaction()
                      //.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                      .show(mMapFragment)
                      .hide(mHoleFragment)
                      .hide(mScorecardFragment)
                      .commit();
                SharedPrefUtils.setIsOnMapScreen(getApplicationContext(), false);
                mMarkerStats.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5000);

        if ( Build.VERSION.SDK_INT >= 23 &&
              ContextCompat.checkSelfPermission(
                    getApplicationContext(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION
              ) != PackageManager.PERMISSION_GRANTED
           )
        {
            Timber.i("Runtime Location Permission Disabled");
        } else {
            Timber.i("Runtime Location Permission Enabled");
            LocationServices
                  .FusedLocationApi
                  .requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Timber.v("Location Connection Suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Timber.v("Location Connection Init Failed");
    }

    @Override
    public void onLocationChanged(Location location) {
        Timber.d(location.toString());
        Timber.d("Meter Accuracy: " + String.valueOf(location.getAccuracy()));
        double lat = location.getLatitude();
        double lng = location.getLongitude();

        SharedPrefUtils.setUserLatLon(getApplicationContext(), lat, lng);

        CameraPosition cameraPosition = new CameraPosition.Builder()
              .target(new LatLng(lat, lng))      // Sets the center of the map to location user
              .zoom(16.9f)
              .build();

        if (mapReady) {
            gMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    private class WindReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Float dir = SharedPrefUtils.getCurrentWindDirection(context);
            String speed = SharedPrefUtils.getCurrentWindSpeed(context);

            Timber.d("Wind Receiver - Speed: "+ speed+ " Direction: "+String.valueOf(dir));
        }
    }

    private class ElevationReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String[] elevationHash = intent.getStringArrayExtra(EXTRA_ELEVATION_HASH);

            SharedPrefUtils.removePendingMarkerHash(context, elevationHash[0]);
            SharedPrefUtils.removePendingMarkerLatLon(context, elevationHash[0]);

            SharedPrefUtils.addEstablishedMarkerHash(context, elevationHash[0]);

            Timber.d("Elevation Receiver - Hash: " +elevationHash[0] + " Elevation: "+ elevationHash[1]);

            //TODO: when we set elevation, check if marker is still there
        }
    }


}
