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
import android.widget.ImageView;
import android.widget.TextView;

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
import java.util.HashSet;
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
    private static final String MAP_FIRST_CENTERED_KEY = "mapFirstCenteredState";
    private static final String MAP_MARKER_INFO_KEY = "maMarkerInfoKey";
    public static final int MAP_STATE = 2;
    public static final int HOLE_STATE = 1;
    public static final int SCORECARD_STATE = 0;
    public static final String CURRENT_MARKER_KEY = "CURRENTmARKERhaSHkEY";

    //    Button mScorecardViewButton, mHoleViewButton, mMapViewButton;
    Boolean mLocationEnabled;
    FrameLayout mFragmentContainer;
    View mMarkerStats;
    MapFragment mMapFragment;
    Fragment mScorecardFragment, mHoleFragment;
    TextView yardageView, windView, elevationView;
    String elevationPrefix, yardagePrefix, windPrefix, elevationDefault, yardageDefault, windDefault;
    ImageView windArrow;

    //Location member variables
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    GoogleMap gMap;
    Boolean mapReady;
    HashMap<String, GolfMarker> golfMarkersInfo = new HashMap<>();
    Boolean mapFirstCentered = false;
    String currentMarkerHash;

    WindReceiver windReceiver = new WindReceiver();
    ElevationReceiver elevationReceiver = new ElevationReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_round);
        elevationView = (TextView) findViewById(R.id.elevationView);
        yardageView = (TextView) findViewById(R.id.yardageView);
        windView = (TextView) findViewById(R.id.windView);
        yardagePrefix = getString(R.string.yardage_prefix);
        windPrefix = getString(R.string.wind_prefix);
        elevationPrefix = getString(R.string.elevation_prefix);
        yardageDefault = getString(R.string.yardage_default);
        windDefault = getString(R.string.wind_default);
        elevationDefault = getString(R.string.elevation_default);
        windArrow = (ImageView)findViewById(R.id.windArrow);

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
        googleMap.setMinZoomPreference(16.3f);
        gMap = googleMap;

        gMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                String hash = new BigInteger(35, new SecureRandom()).toString(32);
                Marker distanceMarker = gMap.addMarker(new MarkerOptions()
                      .position(latLng));
                distanceMarker.setTag(hash);

                Double[] markerLatLng = new Double[] {latLng.latitude, latLng.longitude};
                golfMarkersInfo.put(hash, new GolfMarker(markerLatLng));

                SharedPrefUtils.setPendingMarkerLatLon(getApplicationContext(), hash, markerLatLng);
                SharedPrefUtils.addPendingMarkerHash(getApplicationContext(), hash);

                currentMarkerHash = hash;
                setMarkerInfoView();
            }
        });

        gMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                currentMarkerHash = marker.getTag().toString();
                setMarkerInfoView();
                return false;
            }
        });

        //Remove All Markers and References to GolfMarker objects
        gMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                clearEstablishedMarkerHashes();
                gMap.clear();
                golfMarkersInfo = new HashMap<>();
                currentMarkerHash = null;
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

        outState.putString(CURRENT_MARKER_KEY, currentMarkerHash);
        outState.putBoolean(MAP_FIRST_CENTERED_KEY, mapFirstCentered);
        outState.putSerializable(MAP_MARKER_INFO_KEY, golfMarkersInfo);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState != null) {
            setFragmentViewState(savedInstanceState.getInt(FRAG_VISIBILITY_STATE));
            mapFirstCentered = savedInstanceState.getBoolean(MAP_FIRST_CENTERED_KEY);
            golfMarkersInfo = (HashMap<String, GolfMarker>) savedInstanceState.getSerializable(MAP_MARKER_INFO_KEY);
            currentMarkerHash = savedInstanceState.getString(CURRENT_MARKER_KEY);
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

    @Override
    protected void onDestroy() {
        if (isFinishing()) {
            clearEstablishedMarkerHashes();
        }
        super.onDestroy();
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
            gMap.setMyLocationEnabled(true);
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

        //On map first created, center over user
        if (!mapFirstCentered) {
            CameraPosition cameraPosition = new CameraPosition.Builder()
                  .target(new LatLng(lat, lng))      // Sets the center of the map to location user
                  .build();

            if (mapReady) {
                gMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
            mapFirstCentered = true;
        }

        //Update all established markers
        Set<String> establishedMarkers
              = SharedPrefUtils.getEstablishedMarkerHashes(getApplicationContext());

        for (String hash : establishedMarkers) {
            //Set Marker Distance from current user location
            double[] markerLatLng = golfMarkersInfo.get(hash).getLatLon();
            golfMarkersInfo.get(hash).setDistance(
                  distance(lat, lng, markerLatLng[0], markerLatLng[1]));
            //Set Marker Elevation Delta from current user elevation
            Double markerElevation = golfMarkersInfo.get(hash).getElevation();
            float userElevation = SharedPrefUtils.getUserElevation(getApplicationContext());
            golfMarkersInfo.get(hash).setElevationDelta(markerElevation - userElevation);
        }
        //Update Marker Info View with fresh data
        setMarkerInfoView();
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
            Timber.d("Elevation Receiver - Hash: " +elevationHash[0] + " Elevation: "+ elevationHash[1]);
            SharedPrefUtils.removePendingMarkerHash(context, elevationHash[0]);
            SharedPrefUtils.removePendingMarkerLatLon(context, elevationHash[0]);

            //Check if marker has not been cleared from map
            if (golfMarkersInfo != null) {
                SharedPrefUtils.addEstablishedMarkerHash(context, elevationHash[0]);
                golfMarkersInfo.get(elevationHash[0]).setElevation(Double.valueOf(elevationHash[1]));
            }
        }
    }

    /**
     * Distance Formula used to get level surface distance between two coordinates in yards
     */
    public double distance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of the earth

        Double latDistance = Math.toRadians(lat2 - lat1);
        Double lonDistance = Math.toRadians(lon2 - lon1);
        Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
              + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
              * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c * 1000 * 1.09361; // convert to meters, then yards
    }

    private String viewFormatter(Double metric) {
        return String.valueOf(Math.round(metric));
    }

    /**
     * This method is called in onLocationChanged to update marker info view with new user data
     * Will update the currently selected marker with info
     */
    public void setMarkerInfoView() {
        String yardageViewString, elevationViewString, windViewString;
        if (currentMarkerHash != null) {
            GolfMarker currentMarker = golfMarkersInfo.get(currentMarkerHash);
            Double curMarkerDistance = currentMarker.getDistance();
            Double curMarkerElevationDelta = currentMarker.getElevationDelta();
            if (curMarkerDistance != null) {
                yardageViewString = yardagePrefix + " " + viewFormatter(curMarkerDistance);
                yardageView.setText(yardageViewString);
            } else {
                yardageViewString = yardagePrefix + yardageDefault;
                yardageView.setText(yardageViewString);
            }
            if (curMarkerElevationDelta != null) {
                elevationViewString = elevationPrefix + " " + viewFormatter(curMarkerElevationDelta);
                elevationView.setText(elevationViewString);
                if (curMarkerElevationDelta <= -1) {
                    elevationView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_down_float, 0);
                } else if (curMarkerElevationDelta >= 1) {
                    elevationView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_up_float, 0);
                }
            } else {
                elevationViewString = elevationPrefix + elevationDefault;
                elevationView.setText(elevationViewString);
            }
        } else {
            elevationView.setText(elevationPrefix);
            elevationView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            yardageView.setText(yardagePrefix);
        }
        String curWindSpeed = SharedPrefUtils.getCurrentWindSpeed(getApplicationContext());
        Float curWindDir = SharedPrefUtils.getCurrentWindDirection(getApplicationContext());
        if (curWindSpeed != null) {
            windViewString = windPrefix + " " + viewFormatter(Double.valueOf(curWindSpeed));
            windView.setText(windViewString);
            if (curWindDir >= 0) {
                windArrow.setRotation(curWindDir + 90f);
                windArrow.setVisibility(View.VISIBLE);
            } else {
                windArrow.setVisibility(View.INVISIBLE);
            }
        } else {
            windView.setText(windPrefix);
            windArrow.setVisibility(View.INVISIBLE);
        }
    }

    private void clearEstablishedMarkerHashes() {
        Set<String> hashTemp =
              new HashSet<>(SharedPrefUtils.getEstablishedMarkerHashes(getApplicationContext()));
        for (String hash : hashTemp) {
            SharedPrefUtils.removeEstablishedMarkerHash(getApplicationContext(), hash);
            Timber.d("Marker " + hash + "Removed");
        }
    }
}
