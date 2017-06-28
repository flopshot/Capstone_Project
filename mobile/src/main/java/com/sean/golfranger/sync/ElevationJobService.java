package com.sean.golfranger.sync;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;

import timber.log.Timber;

/**
 * Holds the Job schedule logic and execution logic to get the elevation data through USGS API
 */
public class ElevationJobService extends JobService{
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Timber.d("Elevation Job Started");
        Intent intent = new Intent(getApplicationContext(), ElevationIntentService.class);
        getApplicationContext().startService(intent);
        jobFinished(jobParameters, false);
        Timber.d("Elevation Job Ended OK");
        ElevationJobInfo.initialize(getApplicationContext());
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Timber.d("Elevation Job Ended UNEXPECTEDLY");
        ElevationJobInfo.initialize(getApplicationContext());
        return false;
    }
}
