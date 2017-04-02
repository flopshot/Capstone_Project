package com.sean.golfranger.sync;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;

/**
 * Holds the Job schedule logic and execution logic to get the elevation data through USGS API
 */
public class ElevationJobService extends JobService {

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Intent nowIntent = new Intent(getApplicationContext(), ElevationIntentService.class);
        getApplicationContext().startService(nowIntent);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
}
