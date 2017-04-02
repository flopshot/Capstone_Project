package com.sean.golfranger.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.sean.golfranger.data.Contract.DB_NAME;

/**
 * DbHelper Class used by Content Provider
 */
class DbHelper extends SQLiteOpenHelper {
    private static DbHelper sInstance;

    /**
     * Even though content providers will be used to synchronize database transactions,
     * a synchronized singleton instance of DbHelper will be implemented to avoid ANR errors as a
     * precaution. More info at
     * http://stackoverflow.com/questions/2493331/what-are-the-best-practices-for-sqlite-on-android
     */
    static synchronized DbHelper getHelper(Context context) {
        if (sInstance == null)
            sInstance = new DbHelper(context);

        return sInstance;
    }

    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = DB_NAME + ".db";

    private DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // String to create a table to hold courses data.
        final String SQL_CREATE_COURSES_TABLE = "CREATE TABLE IF NOT EXISTS " +
              Contract.Courses.TABLE_NAME + " (" +
              Contract.Courses._ID + " INTEGER PRIMARY KEY," +
              Contract.Courses.CLUB_NAME + " TEXT NOT NULL," +
              Contract.Courses.COURSE_NAME + " TEXT);";

        // String to create a table to hold player data
        final String SQL_CREATE_PLAYER_TABLE = "CREATE TABLE IF NOT EXISTS " +
              Contract.Players.TABLE_NAME + " (" +
              Contract.Players._ID + " INTEGER PRIMARY KEY," +
              Contract.Players.FIRST_NAME + " TEXT NOT NULL," +
              Contract.Players.LAST_NAME + " TEXT NOT NULL," +
              Contract.Players.HANDICAP + " TEXT);";

        // String to create a table to hold round data
        final String SQL_CREATE_ROUNDS_TABLE = "CREATE TABLE IF NOT EXISTS " +
              Contract.Rounds.TABLE_NAME + " (" +
              Contract.Rounds._ID + " INTEGER PRIMARY KEY," +
              Contract.Rounds.COURSE_ID + " INTEGER ," +
              Contract.Rounds.CLUB_NAME + " TEXT," +
              Contract.Rounds.COURSE_NAME + " TEXT, " +
              Contract.Rounds.PLAYER1_ID + " INTEGER ," +
              Contract.Rounds.PLAYER1_FIRST_NAME + " TEXT," +
              Contract.Rounds.PLAYER1_LAST_NAME + " TEXT," +
              Contract.Rounds.PLAYER1_HANDICAP + " INTEGER," +
              Contract.Rounds.PLAYER2_ID + " INTEGER," +
              Contract.Rounds.PLAYER2_FIRST_NAME + " TEXT," +
              Contract.Rounds.PLAYER2_LAST_NAME + " TEXT," +
              Contract.Rounds.PLAYER2_HANDICAP + " INTEGER," +
              Contract.Rounds.PLAYER3_ID + " INTEGER," +
              Contract.Rounds.PLAYER3_FIRST_NAME + " TEXT," +
              Contract.Rounds.PLAYER3_LAST_NAME + " TEXT," +
              Contract.Rounds.PLAYER3_HANDICAP + " INTEGER," +
              Contract.Rounds.PLAYER4_ID + " INTEGER," +
              Contract.Rounds.PLAYER4_FIRST_NAME + " TEXT," +
              Contract.Rounds.PLAYER4_LAST_NAME + " TEXT," +
              Contract.Rounds.PLAYER4_HANDICAP + " INTEGER," +
              Contract.Rounds.DATE + " TEXT NOT NULL, " +
              " FOREIGN KEY (" + Contract.Rounds.PLAYER1_ID + ") REFERENCES " + Contract.Players.TABLE_NAME + " (" + Contract.Players._ID + ") ON DELETE SET NULL, " +
              " FOREIGN KEY (" + Contract.Rounds.PLAYER2_ID + ") REFERENCES " + Contract.Players.TABLE_NAME + " (" + Contract.Players._ID + ") ON DELETE SET NULL, " +
              " FOREIGN KEY (" + Contract.Rounds.PLAYER3_ID + ") REFERENCES " + Contract.Players.TABLE_NAME + " (" + Contract.Players._ID + ") ON DELETE SET NULL, " +
              " FOREIGN KEY (" + Contract.Rounds.PLAYER4_ID + ") REFERENCES " + Contract.Players.TABLE_NAME + " (" + Contract.Players._ID + ") ON DELETE SET NULL, " +
              " FOREIGN KEY (" + Contract.Rounds.COURSE_ID + ") REFERENCES " + Contract.Players.TABLE_NAME + " (" + Contract.Courses._ID + ") ON DELETE SET NULL" +
              ");";

        final String SQL_CREATE_HOLES_TABLE = "CREATE TABLE IF NOT EXISTS " +
              Contract.Holes.TABLE_NAME + " (" +
              Contract.Holes._ID + " INTEGER PRIMARY KEY," +
              Contract.Holes.ROUND_ID + " INTEGER NOT NULL," +
              Contract.Holes.HOLE_NUMBER + " INTEGER NOT NULL," +
              Contract.Holes.HOLE_PAR + " INTEGER," +
              Contract.Holes.HOLE_DISTANCE + " INTEGER," +
              Contract.Holes.P1_SCORE + " INTEGER," +
              Contract.Holes.P1_PUTTS + " INTEGER," +
              Contract.Holes.P1_PENALTIES + " INTEGER," +
              Contract.Holes.P1_FIR + " BOOLEAN DEFAULT 0 CHECK(" + Contract.Holes.P1_FIR +" IN (0,1))," +
              Contract.Holes.P1_GIR + " BOOLEAN DEFAULT 0 CHECK(" + Contract.Holes.P1_GIR +" IN (0,1))," +
              Contract.Holes.P1_SAND + " BOOLEAN DEFAULT 0 CHECK(" + Contract.Holes.P1_SAND +" IN (0,1))," +
              Contract.Holes.P2_SCORE + " INTEGER," +
              Contract.Holes.P2_PUTTS + " INTEGER," +
              Contract.Holes.P2_PENALTIES + " INTEGER," +
              Contract.Holes.P2_FIR + " BOOLEAN DEFAULT 0 CHECK(" + Contract.Holes.P2_FIR +" IN (0,1))," +
              Contract.Holes.P2_GIR + " BOOLEAN DEFAULT 0 CHECK(" + Contract.Holes.P2_GIR +" IN (0,1))," +
              Contract.Holes.P2_SAND + " BOOLEAN DEFAULT 0 CHECK(" + Contract.Holes.P2_SAND +" IN (0,1))," +
              Contract.Holes.P3_SCORE + " INTEGER," +
              Contract.Holes.P3_PUTTS + " INTEGER," +
              Contract.Holes.P3_PENALTIES + " INTEGER," +
              Contract.Holes.P3_FIR + " BOOLEAN DEFAULT 0 CHECK(" + Contract.Holes.P3_FIR +" IN (0,1))," +
              Contract.Holes.P3_GIR + " BOOLEAN DEFAULT 0 CHECK(" + Contract.Holes.P3_GIR +" IN (0,1))," +
              Contract.Holes.P3_SAND + " BOOLEAN DEFAULT 0 CHECK(" + Contract.Holes.P3_SAND +" IN (0,1))," +
              Contract.Holes.P4_SCORE + " INTEGER," +
              Contract.Holes.P4_PUTTS + " INTEGER," +
              Contract.Holes.P4_PENALTIES + " INTEGER," +
              Contract.Holes.P4_FIR + " BOOLEAN DEFAULT 0 CHECK(" + Contract.Holes.P4_FIR +" IN (0,1))," +
              Contract.Holes.P4_GIR + " BOOLEAN DEFAULT 0 CHECK(" + Contract.Holes.P4_GIR +" IN (0,1))," +
              Contract.Holes.P4_SAND + " BOOLEAN DEFAULT 0 CHECK(" + Contract.Holes.P4_SAND +" IN (0,1))," +
              " FOREIGN KEY (" + Contract.Holes.ROUND_ID + ") REFERENCES " + Contract.Rounds.TABLE_NAME + " (" + Contract.Rounds._ID + ") ON DELETE CASCADE, " +
              " UNIQUE (" + Contract.Holes.ROUND_ID + "," + Contract.Holes.HOLE_NUMBER + ") ON CONFLICT FAIL" +
              ");";

        sqLiteDatabase.execSQL(SQL_CREATE_COURSES_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_PLAYER_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_ROUNDS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_HOLES_TABLE);
    }

    // OVERRIDDEN TO ENFORCE FOREIGN KEY CONSTRAINT OF DB
    @Override
    public void onConfigure(SQLiteDatabase db){
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // No Updates Needed At The Moment
    }


}
