package com.sean.golfranger.utils;

import android.content.Context;
import android.content.res.Resources;

import com.sean.golfranger.R;

/**
 * Helper class to get screen dimensions
 */

public class ScreenUtils {
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
            case 0:
                itemHeight = context.getResources().getDimension(R.dimen.itemHeight);
                break;
            case 1:
                itemHeight = context.getResources().getDimension(R.dimen.match_list_height);
                break;
            default:
                itemHeight = 0f;
                break;
        }

        float screenHeightDp = Resources.getSystem().getDisplayMetrics().heightPixels /
              Resources.getSystem().getDisplayMetrics().density;

        float recyclerViewHeight = screenHeightDp -
              android.R.attr.actionBarSize -
              context.getResources().getDimension(R.dimen.recyclerViewPaddingTop);

        double avgNumItems =(double) recyclerViewHeight / itemHeight;

        return (int) Math.ceil(avgNumItems);

    }
}
