package com.sean.golfranger.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static com.sean.golfranger.data.DbHelper.getHelper;

/**
 * Content Provider used for communication betweeen the app and the database cache store
 */

public class Provider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private SQLiteDatabase mDb;
    private DbHelper mOpenHelper;
    static final int PLAYER = 100;
    static final int COURSE = 101;
    static final int ROUND = 102;
    static final int HOLE = 103;
    static final int ROUND_HOLE = 104;


    // START: implement table JOIN logic on Holes and Rounds tables for Content Provider
    private static final SQLiteQueryBuilder sHoleAndRoundQueryBuilder;

    static{
        sHoleAndRoundQueryBuilder = new SQLiteQueryBuilder();

        //This is an inner join which looks like
        //weather INNER JOIN location ON weather.location_id = location._id
        sHoleAndRoundQueryBuilder.setTables(
              Contract.Rounds.TABLE_NAME + " LEFT JOIN " +
                    Contract.Holes.TABLE_NAME +
                    " ON " + Contract.Rounds.TABLE_NAME +
                    "." + Contract.Rounds._ID +
                    " = " + Contract.Holes.TABLE_NAME +
                    "." + Contract.Holes.ROUND_ID);
    }

    private Cursor getRoundWithHoleCursor(String[] columns, String whereClause, String sortOrder) {
        return sHoleAndRoundQueryBuilder.query(mOpenHelper.getReadableDatabase(),
              columns,
              whereClause,
              null,
              null,
              null,
              sortOrder
        );
    }
    // END: implement table JOIN logic on Holes and Rounds tables for Content Provider

    static UriMatcher buildUriMatcher() {
        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = Contract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, Contract.Players.TABLE_NAME, PLAYER);
        matcher.addURI(authority, Contract.Courses.TABLE_NAME, COURSE);
        matcher.addURI(authority, Contract.Rounds.TABLE_NAME, ROUND);
        matcher.addURI(authority, Contract.Holes.TABLE_NAME, HOLE);
        matcher.addURI(authority,
              Contract.Rounds.TABLE_NAME + "/" + Contract.Holes.TABLE_NAME,
              ROUND_HOLE);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = getHelper(getContext());
        mDb = mOpenHelper.getWritableDatabase();
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] columns, String whereClause, String[] whereArgs,
                        String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case PLAYER:
                return mDb.query(
                      Contract.Players.TABLE_NAME, columns,
                      whereClause, whereArgs, null, null, sortOrder
                );
            case COURSE:
                return mDb.query(
                      Contract.Courses.TABLE_NAME, columns,
                      whereClause, whereArgs, null, null, sortOrder
                );
            case ROUND:
                return mDb.query(
                      Contract.Rounds.TABLE_NAME, columns,
                      whereClause, whereArgs, null, null, sortOrder
                );
            case HOLE:
                return mDb.query(
                      Contract.Holes.TABLE_NAME, columns,
                      whereClause, whereArgs, null, null, sortOrder
                );
            case ROUND_HOLE:
                return getRoundWithHoleCursor(columns, whereClause, sortOrder);
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public String getType(@NonNull Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case PLAYER:
                return Contract.Players.CONTENT_TYPE;
            case COURSE:
                return Contract.Courses.CONTENT_TYPE;
            case ROUND:
                return Contract.Rounds.CONTENT_TYPE;
            case HOLE:
                return Contract.Holes.CONTENT_TYPE;
            case ROUND_HOLE:
                return Contract.Rounds.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case PLAYER: {
                long _id = mDb.insert(Contract.Players.TABLE_NAME, null, contentValues);
                if ( _id > 0 )
                    returnUri = Contract.Players.buildItemUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case COURSE: {
                long _id = mDb.insert(Contract.Courses.TABLE_NAME, null, contentValues);
                if ( _id > 0)
                    returnUri = Contract.Courses.buildItemUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into" + uri);
                break;
            }
            // INSERT_ONE_ROUND => INSERT_18_HOLES
            case ROUND: {
                mDb.beginTransaction();
                long _id = mDb.insert(Contract.Rounds.TABLE_NAME, null, contentValues);
                if ( _id == 0) {
                    throw new android.database.SQLException("Failed to insert row into" + uri);
                }
                int holesCountInserted = bulkInsert18HolesForNewRound(_id);
                if (holesCountInserted != 18) {
                    throw new android.database.SQLException("Failed to insert row into" + uri);
                }
                returnUri = Contract.Rounds.buildItemUri(_id);
                mDb.setTransactionSuccessful();
                mDb.endTransaction();
                break;
            }
//            case HOLE: {
//                long _id = mDb.insert(Contract.Holes.TABLE_NAME, null, contentValues);
//                if ( _id > 0)
//                    returnUri = Contract.Holes.buildItemUri(_id);
//                else
//                    throw new android.database.SQLException("Failed to insert row into " + uri);
//                break;
//            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String whereClause, String[] whereArgs) {
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == whereClause ) whereClause = "1";
        switch (match) {
            case PLAYER:
                rowsDeleted = mDb.delete(
                      Contract.Players.TABLE_NAME, whereClause, whereArgs);
                break;
            case COURSE:
                rowsDeleted = mDb.delete(
                      Contract.Courses.TABLE_NAME, whereClause, whereArgs);
                break;
            case ROUND:
                // Here, the rows in the holes table that correspond to the round by roundId
                // are deleted first. If successful, the row(s) in the rounds table corresponding
                // to the roundId in method is/are deleted

                android.database.Cursor checkDeletedRoundsCursor = mDb.query(
                      Contract.Rounds.TABLE_NAME,
                      new String[] {Contract.Rounds._ID},
                      whereClause,
                      whereArgs,
                      null,
                      null,
                      null);

                int idCount = checkDeletedRoundsCursor.getCount();

                if (idCount < 1) {
                    checkDeletedRoundsCursor.close();
                    throw new UnsupportedOperationException("Could Not Delete Round(s)!");
                } else {

                    String ids[] = new String[idCount];
                    int l = 0;
                    if (checkDeletedRoundsCursor.moveToFirst()) {
                        do {
                            ids[l] = String.valueOf(checkDeletedRoundsCursor.getLong(0));
                            l++;
                        } while (checkDeletedRoundsCursor.moveToNext());
                    }
                    checkDeletedRoundsCursor.close();
                    int holesDeleted =
                          mDb.delete(Contract.Holes.TABLE_NAME, Contract.Holes.ROUND_ID+"=?",ids);

                    if (holesDeleted != 18) {
                        throw new UnsupportedOperationException("Could Not Delete Round(s)!");
                    }
                    rowsDeleted = mDb.delete(
                          Contract.Rounds.TABLE_NAME, whereClause, whereArgs);
                    break;
                }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    private int bulkInsert18HolesForNewRound(long roundId) {
        int returnCount = 0;
        ContentValues values = new ContentValues();
        try {
            for (int k = 1; k < 19; k++) {
                values.put(Contract.Holes.HOLE_NUMBER, String.valueOf(k));
                values.put(Contract.Holes.ROUND_ID, String.valueOf(roundId));
                long _id = mDb.insert(Contract.Holes.TABLE_NAME, null, values);
                if (_id != -1) {
                    returnCount++;
                }
                values.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnCount;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String whereClause,
                      @Nullable String[] whereArgs) {
        return 0;
    }
}
