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
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.sean.golfranger.data.Contract;
import com.sean.golfranger.utils.DialogUtils;
import com.sean.golfranger.utils.SharedPrefUtils;

import timber.log.Timber;

public class PlayerActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    private PlayerAdapter mPlayerAdapter;
    private ContentObserver mMyObserver;
    private static LoaderManager sLoaderManager;
    private static LoaderManager.LoaderCallbacks sLoaderCallback;
    View mLayout;
    private static final String KEY_ANIMATE_RECYCLERVIEW = "keyAnimateRecyclerView";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        mLayout = findViewById(R.id.player_layout);
        mMyObserver = new MyObserver(new Handler());
        sLoaderManager = getSupportLoaderManager();
        getContentResolver()
              .registerContentObserver(Contract.Players.buildDirUri(), true, mMyObserver);
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

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view_players);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration dividerItemDecoration =
              new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        boolean doAnimation;
        if (savedInstanceState != null) {
            doAnimation = savedInstanceState.getBoolean(KEY_ANIMATE_RECYCLERVIEW);
        } else {
            doAnimation = true;
        }

        mPlayerAdapter = new PlayerAdapter(getApplicationContext(), doAnimation,
            new PlayerAdapter.PlayerAdapterOnClickHandler() {
            @Override
            public void onClick(Long playerId) {
                if (getCallingActivity() == null) {
                    Timber.d("No Activity called this activity");
                } else {
                    Intent intent = new Intent();
                    intent.putExtra(StartRoundActivity.EXTRA_RETURN_ID, String.valueOf(playerId));
                    setResult(RESULT_OK, intent);
                    finish();
                    Timber.d("Some Activity DID call this activity");
                }
            }
        }, new PlayerAdapter.PlayerAdapterEditClickHandler() {
            @Override
            public void onEditClick(Long playerId, String firstName, String lastName) {
                showEditPlayerDialog(playerId, firstName, lastName);
            }
        });
        recyclerView.setAdapter(mPlayerAdapter);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_ANIMATE_RECYCLERVIEW, isFinishing());
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
              Contract.Players.buildDirUri(),
              null,
              null,
              null,
              null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mPlayerAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mPlayerAdapter.swapCursor(null);
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
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(PlayerActivity.this);
        alertDialog.setTitle(getString(R.string.playerPlaceHolder));

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText firstInput = new EditText(this);
        firstInput.setHint(getString(R.string.dialogFirstNameHint));
        layout.addView(firstInput);

        final EditText lastInput = new EditText(this);
        lastInput.setHint(getString(R.string.dialogLastNameHint));
        layout.addView(lastInput);
        alertDialog.setView(layout);

        alertDialog.setPositiveButton(getString(R.string.dialogAddButton),
              new DialogInterface.OnClickListener() {
                  public void onClick(DialogInterface dialog, int which) {
                      String firstName =firstInput.getText().toString().trim();
                      String lastName = lastInput.getText().toString().trim();
                      if (!firstName.equals("") & firstName.matches("[a-zA-Z 0-9]+") &
                            !lastName.equals("") & lastName.matches("[a-zA-Z 0-9]+") ) {

                          ContentValues values = new ContentValues();
                          ContentResolver resolver = getContentResolver();
                          values.put(Contract.Players.FIRST_NAME, firstName);
                          values.put(Contract.Players.LAST_NAME, lastName);
                          resolver.insert(Contract.Players.buildDirUri(), values);
                          dialog.dismiss();
                          Timber.d("Player Row Entered");
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

        alertDialog.setNegativeButton(getString(R.string.dialogCancelButton),
              new DialogInterface.OnClickListener() {
                  public void onClick(DialogInterface dialog, int which) {
                      dialog.cancel();
                  }});
        Dialog d = alertDialog.show();
        DialogUtils.doKeepDialog(d);
    }

    private void showEditPlayerDialog(final long playerId, String first, String last){
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(PlayerActivity.this);
        alertDialog.setTitle(getString(R.string.dialogEditPlayer));

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText firstInput = new EditText(this);
        firstInput.setText(first);
        layout.addView(firstInput);

        final EditText lastInput = new EditText(this);
        lastInput.setText(last);
        layout.addView(lastInput);
        alertDialog.setView(layout);

        alertDialog.setPositiveButton(getString(R.string.dialogOk),
              new DialogInterface.OnClickListener() {
                  public void onClick(DialogInterface dialog, int which) {
                      String lastName = lastInput.getText().toString().trim();
                      String firstName = firstInput.getText().toString().trim();
                      if (!lastName.equals("") & lastName.matches("[a-zA-Z 0-9]+") &
                            !firstName.equals("") & firstName.matches("[a-zA-Z 0-9]+") ) {

                          ContentValues values = new ContentValues();
                          ContentResolver resolver = getContentResolver();
                          values.put(Contract.Players.FIRST_NAME, firstName);
                          values.put(Contract.Players.LAST_NAME, lastName);
                          resolver.update(
                                Contract.Players.buildDirUri(),
                                values,
                                Contract.Players._ID + "=?",
                                new String[] {String.valueOf(playerId)});
                          dialog.dismiss();
                          Timber.d("Player Row CHANGED");
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
                      values.put(Contract.Players.PLAYER_ENABLED, "0");
                      resolver.update(
                            Contract.Players.buildDirUri(),
                            values,
                            Contract.Players._ID + "=?",
                            new String[] {String.valueOf(playerId)});
                      dialog.cancel();
                  }});
        Dialog d = alertDialog.show();
        DialogUtils.doKeepDialog(d);
    }

    ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(
          0, ItemTouchHelper.RIGHT) {
        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
            long playerId = mPlayerAdapter.getItemId(viewHolder.getAdapterPosition());
            final String id = String.valueOf(playerId);
            ContentResolver resolver = getContentResolver();
            ContentValues v = new ContentValues();

            Timber.d("playerId: " + id);
            v.put(Contract.Players.PLAYER_ENABLED, "0");
            resolver.update(Contract.Players.buildDirUri(),
                  v,
                  Contract.Players._ID + "=?",
                  new String[]{id});
            v.clear();

            Snackbar snackbar = Snackbar.make(mLayout,
                        R.string.playerSnackBarDelete,
                  Snackbar.LENGTH_LONG);
            snackbar.setAction(R.string.snackbarUndo,
                  new View.OnClickListener() {
                      @Override
                      public void onClick(View v) {
                          ContentValues values = new ContentValues();
                          values.put(Contract.Players.PLAYER_ENABLED, "1");
                          getContentResolver().update(Contract.Players.buildDirUri(),
                                values,
                                Contract.Players._ID + "=?",
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