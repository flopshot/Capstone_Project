package com.sean.golfranger;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.sean.golfranger.utils.SharedPrefUtils;

import timber.log.Timber;

/**
 * Main Activity when round is being played. This activity will hold 3 tabs
 * Scorecard
 * Hole Detail
 * Map
 */
public class RoundActivity extends FragmentActivity
      implements OnMapReadyCallback {

//    Button mScorecardViewButton, mHoleViewButton, mMapViewButton;
    FrameLayout mFragmentContainer;
    View mMarkerStats;
    MapFragment mMapFragment;
    Fragment mScorecardFragment, mHoleFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_round);

        Timber.d("Test");
        mFragmentContainer = (FrameLayout) findViewById(R.id.fragment_container);
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMarkerStats = inflater.inflate(R.layout.marker_stats, null);

        mMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mHoleFragment = getFragmentManager().findFragmentById(R.id.holeFrag);
        mScorecardFragment = getFragmentManager().findFragmentById(R.id.scorecardFrag);
        mMapFragment.getMapAsync(this);
    }

    public void getScorecardView(View view) {
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction()
              .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
              .hide(mHoleFragment)
              .show(mScorecardFragment)
              .hide(mMapFragment)
              .commit();
        SharedPrefUtils.setIsOnMapScreen(getApplicationContext(), false);
    }

    public void getHoleView(View view) {
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction()
              .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
              .show(mHoleFragment)
              .hide(mScorecardFragment)
              .hide(mMapFragment)
              .commit();
        SharedPrefUtils.setIsOnMapScreen(getApplicationContext(), false);
    }

    public void getMapView(View view) {
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction()
              .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
              .hide(mHoleFragment)
              .hide(mScorecardFragment)
              .show(mMapFragment)
              .commit();
        SharedPrefUtils.setIsOnMapScreen(getApplicationContext(), false);
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
//        mFragmentContainer.removeView(mMarkerStats);
//        mFragmentContainer.addView(mMarkerStats);
        googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        LatLng coord = new LatLng(36.17, -115.14);

        CameraPosition cameraPosition = new CameraPosition.Builder()
              .target(coord)      // Sets the center of the map to location user
              .zoom(16.3f)                   // Sets the zoom
              .build();

        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @Override
    protected void onResume() {
        if (SharedPrefUtils.restartMapFromRotation(getApplicationContext())) {
            Timber.d("Getting Map");
            getMap();
        } else {
            Timber.d("Map is Off");
        }
        super.onResume();
    }

    public void getMap() {
        MapFragment mapFragment = MapFragment.newInstance();
        FragmentTransaction fragmentTransaction =
              getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, mapFragment);
        fragmentTransaction.commit();
        mapFragment.getMapAsync(this);
        SharedPrefUtils.setIsOnMapScreen(getApplicationContext(), true);
    }


    @Override
    protected void onDestroy() {
        if (!isFinishing() & SharedPrefUtils.isOnMapScreen(getApplicationContext())) {
            SharedPrefUtils.setRestartMapFromRotation(getApplicationContext(), true);
        } else {
            SharedPrefUtils.setRestartMapFromRotation(getApplicationContext(), false);
        }
        super.onDestroy();
    }



}
