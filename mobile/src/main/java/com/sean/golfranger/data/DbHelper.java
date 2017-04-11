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
              Contract.Players.LAST_NAME + " TEXT NOT NULL);";

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
              Contract.Rounds.PLAYER2_ID + " INTEGER," +
              Contract.Rounds.PLAYER2_FIRST_NAME + " TEXT," +
              Contract.Rounds.PLAYER2_LAST_NAME + " TEXT," +
              Contract.Rounds.PLAYER3_ID + " INTEGER," +
              Contract.Rounds.PLAYER3_FIRST_NAME + " TEXT," +
              Contract.Rounds.PLAYER3_LAST_NAME + " TEXT," +
              Contract.Rounds.PLAYER4_ID + " INTEGER," +
              Contract.Rounds.PLAYER4_FIRST_NAME + " TEXT," +
              Contract.Rounds.PLAYER4_LAST_NAME + " TEXT," +
              Contract.Rounds.DATE + " INTEGER NOT NULL, " +
              " FOREIGN KEY (" + Contract.Rounds.PLAYER1_ID + ") REFERENCES " + Contract.Players.TABLE_NAME + " (" + Contract.Players._ID + ") ON DELETE SET NULL, " +
              " FOREIGN KEY (" + Contract.Rounds.PLAYER2_ID + ") REFERENCES " + Contract.Players.TABLE_NAME + " (" + Contract.Players._ID + ") ON DELETE SET NULL, " +
              " FOREIGN KEY (" + Contract.Rounds.PLAYER3_ID + ") REFERENCES " + Contract.Players.TABLE_NAME + " (" + Contract.Players._ID + ") ON DELETE SET NULL, " +
              " FOREIGN KEY (" + Contract.Rounds.PLAYER4_ID + ") REFERENCES " + Contract.Players.TABLE_NAME + " (" + Contract.Players._ID + ") ON DELETE SET NULL, " +
              " FOREIGN KEY (" + Contract.Rounds.COURSE_ID + ") REFERENCES " + Contract.Courses.TABLE_NAME + " (" + Contract.Courses._ID + ") ON DELETE SET NULL," +
              " CHECK ( " + Contract.Rounds.PLAYER1_ID + "<>" + Contract.Rounds.PLAYER2_ID + " AND " + Contract.Rounds.PLAYER1_ID + "<>" + Contract.Rounds.PLAYER3_ID +
                    " AND " + Contract.Rounds.PLAYER1_ID + "<>" + Contract.Rounds.PLAYER4_ID + " AND " + Contract.Rounds.PLAYER2_ID + "<>" + Contract.Rounds.PLAYER3_ID +
                    " AND " + Contract.Rounds.PLAYER2_ID + "<>" + Contract.Rounds.PLAYER4_ID + " AND " + Contract.Rounds.PLAYER3_ID + "<>" + Contract.Rounds.PLAYER4_ID + ") " +
              ");";

        final String SQL_CREATE_HOLES_TABLE = "CREATE TABLE IF NOT EXISTS " +
              Contract.Holes.TABLE_NAME + " (" +
              Contract.Holes._ID + " INTEGER PRIMARY KEY," +
              Contract.Holes.ROUND_ID + " INTEGER NOT NULL," +
              Contract.Holes.HOLE_NUMBER + " INTEGER NOT NULL," +
              Contract.Holes.HOLE_DISTANCE + " INTEGER," +
              Contract.Holes.HOLE_PAR + " INTEGER," +
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

        final String SQL_CREATE_PLAYER_COURSE_ROUND_VIEW = "CREATE VIEW " +
        Contract.RoundCoursesPlayers.TABLE_NAME +
        " AS SELECT " +
        "r._id AS _id, " +
        "CASE WHEN c._id IS NULL THEN r.clubName ELSE c.clubName END AS clubName, " +
        "CASE WHEN c._id IS NULL THEN r.courseName ELSE c.courseName END AS courseName, " +
        "CASE WHEN p1._id IS NULL THEN r.playerOneFirstName ELSE p1.firstName END AS p1FirstName, " +
        "CASE WHEN p1._id IS NULL THEN r.playerOneLastName ELSE p1.lastName END AS p1LastName, " +
        "CASE WHEN p2._id IS NULL THEN r.playerTwoFirstName ELSE p2.firstName END AS p2FirstName, " +
        "CASE WHEN p2._id IS NULL THEN r.playerTwoLastName ELSE p2.lastName END AS p2LastName, " +
        "CASE WHEN p3._id IS NULL THEN r.playerThreeFirstName ELSE p3.firstName END AS p3FirstName, " +
        "CASE WHEN p3._id IS NULL THEN r.playerThreeLastName ELSE p3.lastName END AS p3LastName, " +
        "CASE WHEN p4._id IS NULL THEN r.playerFourFirstName ELSE p4.firstName END AS p4FirstName, " +
        "CASE WHEN p4._id IS NULL THEN r.playerFourLastName ELSE p4.lastName END AS p4LastName " +
        "FROM rounds AS r " +
        "LEFT JOIN courses AS c " +
        "ON r.courseId = c._id " +
        "LEFT JOIN players AS p1 " +
        "ON r.playerOneId = p1._id " +
        "LEFT JOIN players AS p2 " +
        "ON r.playerTwoId = p2._id " +
        "LEFT JOIN players AS p3 " +
        "ON r.playerThreeId = p3._id " +
        "LEFT JOIN players AS p4 " +
        "ON r.playerFourId = p4._id;";

        final String SQL_CREATE_PLAYERS_TOTAL_SCORE_VIEW ="CREATE VIEW " +
              Contract.PlayerRoundTotals.TABLE_NAME + " AS " +
              "SELECT "+
                "roundId, " +
                "SUM(p1Score) AS p1Total, "+
                "SUM(p2Score) AS p2Total, "+
                "SUM(p3Score) AS p3Total, "+
                "SUM(p4Score) AS P4Total, "+
                "COUNT(DISTINCT playerOneFirstName) AS p1Exists, " +
                "COUNT(DISTINCT playerTwoFirstName) AS p2Exists, " +
                "COUNT(DISTINCT playerThreeFirstName) AS p3Exists, " +
                "COUNT(DISTINCT playerFourFirstName) AS p4Exists " +

              "FROM holes "+
              "LEFT JOIN rounds ON rounds._id = holes.roundId " +
              "GROUP BY roundId; ";

        //For the freakin Widget
        final String SQL_CREATE_PLAYER_TOTALS_VIEW="CREATE VIEW " +
              Contract.PlayerTotals.TABLE_NAME + " AS " +
              "SELECT p._id AS playerId "+
              ",p.firstName AS firstName " +
              ",p.lastName AS lastName " +
              ",COUNT(DISTINCT playerScoreTotals.rID) AS gameCount "+
              ",SUM(playerScoreTotals.score) / SUM(playerScoreTotals.completedHoles) AS meanScore "+
              ",MIN(CASE  "+
              "WHEN playerScoreTotals.completedGame = 1 "+
              "THEN playerScoreTotals.score "+
              "ELSE NULL "+
              "END) AS lowScore "+
              "FROM players as p" +
              " LEFT JOIN" +
              "( "+
              "SELECT r._id AS rId "+
              ",r.playerOneId AS playerId "+
              ",ifNull(sum(ifNull(p1.p1Score, 0)), 0) AS score "+
              ",COUNT(nullif(p1.p1Score, 0)) AS completedHoles "+
              ",CASE  "+
              "WHEN COUNT(nullif(p1.p1Score, 0)) = 18 "+
              "THEN 1 "+
              "ELSE 0 "+
              "END AS completedGame "+
              "FROM rounds AS r "+
              "LEFT JOIN holes AS p1 "+
              "ON r._id = p1.roundId "+
              "GROUP BY r.playerOneId "+
              ",r._id "+
              "UNION ALL "+
              "SELECT r._id AS rId "+
              ",r.playerTwoId AS playerId "+
              ",ifNull(sum(ifNull(p2.p2Score, 0)), 0) AS score "+
              ",COUNT(nullif(p2.p2Score, 0)) AS completedHoles "+
              ",CASE  "+
              "WHEN COUNT(nullif(p2.p2Score, 0)) = 18 "+
              "THEN 1 "+
              "ELSE 0 "+
              "END AS completedGame "+
              "FROM rounds AS r "+
              "LEFT JOIN holes AS p2 "+
              "ON r._id = p2.roundId "+
              "GROUP BY r.playerTwoId "+
              ",r._id "+
              "UNION ALL "+
              "SELECT r._id AS rId "+
              ",r.playerThreeId AS playerId "+
              ",ifNull(sum(ifNull(p3.p3Score, 0)), 0) AS score "+
              ",COUNT(nullif(p3.p3Score, 0)) AS completedHoles "+
              ",CASE  "+
              "WHEN COUNT(nullif(p3.p3Score, 0)) = 18 "+
              "THEN 1 "+
              "ELSE 0 "+
              "END AS completedGame "+
              "FROM rounds AS r "+
              "LEFT JOIN holes AS p3 "+
              "ON r._id = p3.roundId "+
              "GROUP BY r.playerThreeId "+
              ",r._id "+
              "UNION ALL "+
              "SELECT r._id AS rId "+
              ",r.playerFourId AS playerId "+
              ",ifNull(sum(ifNull(p4.p4Score, 0)), 0) AS score "+
              ",COUNT(nullif(p4.p4Score, 0)) AS completedHoles "+
              ",CASE  "+
              "WHEN COUNT(nullif(p4.p4Score, 0)) = 18 "+
              "THEN 1 "+
              "ELSE 0 "+
              "END AS completedGame "+
              "FROM rounds AS r "+
              "LEFT JOIN holes AS p4 "+
              "ON r._id = p4.roundId "+
              "GROUP BY r.playerFourId "+
              ",r._id "+
              ") AS playerScoreTotals "+
              "ON p._id = playerScoreTotals.playerId " +
              "GROUP BY p._id" +
                ";";

        sqLiteDatabase.execSQL(SQL_CREATE_COURSES_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_PLAYER_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_ROUNDS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_HOLES_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_PLAYER_COURSE_ROUND_VIEW);
        sqLiteDatabase.execSQL(SQL_CREATE_PLAYERS_TOTAL_SCORE_VIEW);
        sqLiteDatabase.execSQL(SQL_CREATE_PLAYER_TOTALS_VIEW);
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
