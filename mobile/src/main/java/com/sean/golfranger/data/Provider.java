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

import java.util.ArrayList;
import java.util.List;

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
    private static final int HOLE = 103;
    private static final int ROUND_HOLE = 104;
    private static final int ROUND_COURSES_PLAYERS = 105;
    private static final int PLAYERS_ROUND_TOTALS = 106;


    // START: implement table JOIN logic on Holes and Rounds tables for Content Provider
    private static final SQLiteQueryBuilder sHoleAndRoundQueryBuilder;

    static{
        sHoleAndRoundQueryBuilder = new SQLiteQueryBuilder();

        //This is an inner join which looks like
        //rounds LEFT JOIN holes ON rounds._id = holes.roundId
        sHoleAndRoundQueryBuilder.setTables(
              Contract.Holes.TABLE_NAME + " LEFT JOIN " +
                    Contract.Rounds.TABLE_NAME +
                    " ON " + Contract.Rounds.TABLE_NAME +
                    "." + Contract.Rounds._ID +
                    " = " + Contract.Holes.TABLE_NAME +
                    "." + Contract.Holes.ROUND_ID);
    }

    private Cursor getRoundWithHoleCursor(String[] columns, String whereClause, String[] whereArgs, String sortOrder) {
        return sHoleAndRoundQueryBuilder.query(
              mOpenHelper.getReadableDatabase(),
              columns,
              whereClause,
              whereArgs,
              null,
              null,
              sortOrder
        );
    }
    // END: implement table JOIN logic on Holes and Rounds tables for Content Provider

    // START: implement table JOIN logic on PLAYERS,COURSES and ROUNDS tables for Content Provider
//    private static final SQLiteQueryBuilder sPlayersCoursesAndRoundQueryBuilder;
//
//    static{
//        sPlayersCoursesAndRoundQueryBuilder = new SQLiteQueryBuilder();
//
//        //This is an inner join which looks like
////        FROM rounds AS r
////        LEFT JOIN courses AS c
////        ON r.courseId = c._id
////        LEFT JOIN players AS p1
////        ON r.playerOneId = p1._id
////        LEFT JOIN players AS p2
////        ON r.playerTwoId = p2._id
////        LEFT JOIN players AS p3
////        ON r.playerThreeId = p3._id
////        LEFT JOIN players AS p4
////        ON r.playerFourId = p4._id
//        sPlayersCoursesAndRoundQueryBuilder.setTables(
//              Contract.Rounds.TABLE_NAME + " AS r" +
//                    " LEFT JOIN " +
//                    Contract.Courses.TABLE_NAME + " AS c" +
//                    " ON " + "r" +
//                    "." + Contract.Rounds.COURSE_ID +
//                    " = " + "c" +
//                    "." + Contract.Courses._ID +
//                    " LEFT JOIN " +
//                    Contract.Players.TABLE_NAME + " AS p1" +
//                    " ON " + "r" +
//                    "." + Contract.Rounds.PLAYER1_ID +
//                    " = " + "p1" +
//                    "." + Contract.Players._ID +
//                    " LEFT JOIN " +
//                    Contract.Players.TABLE_NAME + " AS p2" +
//                    " ON " + "r" +
//                    "." + Contract.Rounds.PLAYER2_ID +
//                    " = " + "p2" +
//                    "." + Contract.Players._ID +
//                    " LEFT JOIN " +
//                    Contract.Players.TABLE_NAME + " AS p3" +
//                    " ON " + "r" +
//                    "." + Contract.Rounds.PLAYER3_ID +
//                    " = " + "p3" +
//                    "." + Contract.Players._ID +
//                    " LEFT JOIN " +
//                    Contract.Players.TABLE_NAME + " AS p4" +
//                    " ON " + "r" +
//                    "." + Contract.Rounds.PLAYER4_ID +
//                    " = " + "p4" +
//                    "." + Contract.Players._ID
//        );
//    }
//
//    private Cursor getRoundWithPlayersCoursesCursor(String[] columns, String whereClause, String sortOrder) {
//        return sPlayersCoursesAndRoundQueryBuilder.query(
//              mOpenHelper.getReadableDatabase(),
//              columns,
//              whereClause,
//              null,
//              null,
//              null,
//              sortOrder
//        );
//    }
    // END: implement table JOIN logic on Holes and Rounds tables for Content Provider

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
        matcher.addURI(authority, Contract.Holes.TABLE_NAME, HOLE);
        matcher.addURI(authority,
              Contract.Rounds.TABLE_NAME + "/" + Contract.Holes.TABLE_NAME,
              ROUND_HOLE);
        matcher.addURI(authority,
              Contract.Rounds.TABLE_NAME + "/" + Contract.Courses.TABLE_NAME + "/" + Contract.Players.TABLE_NAME,
              ROUND_COURSES_PLAYERS);
        matcher.addURI(authority, Contract.PlayerRoundTotals.TABLE_NAME,PLAYERS_ROUND_TOTALS);
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
                return getRoundWithHoleCursor(columns, whereClause, whereArgs, sortOrder);

            case ROUND_COURSES_PLAYERS:
                return mDb.query(
                      Contract.RoundCoursesPlayers.TABLE_NAME, columns,
                      whereClause, whereArgs, null, null, sortOrder
                );
                //return getRoundWithPlayersCoursesCursor(columns, whereClause, sortOrder);
            case PLAYERS_ROUND_TOTALS:
                return mDb.query(
                      Contract.PlayerRoundTotals.TABLE_NAME, columns,
                      whereClause, whereArgs, null, null, sortOrder
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
                if ( _id < 1) {
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

//                android.database.Cursor checkDeletedRoundsCursor = mDb.query(
//                      Contract.Rounds.TABLE_NAME,
//                      new String[] {Contract.Rounds._ID},
//                      whereClause,
//                      whereArgs,
//                      null,
//                      null,
//                      null);
//
//                int idCount = checkDeletedRoundsCursor.getCount();
//
//                if (idCount < 1) {
//                    checkDeletedRoundsCursor.close();
//                    throw new UnsupportedOperationException("Could Not Delete Round(s)!");
//                } else {
//
//                    String roundIds[] = new String[idCount];
//                    int l = 0;
//                    if (checkDeletedRoundsCursor.moveToFirst()) {
//                        do {
//                            roundIds[l] = String.valueOf(checkDeletedRoundsCursor.getLong(0));
//                            l++;
//                        } while (checkDeletedRoundsCursor.moveToNext());
//                    }
//                    checkDeletedRoundsCursor.close();
//                String[] roundIds = getAffectedIds(Contract.Rounds.TABLE_NAME, whereClause, whereArgs);
//                int holesDeleted =
//                      mDb.delete(Contract.Holes.TABLE_NAME, Contract.Holes.ROUND_ID+"=?",roundIds);
//
//                if (holesDeleted != 18) {
//                    throw new UnsupportedOperationException("Incorrect Deletion!");
//                }
                rowsDeleted = mDb.delete(
                      Contract.Rounds.TABLE_NAME, whereClause, whereArgs);
                break;
//                }
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
                String[] playerIds =
                      getAffectedIds(Contract.Players.TABLE_NAME, whereClause, whereArgs);
                rowsUpdated = mDb.update(Contract.Players.TABLE_NAME,
                      values, whereClause, whereArgs);
                updateRoundPlayers(playerIds);
                break;
            case COURSE:
                String[] courseIds =
                      getAffectedIds(Contract.Courses.TABLE_NAME, whereClause, whereArgs);
                rowsUpdated = mDb.update(Contract.Courses.TABLE_NAME,
                      values, whereClause, whereArgs);
                updateRoundCourses(courseIds);
                break;
            case ROUND:
                rowsUpdated = mDb.update(Contract.Rounds.TABLE_NAME,
                      values, whereClause, whereArgs);
                break;
            case HOLE:
                rowsUpdated = mDb.update(Contract.Holes.TABLE_NAME,
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

    /**
     * When a new round is inserted, this method will be triggered. The method creates 18 records
     * in the holes table which has a foreign key reference to the round by roundId
     */
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

    /**
     * Preliminary method to ascertain what record id's will be affected by the whereClause,
     * before applying the SQL command to execute and change the DB. returns the record _id's
     * in a String Array
     */
    private String[] getAffectedIds(String tableName, String whereClause, String[] whereArgs) {
        if (whereClause == null | whereArgs.length == 0) {
            throw new UnsupportedOperationException("Could Not Execute SQL Command!");
        }
        android.database.Cursor getAffectedIdsCursor = mDb.query(
              tableName,
              new String[] {"_id"},
              whereClause,
              whereArgs,
              null,
              null,
              null);

        int idCount = getAffectedIdsCursor.getCount();

        if (idCount < 1) {
            getAffectedIdsCursor.close();
            throw new UnsupportedOperationException("Could Not Execute SQL Command!");
        } else {

            String ids[] = new String[idCount];
            int l = 0;
            if (getAffectedIdsCursor.moveToFirst()) {
                do {
                    ids[l] = String.valueOf(getAffectedIdsCursor.getLong(0));
                    l++;
                } while (getAffectedIdsCursor.moveToNext());
            }
            getAffectedIdsCursor.close();
            return ids;
        }
    }

    /**
     * Preferably, we would like a cascade update to change all foreign references of the updated
     * courses attributes in the courses table, but with the schema implemented in this app, it is
     * not possible. Thus we will manually update course attributes of foreign references with this
     * method in the rounds table
     */
    private void updateRoundCourses (String[] courseIds){
        Cursor updatedCoursesCursor = mDb.query(
              Contract.Courses.TABLE_NAME,
              null,
              Contract.Courses._ID + "=?",
              courseIds,
              null,
              null,
              null
        );
        int idCount = updatedCoursesCursor.getCount();
        if (idCount<1) {
            updatedCoursesCursor.close();
            return;
        }
        List<String[]> courseRecords = new ArrayList<>();
        if (updatedCoursesCursor.moveToFirst()) {
            do {
                courseRecords.add(new String[] {
                      String.valueOf(updatedCoursesCursor.getLong(0)), //_id
                      updatedCoursesCursor.getString(1), //Club Name
                      updatedCoursesCursor.getString(2), //Course Name
                      // TODO: Reference hardcoded column indices from constants in Contract
                });
            } while (updatedCoursesCursor.moveToNext());
        }
        updatedCoursesCursor.close();
        ContentValues values = new ContentValues();
        for (String[] record: courseRecords) {
            values.put(Contract.Rounds.CLUB_NAME, record[1]);
            values.put(Contract.Rounds.COURSE_NAME, record[2]);

            mDb.update(
                  Contract.Rounds.TABLE_NAME,
                  values,
                  Contract.Rounds.COURSE_ID + "=?",
                  new String[]{record[0]}
            );
            values.clear();
        }
    }

    /**
     * Preferably, we would like a cascade update to change all foreign references of the updated
     * player attributes in the players table, but with the schema implemented in this app, it is
     * not possible. Thus we will manually update player attributes of foreign references with this
     * method in the rounds table
     */
    private void updateRoundPlayers(String[] playerIds) {
        Cursor updatedPlayersCursor = mDb.query(
              Contract.Players.TABLE_NAME,
              null,
              Contract.Players._ID + "=?",
              playerIds,
              null,
              null,
              null
        );
        int idCount = updatedPlayersCursor.getCount();

        if (idCount<1) {
            updatedPlayersCursor.close();
            return;
        }

        List<String[]> playerRecords = new ArrayList<>();
        if (updatedPlayersCursor.moveToFirst()) {
            do {
                playerRecords.add(new String[] {
                      String.valueOf(updatedPlayersCursor.getLong(0)), //_id
                      updatedPlayersCursor.getString(1), //First Name
                      updatedPlayersCursor.getString(2), //Last Name
                      // TODO: Reference hardcoded column indices from constants in Contract
                });
            } while (updatedPlayersCursor.moveToNext());
        }
        updatedPlayersCursor.close();
        updatePlayersByPosition(playerRecords);
    }

    /**
     * helper method to update play attributes by each position for the updateRoundPlayers method
     */
    private void updatePlayersByPosition(List<String[]> records) {
        ContentValues values = new ContentValues();

        for (String[] record: records) {
            values.put(Contract.Rounds.PLAYER1_FIRST_NAME, record[1]);
            values.put(Contract.Rounds.PLAYER1_LAST_NAME, record[2]);

            mDb.update(
                  Contract.Rounds.TABLE_NAME,
                  values,
                  Contract.Rounds.PLAYER1_ID +"=?",
                  new String[] {record[0]}
            );
            values.clear();

            values.put(Contract.Rounds.PLAYER2_FIRST_NAME, record[1]);
            values.put(Contract.Rounds.PLAYER2_LAST_NAME, record[2]);

            mDb.update(
                  Contract.Rounds.TABLE_NAME,
                  values,
                  Contract.Rounds.PLAYER2_ID +"=?",
                  new String[] {record[0]}
            );
            values.clear();

            values.put(Contract.Rounds.PLAYER3_FIRST_NAME, record[1]);
            values.put(Contract.Rounds.PLAYER3_LAST_NAME, record[2]);

            mDb.update(
                  Contract.Rounds.TABLE_NAME,
                  values,
                  Contract.Rounds.PLAYER3_ID +"=?",
                  new String[] {record[0]}
            );
            values.clear();

            values.put(Contract.Rounds.PLAYER4_FIRST_NAME, record[1]);
            values.put(Contract.Rounds.PLAYER4_LAST_NAME, record[2]);

            mDb.update(
                  Contract.Rounds.TABLE_NAME,
                  values,
                  Contract.Rounds.PLAYER4_ID +"=?",
                  new String[] {record[0]}
            );
            values.clear();
        }
    }
}
