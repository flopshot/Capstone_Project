package com.sean.golfranger;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.sean.golfranger.data.Contract;
import com.sean.golfranger.utils.DialogUtils;
import com.sean.golfranger.utils.SharedPrefUtils;

import timber.log.Timber;


public class CoursesActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    private CourseAdapter mCourseAdapter;
    private ContentObserver mMyObserver;
    private static LoaderManager sLoaderManager;
    private static LoaderManager.LoaderCallbacks sLoaderCallback;
    View mLayout;
    private static final String KEY_ANIMATE_RECYCLERVIEW = "keyAnimateRecyclerView";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses);

        mLayout = findViewById(R.id.course_layout);
        mMyObserver = new MyObserver(new Handler());
        sLoaderManager = getSupportLoaderManager();
        getContentResolver()
              .registerContentObserver(Contract.Courses.buildDirUri(), true, mMyObserver);
        sLoaderCallback = this;
        sLoaderManager.initLoader(0, null, sLoaderCallback);

        // my_child_toolbar is defined in the layout file
        Toolbar myToolbar =
        (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        ab.setDisplayShowTitleEnabled(false);

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        boolean doAnimation;
        if (savedInstanceState != null) {
            doAnimation = savedInstanceState.getBoolean(KEY_ANIMATE_RECYCLERVIEW);
        } else {
            doAnimation = true;
        }

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view_courses);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration dividerItemDecoration =
              new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        mCourseAdapter = new CourseAdapter(this, doAnimation,
              new CourseAdapter.CourseAdapterOnClickHandler() {
            @Override
            public void onClick(Long courseId) {
                if (getCallingActivity() == null) {
                    Timber.d("No Activity Called This Intent");
                } else {
                    Intent intent = new Intent();
                    intent.putExtra(StartRoundActivity.EXTRA_RETURN_ID, String.valueOf(courseId));
                    setResult(RESULT_OK, intent);
                    finish();
                    Timber.d("Some Activity DID call this activity");
                }
            }
            }, new CourseAdapter.CourseAdapterEditClickHandler() {
            @Override
            public void onEditClick(Long courseId, String clubName, String courseName, int holeCount) {
                showEditCourseDialog(courseId, clubName, courseName, holeCount);
            }
        });
        recyclerView.setAdapter(mCourseAdapter);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isFinishing()) {
            SharedPrefUtils.setAnimateIds(getApplicationContext(), null);
        }
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

    public void onFabClick(View v){
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

        final EditText holeInput = new EditText(this);
        holeInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        holeInput.setHint(R.string.CourseActivityDialogHoleCnt);
        layout.addView(holeInput);
        alertDialog.setView(layout);

        alertDialog.setPositiveButton(getString(R.string.dialogAddButton),
              new DialogInterface.OnClickListener() {
                  public void onClick(DialogInterface dialog, int which) {
                      String courseName = courseInput.getText().toString().trim();
                      String clubName = clubInput.getText().toString().trim();
                      String holeCount = holeInput.getText().toString().trim();
                      if (!courseName.equals("") & courseName.matches("[a-zA-Z 0-9]+") &
                            !clubName.equals("") & clubName.matches("[a-zA-Z 0-9]+")  &
                            !holeCount.equals("") & holeCount.matches("^([1-9]|[12][0-9]|[3][0-6])$")
                         ) {

                          ContentValues values = new ContentValues();
                          ContentResolver resolver = getContentResolver();
                          values.put(Contract.Courses.CLUB_NAME, clubName);
                          values.put(Contract.Courses.COURSE_NAME, courseName);
                          values.put(Contract.Courses.HOLE_CNT, holeCount);
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

    private void showEditCourseDialog(final long courseId, String club, String course, int holeCount){
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

        final EditText holeInput = new EditText(this);
        holeInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        holeInput.setText(String.valueOf(holeCount));
        layout.addView(holeInput);
        alertDialog.setView(layout);

        alertDialog.setPositiveButton(getString(R.string.dialogOk),
              new DialogInterface.OnClickListener() {
                  public void onClick(DialogInterface dialog, int which) {
                      String courseName = courseInput.getText().toString().trim();
                      String clubName = clubInput.getText().toString().trim();
                      String holeCount = holeInput.getText().toString().trim();
                      if (!courseName.equals("") & courseName.matches("[a-zA-Z 0-9]+") &
                            !clubName.equals("") & clubName.matches("[a-zA-Z 0-9]+")  &
                            !holeCount.equals("") & holeCount.matches("^([1-9]|[12][0-9]|[3][0-6])$")
                            ) {

                          ContentValues values = new ContentValues();
                          ContentResolver resolver = getContentResolver();
                          values.put(Contract.Courses.CLUB_NAME, clubName);
                          values.put(Contract.Courses.COURSE_NAME, courseName);
                          values.put(Contract.Courses.HOLE_CNT, holeCount);
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

        alertDialog.setNegativeButton(getString(R.string.dialogDelete),
              new DialogInterface.OnClickListener() {
                  public void onClick(DialogInterface dialog, int which) {
                      ContentResolver resolver = getContentResolver();
                      ContentValues values = new ContentValues();
                      values.put(Contract.Courses.COURSE_ENABLED, "0");
                      resolver.update(
                            Contract.Courses.buildDirUri(),
                            values,
                            Contract.Courses._ID + "=?",
                            new String[] {String.valueOf(courseId)});
                      dialog.cancel();
                  }});
        Dialog d = alertDialog.show();
        DialogUtils.doKeepDialog(d);
    }

    ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(
          0, ItemTouchHelper.RIGHT) {
        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
            long courseId = mCourseAdapter.getItemId(viewHolder.getAdapterPosition());
            final String id = String.valueOf(courseId);
            ContentResolver resolver = getContentResolver();
            ContentValues v = new ContentValues();

            Timber.d("courseId: " + id);
            v.put(Contract.Courses.COURSE_ENABLED, "0");
            resolver.update(Contract.Courses.buildDirUri(),
                  v,
                  Contract.Courses._ID + "=?",
                  new String[]{id});

            Snackbar snackbar = Snackbar.make(mLayout,
                  R.string.snackBarCourseDeleted,
                  Snackbar.LENGTH_LONG);
            snackbar.setAction(R.string.snackbarUndo,
                  new View.OnClickListener() {
                      @Override
                      public void onClick(View v) {
                          ContentValues values = new ContentValues();
                          values.put(Contract.Courses.COURSE_ENABLED, "1");
                          getContentResolver().update(Contract.Courses.buildDirUri(),
                                values,
                                Contract.Courses._ID + "=?",
                                new String[]{id});
                      }
                  })
                  .setActionTextColor(Color.GREEN);
            snackbar.show();
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                              RecyclerView.ViewHolder target) { return false; }
    };

    ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
}
