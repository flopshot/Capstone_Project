package com.sean.golfranger.sync;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;

import timber.log.Timber;

/**
 * Holds the Job schedule logic and execution logic. Mainly, to get the wind data through the Yahoo
 * Weather API wrapped in the helper library yweathergetter4a
 */

public class WindJobInfo {

    //Job Scheduling constants. Get wind data from api every
    private static final int PERIOD = 5000; //Every 5min TODO: Change it back to 5min
    private static final int INITIAL_BACKOFF = 3000;
    private static final int PERIODIC_ID = 2;

    public static synchronized void initialize(final Context context) {
        schedulePeriodic(context);
    }

    private static synchronized void schedulePeriodic(Context context) {
        Timber.d("Scheduling Wind Task");
        JobInfo.Builder builder = new JobInfo
              .Builder(PERIODIC_ID, new ComponentName(context, WindJobService.class));

        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
              .setPeriodic(PERIOD)
              .setBackoffCriteria(INITIAL_BACKOFF, JobInfo.BACKOFF_POLICY_EXPONENTIAL);

        JobScheduler scheduler = (JobScheduler) context
              .getSystemService(Context.JOB_SCHEDULER_SERVICE);

        scheduler.schedule(builder.build());
    }
}
