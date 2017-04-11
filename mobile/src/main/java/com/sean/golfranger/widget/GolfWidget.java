package com.sean.golfranger.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.widget.RemoteViews;

import com.sean.golfranger.R;

import timber.log.Timber;

/**
 * Implementation of App Widget functionality.
 */
public class GolfWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        for (int appWidgetId:appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.golf_widget);
            // Set up the ListView collection
            setRemoteAdapter(context, views);
            views.setEmptyView(R.id.widget_list, R.id.widget_empty);

            // Set up the Refresh Button
            Intent intentSync = new Intent(context, GolfWidget.class);
            intentSync.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            PendingIntent pendingSync = PendingIntent
                  .getBroadcast(context,0, intentSync, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.widget,pendingSync);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        super.onReceive(context, intent);
        String actionName = "android.appwidget.action.APPWIDGET_UPDATE";
        if (actionName.equals(intent.getAction())) {
            Timber.v("Starting on Receive Method from Intent Data Changed");
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                  new ComponentName(context, getClass()));
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list);
            onUpdate(context, appWidgetManager,appWidgetIds);
        }
    }

    private void setRemoteAdapter(Context context, @NonNull final RemoteViews views) {
        Timber.d("We set the remote Adapter");
        views.setRemoteAdapter(R.id.widget_list,
              new Intent(context, WidgetRemoteViewsService.class));
    }
}