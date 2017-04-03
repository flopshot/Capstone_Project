package com.sean.golfranger;

import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
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
public class RoundActivity extends AppCompatActivity
      implements OnMapReadyCallback
{

    Button mScorecardViewButton, mHoleViewButton, mMapViewButton;
    FrameLayout mFragmentContainer;
    View mMarkerStats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_round);

        Timber.d("Test");
        mFragmentContainer = (FrameLayout) findViewById(R.id.fragment_container);
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMarkerStats = inflater.inflate(R.layout.marker_stats, null);

        mScorecardViewButton = (Button) findViewById(R.id.scorecardView);
        mHoleViewButton = (Button) findViewById(R.id.holeView);
        mMapViewButton = (Button) findViewById(R.id.mapView);
    }

    public void getScorecardView(View view) {

    }

    public void getHoleView(View view) {

    }

    public void getMapView(View view) {
        getMap();
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mFragmentContainer.removeView(mMarkerStats);
        mFragmentContainer.addView(mMarkerStats);
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
        if (SharedPrefUtils.isOnMapScreen(getApplicationContext())) {
            Timber.d("Getting Map");
            getMap();
        } else {
            Timber.d("Map is Off");
        }
        super.onResume();
    }

    public void getMap() {
        MapFragment mapFragment = GolfMapFragment.newInstance();
        FragmentTransaction fragmentTransaction =
              getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, mapFragment);
        fragmentTransaction.commit();
        mapFragment.getMapAsync(this);
        SharedPrefUtils.setIsOnMapScreen(getApplicationContext(), true);
    }

    @Override
    protected void onDestroy() {
        if (isFinishing()) {
            SharedPrefUtils.setIsOnMapScreen(getApplicationContext(), false);
        }
        super.onDestroy();
    }
}
