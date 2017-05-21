package com.sean.golfranger;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.sean.golfranger.data.Contract;

import timber.log.Timber;

public class MatchesActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    private MatchAdapter mMatchAdapter;
    private ContentObserver mMyObserver;
    private static LoaderManager sLoaderManager;
    private static LoaderManager.LoaderCallbacks sLoaderCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matches);

        mMyObserver = new MyObserver(new Handler());
        sLoaderManager = getSupportLoaderManager();
        getContentResolver()
              .registerContentObserver(Contract.Rounds.buildDirUri(), true, mMyObserver);
        getContentResolver()
              .registerContentObserver(Contract.RoundPlayers.buildDirUri(), true, mMyObserver);
        sLoaderCallback = this;
        sLoaderManager.initLoader(0, null, sLoaderCallback);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view_matches);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration dividerItemDecoration =
              new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        mMatchAdapter = new MatchAdapter(this);
        recyclerView.setAdapter(mMatchAdapter);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getContentResolver().unregisterContentObserver(mMyObserver);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getApplicationContext(),
              Contract.MatchesView.buildDirUri(),
              null,
              null,
              null,
              null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mMatchAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMatchAdapter.swapCursor(null);
    }

    private class MyObserver extends ContentObserver {
        // Consider Refactor to new class
        MyObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            this.onChange(selfChange,null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            sLoaderManager.restartLoader(0, null, sLoaderCallback);
        }
    }

    public void startNewRound(View v) {
        Intent intent = new Intent(getApplicationContext(), StartRoundActivity.class);
        startActivity(intent);
    }

    ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(
          0, ItemTouchHelper.RIGHT) {
        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
            long roundId = mMatchAdapter.getItemId(viewHolder.getAdapterPosition());
            String id = String.valueOf(roundId);
            ContentResolver resolver = getContentResolver();
            ContentValues v = new ContentValues();

            Timber.d("roundId: " + id);
            v.put(Contract.Rounds.ROUND_ENABLED, "0");
            resolver.update(Contract.Rounds.buildDirUri(),
                  v,
                  Contract.Rounds._ID + "=?",
                  new String[]{id});
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                              RecyclerView.ViewHolder target) {
            return false;
        }
    };

    ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
}
