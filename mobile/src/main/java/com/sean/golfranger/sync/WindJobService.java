package com.sean.golfranger.sync;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;

import com.sean.golfranger.utils.SharedPrefUtils;

import timber.log.Timber;
import zh.wang.android.yweathergetter4a.WeatherInfo;
import zh.wang.android.yweathergetter4a.YahooWeather;
import zh.wang.android.yweathergetter4a.YahooWeatherInfoListener;

/**
 * Job Service to get wind metrics every X minutes, depending on the job build schedule
 * Implements YahooWeather Listener interface callback, which passes back wind data
 */
public class WindJobService extends JobService implements YahooWeatherInfoListener {
    JobParameters mJobParams;
    private static final int CONNECTION_TIMEOUT = 5000;
    private static final String ACTION_WIND_UPDATED = "com.sean.golfranger.ACTION_WIND_UPDATED";

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Timber.d("Starting Wind Sync Job");
        mJobParams = jobParameters;

        Double[] latLon = SharedPrefUtils.getUserLatLonDouble(getApplicationContext());

        if (latLon[0] == 0.) {
            Timber.d("User had bad Coordinates, ending Wind Job");
            jobFinished(jobParameters, false);
        } else {
            YahooWeather yahooWeather = YahooWeather.getInstance(CONNECTION_TIMEOUT, true);
            yahooWeather.setNeedDownloadIcons(false);
            yahooWeather.setUnit(YahooWeather.UNIT.FAHRENHEIT);
            yahooWeather.queryYahooWeatherByLatLon(getApplicationContext(), latLon[0], latLon[1], this);
        }
        Timber.d("Wind Sync Job Finished OK: ");
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Timber.d("Wind Sync Job Finished UNEXPECTEDLY");
        return false;
    }

    @Override
    public void gotWeatherInfo(final WeatherInfo weatherInfo, YahooWeather.ErrorType errorType) {
        if (weatherInfo != null) {
            Float dir = Float.valueOf(weatherInfo.getWindDirection());
            String speed = weatherInfo.getWindSpeed();
            Timber.d("Wind Dir: " + String.valueOf(dir));
            Timber.d("Wind Speed: " + speed);
            //Save Results to Shared Prefs
            SharedPrefUtils.setCurrentWindSpeed(getApplicationContext(), speed);
            SharedPrefUtils.setCurrentWindDirection(getApplicationContext(), dir);

            Timber.d("Wind Saved to Shared Prefs");
            jobFinished(mJobParams, false);
        } else {
            Timber.w("Weather Info was NULL, Saved to Shared Prefs");
            SharedPrefUtils.setCurrentWindSpeed(getApplicationContext(), null);
            SharedPrefUtils.setCurrentWindDirection(getApplicationContext(), -1f);
            jobFinished(mJobParams, false);
        }
        // Update map of wind response
        sendBroadcast(new Intent(ACTION_WIND_UPDATED));
    }
}
