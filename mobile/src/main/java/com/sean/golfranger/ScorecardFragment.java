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

import static com.sean.golfranger.R.id.p4Total;

/**
 * Will Display the Round Overview. Will show all participating player names, scores, hole number,
 * and hole par number
 */
public class ScorecardFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private  MyObserver myObserver;
    private static final int HOLES_LOADER = 100;
    private static final int TOTAL_LOADER = 101;
    private static LoaderManager sLoaderManager;
    private static LoaderManager.LoaderCallbacks sLoaderCallback;
    private ScorecardAdapter mScoreCardAdapter;
    private TextView p1TotalView, p2TotalView, p3TotalView, p4TotalView;
    private TextView p1Initials, p2Initials, p3Initials, p4Initials;

    public ScorecardFragment() {
        // Required empty public constructor
    }

    public static ScorecardFragment newInstance() {
        return new ScorecardFragment();
    }

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
              .registerContentObserver(Contract.Rounds.buildDirUri(), true, myObserver);
        getActivity()
              .getContentResolver()
              .registerContentObserver(Contract.Holes.buildDirUri(), true, myObserver);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        sLoaderManager.initLoader(HOLES_LOADER, null, sLoaderCallback);
        sLoaderManager.initLoader(TOTAL_LOADER, null, sLoaderCallback);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragmet_scorecard, container, false);
        p1TotalView = (TextView) rootView.findViewById(R.id.p1Total);
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

        mScoreCardAdapter = new ScorecardAdapter(getActivity().getApplicationContext());
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
        switch (id) {
            case HOLES_LOADER:
                Timber.d("Creating Hole Data Loader");
                return new CursorLoader(getActivity().getApplicationContext(),
                      Contract.roundHolesUri(),
                      null,
                      Contract.Holes.ROUND_ID + "=?",
                      new String[]{
                            SharedPrefUtils.getCurrentRoundId(getActivity().getApplicationContext())
                      },
                      null);
            case TOTAL_LOADER:
                return new CursorLoader(getActivity().getApplicationContext(),
                      Contract.PlayerRoundTotals.buildDirUri(),
                      null,
                      Contract.Holes.ROUND_ID + "=?",
                      new String[] {
                            SharedPrefUtils.getCurrentRoundId(getActivity().getApplicationContext())
                      },
                      null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        switch(loader.getId()) {
            case HOLES_LOADER:
                mScoreCardAdapter.swapCursor(cursor);
                if (cursor != null && cursor.moveToFirst()) {

                    String p1FirstName = cursor.getString(
                          cursor.getColumnIndex(
                                Contract.Rounds.PLAYER1_FIRST_NAME));
                    String p1LastName = cursor.getString(
                          cursor.getColumnIndex(
                                Contract.Rounds.PLAYER1_LAST_NAME));
                    Timber.d("Player1 First Name "+p1FirstName);
                    if (p1LastName != null && p1FirstName != null) {
                        String initials = p1FirstName.substring(0,1) + p1LastName.substring(0,1);
                        p1Initials.setText(initials);
                    }

                    String p2FirstName = cursor.getString(
                          cursor.getColumnIndex(
                                Contract.Rounds.PLAYER2_FIRST_NAME));
                    String p2LastName = cursor.getString(
                          cursor.getColumnIndex(
                                Contract.Rounds.PLAYER2_LAST_NAME));
                    if (p2LastName != null && p2FirstName != null) {
                        String initials = p2FirstName.substring(0,1) + p2LastName.substring(0,1);
                        p2Initials.setText(initials);
                    }

                    String p3FirstName = cursor.getString(
                          cursor.getColumnIndex(
                                Contract.Rounds.PLAYER3_FIRST_NAME));
                    String p3LastName = cursor.getString(
                          cursor.getColumnIndex(
                                Contract.Rounds.PLAYER3_LAST_NAME));
                    if (p3LastName != null && p3FirstName != null) {
                        String initials = p3FirstName.substring(0,1) + p3LastName.substring(0,1);
                        p3Initials.setText(initials);
                    }

                    String p4FirstName = cursor.getString(
                          cursor.getColumnIndex(
                                Contract.Rounds.PLAYER4_FIRST_NAME));
                    String p4LastName = cursor.getString(
                          cursor.getColumnIndex(
                                Contract.Rounds.PLAYER4_LAST_NAME));
                    if (p4LastName != null && p4FirstName != null) {
                        String initials = p4FirstName.substring(0,1) + p4LastName.substring(0,1);
                        p4Initials.setText(initials);
                    }
                }
                break;
            case TOTAL_LOADER:
                if (cursor != null && cursor.moveToFirst()) {
                    try {
                        String p1Total;
                        if (cursor.getLong(Contract.PlayerRoundTotals.P1_EXISTS_COL_INDEX) == 1) {
                            p1Total = String.valueOf(cursor.getLong(Contract.PlayerRoundTotals.P1_TOTAL_COL_INDEX));
                        } else {
                            p1Total = null;
                        }
                        p1TotalView.setText(p1Total);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        String p2Total;
                        if (cursor.getLong(Contract.PlayerRoundTotals.P2_EXISTS_COL_INDEX) == 1) {
                            p2Total = String.valueOf(cursor.getLong(Contract.PlayerRoundTotals.P2_TOTAL_COL_INDEX));
                        } else {
                            p2Total = null;
                        }
                        p2TotalView.setText(p2Total);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        String p3Total;
                        if (cursor.getLong(Contract.PlayerRoundTotals.P3_EXISTS_COL_INDEX) == 1) {
                            p3Total = String.valueOf(cursor.getLong(Contract.PlayerRoundTotals.P3_TOTAL_COL_INDEX));
                        } else {
                            p3Total = null;
                        }
                        p3TotalView.setText(p3Total);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        String p4Total;
                        if (cursor.getLong(Contract.PlayerRoundTotals.P4_EXISTS_COL_INDEX) == 1) {
                            p4Total = String.valueOf(cursor.getLong(Contract.PlayerRoundTotals.P4_TOTAL_COL_INDEX));
                        } else {
                            p4Total = null;
                        }
                        p4TotalView.setText(p4Total);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (HOLES_LOADER == loader.getId()) {
            mScoreCardAdapter.swapCursor(null);
        }
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
            sLoaderManager.restartLoader(TOTAL_LOADER, null, sLoaderCallback);
            sLoaderManager.restartLoader(HOLES_LOADER, null, sLoaderCallback);
        }
    }
}
