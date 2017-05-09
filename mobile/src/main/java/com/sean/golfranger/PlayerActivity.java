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

public class PlayerActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    private PlayerAdapter mPlayerAdapter;
    private ContentObserver mMyObserver;
    private static LoaderManager sLoaderManager;
    private static LoaderManager.LoaderCallbacks sLoaderCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        mMyObserver = new MyObserver(new Handler());
        sLoaderManager = getSupportLoaderManager();
        getContentResolver()
              .registerContentObserver(Contract.Players.buildDirUri(), true, mMyObserver);
        sLoaderCallback = this;
        sLoaderManager.initLoader(0, null, sLoaderCallback);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view_players);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration dividerItemDecoration =
              new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        mPlayerAdapter = new PlayerAdapter(new PlayerAdapter.PlayerAdapterOnClickHandler() {
            @Override
            public void onClick(Long playerId, String firstName, String lastName) {
                if (getCallingActivity() == null) {
                    Timber.d("No Activity called this activity");
                } else {
                    Intent intent = new Intent();
                    intent.putExtra(StartRoundActivity.EXTRA_RETURN_ID, String.valueOf(playerId));
                    intent.putExtra(StartRoundActivity.EXTRA_RETURN_FIRST_ITEM, firstName);
                    intent.putExtra(StartRoundActivity.EXTRA_RETURN_SECOND_ITEM, lastName);
                    setResult(RESULT_OK, intent);
                    finish();
                    Timber.d("Some Activity DID call this activity");
                }
            }
        }, new PlayerAdapter.PlayerAdapterLongClickHandler() {
            @Override
            public void onLongClick(Long playerId, String firstName, String lastName) {
                showEditPlayerDialog(playerId, firstName, lastName);
            }
        });
        recyclerView.setAdapter(mPlayerAdapter);
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

    public void addPlayer(View v){
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
}
