package com.sean.golfranger;

import android.app.Dialog;
import android.content.ContentResolver;
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
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.sean.golfranger.data.Contract;
import com.sean.golfranger.utils.DialogUtils;

import timber.log.Timber;

public class CoursesActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    private CourseAdapter mCourseAdapter;
    private ContentObserver mMyObserver;
    private static LoaderManager sLoaderManager;
    private static LoaderManager.LoaderCallbacks sLoaderCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses);

        mMyObserver = new MyObserver(new Handler());
        sLoaderManager = getSupportLoaderManager();
        getContentResolver()
              .registerContentObserver(Contract.Courses.buildDirUri(), true, mMyObserver);
        sLoaderCallback = this;
        sLoaderManager.initLoader(0, null, sLoaderCallback);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view_courses);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration dividerItemDecoration =
              new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        mCourseAdapter = new CourseAdapter(new CourseAdapter.CourseAdapterOnClickHandler() {
            @Override
            public void onClick(Long courseId, String clubName, String courseName) {
                if (getCallingActivity() == null) {
                    Timber.d("No Activity Called This Intent");
                } else {
                    Intent intent = new Intent();
                    intent.putExtra(StartRoundActivity.EXTRA_RETURN_ID, String.valueOf(courseId));
                    intent.putExtra(StartRoundActivity.EXTRA_RETURN_FIRST_ITEM, clubName);
                    intent.putExtra(StartRoundActivity.EXTRA_RETURN_SECOND_ITEM, courseName);
                    setResult(RESULT_OK, intent);
                    finish();
                    Timber.d("Some Activity DID call this activity");
                }
            }
            }, new CourseAdapter.CourseAdapterLongClickHandler() {
            @Override
            public void onLongClick(Long courseId, String clubName, String courseName) {
                showEditCourseDialog(courseId, clubName, courseName);
            }
        });
        recyclerView.setAdapter(mCourseAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getContentResolver().unregisterContentObserver(mMyObserver);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getApplicationContext(),
              Contract.Courses.buildDirUri(),
              null,
              null,
              null,
              null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mCourseAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCourseAdapter.swapCursor(null);
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

    public void addCourse(View v){
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(CoursesActivity.this);
        alertDialog.setTitle(getString(R.string.addCourseButton));

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText clubInput = new EditText(this);
        clubInput.setHint(getString(R.string.dialogClubNameHint));
        layout.addView(clubInput);

        final EditText courseInput = new EditText(this);
        courseInput.setHint(getString(R.string.dialogCourseNameHint));
        layout.addView(courseInput);
        alertDialog.setView(layout);

        alertDialog.setPositiveButton(getString(R.string.dialogAddButton),
              new DialogInterface.OnClickListener() {
                  public void onClick(DialogInterface dialog, int which) {
                      String courseName = courseInput.getText().toString().trim();
                      String clubName = clubInput.getText().toString().trim();
                      if (!courseName.equals("") & courseName.matches("[a-zA-Z 0-9]+") &
                            !clubName.equals("") & clubName.matches("[a-zA-Z 0-9]+") ) {

                          ContentValues values = new ContentValues();
                          ContentResolver resolver = getContentResolver();
                          values.put(Contract.Courses.CLUB_NAME, clubName);
                          values.put(Contract.Courses.COURSE_NAME, courseName);
                          resolver.insert(Contract.Courses.buildDirUri(), values);
                          dialog.dismiss();
                          Timber.d("Course Row Entered");
                      } else {
                          Toast.makeText(
                                getApplicationContext(),
                                getString(R.string.dialogErrorMsg),
                                Toast.LENGTH_LONG
                          ).show();
                      }
                  }
              }
        );

        alertDialog.setNegativeButton(getString(R.string.dialogCancelButton),
              new DialogInterface.OnClickListener() {
                  public void onClick(DialogInterface dialog, int which) {
                      dialog.cancel();
                  }});
        Dialog d = alertDialog.show();
        DialogUtils.doKeepDialog(d);
    }

    private void showEditCourseDialog(final long courseId, String club, String course){
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(CoursesActivity.this);
        alertDialog.setTitle(getString(R.string.dialogEditCourse));

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText clubInput = new EditText(this);
        clubInput.setText(club);
        layout.addView(clubInput);

        final EditText courseInput = new EditText(this);
        courseInput.setText(course);
        layout.addView(courseInput);
        alertDialog.setView(layout);

        alertDialog.setNegativeButton(getString(R.string.dialogOk),
              new DialogInterface.OnClickListener() {
                  public void onClick(DialogInterface dialog, int which) {
                      String courseName = courseInput.getText().toString().trim();
                      String clubName = clubInput.getText().toString().trim();
                      if (!courseName.equals("") & courseName.matches("[a-zA-Z 0-9]+") &
                            !clubName.equals("") & clubName.matches("[a-zA-Z 0-9]+") ) {

                          ContentValues values = new ContentValues();
                          ContentResolver resolver = getContentResolver();
                          values.put(Contract.Courses.CLUB_NAME, clubName);
                          values.put(Contract.Courses.COURSE_NAME, courseName);
                          resolver.update(
                                Contract.Courses.buildDirUri(),
                                values,
                                Contract.Courses._ID + "=?",
                                new String[] {String.valueOf(courseId)});
                          dialog.dismiss();
                          Timber.d("Course Row CHANGED");
                      } else {
                          Toast.makeText(
                                getApplicationContext(),
                                getString(R.string.dialogErrorMsg),
                                Toast.LENGTH_SHORT
                          ).show();
                      }
                  }
              }
        );

        alertDialog.setPositiveButton(getString(R.string.dialogDelete),
              new DialogInterface.OnClickListener() {
                  public void onClick(DialogInterface dialog, int which) {
                      ContentResolver resolver = getContentResolver();
                      resolver.delete(
                            Contract.Courses.buildDirUri(),
                            Contract.Courses._ID + "=?",
                            new String[] {String.valueOf(courseId)});
                      dialog.cancel();
                  }});
        Dialog d = alertDialog.show();
        DialogUtils.doKeepDialog(d);
    }
}
