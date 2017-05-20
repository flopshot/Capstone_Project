package com.sean.golfranger;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sean.golfranger.data.Contract;
import com.sean.golfranger.utils.SharedPrefUtils;

import timber.log.Timber;

import static com.sean.golfranger.R.id.p1Total;
import static com.sean.golfranger.R.id.p4Total;

/**
 * Will Display the Round Overview. Will show all participating player names, scores, hole number,
 * and hole par number
 */
public class ScorecardFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private  MyObserver myObserver;
    private static final int SCORECARD_LOADER = 100;
    private static LoaderManager sLoaderManager;
    private static LoaderManager.LoaderCallbacks sLoaderCallback;
    private ScorecardAdapter mScoreCardAdapter;
    private TextView p1TotalView, p2TotalView, p3TotalView, p4TotalView;
    private TextView p1Initials, p2Initials, p3Initials, p4Initials;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sLoaderManager = getLoaderManager();

        // Observe Content Provider Data Changes
        myObserver = new MyObserver(new Handler());
        sLoaderCallback = this;

        // Register ContentObserver in onCreate to catch changes in detail fragment
        getActivity()
              .getContentResolver()
              .registerContentObserver(Contract.RoundPlayerHoles.buildDirUri(), true, myObserver);
        getActivity()
              .getContentResolver()
              .registerContentObserver(Contract.CourseHoles.buildDirUri(), true, myObserver);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        sLoaderManager.initLoader(SCORECARD_LOADER, null, sLoaderCallback);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragmet_scorecard, container, false);
        p1TotalView = (TextView) rootView.findViewById(p1Total);
        p2TotalView = (TextView) rootView.findViewById(R.id.p2Total);
        p3TotalView = (TextView) rootView.findViewById(R.id.p3Total);
        p4TotalView = (TextView) rootView.findViewById(p4Total);

        p1Initials = (TextView) rootView.findViewById(R.id.p1Name);
        p2Initials = (TextView) rootView.findViewById(R.id.p2Name);
        p3Initials = (TextView) rootView.findViewById(R.id.p3Name);
        p4Initials = (TextView) rootView.findViewById(R.id.p4Name);

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_holes);
        LinearLayoutManager layMan = new LinearLayoutManager(getActivity().getApplicationContext());
        int orientation = getResources().getConfiguration().orientation;

        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            layMan.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(layMan);
        } else {
            layMan.setOrientation(LinearLayoutManager.HORIZONTAL);
            recyclerView.setLayoutManager(layMan);
        }

        mScoreCardAdapter = new ScorecardAdapter();
        recyclerView.setAdapter(mScoreCardAdapter);
        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().getContentResolver().unregisterContentObserver(myObserver);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        Timber.d("Creating Hole Data Loader");
        return new CursorLoader(getActivity().getApplicationContext(),
              Contract.ScorecardView.buildDirUri(),
              null,
              Contract.ScorecardView.ROUND_ID + "=?",
              new String[]{
                    SharedPrefUtils.getCurrentRoundId(getActivity().getApplicationContext())
              },
              null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mScoreCardAdapter.swapCursor(cursor);
        if (cursor != null && cursor.moveToFirst()) {

            String p1Initial = cursor.getString(Contract.ScorecardView.P1INITIALS_POS);
            Timber.d("Player1 Initials "+p1Initial);
            if (p1Initial != null) {
                p1Initials.setText(p1Initial);
                p1TotalView.setText(String.valueOf(cursor.getLong(Contract.ScorecardView.P1TOTAL_POS)));
            }

            String p2Initial = cursor.getString(Contract.ScorecardView.P2INITIALS_POS);
            Timber.d("Player2 Initials "+p2Initial);
            if (p2Initial != null) {
                p2Initials.setText(p2Initial);
                p2TotalView.setText(String.valueOf(cursor.getLong(Contract.ScorecardView.P2TOTAL_POS)));
            }


            String p3Initial = cursor.getString(Contract.ScorecardView.P3INITIALS_POS);
            Timber.d("Player3 Initials "+p3Initial);
            if (p3Initial != null) {
                p3Initials.setText(p3Initial);
                p3TotalView.setText(String.valueOf(cursor.getLong(Contract.ScorecardView.P3TOTAL_POS)));
            }

            String p4Initial = cursor.getString(Contract.ScorecardView.P4INITIALS_POS);
            Timber.d("Player4 Initials "+p4Initial);
            if (p4Initial != null) {
                p4Initials.setText(p4Initial);
                p4TotalView.setText(String.valueOf(cursor.getLong(Contract.ScorecardView.P4TOTAL_POS)));
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mScoreCardAdapter.swapCursor(null);
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
            Timber.d("Change in Hole of Round Observed and ReInit Loaders");
            sLoaderManager.restartLoader(SCORECARD_LOADER, null, sLoaderCallback);
        }
    }
}
