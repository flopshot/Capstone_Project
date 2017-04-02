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
//TODO: Consider Putting user-sync function in map frag, separate from the JobService Logic
public class WindJobService extends JobService implements YahooWeatherInfoListener {
    JobParameters mJobParams;
    private static final int CONNECTION_TIMEOUT = 10000;
    private static final String ACTION_WIND_UPDATED = "com.sean.golfranger.ACTION_WIND_UPDATED";
    private static final String EXTRA_WIND_SPEED = "WindJobService.EXTRA_WIND_SPEED";
    private static final String EXTRA_WIND_DIRECTION = "WindJobService.EXTRA_WIND_SPEED";

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Timber.d("Starting Wind Sync Job");
        mJobParams = jobParameters;

        boolean isOnMapScreen = SharedPrefUtils.isOnMapScreen(getApplicationContext());
        if (isOnMapScreen) {
            Timber.d("Map Screen is in Forefront, Starting Weather Api Call");
            Double[] latLon = SharedPrefUtils.getUserLatLonDouble(getApplicationContext());

            if (latLon[0] == 0.) {
                Timber.d("User had bad Coordinates, ending Wind Job");
            } else {
                YahooWeather yahooWeather = YahooWeather.getInstance(CONNECTION_TIMEOUT, true);
                yahooWeather.setNeedDownloadIcons(false);
                yahooWeather.setUnit(YahooWeather.UNIT.FAHRENHEIT);
                yahooWeather.queryYahooWeatherByLatLon(getApplicationContext(), latLon[0], latLon[1], this);
            }
        }
        Timber.d("Wind Sync Job Finished OK");
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Timber.d("Wind Sync Job Finished UNEXPECTEDLY");
        return false;
    }

    @Override
    public void gotWeatherInfo(final WeatherInfo weatherInfo, YahooWeather.ErrorType errorType) {
        if (weatherInfo != null && errorType != null) {
            weatherInfo.getWindDirection();
            weatherInfo.getWindSpeed();

            //Broadcast Results to Map Fragment
            Intent windToMapBroadcast = new Intent(ACTION_WIND_UPDATED);
            windToMapBroadcast.putExtra(EXTRA_WIND_SPEED, weatherInfo.getWindSpeed());
            windToMapBroadcast.putExtra(EXTRA_WIND_DIRECTION, weatherInfo.getWindDirection());
            sendBroadcast(windToMapBroadcast);
            Timber.d("Wind Broadcast Sent to Map");
            jobFinished(mJobParams, false);

        } else {
            Timber.w("Weather Info was NULL, no broadcast performed");
            jobFinished(mJobParams, false);
        }
    }
}
