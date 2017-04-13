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
                Timber.d("Widget SErvice Created");
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
                      Contract.PlayerTotals.buildDirUri(),
                      null,
                      null,
                      null,
                      null);
                Binder.restoreCallingIdentity(identityToken);

                if (data.getCount() != 0 && data.moveToFirst()) {
                    String name = data.getString(Contract.PlayerTotals.PLAYER_FIRST) +
                          data.getString(Contract.PlayerTotals.PLAYER_LAST);
                    Long gamesPlayed = data.getLong(Contract.PlayerTotals.PLAYER_GAME_COUNT);
                    Float meanScore = data.getFloat(Contract.PlayerTotals.PLAYER_MEAN_SCORE);
                    Long minScore = data.getLong(Contract.PlayerTotals.PLAYER_MIN_SCORE);
                    String formattedMinScore = (minScore == 0) ? getString(R.string.widgetValNA) : String.valueOf(minScore);
                    Timber.d("Name: " + name + " GamesPlayed: " + gamesPlayed + " meanScore: " + meanScore + "minScore: " + formattedMinScore);
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

                String name = data.getString(Contract.PlayerTotals.PLAYER_FIRST) + " " +
                      data.getString(Contract.PlayerTotals.PLAYER_LAST);
                Long gamesPlayed = data.getLong(Contract.PlayerTotals.PLAYER_GAME_COUNT);
                Float meanScore = data.getFloat(Contract.PlayerTotals.PLAYER_MEAN_SCORE);
                Long minScore = data.getLong(Contract.PlayerTotals.PLAYER_MIN_SCORE);
                String formattedMinScore = (minScore == 0) ? getString(R.string.widgetValNA) : String.valueOf(minScore);

                views.setTextViewText(R.id.widgetPlayerName, name);
                views.setTextViewText(R.id.widgetGames, String.valueOf(gamesPlayed));
                views.setTextViewText(R.id.widgetAvgScore, String.valueOf(Math.round(meanScore)));
                views.setTextViewText(R.id.widgetLowScore, formattedMinScore);

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
                    return data.getLong(Contract.PlayerTotals.PLAYER_ID);
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}