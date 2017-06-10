package com.sean.golfranger.utils;

import android.content.Context;
import android.content.res.Resources;

import com.sean.golfranger.R;

import timber.log.Timber;

/**
 * Helper class to get screen dimensions
 */

public class ScreenUtils {
    private static final int ACTIONBAR_SIZE = 56;

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    public static float getScreenDensity() {
        return Resources.getSystem().getDisplayMetrics().density;
    }

    public static int getMaxNumListItems(Context context, int type) {
        float itemHeight;
        switch (type) {
            case 0: //Player or Course Item Height
                itemHeight = context.getResources().getDimension(R.dimen.itemHeight)/
                      Resources.getSystem().getDisplayMetrics().density;
                break;
            case 1: //Round Item Height
                itemHeight = context.getResources().getDimension(R.dimen.match_list_height)/
                      Resources.getSystem().getDisplayMetrics().density;
                break;
            default:
                itemHeight = 0f;
                break;
        }

        float screenHeightDp = Resources.getSystem().getDisplayMetrics().heightPixels /
              Resources.getSystem().getDisplayMetrics().density;

        float recyclerViewHeight = screenHeightDp -
              ACTIONBAR_SIZE -
              context.getResources().getDimension(R.dimen.recyclerViewPaddingTop);

        Timber.d("RecyclerView Height: " + recyclerViewHeight + " itemHeight: " + itemHeight);
        double avgNumItems = (double) recyclerViewHeight / itemHeight;

        return (int) Math.ceil(avgNumItems);
    }
}
