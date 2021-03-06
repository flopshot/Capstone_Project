package com.sean.golfranger.sync;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;

import timber.log.Timber;

/**
 * Holds the Job schedule logic and execution logic. Mainly, to get the wind data through the Yahoo
 * Weather API wrapped in the helper library yweathergetter4a
 */

public class WindJobInfo {

    //Job Scheduling constants. Get wind data from api every
    private static final int PERIOD = 60000;
    private static final int INITIAL_BACKOFF = 3000;
    private static final int PERIODIC_ID = 2;

    public static synchronized void initialize(final Context context) {
        schedulePeriodic(context);
    }

    private static synchronized void schedulePeriodic(Context context) {
        Timber.d("Scheduling Wind Task");
        JobInfo.Builder builder = new JobInfo
              .Builder(PERIODIC_ID, new ComponentName(context, WindJobService.class));

        if (Build.VERSION.SDK_INT < 24) {
            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                  .setPeriodic(PERIOD)
                  .setBackoffCriteria(INITIAL_BACKOFF, JobInfo.BACKOFF_POLICY_EXPONENTIAL);
        } else {
            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                  .setMinimumLatency(PERIOD);
        }

        JobScheduler scheduler = (JobScheduler) context
              .getSystemService(Context.JOB_SCHEDULER_SERVICE);

        scheduler.schedule(builder.build());
    }
}
