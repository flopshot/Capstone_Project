package com.sean.golfranger.sync;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * Intent Service Used to Spin Up background thread for Elevation Api Call task
 */

public class ElevationIntentService extends IntentService {
    public ElevationIntentService() {
        super(ElevationIntentService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        ElevationJobInfo.getElevation(getApplicationContext());
    }
}
