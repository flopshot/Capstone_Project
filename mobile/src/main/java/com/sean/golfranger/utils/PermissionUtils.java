package com.sean.golfranger.utils;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

import com.sean.golfranger.HomeActivity;

/**
 * Helper Methods to enable permission logic. If location permission is not granted, send user to
 * home screen and to grant permission, then send them back to where the user was.
 */

public class PermissionUtils {
    private static final String PERMISSION_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String FINISH_EXTRA = "PermissionUtilsFinishExtra";

    public static void checkLocationPermission(Context context) {
        if (ActivityCompat.checkSelfPermission(context, PERMISSION_LOCATION)
              != PackageManager.PERMISSION_GRANTED) {
            // Location permission has not been granted.
            Intent intent = new Intent(context, HomeActivity.class);
            intent.putExtra(FINISH_EXTRA, true);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
        }

    }
}
