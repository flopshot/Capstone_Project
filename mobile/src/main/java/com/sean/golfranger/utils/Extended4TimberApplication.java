package com.sean.golfranger.utils;

import android.app.Application;

import com.sean.golfranger.BuildConfig;

import timber.log.Timber;

/**
 * Extended App Functionality to Include Timber Logging
 */

public class Extended4TimberApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree() {
                //Appending Dev tag to better filter in logcat
                @Override
                protected void log(int priority, String tag, String message, Throwable t) {
                    TimberUtil.appendedLog(priority, tag, message, t);
                }

                //Adds line numbers to tag
                @Override
                protected String createStackElementTag(StackTraceElement element) {
                    return super.createStackElementTag(element) + ":" + element.getLineNumber();
                }
            });
        } else {
            Timber.plant(new Timber.DebugTree() {
                // In release mode, only debug W, E, WTF priority
                @Override
                protected boolean isLoggable(String tag, int priority) {
                    return TimberUtil.isReleaseLoggable(priority);
                }

                //Appending Dev tag to better filter in logcat
                @Override
                protected void log(int priority, String tag, String message, Throwable t) {
                    TimberUtil.appendedLog(priority, tag, message, t);
                }

                //Adds line numbers to tag
                @Override
                protected String createStackElementTag(StackTraceElement element) {
                    return super.createStackElementTag(element) + ":" + element.getLineNumber();
                }
            });
        }
    }
}
