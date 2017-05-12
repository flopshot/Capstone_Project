package com.sean.golfranger.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Locale;

import static com.sean.golfranger.data.DbHelper.getHelper;

/**
 * Content Provider used for communication between the app and the database cache store
 */
@SuppressWarnings("ConstantConditions")
public class Provider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private SQLiteDatabase mDb;
    private DbHelper mOpenHelper;
    private static final int PLAYER = 100;
    private static final int COURSE = 101;
    private static final int ROUND = 102;
    private static final int COURSE_HOLE = 103;
    private static final int ROUND_PLAYER = 104;
    private static final int ROUND_PLAYER_HOLE = 105;
    private static final int PLAYER_LOCATION = 107;
    private static final int MARKER_LOCATION = 108;
    private static final int WIND = 109;
    private static final int MATCHES = 110;

    private static UriMatcher buildUriMatcher() {
        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = Contract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, Contract.Players.TABLE_NAME, PLAYER);
        matcher.addURI(authority, Contract.Courses.TABLE_NAME, COURSE);
        matcher.addURI(authority, Contract.Rounds.TABLE_NAME, ROUND);
        matcher.addURI(authority, Contract.CourseHoles.TABLE_NAME, COURSE_HOLE);
        matcher.addURI(authority, Contract.RoundPlayers.TABLE_NAME,ROUND_PLAYER);
        matcher.addURI(authority, Contract.RoundPlayerHoles.TABLE_NAME, ROUND_PLAYER_HOLE);
        matcher.addURI(authority, Contract.PlayerLocation.TABLE_NAME, PLAYER_LOCATION);
        matcher.addURI(authority, Contract.MarkerLocation.TABLE_NAME, MARKER_LOCATION);
        matcher.addURI(authority, Contract.Wind.TABLE_NAME, WIND);
        matcher.addURI(authority, Contract.MatchesView.TABLE_NAME, MATCHES);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        //Set db connection
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
                if (whereClause == null) {
                    whereClause = Contract.Players.PLAYER_ENABLED + "=?";
                    whereArgs = new String[]{"1"};
                } else {
                    whereClause = whereClause + " AND " + Contract.Players.PLAYER_ENABLED + "=?";
                    whereArgs = concat(whereArgs, new String[]{"1"});
                }
                return mDb.query(
                      Contract.Players.TABLE_NAME, columns,
                      whereClause, whereArgs, null, null, sortOrder
                );
            case COURSE:
                if (whereClause == null) {
                    whereClause = Contract.Courses.COURSE_ENABLED + "=?";
                    whereArgs = new String[]{"1"};
                } else {
                    whereClause = whereClause + " AND " + Contract.Courses.COURSE_ENABLED + "=?";
                    whereArgs = concat(whereArgs, new String[]{"1"});
                }
                return mDb.query(
                      Contract.Courses.TABLE_NAME, columns,
                      whereClause, whereArgs, null, null, sortOrder
                );
            case ROUND:
                if (whereClause == null) {
                    whereClause = Contract.Rounds.ROUND_ENABLED + "=?";
                    whereArgs = new String[]{"1"};
                } else {
                    whereClause = whereClause + " AND " + Contract.Rounds.ROUND_ENABLED + "=?";
                    whereArgs = concat(whereArgs, new String[]{"1"});
                }
                return mDb.query(
                      Contract.Rounds.TABLE_NAME, columns,
                      whereClause, whereArgs, null, null, sortOrder
                );
            case ROUND_PLAYER:
                return mDb.query(
                      Contract.RoundPlayers.TABLE_NAME, columns,
                      whereClause, whereArgs, null, null, sortOrder
                );
            case COURSE_HOLE:
                return mDb.query(
                      Contract.CourseHoles.TABLE_NAME, columns,
                      whereClause, whereArgs, null, null, sortOrder
                );
            case ROUND_PLAYER_HOLE:
                return mDb.query(
                      Contract.RoundPlayerHoles.TABLE_NAME, columns,
                      whereClause, whereArgs, null, null, sortOrder
                );
            case PLAYER_LOCATION:
                return mDb.query(
                      Contract.PlayerLocation.TABLE_NAME, columns,
                      whereClause, whereArgs, null, null, sortOrder
                );
            case MARKER_LOCATION:
                return mDb.query(
                      Contract.MarkerLocation.TABLE_NAME, columns,
                      whereClause, whereArgs, null, null, sortOrder
                );
            case WIND:
                return mDb.query(
                      Contract.Wind.TABLE_NAME, columns,
                      whereClause, whereArgs, null, null, sortOrder
                );
            case MATCHES:
                return mDb.query(
                      Contract.MatchesView.TABLE_NAME, columns, whereClause,
                      whereArgs, null, null, sortOrder
                );
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
            case COURSE_HOLE:
                return Contract.CourseHoles.CONTENT_TYPE;
            case ROUND_PLAYER:
                return Contract.RoundPlayers.CONTENT_TYPE;
            case ROUND_PLAYER_HOLE:
                return Contract.RoundPlayerHoles.CONTENT_TYPE;
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
                contentValues.put(Contract.Players.DATE_CREATED, getCurTimeStamp());
                contentValues.put(Contract.Players.DATE_UPDATED, getCurTimeStamp());
                long _id = mDb.insert(Contract.Players.TABLE_NAME, null, contentValues);
                if ( _id > 0 )
                    returnUri = Contract.Players.buildItemUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case COURSE: {
                contentValues.put(Contract.Courses.DATE_CREATED, getCurTimeStamp());
                contentValues.put(Contract.Courses.DATE_UPDATED, getCurTimeStamp());
                int holeCount = contentValues.getAsInteger(Contract.Courses.HOLE_CNT);

                long _id = mDb.insert(Contract.Courses.TABLE_NAME, null, contentValues);
                if ( _id > 0) {
                    bulkInsertCourseHoles(String.valueOf(_id), holeCount);
                    returnUri = Contract.Courses.buildItemUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into" + uri);
                }
                break;
            }
            case ROUND: {
                contentValues.put(Contract.Rounds.DATE_CREATED, getCurTimeStamp());
                contentValues.put(Contract.Rounds.DATE_UPDATED, getCurTimeStamp());
                long _id = mDb.insert(Contract.Rounds.TABLE_NAME, null, contentValues);
                if (_id > 0) {
                    bulkInsertRoundPlayerHoles(String.valueOf(_id));
                    returnUri = Contract.Rounds.buildItemUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into" + uri);
                }
                break;
            }
            case COURSE_HOLE: {
                long _id = mDb.insert(Contract.CourseHoles.TABLE_NAME, null, contentValues);
                if ( _id > 0) {
                    returnUri = Contract.CourseHoles.buildItemUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into" + uri);
                }
                break;
            }
            case ROUND_PLAYER: {
                long _id = mDb.insert(Contract.RoundPlayers.TABLE_NAME, null, contentValues);
                if ( _id > 0) {
                    returnUri = Contract.RoundPlayers.buildItemUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into" + uri);
                }
                break;
            }
            case ROUND_PLAYER_HOLE: {
                long _id = mDb.insert(Contract.RoundPlayerHoles.TABLE_NAME, null, contentValues);
                if ( _id > 0) {
                    returnUri = Contract.RoundPlayerHoles.buildItemUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into" + uri);
                }
                break;
            }
            case PLAYER_LOCATION: {
                contentValues.put(Contract.PlayerLocation.DATE_UPDATED, getCurTimeStamp());
                long _id = mDb.insert(Contract.PlayerLocation.TABLE_NAME, null, contentValues);
                if ( _id > 0)
                    returnUri = Contract.PlayerLocation.buildItemUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into" + uri);
                break;
            }
            case MARKER_LOCATION: {
                contentValues.put(Contract.MarkerLocation.DATE_UPDATED, getCurTimeStamp());
                long _id = mDb.insert(Contract.MarkerLocation.TABLE_NAME, null, contentValues);
                if ( _id > 0)
                    returnUri = Contract.MarkerLocation.buildItemUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into" + uri);
                break;
            }
            case WIND: {
                contentValues.put(Contract.Wind.DATE_UPDATED, getCurTimeStamp());
                long _id = mDb.insert(Contract.Wind.TABLE_NAME, null, contentValues);
                if ( _id > 0)
                    returnUri = Contract.Wind.buildItemUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into" + uri);
                break;
            }
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
                rowsDeleted = mDb.delete(
                      Contract.Rounds.TABLE_NAME, whereClause, whereArgs);
                break;
            case COURSE_HOLE:
                rowsDeleted = mDb.delete(
                      Contract.CourseHoles.TABLE_NAME, whereClause, whereArgs);
                break;
            case ROUND_PLAYER:
                rowsDeleted = mDb.delete(
                      Contract.RoundPlayers.TABLE_NAME, whereClause, whereArgs);
                break;
            case ROUND_PLAYER_HOLE:
                rowsDeleted = mDb.delete(
                      Contract.RoundPlayerHoles.TABLE_NAME, whereClause, whereArgs);
                break;
            case PLAYER_LOCATION:
                rowsDeleted = mDb.delete(
                      Contract.PlayerLocation.TABLE_NAME, whereClause, whereArgs);
                break;
            case MARKER_LOCATION:
                rowsDeleted = mDb.delete(
                      Contract.MarkerLocation.TABLE_NAME, whereClause, whereArgs);
                break;
            case WIND:
                rowsDeleted = mDb.delete(
                      Contract.Wind.TABLE_NAME, whereClause, whereArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values,
                      @Nullable String whereClause, @Nullable String[] whereArgs) {
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;
        switch (match) {
            case PLAYER:
                values.put(Contract.Players.DATE_UPDATED, getCurTimeStamp());
                rowsUpdated = mDb.update(Contract.Players.TABLE_NAME,
                      values, whereClause, whereArgs);
                break;
            case COURSE:
                values.put(Contract.Courses.DATE_UPDATED, getCurTimeStamp());
                rowsUpdated = mDb.update(Contract.Courses.TABLE_NAME,
                      values, whereClause, whereArgs);
                int holeCount = values.getAsInteger(Contract.Courses.HOLE_CNT);
                bulkInsertCourseHoles(whereArgs[0], holeCount);
                break;
            case ROUND:
                values.put(Contract.Rounds.DATE_UPDATED, getCurTimeStamp());
                rowsUpdated = mDb.update(Contract.Rounds.TABLE_NAME,
                      values, whereClause, whereArgs);
                break;
            case COURSE_HOLE:
                values.put(Contract.Courses.DATE_UPDATED, getCurTimeStamp());
                rowsUpdated = mDb.update(Contract.CourseHoles.TABLE_NAME,
                      values, whereClause, whereArgs);
                break;
            case ROUND_PLAYER:
                values.put(Contract.Rounds.DATE_UPDATED, getCurTimeStamp());
                rowsUpdated = mDb.update(Contract.RoundPlayers.TABLE_NAME,
                      values, whereClause, whereArgs);
                break;
            case ROUND_PLAYER_HOLE:
                values.put(Contract.Rounds.DATE_UPDATED, getCurTimeStamp());
                rowsUpdated = mDb.update(Contract.RoundPlayerHoles.TABLE_NAME,
                      values, whereClause, whereArgs);
                break;
            case PLAYER_LOCATION:
                values.put(Contract.PlayerLocation.DATE_UPDATED, getCurTimeStamp());
                rowsUpdated = mDb.update(Contract.PlayerLocation.TABLE_NAME,
                      values, whereClause, whereArgs);
                break;
            case MARKER_LOCATION:
                values.put(Contract.MarkerLocation.DATE_UPDATED, getCurTimeStamp());
                rowsUpdated = mDb.update(Contract.MarkerLocation.TABLE_NAME,
                      values, whereClause, whereArgs);
                break;
            case WIND:
                values.put(Contract.Wind.DATE_UPDATED, getCurTimeStamp());
                rowsUpdated = mDb.update(Contract.Wind.TABLE_NAME,
                      values, whereClause, whereArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    private void bulkInsertCourseHoles(String courseId, int holeCount) {
        ContentValues values = new ContentValues();
        try {
            for (int k = 1; k < 37; k++) {
                values.put(Contract.CourseHoles.HOLE_NUMBER, String.valueOf(k));
                values.put(Contract.CourseHoles.COURSE_ID, courseId);
                values.put(Contract.CourseHoles._ID, courseId + String.format(Locale.getDefault(), "%02d", k));
                mDb.insert(Contract.CourseHoles.TABLE_NAME, null, values);
                values.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            for (int j = 36; j > holeCount; j--) {
                mDb.delete(
                      Contract.CourseHoles.TABLE_NAME,
                      Contract.CourseHoles._ID + "=?",
                      new String[] {courseId + String.format(Locale.getDefault(), "%02d", j)});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void bulkInsertRoundPlayerHoles(String roundId) {
        ContentValues values = new ContentValues();
        try {
            for (int k = 1; k < 4; k++) {
                for (int l = 1; l < 37; l++) {
                    values.put(
                          Contract.RoundPlayerHoles._ID,
                          roundId + String.valueOf(k) + String.format(Locale.getDefault(), "%02d", l)
                    );
                    values.put(Contract.RoundPlayerHoles.ROUNDPLAYER_ID, roundId + String.valueOf(k));
                    values.put(Contract.RoundPlayerHoles.HOLE_NUM, String.valueOf(l));
                    mDb.insert(Contract.RoundPlayerHoles.TABLE_NAME, null, values);
                    values.clear();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // set the format to sql date time
    private String getCurTimeStamp() {
        Long tsLong = System.currentTimeMillis();
        return tsLong.toString();
    }

    //Helper function for whereArgs concatenation
    private String[] concat(String[] a, String[] b) {
        int aLen = a.length;
        int bLen = b.length;
        String[] c = new String[aLen+bLen];
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);
        return c;
    }
}