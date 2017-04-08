package com.sean.golfranger;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
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
import com.sean.golfranger.utils.SharedPrefUtils;

import timber.log.Timber;

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

        club = (TextView) findViewById(R.id.clubName);
        course = (TextView) findViewById(R.id.courseName);
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
        getContentResolver()
              .registerContentObserver(Contract.Rounds.buildDirUri(), true, mMyObserver);
        sLoaderCallback = this;

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
    protected void onStop() {
        super.onStop();
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
        String roundWhereClause = Contract.Rounds._ID + "=?";
        String[] roundId;
        Timber.d("Loader Args Are: " + String.valueOf(args != null));
        Timber.d("RoundId Initialized: " + String.valueOf(mRoundId!=null));
        if (mRoundId == null) {
            if (args != null) {
                mRoundId = args.getString(EXTRA_ROUND_ID);
            } else {
                Long tsLong = System.currentTimeMillis();
                String ts = tsLong.toString();
                ContentValues values = new ContentValues();
                ContentResolver resolver = getContentResolver();
                values.put(Contract.Rounds.DATE, ts);
                Uri insertUri = resolver.insert(Contract.Rounds.buildDirUri(), values);
                mRoundId = String.valueOf(ContentUris.parseId(insertUri));
                Timber.d("Triggered new Round Entry in Table With ID: " + mRoundId);
            }
        }
        roundId = new String[]{mRoundId};
        return new CursorLoader(getApplicationContext(),
              Contract.RoundCoursesPlayers.roundCoursesPlayersUri(),
              null,
              roundWhereClause,
              roundId,
              null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor != null && cursor.moveToFirst()) {
            String str = cursor.getString(1);
            if (!(str == null || str.isEmpty() || str.equalsIgnoreCase("null"))) {
                club.setText(str);
            } else {
                club.setText("Add Course");
            }
            str = cursor.getString(2);
            if (!(str == null || str.isEmpty() || str.equalsIgnoreCase("null"))) {
                course.setText(str);
                course.setVisibility(View.VISIBLE);
            } else {
                course.setVisibility(View.INVISIBLE);
            }
            str = cursor.getString(3);
            if (!(str == null || str.isEmpty() || str.equalsIgnoreCase("null"))) {
                Timber.d("First Name of P1: " + str);
                p1First.setText(str);
            } else {
                p1First.setText("Add Player");
            }
            str = cursor.getString(4);
            if (!(str == null || str.isEmpty() || str.equalsIgnoreCase("null"))) {
                p1Last.setText(str);
                p1Last.setVisibility(View.VISIBLE);

            } else {
                p1Last.setVisibility(View.INVISIBLE);
            }
            str = cursor.getString(5);
            if (!(str == null || str.isEmpty() || str.equalsIgnoreCase("null"))) {
                p2First.setText(str);
            } else {
                p2First.setText("Add Player");
            }
            str = cursor.getString(6);
            if (!(str == null || str.isEmpty() || str.equalsIgnoreCase("null"))) {
                p2Last.setText(str);
                p2Last.setVisibility(View.VISIBLE);

            } else {
                p2Last.setVisibility(View.INVISIBLE);
            }
            str = cursor.getString(7);
            if (!(str == null || str.isEmpty() || str.equalsIgnoreCase("null"))) {
                p3First.setText(str);
            } else {
                p3First.setText("Add Player");
            }
            str = cursor.getString(8);
            if (!(str == null || str.isEmpty() || str.equalsIgnoreCase("null"))) {
                p3Last.setText(str);
                p3Last.setVisibility(View.VISIBLE);

            } else {
                p3Last.setVisibility(View.INVISIBLE);
            }
            str = cursor.getString(9);
            if (!(str == null || str.isEmpty() || str.equalsIgnoreCase("null"))) {
                p4First.setText(str);
            } else {
                p4First.setText("Add Player");
            }
            str = cursor.getString(10);
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
        //TODO ADD STRINGS to strings.xml
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(StartRoundActivity.this);
        alertDialog.setTitle("Change");
        String buttonMsg;
        Timber.d(text);
        if (text.equals("Add Player") | text.equals("Add Course") | text.isEmpty()) {
            buttonMsg = "ADD";
        } else {
            buttonMsg = "EDIT";
        }

        alertDialog.setNegativeButton(buttonMsg,
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

        alertDialog.setPositiveButton("DELETE",
              new DialogInterface.OnClickListener() {
                  public void onClick(DialogInterface dialog, int which) {
                      Timber.d(String.valueOf(requestCode));
                      ContentValues values = new ContentValues();
                      switch (requestCode) {
                          case REQUEST_CODE_COURSE:
                              values.putNull(Contract.Rounds.COURSE_ID);
                              values.putNull(Contract.Rounds.COURSE_NAME);
                              values.putNull(Contract.Rounds.CLUB_NAME);
                              break;
                          case REQUEST_CODE_P1:
                              Timber.d("Putting Null Values in Player 1 Round Info");
                              values.putNull(Contract.Rounds.PLAYER1_ID);
                              values.putNull(Contract.Rounds.PLAYER1_FIRST_NAME);
                              values.putNull(Contract.Rounds.PLAYER1_LAST_NAME);
                              break;
                          case REQUEST_CODE_P2:
                              values.putNull(Contract.Rounds.PLAYER2_ID);
                              values.putNull(Contract.Rounds.PLAYER2_FIRST_NAME);
                              values.putNull(Contract.Rounds.PLAYER2_LAST_NAME);
                              break;
                          case REQUEST_CODE_P3:
                              values.putNull(Contract.Rounds.PLAYER3_ID);
                              values.putNull(Contract.Rounds.PLAYER3_FIRST_NAME);
                              values.putNull(Contract.Rounds.PLAYER3_LAST_NAME);
                              break;
                          case REQUEST_CODE_P4:
                              values.putNull(Contract.Rounds.PLAYER4_ID);
                              values.putNull(Contract.Rounds.PLAYER4_FIRST_NAME);
                              values.putNull(Contract.Rounds.PLAYER4_LAST_NAME);
                              break;
                      }

                      ContentResolver resolver = getContentResolver();
                      resolver.update(
                            Contract.Rounds.buildDirUri(),
                            values,
                            Contract.Rounds._ID + "=?",
                            new String[]{mRoundId});
                      values.clear();
                      dialog.cancel();

                  }});

        alertDialog.setNeutralButton("CANCEL",
              new DialogInterface.OnClickListener() {
                  public void onClick(DialogInterface dialog, int which) {
                      dialog.cancel();
                  }}
              );
        alertDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ContentValues values = new ContentValues();
        ContentResolver resolver = getContentResolver();
        String playerId;
        String firstName;
        String lastName;

        if (resultCode == RESULT_OK){
            switch (requestCode) {
                case REQUEST_CODE_COURSE:
                    String courseID = data.getStringExtra(EXTRA_RETURN_ID);
                    String clubName = data.getStringExtra(EXTRA_RETURN_FIRST_ITEM);
                    String courseName = data.getStringExtra(EXTRA_RETURN_SECOND_ITEM);

                    values.put(Contract.Rounds.COURSE_ID, courseID);
                    values.put(Contract.Rounds.CLUB_NAME, clubName);
                    values.put(Contract.Rounds.COURSE_NAME, courseName);
                    resolver.update(
                          Contract.Rounds.buildDirUri(),
                          values,
                          Contract.Rounds._ID + "=?",
                          new String[]{mRoundId});
                    values.clear();
                    break;
                case REQUEST_CODE_P1:
                    playerId = data.getStringExtra(EXTRA_RETURN_ID);
                    firstName = data.getStringExtra(EXTRA_RETURN_FIRST_ITEM);
                    lastName = data.getStringExtra(EXTRA_RETURN_SECOND_ITEM);

                    resolver = getContentResolver();
                    values.put(Contract.Rounds.PLAYER1_ID, playerId);
                    values.put(Contract.Rounds.PLAYER1_FIRST_NAME, firstName);
                    values.put(Contract.Rounds.PLAYER1_LAST_NAME, lastName);
                    resolver.update(
                          Contract.Rounds.buildDirUri(),
                          values,
                          Contract.Rounds._ID + "=?",
                          new String[]{mRoundId});
                    values.clear();
                    break;
                case REQUEST_CODE_P2:
                    playerId = data.getStringExtra(EXTRA_RETURN_ID);
                    firstName = data.getStringExtra(EXTRA_RETURN_FIRST_ITEM);
                    lastName = data.getStringExtra(EXTRA_RETURN_SECOND_ITEM);

                    resolver = getContentResolver();
                    values.put(Contract.Rounds.PLAYER2_ID, playerId);
                    values.put(Contract.Rounds.PLAYER2_FIRST_NAME, firstName);
                    values.put(Contract.Rounds.PLAYER2_LAST_NAME, lastName);
                    resolver.update(
                          Contract.Rounds.buildDirUri(),
                          values,
                          Contract.Rounds._ID + "=?",
                          new String[]{mRoundId});
                    values.clear();
                    break;
                case REQUEST_CODE_P3:
                    playerId = data.getStringExtra(EXTRA_RETURN_ID);
                    firstName = data.getStringExtra(EXTRA_RETURN_FIRST_ITEM);
                    lastName = data.getStringExtra(EXTRA_RETURN_SECOND_ITEM);

                    resolver = getContentResolver();
                    values.put(Contract.Rounds.PLAYER3_ID, playerId);
                    values.put(Contract.Rounds.PLAYER3_FIRST_NAME, firstName);
                    values.put(Contract.Rounds.PLAYER3_LAST_NAME, lastName);
                    resolver.update(
                          Contract.Rounds.buildDirUri(),
                          values,
                          Contract.Rounds._ID + "=?",
                          new String[]{mRoundId});
                    values.clear();
                    break;
                case REQUEST_CODE_P4:
                    playerId = data.getStringExtra(EXTRA_RETURN_ID);
                    firstName = data.getStringExtra(EXTRA_RETURN_FIRST_ITEM);
                    lastName = data.getStringExtra(EXTRA_RETURN_SECOND_ITEM);

                    resolver = getContentResolver();
                    values.put(Contract.Rounds.PLAYER4_ID, playerId);
                    values.put(Contract.Rounds.PLAYER4_FIRST_NAME, firstName);
                    values.put(Contract.Rounds.PLAYER4_LAST_NAME, lastName);
                    resolver.update(
                          Contract.Rounds.buildDirUri(),
                          values,
                          Contract.Rounds._ID + "=?",
                          new String[]{mRoundId});
                    values.clear();
                    break;
            }
        }
    }

    public void startRound(View view) {
        String courseText = course.getText().toString();
        if (courseText.equals("Add Course") | courseText.equals("")) {
            Toast.makeText(this, "You Must Add A Course Before We Begin", Toast.LENGTH_LONG).show();
            return;
        }

        String p1FirstText = p1First.getText().toString();
        if (p1FirstText.equals("Add Player") | p1FirstText.equals("")) {
            Toast.makeText(this, "You Must Add A First Player Before We Begin", Toast.LENGTH_LONG).show();
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
        if (courseText.equals("Add Course") | courseText.equals("")) {
            Toast.makeText(this, "You Must Add A Course Before We Begin", Toast.LENGTH_LONG).show();
            return;
        }

        String p1FirstText = p1First.getText().toString();
        if (p1FirstText.equals("Add Player") | p1FirstText.equals("")) {
            Toast.makeText(this, "You Must Add A First Player Before We Begin", Toast.LENGTH_LONG).show();
            return;
        }
        mDoSave = true;
        Toast.makeText(getApplicationContext(), "Round is Safe With Me!", Toast.LENGTH_SHORT).show();
    }
}