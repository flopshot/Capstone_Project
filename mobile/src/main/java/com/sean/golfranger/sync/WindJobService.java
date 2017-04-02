package com.sean.golfranger.sync;

import android.app.job.JobParameters;
import android.app.job.JobService;

import com.sean.golfranger.utils.SharedPrefUtils;

import zh.wang.android.yweathergetter4a.WeatherInfo;
import zh.wang.android.yweathergetter4a.YahooWeather;
import zh.wang.android.yweathergetter4a.YahooWeatherInfoListener;

/**
 * Job Service to get wind metrics every X minutes, depending on the job build schedule
 */
//TODO: Consider Putting user-sync function in map frag, separate from the JobService Logic
public class WindJobService extends JobService implements YahooWeatherInfoListener {
    JobParameters mJobParams;
    private static final int CONNECTION_TIMEOUT = 10000;

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        mJobParams = jobParameters;

        boolean isOnMapScreen = SharedPrefUtils.isOnMapScreen(getApplicationContext());
        if (isOnMapScreen) {
            Double[] latLon = SharedPrefUtils.getUserLatLonDouble(getApplicationContext());

            if (latLon[0] == 0.) {
                //TODO: end task, try again, warn user, set default, etc.
                //TODO: Remove debug boolean in getInstance
            }
            YahooWeather yahooWeather = YahooWeather.getInstance(CONNECTION_TIMEOUT, true);
            yahooWeather.setNeedDownloadIcons(false);
            yahooWeather.setUnit(YahooWeather.UNIT.FAHRENHEIT);
            yahooWeather.queryYahooWeatherByLatLon(getApplicationContext(),latLon[0], latLon[1], this);
        }

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        //TODO: Set Logic for the job finishing prematurely
        return false;
    }

    @Override
    public void gotWeatherInfo(final WeatherInfo weatherInfo, YahooWeather.ErrorType errorType) {
        if (weatherInfo != null && errorType != null) {
            weatherInfo.getWindDirection();
            weatherInfo.getWindSpeed();

            //TODO: Broadcast Receiver to Map Fragment
            jobFinished(mJobParams, false);
        } else {
            //TODO: Exit with wind task error
            jobFinished(mJobParams, false);
        }
    }
}
