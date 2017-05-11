package com.sean.golfranger;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.sean.golfranger.data.Contract;
import com.sean.golfranger.utils.DialogUtils;
import com.sean.golfranger.utils.SharedPrefUtils;

import timber.log.Timber;

import static com.sean.golfranger.R.id.clubName;
import static com.sean.golfranger.R.id.courseName;

public class StartRoundActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int REQUEST_CODE_P1 = 101;
    private static final int REQUEST_CODE_P2 = 102;
    private static final int REQUEST_CODE_P3 = 103;
    private static final int REQUEST_CODE_P4 = 104;
    private static final int REQUEST_CODE_COURSE = 105;

    public static final String EXTRA_ROUND_ID = "startRoundActivityRoundId";
    public static final String EXTRA_RETURN_ID = "RETURNID";
    public static final String EXTRA_RETURN_FIRST_ITEM = "RETURNFIRSTITEM";
    public static final String EXTRA_RETURN_SECOND_ITEM = "RETURNSECONDITEM";
    private static final String EXTRA_SAVE_STATE = "mDoSaveState4SaveInstanceArg";

    private String mRoundId;
    private boolean mDoSave = false;
    private ContentObserver mMyObserver;
    private static LoaderManager sLoaderManager;
    private static LoaderManager.LoaderCallbacks sLoaderCallback;

    private TextView club, course, p1First, p1Last, p2First, p2Last, p3First, p3Last, p4First, p4Last;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Timber.d("onCreate mDoSave: " + String.valueOf(mDoSave));
        Bundle roundIdBundle;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_round);

        club = (TextView) findViewById(clubName);
        course = (TextView) findViewById(courseName);
        p1First = (TextView) findViewById(R.id.p1First);
        p1Last = (TextView) findViewById(R.id.p1Last);
        p2First = (TextView) findViewById(R.id.p2First);
        p2Last = (TextView) findViewById(R.id.p2Last);
        p3First = (TextView) findViewById(R.id.p3First);
        p3Last = (TextView) findViewById(R.id.p3Last);
        p4First = (TextView) findViewById(R.id.p4First);
        p4Last = (TextView) findViewById(R.id.p4Last);

        mMyObserver = new MyObserver(new Handler());
        sLoaderManager = getSupportLoaderManager();
        sLoaderCallback = this;
        getContentResolver()
              .registerContentObserver(Contract.Rounds.buildDirUri(), true, mMyObserver);
        getContentResolver()
              .registerContentObserver(Contract.RoundPlayers.buildDirUri(), true, mMyObserver);

        Timber.d("Saved Instance: " + String.valueOf(savedInstanceState != null));
        if (savedInstanceState != null) {
            roundIdBundle = savedInstanceState;
            mDoSave = savedInstanceState.getBoolean(EXTRA_SAVE_STATE);
            mRoundId = savedInstanceState.getString(EXTRA_ROUND_ID);
        } else {
            roundIdBundle = getIntent().getExtras();
            if (roundIdBundle != null) {
                mDoSave = true;
            }
        }

        Timber.d("RoundId Initialized: " + String.valueOf(mRoundId!=null));
        Timber.d("mDoSave: " + String.valueOf(mDoSave));
        sLoaderManager.initLoader(0, roundIdBundle, sLoaderCallback);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(EXTRA_ROUND_ID, mRoundId);
        outState.putBoolean(EXTRA_SAVE_STATE, mDoSave);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        if (isFinishing() & !mDoSave) {
            ContentResolver resolver = getContentResolver();
            resolver.delete(
                  Contract.Rounds.buildDirUri(),
                  Contract.Rounds._ID + "=?",
                  new String[]{mRoundId}
            );
            Timber.d("RoundID DEL: " + mRoundId);
            Timber.d("onDestroy mDoSave: " + String.valueOf(mDoSave));
        }

        getContentResolver().unregisterContentObserver(mMyObserver);
        super.onDestroy();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Timber.d("Loader Args Are: " + String.valueOf(args != null));
        Timber.d("RoundId Initialized: " + String.valueOf(mRoundId!=null));
        if (mRoundId == null) {
            if (args != null) {
                mRoundId = args.getString(EXTRA_ROUND_ID);
            } else {
                ContentResolver resolver = getContentResolver();
                Uri insertUri = resolver.insert(Contract.Rounds.buildDirUri(), new ContentValues());
                mRoundId = String.valueOf(ContentUris.parseId(insertUri));
                Timber.d("Triggered new Round Entry in Table With ID: " + mRoundId);
            }
        }
        String[] roundId = new String[]{mRoundId};
        String roundWhereClause = Contract.MatchesView.ROUND_ID + "=?";
        return new CursorLoader(getApplicationContext(),
              Contract.MatchesView.buildDirUri(),
              null,
              roundWhereClause,
              roundId,
              null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor != null && cursor.moveToFirst()) {
            String str = cursor.getString(Contract.MatchesView.CLUBNAME_COL_INDEX);
            if (!(str == null || str.isEmpty() || str.equalsIgnoreCase("null"))) {
                club.setText(str);
                club.setTypeface(null, Typeface.NORMAL);
            } else {
                club.setText(getString(R.string.coursePlaceHolder));
                club.setTypeface(null, Typeface.ITALIC);
            }
            str = cursor.getString(Contract.MatchesView.COURSENAME_COL_INDEX);
            if (!(str == null || str.isEmpty() || str.equalsIgnoreCase("null"))) {
                course.setText(str);
                course.setVisibility(View.VISIBLE);
            } else {
                course.setVisibility(View.INVISIBLE);
            }
            str = cursor.getString(Contract.MatchesView.P1_FIRST_COL_INDEX);
            if (!(str == null || str.isEmpty() || str.equalsIgnoreCase("null"))) {
                p1First.setText(str);
                p1First.setTypeface(null, Typeface.NORMAL);
            } else {
                p1First.setText(getString(R.string.playerPlaceHolder));
                p1First.setTypeface(null, Typeface.ITALIC);
            }
            str = cursor.getString(Contract.MatchesView.P1_LAST_COL_INDEX);
            if (!(str == null || str.isEmpty() || str.equalsIgnoreCase("null"))) {
                p1Last.setText(str);
                p1Last.setVisibility(View.VISIBLE);
            } else {
                p1Last.setVisibility(View.INVISIBLE);
            }
            str = cursor.getString(Contract.MatchesView.P2_FIRST_COL_INDEX);
            if (!(str == null || str.isEmpty() || str.equalsIgnoreCase("null"))) {
                p2First.setText(str);
                p2First.setTypeface(null, Typeface.NORMAL);
            } else {
                p2First.setText(getString(R.string.playerPlaceHolder));
                p2First.setTypeface(null, Typeface.ITALIC);
            }
            str = cursor.getString(Contract.MatchesView.P2_LAST_COL_INDEX);
            if (!(str == null || str.isEmpty() || str.equalsIgnoreCase("null"))) {
                p2Last.setText(str);
                p2Last.setVisibility(View.VISIBLE);

            } else {
                p2Last.setVisibility(View.INVISIBLE);
            }
            str = cursor.getString(Contract.MatchesView.P3_FIRST_COL_INDEX);
            if (!(str == null || str.isEmpty() || str.equalsIgnoreCase("null"))) {
                p3First.setText(str);
                p3First.setTypeface(null, Typeface.NORMAL);
            } else {
                p3First.setText(getString(R.string.playerPlaceHolder));
                p3First.setTypeface(null, Typeface.ITALIC);
            }
            str = cursor.getString(Contract.MatchesView.P3_LAST_COL_INDEX);
            if (!(str == null || str.isEmpty() || str.equalsIgnoreCase("null"))) {
                p3Last.setText(str);
                p3Last.setVisibility(View.VISIBLE);

            } else {
                p3Last.setVisibility(View.INVISIBLE);
            }
            str = cursor.getString(Contract.MatchesView.P4_FIRST_COL_INDEX);
            if (!(str == null || str.isEmpty() || str.equalsIgnoreCase("null"))) {
                p4First.setText(str);
                p4First.setTypeface(null, Typeface.NORMAL);
            } else {
                p4First.setText(getString(R.string.playerPlaceHolder));
                p4First.setTypeface(null, Typeface.ITALIC);
            }
            str = cursor.getString(Contract.MatchesView.P4_LAST_COL_INDEX);
            if (!(str == null || str.isEmpty() || str.equalsIgnoreCase("null"))) {
                p4Last.setText(str);
                p4Last.setVisibility(View.VISIBLE);
            } else {
                p4Last.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

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
            Timber.d("Observed Change in Rounds Table");
            sLoaderManager.restartLoader(0, null, sLoaderCallback);
        }
    }

    public void getCourse(View view) {
        startPlayerCourseIntent(REQUEST_CODE_COURSE, club.getText().toString());
    }

    public void getP1(View view) {
        startPlayerCourseIntent(REQUEST_CODE_P1, p1First.getText().toString());
    }

    public void getP2(View view) {
        startPlayerCourseIntent(REQUEST_CODE_P2, p2First.getText().toString());
    }

    public void getP3(View view) {

        startPlayerCourseIntent(REQUEST_CODE_P3, p3First.getText().toString());
    }

    public void getP4(View view) {
        startPlayerCourseIntent(REQUEST_CODE_P4, p4First.getText().toString());
    }

    private void startPlayerCourseIntent(final int requestCode, String text) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(StartRoundActivity.this);
        alertDialog.setTitle(getString(R.string.dialogChange));
        String buttonMsg;
        Timber.d(text);
        if (text.equals(getString(R.string.playerPlaceHolder)) | text.equals(getString(R.string.coursePlaceHolder)) | text.isEmpty()) {
            buttonMsg = getString(R.string.dialogAddButton);
        } else {
            buttonMsg = getString(R.string.dialogEditButton);
        }

        alertDialog.setPositiveButton(buttonMsg,
              new DialogInterface.OnClickListener() {
                  public void onClick(DialogInterface dialog, int which) {
                      if (requestCode == REQUEST_CODE_COURSE) {
                          Intent intent = new Intent(getApplicationContext(), CoursesActivity.class);
                          startActivityForResult(intent, requestCode);
                      } else {
                          Intent intent = new Intent(getApplicationContext(), PlayerActivity.class);
                          startActivityForResult(intent, requestCode);
                      }
                  }
              }
        );

        alertDialog.setNegativeButton(getString(R.string.dialogDelete),
              new DialogInterface.OnClickListener() {
                  public void onClick(DialogInterface dialog, int which) {
                      Timber.d(String.valueOf(requestCode));
                      ContentResolver resolver = getContentResolver();
                      switch (requestCode) {
                          case REQUEST_CODE_COURSE:
                              ContentValues values = new ContentValues();
                              values.putNull(Contract.Rounds.COURSE_ID);
                              resolver.update(
                                    Contract.Rounds.buildDirUri(),
                                    values,
                                    Contract.Rounds._ID + "=?",
                                    new String[] {mRoundId}
                              );
                              break;
                          case REQUEST_CODE_P1:
                              resolver.delete(
                                    Contract.RoundPlayers.buildDirUri(),
                                    Contract.RoundPlayers.ROUND_ID + "=? AND " + Contract.RoundPlayers.PLAYER_ORDER + "=?",
                                    new String[]{mRoundId, "1"}
                              );
                              break;
                          case REQUEST_CODE_P2:
                              resolver.delete(
                                    Contract.RoundPlayers.buildDirUri(),
                                    Contract.RoundPlayers.ROUND_ID + "=? AND " + Contract.RoundPlayers.PLAYER_ORDER + "=?",
                                    new String[]{mRoundId, "2"}
                              );
                              break;
                          case REQUEST_CODE_P3:
                              resolver.delete(
                                    Contract.RoundPlayers.buildDirUri(),
                                    Contract.RoundPlayers.ROUND_ID + "=? AND " + Contract.RoundPlayers.PLAYER_ORDER + "=?",
                                    new String[]{mRoundId, "3"}
                              );
                              break;
                          case REQUEST_CODE_P4:
                              resolver.delete(
                                    Contract.RoundPlayers.buildDirUri(),
                                    Contract.RoundPlayers.ROUND_ID + "=? AND " + Contract.RoundPlayers.PLAYER_ORDER + "=?",
                                    new String[]{mRoundId, "4"}
                              );
                              break;
                      }
                      dialog.cancel();
                  }});

        alertDialog.setNeutralButton(getString(R.string.dialogCancelButton),
              new DialogInterface.OnClickListener() {
                  public void onClick(DialogInterface dialog, int which) {
                      dialog.cancel();
                  }}
              );
        Dialog d = alertDialog.show();
        DialogUtils.doKeepDialog(d);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ContentValues values = new ContentValues();
        ContentResolver resolver = getContentResolver();
        String playerId;

        if (resultCode == RESULT_OK){
            switch (requestCode) {
                case REQUEST_CODE_COURSE:
                    String courseID = data.getStringExtra(EXTRA_RETURN_ID);
                    values.put(Contract.Rounds.COURSE_ID, courseID);
                    resolver.update(
                          Contract.Rounds.buildDirUri(),
                          values,
                          Contract.Rounds._ID + "=?",
                          new String[]{mRoundId});
                    values.clear();
                    break;
                case REQUEST_CODE_P1:
                    playerId = data.getStringExtra(EXTRA_RETURN_ID);
                    values.put(Contract.RoundPlayers.PLAYER_ID, playerId);
                    values.put(Contract.RoundPlayers.ROUND_ID, mRoundId);
                    //Update Player 1 with new player info
                    int rowUpdated1 = resolver.update(
                          Contract.Rounds.buildDirUri(),
                          values,
                          Contract.RoundPlayers.PLAYER_ID + "=? AND " + Contract.RoundPlayers.PLAYER_ORDER + "=?",
                          new String[]{mRoundId, "1"});
                    //If no player is selected for Player 1, create a new row
                    if (rowUpdated1 == 0) {
                        values.put(Contract.RoundPlayers.PLAYER_ORDER, "1");
                        resolver.insert(Contract.Rounds.buildDirUri(),values);
                    }
                    break;
                case REQUEST_CODE_P2:
                    playerId = data.getStringExtra(EXTRA_RETURN_ID);
                    values.put(Contract.RoundPlayers.PLAYER_ID, playerId);
                    values.put(Contract.RoundPlayers.ROUND_ID, mRoundId);
                    //Update Player 2 with new player info
                    int rowUpdated2 = resolver.update(
                          Contract.Rounds.buildDirUri(),
                          values,
                          Contract.RoundPlayers.PLAYER_ID + "=? AND " + Contract.RoundPlayers.PLAYER_ORDER + "=?",
                          new String[]{mRoundId, "2"});
                    //If no player is selected for Player 2, create a new row
                    if (rowUpdated2 == 0) {
                        values.put(Contract.RoundPlayers.PLAYER_ORDER, "2");
                        resolver.insert(Contract.Rounds.buildDirUri(),values);
                    }
                    break;
                case REQUEST_CODE_P3:
                    playerId = data.getStringExtra(EXTRA_RETURN_ID);
                    values.put(Contract.RoundPlayers.PLAYER_ID, playerId);
                    values.put(Contract.RoundPlayers.ROUND_ID, mRoundId);
                    //Update Player 3 with new player info
                    int rowUpdated3 = resolver.update(
                          Contract.Rounds.buildDirUri(),
                          values,
                          Contract.RoundPlayers.PLAYER_ID + "=? AND " + Contract.RoundPlayers.PLAYER_ORDER + "=?",
                          new String[]{mRoundId, "3"});
                    //If no player is selected for Player 3, create a new row
                    if (rowUpdated3 == 0) {
                        values.put(Contract.RoundPlayers.PLAYER_ORDER, "3");
                        resolver.insert(Contract.Rounds.buildDirUri(),values);
                    }
                    break;
                case REQUEST_CODE_P4:
                    playerId = data.getStringExtra(EXTRA_RETURN_ID);
                    values.put(Contract.RoundPlayers.PLAYER_ID, playerId);
                    values.put(Contract.RoundPlayers.ROUND_ID, mRoundId);
                    //Update Player 4 with new player info
                    int rowUpdated4 = resolver.update(
                          Contract.Rounds.buildDirUri(),
                          values,
                          Contract.RoundPlayers.PLAYER_ID + "=? AND " + Contract.RoundPlayers.PLAYER_ORDER + "=?",
                          new String[]{mRoundId, "4"});
                    //If no player is selected for Player 4, create a new row
                    if (rowUpdated4 == 0) {
                        values.put(Contract.RoundPlayers.PLAYER_ORDER, "4");
                        resolver.insert(Contract.Rounds.buildDirUri(),values);
                    }
                    break;
            }
        }
    }

    public void startRound(View view) {
        String courseText = course.getText().toString();
        if (courseText.equals(getString(R.string.coursePlaceHolder)) | courseText.equals("")) {
            Toast.makeText(this, getString(R.string.noCourseErrorMsg), Toast.LENGTH_LONG).show();
            return;
        }

        String p1FirstText = p1First.getText().toString();
        if (p1FirstText.equals(getString(R.string.playerPlaceHolder)) | p1FirstText.equals("")) {
            Toast.makeText(this, getString(R.string.noPlayer1ErrorMsg), Toast.LENGTH_LONG).show();
            return;
        }

        SharedPrefUtils.setCurrentRoundId(getApplicationContext(), mRoundId);

        Intent intent = new Intent(getApplicationContext(), RoundActivity.class);
        intent.putExtra(EXTRA_ROUND_ID, mRoundId);
        startActivity(intent);
        mDoSave = true;
    }

    public void onSave(View view) {
        String courseText = course.getText().toString();
        if (courseText.equals(getString(R.string.coursePlaceHolder)) | courseText.equals("")) {
            Toast.makeText(this, getString(R.string.noCourseErrorMsg), Toast.LENGTH_LONG).show();
            return;
        }

        String p1FirstText = p1First.getText().toString();
        if (p1FirstText.equals(getString(R.string.playerPlaceHolder)) | p1FirstText.equals("")) {
            Toast.makeText(this, getString(R.string.noPlayer1ErrorMsg), Toast.LENGTH_LONG).show();
            return;
        }
        mDoSave = true;
        Toast.makeText(getApplicationContext(), getString(R.string.safeRound), Toast.LENGTH_SHORT).show();
    }
}
