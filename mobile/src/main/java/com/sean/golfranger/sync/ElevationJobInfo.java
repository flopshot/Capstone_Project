package com.sean.golfranger.sync;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.sean.golfranger.sync.elevationapi.ApiContract;
import com.sean.golfranger.sync.elevationapi.ApiJsonParser;
import com.sean.golfranger.utils.NetworkUtils;
import com.sean.golfranger.utils.SharedPrefUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import timber.log.Timber;

/**
 * Job Service to get Elevation metrics of markers
 * every X minutes, depending on the job build schedule
 */

public class ElevationJobInfo {
    //Job Scheduling constants. Get elevation data from api every
    private static final int PERIOD = 2000; //Every 5sec
    private static final int INITIAL_BACKOFF = 1000;
    private static final int PERIODIC_ID = 1;

    private static final String ACTION_ELEVATION_RETRIEVED = "com.sean.golfranger.ACTION_ELEVATION_RETRIEVED";
    private static final String EXTRA_ELEVATION_HASH = "com.sean.golfranger.EXTRA_ELEVATION_HASH";

    static void getElevation(Context context) {

        //If there is no network connectivity, return the appropriate message and end task
        if(!NetworkUtils.isNetworkAvailable(context)) {
            Timber.d("Elevation Task Ended: NO CONNECTION");
            return ;
        }

        int establishedHashesCount = SharedPrefUtils.getEstablishedMarkerHashes(context).size();
        Set<String> pendingHashes = new HashSet<>(SharedPrefUtils.getPendingMarkerHashes(context));

        Timber.d("Established Hash Size: " + String.valueOf(establishedHashesCount) + " Pending Hash Size: " + String.valueOf(pendingHashes.size()));
        // If there are no pending and no established marker hashes, then end task
        if(establishedHashesCount == 0 & pendingHashes.size() == 0) {
            Timber.d("no pending and no established marker hashes, end getElevation()");
            return;
        }
        //Get User LatLon from Shared Prefs to make Elevation API call and save elevation in SP
        Double[] userLatLon = SharedPrefUtils.getUserLatLonDouble(context);
        String userRequestQuery = ApiContract.buildRequestUrl(userLatLon);
        String jsonUserElevationData = apiCallOverHTTP(userRequestQuery);
        String userElevationString = null;
        try {
              userElevationString= ApiJsonParser.getElevation(jsonUserElevationData);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        if (userElevationString != null) {

            SharedPrefUtils.setUserElevation(context, Float.valueOf(userElevationString));
        } else {
            Timber.d("JSON string returned from User Elevation NULL, storing in SP and continuing");
        }

        // If there are no pending marker hashes, then end task
        if(pendingHashes.size() == 0) {
            Timber.d("There are established, but no pending marker hashes, task done");
            return;
        }

        for (String hash : pendingHashes) {
            //Iterate over marker hashes, pull latlon data from shared prefs for each marker hash,
            //make api call for marker elevation, send broadcast to map fragment on successful
            //api call. It is Map Fragment's job to update marker hash's status in appropriate SP
            Double[] pendingMarkerLatLon = SharedPrefUtils.getPendingMarkerLatLon(context, hash);
            String markerRequestQuery = ApiContract.buildRequestUrl(pendingMarkerLatLon);
            Timber.d("Elevation URL: "+markerRequestQuery);
            String jsonMarkerElevationData = apiCallOverHTTP(markerRequestQuery);
            String markerElevationString = ApiJsonParser.getElevation(jsonMarkerElevationData);
            if (markerElevationString != null) {
                //Broadcast Results to Map Fragment
                Intent elevationToMapBroadcast = new Intent(ACTION_ELEVATION_RETRIEVED);
                elevationToMapBroadcast
                      .putExtra(EXTRA_ELEVATION_HASH, new String[]{hash,markerElevationString});
                context.sendBroadcast(elevationToMapBroadcast);
                Timber.d("Sent " + hash + " Elevation of " + markerElevationString + " to Map");

            } else {
                Timber.d("Elevation String was null for hash " + hash);
            }
        }
    }


    public static synchronized void initialize(final Context context) {
        schedulePeriodic(context);
    }

    private static synchronized void schedulePeriodic(Context context) {
        Timber.d("Scheduling Elevation Task");
        JobInfo.Builder builder = new JobInfo
              .Builder(PERIODIC_ID, new ComponentName(context, ElevationJobService.class));

        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
              .setPeriodic(PERIOD)
              .setBackoffCriteria(INITIAL_BACKOFF, JobInfo.BACKOFF_POLICY_EXPONENTIAL);

        JobScheduler scheduler = (JobScheduler) context
              .getSystemService(Context.JOB_SCHEDULER_SERVICE);

        scheduler.schedule(builder.build());
    }

    /**
     * Boiler Plate code to make API call over network using HTTP protocol
     */
    private static String apiCallOverHTTP(String requestUrl) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        try {
            URL url = new URL(requestUrl);

            // Create the request to Server, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
//            urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
//            urlConnection.setReadTimeout(CONNECTION_TIMEOUT);
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                Timber.d("Input Stream was Null, ending connection");
                // Nothing to do.
                urlConnection.disconnect();
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                Timber.d("Nothing was Read to the buffer string, ending connection");
                urlConnection.disconnect();
                reader.close();
                // Stream was empty.  No point in parsing.
                return null;
            }
            return buffer.toString(); //Return JSON String of elevation for LatLon
        } catch (IOException e) {
            e.printStackTrace();
            // If the code didn't successfully get the elevation data, there's no point in attempting
            // to parse it.
            return null;
        } finally{
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
