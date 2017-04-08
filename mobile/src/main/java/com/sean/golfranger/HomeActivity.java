package com.sean.golfranger;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import timber.log.Timber;

/**
 * Home Screen Activity displayed when app is first launched
 */
public class HomeActivity extends AppCompatActivity
    implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int PERMISSION_LOCATION_CODE = 101;
    private static final String PERMISSION_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    View mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mLayout = findViewById(R.id.main_layout);

        // Check if the Location permission is already available.
        // If Location permissions is already available, continue.
        if (ActivityCompat.checkSelfPermission(this, PERMISSION_LOCATION)
              != PackageManager.PERMISSION_GRANTED) {
            // Location permission has not been granted.
            requestLocationPermission();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void startNewRound(View v) {
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), StartRoundActivity.class);
        startActivity(intent);
    }

    public void viewOldRound(View v) {
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), MatchesActivity.class);
        startActivity(intent);
    }

    public void viewPlayers(View v) {
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), PlayerActivity.class);
        startActivity(intent);
    }

    public void viewCourses(View v) {
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), CoursesActivity.class);
        startActivity(intent);
    }

    /**
     * Requests the Location permission.
     * If the permission has been denied previously, a SnackBar will prompt the user to grant the
     * permission, otherwise it is requested directly.
     */
    private void requestLocationPermission() {
        Timber.i("Location permission has NOT been granted. Requesting permission.");

        // BEGIN_INCLUDE(location_permission_request)
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
              PERMISSION_LOCATION)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example if the user has previously denied the permission.
            Timber.i("Displaying location permission rationale to provide additional context.");
            Snackbar.make(mLayout, R.string.permission_location_rationale,
                  Snackbar.LENGTH_INDEFINITE)
                  .setAction(R.string.ok, new View.OnClickListener() {
                      @Override
                      public void onClick(View view) {
                          ActivityCompat.requestPermissions(HomeActivity.this,
                                new String[]{PERMISSION_LOCATION},
                                PERMISSION_LOCATION_CODE);
                      }
                  })
                  .setActionTextColor(Color.GREEN)
                  .show();
        } else {

            // Location permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(this, new String[]{PERMISSION_LOCATION},
                  PERMISSION_LOCATION_CODE);
        }
        // END_INCLUDE(location_permission_request)
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_LOCATION_CODE:
                if (grantResults.length > 0
                      && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    // permission was not granted
                    Timber.i("Displaying location permission rationale to provide additional context.");
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                          PERMISSION_LOCATION)) {
                        Snackbar.make(mLayout, R.string.permission_location_rationale,
                              Snackbar.LENGTH_INDEFINITE)
                              .setAction(R.string.ok, new View.OnClickListener() {
                                  @Override
                                  public void onClick(View view) {
                                      ActivityCompat.requestPermissions(HomeActivity.this,
                                            new String[]{PERMISSION_LOCATION},
                                            PERMISSION_LOCATION_CODE);
                                  }
                              })
                              .setActionTextColor(Color.GREEN)
                              .show();
                    } else {
                        Snackbar snackbar = Snackbar.make(mLayout,
                              getResources()
                                    .getString(R.string.permission_location_rationale),
                              Snackbar.LENGTH_INDEFINITE);
                        snackbar.setAction(getResources().getString(R.string.settings),
                              new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY
                                      |Intent.FLAG_ACTIVITY_NEW_TASK
                                      |Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);
                            }
                        })
                        .setActionTextColor(Color.GREEN);
                        snackbar.show();
                    }
                }
                break;
        }
    }
}
