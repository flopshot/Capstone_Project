package com.sean.golfranger.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.HashSet;
import java.util.Set;

/**
 * Utility Class to easily access Shared Preference Stores
 */

public class SharedPrefUtils {
    private static final String KEY_USER_LAT = "SharedPrefUtilsUserLat";
    private static final String KEY_USER_LON = "SharedPrefUtilUserLon";
    private static final String KEY_ON_MAP = "SharedPrefUtilsOnMap";
    private static final String KEY_USER_ELEVATION = "SharedPrefUtilsUserElevation";
    private static final String ESTABLISHED_MARKER_HASHES_KEY = "SharedPrefUtilsEMH";
    private static final String PENDING_MARKER_HASHES_KEY = "SharedPrefUtilsPMH";
    private static final String KEY_LAT = "lat";
    private static final String KEY_LON = "lon";
    private static final String KEY_RESTART_MAP_FROM_ROTATION = "restartMapFromRotation";
    private static final String CURRENT_ROUNDID_KEY = "SharedPrefUtilsRoundIdCur";

    //Stores Current Lat Lon of Device
    public static void setUserLatLon(Context context, float lat, float lon) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putFloat(KEY_USER_LAT, lat);
        editor.putFloat(KEY_USER_LON, lon);
        editor.apply();
    }

    //Overloaded to allow for multiple decimal types
    public static void setUserLatLon(Context context, double latd, double lond) {
        Double latD = latd;
        float lat = latD.floatValue();
        Double lonD = lond;
        float lon = lonD.floatValue();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putFloat(KEY_USER_LAT, lat);
        editor.putFloat(KEY_USER_LON, lon);
        editor.apply();
    }

    /**
     * Retrieves currently stored lat lon on device in Double[] for YahooWeather class
     */
    public static Double[] getUserLatLonDouble(Context context) {
        Double[] curLatLonD = new Double[2];
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Double latD = Double.valueOf(prefs.getFloat(KEY_USER_LAT,0f));
        Double lonD = Double.valueOf(prefs.getFloat(KEY_USER_LON,0f));

        curLatLonD[0] = latD;
        curLatLonD[1] = lonD;

        return curLatLonD;
    }

    /**
     * Retrieves currently stored lat lon on device in Float[] of default float type
     */
    public static Float[] getUserLatLonFloat(Context context) {
        Float[] curLatLonF = new Float[2];
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        curLatLonF[0] = prefs.getFloat(KEY_USER_LAT,0f);
        curLatLonF[1] = prefs.getFloat(KEY_USER_LON,0f);

        return curLatLonF;
    }

    /**
     * Returns true if the map is resumed. False if the map is paused
     */
    public static boolean isOnMapScreen(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(KEY_ON_MAP, false);
    }

    /**
     * Set true if the map is resumed. False if the map is paused
     */
    public static void setIsOnMapScreen(Context context, boolean isOnMap) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_ON_MAP, isOnMap);
        editor.apply();
    }

    /**
     * This method must only be called by Elevation task
     * after successfully retrieving user elevation
     */
    public static void setUserElevation(Context context, float elevation) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putFloat(KEY_USER_ELEVATION, elevation);
        editor.apply();
    }

    /**
     * This method will be called by Calculation Class
     * to get user elevation stored after API call and
     * calculate relative elevation to markers
     */
    public static float getUserElevation(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getFloat(KEY_USER_ELEVATION, -20000f);
    }

    /**
     * This method will be called by Elevation task to check if established markers exist.
     * If none exist, and there re no markers pending, no api call will be made
     */
    public static Set<String> getEstablishedMarkerHashes(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getStringSet(ESTABLISHED_MARKER_HASHES_KEY, new HashSet<String>());
    }

    /**
     * This method will be called by Elevation task. These will hold the marker
     * keys that store the lat/lon's of the markers that are pending elevation api calls
     */
    public static Set<String> getPendingMarkerHashes(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getStringSet(PENDING_MARKER_HASHES_KEY, new HashSet<String>());
    }

    /**
     * After Marker Hash is written to Pending Marker hash set, map frag will then store a latlon
     * in SharedPrefs with hash corresponding to the marker hash
     */
    public static void setPendingMarkerLatLon(Context context, String hash, float[] latlon) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putFloat(hash+KEY_LAT, latlon[0]);
        editor.putFloat(hash+KEY_LON, latlon[1]);
        editor.apply();
    }

    /**
     * Elevation Task will use Hash set to find latlon stored in SharedPrefs using this method
     */
    public static Float[] getPendingMarkerLatLon(Context context, String hash) {
        Float[] latlon = new Float[2];
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        latlon[0] = prefs.getFloat(hash+KEY_LAT, 0f);
        latlon[1] = prefs.getFloat(hash+KEY_LON, 0f);
        return latlon;
    }

    /**
     * After the elevation has been successfully broadcast or the marker no longer exists,
     * the map fragment will remove its latlon store in shared prefs
     */
    public static void removePendingMarkerLocation(Context context, String hash) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(hash);
        editor.apply();
    }

    /**
     * Used only by Map Fragment to add to the list of pending marker hashes
     */
    public static void addPendingMarkerHash(Context context, String hash) {
        Set<String> pendingMarkerHashes = getPendingMarkerHashes(context);
        pendingMarkerHashes.add(hash);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putStringSet(PENDING_MARKER_HASHES_KEY, pendingMarkerHashes);
        editor.apply();
    }

    /**
     * Used only by Map Fragment to remove to the list of pending marker hashes after successful
     * Elevation broadcast or marker deletion
     */
    public static void removePendingMarkerHash(Context context, String hash) {
        Set<String> pendingMarkerHashes = getPendingMarkerHashes(context);
        pendingMarkerHashes.remove(hash);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putStringSet(PENDING_MARKER_HASHES_KEY, pendingMarkerHashes);
        editor.apply();
    }

    /**
     * Used only by Map Fragment to add to the list of established marker hashes
     */
    public static void addEstablishedMarkerHash(Context context, String hash) {
        Set<String> establishedMarkerHashes = getEstablishedMarkerHashes(context);
        establishedMarkerHashes.add(hash);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putStringSet(ESTABLISHED_MARKER_HASHES_KEY, establishedMarkerHashes);
        editor.apply();
    }

    /**
     * Used only by Map Fragment to remove to the list of established marker hashes after
     * marker deletion from map fragment.
     */
    public static void removeEstablishedMarkerHash(Context context, String hash) {
        Set<String> establishedMarkerHashes = getEstablishedMarkerHashes(context);
        establishedMarkerHashes.remove(hash);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putStringSet(ESTABLISHED_MARKER_HASHES_KEY, establishedMarkerHashes);
        editor.apply();
    }

    /**
     * Method will only be called when use presses the "Let's Play" Button in the Start Round
     * Activity. This will save the current round indefinitely; until user launches another round
     * to be played.
     */
    public static void setCurrentRoundId(Context context, String roundId) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(CURRENT_ROUNDID_KEY, roundId);
        editor.apply();
    }

    /**
     * Getter Method for getting Current RoundId
     */
    public static String getCurrentRoundId(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(CURRENT_ROUNDID_KEY, String.valueOf(0));
    }
}
