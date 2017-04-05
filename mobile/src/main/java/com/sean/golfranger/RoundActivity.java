package com.sean.golfranger;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.sean.golfranger.utils.SharedPrefUtils;

/**
 * Main Activity when round is being played. This activity will hold 3 tabs
 * Scorecard
 * Hole Detail
 * Map
 */
public class RoundActivity extends FragmentActivity
      implements OnMapReadyCallback {
    public static final String FRAG_VISIBILITY_STATE = "fragVisibilityState";
    public static final int MAP_STATE = 2;
    public static final int HOLE_STATE = 1;
    public static final int SCORECARD_STATE = 0;

//    Button mScorecardViewButton, mHoleViewButton, mMapViewButton;
    FrameLayout mFragmentContainer;
    View mMarkerStats;
    MapFragment mMapFragment;
    Fragment mScorecardFragment, mHoleFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_round);

        mFragmentContainer = (FrameLayout) findViewById(R.id.fragment_container);

        mMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mHoleFragment = getFragmentManager().findFragmentById(R.id.holeFrag);
        mScorecardFragment = getFragmentManager().findFragmentById(R.id.scorecardFrag);
        mMarkerStats = findViewById(R.id.markerStats);

        if (savedInstanceState == null) {
            setFragmentViewState(SCORECARD_STATE);
        }
        mMapFragment.getMapAsync(this);
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        LatLng coord = new LatLng(36.17, -115.14);

        CameraPosition cameraPosition = new CameraPosition.Builder()
              .target(coord)      // Sets the center of the map to location user
              .zoom(16.3f)
              .build();

        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
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

    private void setFragmentViewState(int fragViewState) {
        FragmentManager fm = getFragmentManager();
        switch (fragViewState) {
            case SCORECARD_STATE:
                fm.beginTransaction()
                      //.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                      .hide(mHoleFragment)
                      .show(mScorecardFragment)
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
                      .hide(mHoleFragment)
                      .hide(mScorecardFragment)
                      .show(mMapFragment)
                      .commit();
                SharedPrefUtils.setIsOnMapScreen(getApplicationContext(), false);
                mMarkerStats.setVisibility(View.VISIBLE);
                break;
        }
    }
}
