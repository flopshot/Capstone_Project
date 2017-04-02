package com.sean.golfranger.utils;

import android.util.Log;

/**
 * Helper class to tweak Timber Features
 */

class TimberUtil {
    private static final int MAX_LOG_LENGTH = 4000;
    private static final String APPENDED_MSG_TAG = "-MyLog";

    /**
     * logs only WTF, W, and E priority messages
     */
    static boolean isReleaseLoggable(int priority) {
        return !(priority == Log.VERBOSE || priority == Log.DEBUG || priority == Log.INFO );
    }

    /**
     * Override Logging to append "-MyLog" to each log
     * message for logcat filtering of only my source code logs
     */
    static void appendedLog(int priority, String tag, String message, Throwable t) {
        if (message.length() < MAX_LOG_LENGTH) {
            if (priority == Log.ASSERT) {
                Log.wtf(tag, message + APPENDED_MSG_TAG);
            } else {
                Log.println(priority, tag, message + APPENDED_MSG_TAG);
            }
            return;
        }

        // Split by line, then ensure each line can fit into Log's maximum length.
        for (int i = 0, length = message.length(); i < length; i++) {
            int newline = message.indexOf('\n', i);
            newline = newline != -1 ? newline : length;
            do {
                int end = Math.min(newline, i + MAX_LOG_LENGTH);
                String part = message.substring(i, end);
                if (priority == Log.ASSERT) {
                    Log.wtf(tag, part + APPENDED_MSG_TAG);
                } else {
                    Log.println(priority, tag, part + APPENDED_MSG_TAG);
                }
                i = end;
            } while (i < newline);
        }
    }
}
