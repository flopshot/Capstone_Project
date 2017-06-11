package com.sean.golfranger.utils;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import java.util.HashSet;
import java.util.Set;

import timber.log.Timber;

/**
 * Helper Class for RecyclerView Item animations
 */
public class AnimateUtils {
    public static final int PLAYER_TYPE = 100;
    public static final int MATCH_TYPE = 101;
    public static final int COURSE_TYPE = 102;

    public static void runEnterAnimation(Context context, View view, boolean doAnimation,
                                            int type, int position, String id, Set<String> newIds) {

        int itemType;
        if (type == 102) {itemType = 1;} else {itemType = 0;}

        int maxItems = ScreenUtils.getMaxNumListItems(context, itemType);

        Timber.i("Do Animation: " + doAnimation + " position: " + position + " maxItems: " + maxItems);
        Timber.i("New Ids: " + newIds);
        if ((doAnimation && position <= maxItems) | (!doAnimation && newIds.contains(id))) {
            view.setTranslationX(ScreenUtils.getScreenWidth());
            view.animate()
                  .translationX(0)
                  .setInterpolator(new DecelerateInterpolator(1.5f))
                  .setDuration(700)
                  .setStartDelay(position * 90)
                  .start();
        }
    }

    public static Set<String> newIdsFromCursor(Context context, Cursor newCursor) {
        Set<String> oldIds = SharedPrefUtils.getAnimateIds(context);
        Set<String> newIds = new HashSet<>();

        try {
            if (newCursor.moveToFirst()) {
                do {
                    newIds.add(newCursor.getString(0)); // Id column of all tables should be 0th
                } while (newCursor.moveToNext());
            }

            SharedPrefUtils.setAnimateIds(context, newIds);
            newIds.removeAll(oldIds);
            return newIds;

        } catch (NullPointerException e) {
            SharedPrefUtils.setAnimateIds(context, newIds);
            return newIds;
        }
    }
}
