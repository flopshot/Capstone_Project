package com.sean.golfranger.sync;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;

/**
 * Holds the Job schedule logic and execution logic. Mainly, to get the wind data through the Yahoo
 * Weather API wrapped in the helper library yweathergetter4a
 */

// TODO: call cancelAll() on process destroyed
// jobScheduler = (JobScheduler)this.getSystemService(Context.JOB_SCHEDULER_SERVICE );
// jobScheduler.cancelAll();
public class WindJobInfo {

    //Job Scheduling constants. Get wind data from api every
    private static final int PERIOD = 3000000; //Every 5min
    private static final int INITIAL_BACKOFF = 3000;
    private static final int PERIODIC_ID = 1;
    private static final int MIN_LATENCY = 3000;
    private static final int MAX_WAIT = 60000;


    public static synchronized void initialize(final Context context) {
        schedulePeriodic(context);
    }

    private static void schedulePeriodic(Context context) {
        JobInfo.Builder builder = new JobInfo
              .Builder(PERIODIC_ID, new ComponentName(context, WindJobService.class));

        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
              .setPeriodic(PERIOD)
              .setBackoffCriteria(INITIAL_BACKOFF, JobInfo.BACKOFF_POLICY_EXPONENTIAL)
              .setMinimumLatency(MIN_LATENCY)
              .setOverrideDeadline(MAX_WAIT);

        JobScheduler scheduler = (JobScheduler) context
              .getSystemService(Context.JOB_SCHEDULER_SERVICE);

        scheduler.schedule(builder.build());
    }
}
