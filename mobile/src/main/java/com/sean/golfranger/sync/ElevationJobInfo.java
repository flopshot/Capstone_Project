package com.sean.golfranger.sync;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;

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
import java.util.Set;

/**
 * Job Service to get Elevation metrics of markers
 * every X minutes, depending on the job build schedule
 */

// TODO: call cancelAll() on process destroyed
// jobScheduler = (JobScheduler)this.getSystemService(Context.JOB_SCHEDULER_SERVICE );
// jobScheduler.cancelAll();
public class ElevationJobInfo {
    //Job Scheduling constants. Get elevation data from api every
    private static final int PERIOD = 6000; //Every 6sec
    private static final int INITIAL_BACKOFF = 1000;
    private static final int PERIODIC_ID = 1;
    private static final int MIN_LATENCY = 500;
    private static final int MAX_WAIT = 900;
    private static final int CONNECTION_TIMEOUT = 10000;

    static void getElevation(Context context) {

        //If there is no network connectivity, return the appropriate message and end task
        if(!NetworkUtils.isNetworkAvailable(context)) {
            //TODO: Account for no network connection
            return ;
        }

        int establishedHashesCount = SharedPrefUtils.getEstablishedMarkerHashes(context).size();
        Set<String> pendingHashes = SharedPrefUtils.getPendingMarkerHashes(context);

        // If there are no pending and no established marker hashes, then end task
        if(establishedHashesCount == 0 & pendingHashes.size() == 0) {
            return;
        }
        //Get User LatLon from Shared Prefs to make Elevation API call and save elevation in SP
        Float[] userLatLon = SharedPrefUtils.getUserLatLonFloat(context);
        String userRequestQuery = ApiContract.buildRequestUrl(userLatLon);
        String jsonUserElevationData = apiCallOverHTTP(userRequestQuery);
        String userElevationString = ApiJsonParser.getElevation(jsonUserElevationData);
        if (userElevationString != null) {
            SharedPrefUtils.setUserElevation(context, Float.valueOf(userElevationString));
        }

        // If there are no pending marker hashes, then end task
        if(pendingHashes.size() == 0) {
            return;
        }

        for (String hash : pendingHashes) {
            //Iterate over marker hashes, pull latlon data from shared prefs for each marker hash,
            //make api call for marker elevation, send broadcast to map fragment on successful
            //api call. It is Map Fragment's job to update marker hash's status in appropriate SP
            Float[] pendingMarkerLatLon = SharedPrefUtils.getPendingMarkerLatLon(context, hash);
            String markerRequestQuery = ApiContract.buildRequestUrl(pendingMarkerLatLon);
            String jsonMarkerElevationData = apiCallOverHTTP(markerRequestQuery);
            String markerElevationString = ApiJsonParser.getElevation(jsonMarkerElevationData);
            if (markerElevationString != null) {
                //TODO: Send Broadcast
            }
        }
    }


    public static synchronized void initialize(final Context context) {
        schedulePeriodic(context);
    }

    private static void schedulePeriodic(Context context) {
        JobInfo.Builder builder = new JobInfo
              .Builder(PERIODIC_ID, new ComponentName(context, ElevationJobService.class));

        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
              .setPeriodic(PERIOD)
              .setBackoffCriteria(INITIAL_BACKOFF, JobInfo.BACKOFF_POLICY_EXPONENTIAL)
              .setMinimumLatency(MIN_LATENCY)
              .setOverrideDeadline(MAX_WAIT);

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
            urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
            urlConnection.setReadTimeout(CONNECTION_TIMEOUT);
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
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
                urlConnection.disconnect();
                reader.close();
                // Stream was empty.  No point in parsing.
                return null;
            }
            return buffer.toString(); //Return JSON String of elevation for LatLon
        } catch (IOException e) {
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
