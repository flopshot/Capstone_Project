package com.sean.golfranger.widget;

import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sean.golfranger.R;
import com.sean.golfranger.data.Contract;

import timber.log.Timber;

/**
 * Service for our Golf Ranger Widget. For binding data from cursor to views
 */
public class WidgetRemoteViewsService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {

        return new RemoteViewsFactory() {
            private Cursor data = null;

            @Override
            public void onCreate() {
                Timber.d("Widget Service Created");
                // Nothing to do
            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }
                // This method is called by the app hosting the widget (e.g., the launcher)
                // However, our ContentProvider is not exported so it doesn't have access to the
                // data. Therefore we need to clear (and finally restore) the calling identity so
                // that calls use our process and permission
                final long identityToken = Binder.clearCallingIdentity();
                Timber.d("We queried the data");
                data = getContentResolver().query(
                      Contract.WidgetView.buildDirUri(),
                      null,
                      null,
                      null,
                      null);
                Binder.restoreCallingIdentity(identityToken);

                if (data.getCount() != 0 && data.moveToFirst()) {
                    String name = data.getString(Contract.WidgetView.PLAYERFIRST_POS) +"\n"+
                          data.getString(Contract.WidgetView.PLAYERLAST_POS);
                    Long gamesPlayed = data.getLong(Contract.WidgetView.GAMECNT_POS);
                    Integer meanScore = data.getInt(Contract.WidgetView.AVGSCORE_POS);
                    Long minScore = data.getLong(Contract.WidgetView.LOWSCORE_POS);
                    String formattedMinScore = String.valueOf(minScore); //(minScore == 0) ? getString(R.string.widgetValNA) : String.valueOf(minScore);
                    Timber.d("Name: " + name + " GamesPlayed: " + gamesPlayed + " meanScore: " + meanScore + " minScore: " + formattedMinScore);
                } else {
                    Timber.d("Data is null");
                }
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                      data == null || !data.moveToPosition(position)) {

                    return null;
                }
                RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_list_item);

                String name = data.getString(Contract.WidgetView.PLAYERFIRST_POS) + "\n" +
                      data.getString(Contract.WidgetView.PLAYERLAST_POS);
                String gamesPlayed = data.getString(Contract.WidgetView.GAMECNT_POS);
                String meanScore =data.getString(Contract.WidgetView.AVGSCORE_POS);
                String minScore = data.getString(Contract.WidgetView.LOWSCORE_POS);//(minScore == 0) ? getString(R.string.widgetValNA) : String.valueOf(minScore);

                views.setTextViewText(R.id.widgetPlayerName, name);
                views.setTextViewText(R.id.widgetGames, gamesPlayed);
                views.setTextViewText(R.id.widgetAvgScore, meanScore);
                views.setTextViewText(R.id.widgetLowScore, minScore);

                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                Timber.d("getLoadingView called");
                return new RemoteViews(getPackageName(), R.layout.widget_list_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (data.moveToPosition(position))
                    return data.getLong(Contract.WidgetView.PLAYERID_POS);
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}