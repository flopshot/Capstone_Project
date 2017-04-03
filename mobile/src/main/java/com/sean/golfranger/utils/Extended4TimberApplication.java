package com.sean.golfranger.utils;

import android.app.Application;
import android.util.Log;

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
                //Adds line numbers to tag, used to filter out only Sourcecode Logs
                @Override
                protected String createStackElementTag(StackTraceElement element) {
                    return super.createStackElementTag(element) + "(" + element.getLineNumber() + ")";
                }
            });
        } else {
            Timber.plant(new Timber.DebugTree() {
                // In release mode, only debug W, E, WTF priority
                @Override
                protected boolean isLoggable(String tag, int priority) {
                    return isReleaseLoggable(priority);
                }

                //Adds line numbers to tag, used to filter out only Sourcecode Logs
                @Override
                protected String createStackElementTag(StackTraceElement element) {
                    return super.createStackElementTag(element) + "(" + element.getLineNumber() + ")";
                }
            });
        }
    }

    /**
     * Logic for Release Config APK logging.
     *
     * CURRENTLY: WARNING, ERROR, AND WTF
     */
    static boolean isReleaseLoggable(int priority) {
        return !(priority == Log.VERBOSE || priority == Log.DEBUG || priority == Log.INFO );
    }
}
