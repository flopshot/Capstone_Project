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
              Contract.Courses.COURSE_NAME + " TEXT, " +
              Contract.Courses.COURSE_ENABLED + " BOOLEAN DEFAULT 1 CHECK(" + Contract.Courses.COURSE_ENABLED +" IN (0,1)), " +
              Contract.Courses.DATE_CREATED + " INTEGER NOT NULL, " +
              Contract.Courses.DATE_UPDATED + " INTEGER NOT NULL, " +
              Contract.Courses.HOLE_CNT + " INTEGER " +
              ");";

        // String to create a table to hold player data
        final String SQL_CREATE_PLAYER_TABLE = "CREATE TABLE IF NOT EXISTS " +
              Contract.Players.TABLE_NAME + " (" +
              Contract.Players._ID + " INTEGER PRIMARY KEY," +
              Contract.Players.FIRST_NAME + " TEXT NOT NULL," +
              Contract.Players.LAST_NAME + " TEXT NOT NULL," +
              Contract.Players.PLAYER_ENABLED + " BOOLEAN DEFAULT 1 CHECK(" + Contract.Players.PLAYER_ENABLED +" IN (0,1)), " +
              Contract.Players.DATE_CREATED + " INTEGER NOT NULL, " +
              Contract.Players.DATE_UPDATED + " INTEGER NOT NULL, " +
              Contract.Players.HANDICAP + " INTEGER" +
              ");";

        // String to Create a table to hold round data
        final String SQL_CREATE_ROUNDS_TABLE = "CREATE TABLE IF NOT EXISTS " +
              Contract.Rounds.TABLE_NAME + " (" +
              Contract.Rounds._ID + " INTEGER PRIMARY KEY," +
              Contract.Rounds.COURSE_ID + " INTEGER ," +
              Contract.Rounds.DATE_CREATED + " INTEGER NOT NULL, " +
              Contract.Rounds.DATE_UPDATED + " INTEGER NOT NULL, " +
              Contract.Rounds.ROUND_ENABLED + " BOOLEAN DEFAULT 1 CHECK(" + Contract.Rounds.ROUND_ENABLED +" IN (0,1))," +
              " FOREIGN KEY (" + Contract.Rounds.COURSE_ID + ") REFERENCES " + Contract.Courses.TABLE_NAME + " (" + Contract.Courses._ID + ")" +
              ");";

        // String to Create a table to hold course/holes data
        final String SQL_CREATE_COURSE_HOLES_TABLE = "CREATE TABLE IF NOT EXISTS " +
              Contract.CourseHoles.TABLE_NAME + " (" +
              Contract.CourseHoles._ID + " INTEGER PRIMARY KEY," +
              Contract.CourseHoles.COURSE_ID + " INTEGER ," +
              Contract.CourseHoles.HOLE_NUMBER + " INTEGER, " +
              Contract.CourseHoles.HOLE_PAR + " INTEGER, " +
              Contract.CourseHoles.HOLE_DISTANCE + " INTEGER, " +
              Contract.CourseHoles.HOLE_HANDICAP + " INTEGER, " +
              " UNIQUE (" + Contract.CourseHoles._ID + ") ON CONFLICT IGNORE, " +
              " FOREIGN KEY (" + Contract.CourseHoles.COURSE_ID + ") REFERENCES " + Contract.Courses.TABLE_NAME + " (" + Contract.Courses._ID + ")" +
              ");";

        // String to Create a table to hold ROUND/PLAYERS data
        final String SQL_CREATE_ROUND_PLAYERS_TABLE = "CREATE TABLE IF NOT EXISTS " +
              Contract.RoundPlayers.TABLE_NAME + " (" +
              Contract.RoundPlayers._ID + " INTEGER PRIMARY KEY," +
              Contract.RoundPlayers.ROUND_ID + " INTEGER NOT NULL," +
              Contract.RoundPlayers.PLAYER_ID + " INTEGER ," +
              Contract.RoundPlayers.PLAYER_ORDER + " INTEGER, " +
              " UNIQUE (" + Contract.RoundPlayers._ID + ") ON CONFLICT REPLACE, " +
              " FOREIGN KEY (" + Contract.RoundPlayers.PLAYER_ID + ") REFERENCES " + Contract.Players.TABLE_NAME + " (" + Contract.Players._ID + ")," +
              " FOREIGN KEY (" + Contract.RoundPlayers.ROUND_ID + ") REFERENCES " + Contract.Rounds.TABLE_NAME + " (" + Contract.Rounds._ID + ") ON DELETE CASCADE" +
              ");";

        // String to create a table to hold ROUND/PLAYER/HOLES data
        final String SQL_CREATE_ROUND_PLAYER_HOLES_TABLE = "CREATE TABLE IF NOT EXISTS " +
              Contract.RoundPlayerHoles.TABLE_NAME + " (" +
              Contract.RoundPlayerHoles._ID + " INTEGER PRIMARY KEY," + //Concatenation of RoundId, player position,  and hole number
              Contract.RoundPlayerHoles.ROUNDPLAYER_ID + " INTEGER NOT NULL," +
              Contract.RoundPlayerHoles.ROUND_ID + " INTEGER NOT NULL, " +
              Contract.RoundPlayerHoles.SCORE + " INTEGER," +
              Contract.RoundPlayerHoles.PENALTIES + " INTEGER," +
              Contract.RoundPlayerHoles.PUTTS + " INTEGER," +
              Contract.RoundPlayerHoles.SAND_SHOTS + " INTEGER," +
              Contract.RoundPlayerHoles.SAND_FLAG + "  BOOLEAN DEFAULT 0 CHECK(" + Contract.RoundPlayerHoles.SAND_FLAG+" IN (0,1))," +
              Contract.RoundPlayerHoles.GIR_FLAG + "  BOOLEAN DEFAULT 0 CHECK(" + Contract.RoundPlayerHoles.GIR_FLAG+" IN (0,1))," +
              Contract.RoundPlayerHoles.FIR_FLAG + "  BOOLEAN DEFAULT 0 CHECK(" + Contract.RoundPlayerHoles.FIR_FLAG+" IN (0,1))," +
              Contract.RoundPlayerHoles.HOLE_NUM + " INTEGER NOT NULL, " +
              " UNIQUE (" + Contract.RoundPlayerHoles._ID + ") ON CONFLICT IGNORE, " +
              " FOREIGN KEY (" + Contract.RoundPlayerHoles.ROUNDPLAYER_ID + ") REFERENCES " + Contract.RoundPlayers.TABLE_NAME + " (" + Contract.RoundPlayers._ID + "), " +
              " FOREIGN KEY (" + Contract.RoundPlayerHoles.ROUND_ID + ") REFERENCES " + Contract.Rounds.TABLE_NAME + " (" + Contract.Rounds._ID + ") ON DELETE CASCADE" +
              ");";

        // String to create a table to hold Player Location data
        final String SQL_CREATE_PLAYER_LOCATION_TABLE = "CREATE TABLE IF NOT EXISTS " +
              Contract.PlayerLocation.TABLE_NAME + " (" +
              Contract.PlayerLocation._ID + " INTEGER PRIMARY KEY," +
              Contract.PlayerLocation.LOCATION + " REAL ," +
              Contract.PlayerLocation.DATE_UPDATED + " INTEGER, " +
              Contract.PlayerLocation.STATUS + " INTEGER " +
              ");";

        // String to create a table to hold Marker Location data
        final String SQL_CREATE_MARKER_LOCATION_TABLE = "CREATE TABLE IF NOT EXISTS " +
              Contract.MarkerLocation.TABLE_NAME + " (" +
              Contract.MarkerLocation._ID + " INTEGER NOT NULL," +
              Contract.MarkerLocation.LOCATION + " REAL ," +
              Contract.MarkerLocation.DATE_UPDATED + " INTEGER NOT NULL, " +
              Contract.MarkerLocation.STATUS + " INTEGER " +
              ");";

        // String to create a table to hold Wind data
        final String SQL_CREATE_WIND_TABLE = "CREATE TABLE IF NOT EXISTS " +
              Contract.Wind.TABLE_NAME + " (" +
              Contract.Wind._ID + " INTEGER PRIMARY KEY," +
              Contract.Wind.WIND_SPEED + " REAL ," +
              Contract.Wind.WIND_DIRECTION + " REAL ," +
              Contract.Wind.DATE_UPDATED + " INTEGER, " +
              Contract.PlayerLocation.STATUS + " INTEGER " +
              ");";

        final String SQL_CREATE_MATCHES_ADAPTER_VIEW = "CREATE VIEW " +
        Contract.MatchesView.TABLE_NAME + " AS " +
              "SELECT " +
              "r." + Contract.Rounds._ID + " AS " + Contract.MatchesView.ROUND_ID + ", " +
              "c." + Contract.Courses._ID + " AS courseId, " +
              "c." + Contract.Courses.COURSE_NAME + " AS courseName, " +
              "c." + Contract.Courses.CLUB_NAME + " AS clubName, " +
              "p1." + Contract.Players._ID + " AS player1Id, " +
              "p1." + Contract.Players.FIRST_NAME + " AS player1First, " +
              "p1." + Contract.Players.LAST_NAME + " AS player1Last, " +
              "p2." + Contract.Players._ID + " AS player2Id, " +
              "p2." + Contract.Players.FIRST_NAME + " AS player2First, " +
              "p2." + Contract.Players.LAST_NAME + " AS player2Last, " +
              "p3." + Contract.Players._ID + " AS player3Id, " +
              "p3." + Contract.Players.FIRST_NAME + " AS player3First, " +
              "p3." + Contract.Players.LAST_NAME + " AS player3Last, " +
              "p4." + Contract.Players._ID + " AS player4Id, " +
              "p4." + Contract.Players.FIRST_NAME + " AS player4First, " +
              "p4." + Contract.Players.LAST_NAME + " AS player4Last, " +
              "r." + Contract.Rounds.DATE_CREATED + " AS startDate " +

              " FROM " + Contract.Rounds.TABLE_NAME + " AS r " +
              " LEFT JOIN " + Contract.Courses.TABLE_NAME + " AS c " +
              " ON c." + Contract.Courses._ID + " = r." + Contract.Rounds.COURSE_ID +
              " LEFT JOIN " + Contract.RoundPlayers.TABLE_NAME + " AS rp1 " +
              " ON r." + Contract.Rounds._ID + " = rp1." + Contract.RoundPlayers.ROUND_ID + " AND  rp1." + Contract.RoundPlayers.PLAYER_ORDER + " = 1 " +
              " LEFT JOIN " + Contract.Players.TABLE_NAME + " AS p1 " +
              " ON rp1." + Contract.RoundPlayers.PLAYER_ID + " = p1." + Contract.Players._ID + " AND rp1." + Contract.RoundPlayers.PLAYER_ORDER + " = 1 " +
              " LEFT JOIN " + Contract.RoundPlayers.TABLE_NAME + " AS rp2 " +
              " ON r." + Contract.Rounds._ID + " = rp2." + Contract.RoundPlayers.ROUND_ID + " AND rp2." + Contract.RoundPlayers.PLAYER_ORDER + " = 2 " +
              " LEFT JOIN " + Contract.Players.TABLE_NAME + " AS p2 " +
              " ON rp2." + Contract.RoundPlayers.PLAYER_ID + " = p2." + Contract.Players._ID + " AND rp2." + Contract.RoundPlayers.PLAYER_ORDER + " = 2 " +
              " LEFT JOIN " + Contract.RoundPlayers.TABLE_NAME + " AS rp3 " +
              " ON r." + Contract.Rounds._ID + " = rp3." + Contract.RoundPlayers.ROUND_ID + " AND rp3." + Contract.RoundPlayers.PLAYER_ORDER + " = 3 " +
              " LEFT JOIN " + Contract.Players.TABLE_NAME + " AS p3 " +
              " ON rp3." + Contract.RoundPlayers.PLAYER_ID + " = p3." + Contract.Players._ID + " AND rp3." + Contract.RoundPlayers.PLAYER_ORDER + " = 3 " +
              " LEFT JOIN " + Contract.RoundPlayers.TABLE_NAME + " AS rp4 " +
              " ON r." + Contract.Rounds._ID + " = rp4." + Contract.RoundPlayers.ROUND_ID + " AND rp4." + Contract.RoundPlayers.PLAYER_ORDER + " = 4" +
              " LEFT JOIN " + Contract.Players.TABLE_NAME + " AS p4 " +
              " ON rp4." + Contract.RoundPlayers.PLAYER_ID + " = p4." + Contract.Players._ID + " AND rp4." + Contract.RoundPlayers.PLAYER_ORDER + " = 4" +
              " WHERE r." + Contract.Rounds.ROUND_ENABLED + " = 1; ";

        final String SQL_CREATE_SCORECARD_VIEW = "CREATE VIEW " +
            Contract.ScorecardView.TABLE_NAME + " AS SELECT " +
            "r._id AS " + Contract.ScorecardView.ROUND_ID +
            ",ch.holeNumber" +
            ",ch.holePar" +
            ",rph1.score AS p1Score" +
            ",rph2.score AS p2Score" +
            ",rph3.score AS p3Score" +
            ",rph4.score AS p4Score" +
            ",substr(p1.firstName,1,1) || substr(p1.lastName,1,1) AS p1Initials" +
            ",substr(p2.firstName,1,1) || substr(p2.lastName,1,1) AS p2Initials" +
            ",substr(p3.firstName,1,1) || substr(p3.lastName,1,1) AS p3Initials" +
            ",substr(p4.firstName,1,1) || substr(p4.lastName,1,1) AS p4Initials" +
            ",rt1.p1Total" +
            ",rt2.p2Total" +
            ",rt3.p3Total" +
            ",rt4.p4Total " +
            "FROM rounds AS r " +
            "LEFT JOIN CourseHoles AS ch " +
            "ON ch.courseId = r.courseId " +

            "LEFT JOIN roundPlayers AS rp1 ON r._id = rp1.roundId AND rp1.playerOrder = 1 " +
            "LEFT JOIN players AS p1 ON rp1.playerId = p1._id " +
            "LEFT JOIN roundPlayers AS rp2 ON r._id = rp2.roundId AND rp2.playerOrder = 2 " +
            "LEFT JOIN players AS p2 ON rp2.playerId = p2._id " +
            "LEFT JOIN roundPlayers AS rp3 ON r._id = rp3.roundId AND rp3.playerOrder = 3 " +
            "LEFT JOIN players AS p3 ON rp3.playerId = p3._id " +
            "LEFT JOIN roundPlayers AS rp4 ON r._id = rp4.roundId AND rp4.playerOrder = 4 " +
            "LEFT JOIN players AS p4 ON rp4.playerId = p4._id " +

            "LEFT JOIN roundPlayerHoles AS rph1 " +
            "ON rph1.roundPlayerId = rp1._id " +
            "AND ch.holeNumber = rph1.holeNumber " +
            "LEFT JOIN roundPlayerHoles AS rph2 " +
            "ON rph2.roundPlayerId = rp2._id " +
            "AND ch.holeNumber = rph2.holeNumber " +
            "LEFT JOIN roundPlayerHoles AS rph3 " +
            "ON rph3.roundPlayerId = rp3._id " +
            "AND ch.holeNumber = rph3.holeNumber " +
            "LEFT JOIN roundPlayerHoles AS rph4 " +
            "ON rph4.roundPlayerId = rp4._id " +
            "AND ch.holeNumber = rph4.holeNumber " +

            "LEFT JOIN ( " +
            "SELECT roundId ,SUM(score) AS p1Total " +
            "FROM roundPlayerHoles " +
            "WHERE substr(roundPlayerId, -1) = '1' " +
            "GROUP BY roundPlayerId " +
            ") AS rt1 ON rt1.roundId = r._id " +
            "LEFT JOIN ( " +
            "SELECT roundId ,SUM(score) AS p2Total " +
            "FROM roundPlayerHoles " +
            "WHERE substr(roundPlayerId, -1) = '2' " +
            "GROUP BY roundPlayerId " +
            ") AS rt2 ON rt2.roundId = r._id " +
            "LEFT JOIN ( " +
            "SELECT roundId ,SUM(score) AS p3Total " +
            "FROM roundPlayerHoles " +
            "WHERE substr(roundPlayerId, -1) = '3' " +
            "GROUP BY roundPlayerId " +
            ") AS rt3 ON rt3.roundId = r._id " +
            "LEFT JOIN ( " +
            "SELECT roundId ,SUM(score) AS p4Total " +
            "FROM roundPlayerHoles " +
            "WHERE substr(roundPlayerId, -1) = '4' " +
            "GROUP BY roundPlayerId " +
            ") AS rt4 ON rt4.roundId = r._id;";

        final String SQL_CREATE_HOLE_VIEW = "CREATE VIEW " +
              Contract.HoleView.TABLE_NAME + " AS SELECT " +
              "r._id AS " + Contract.HoleView.ROUND_ID +
              ",ch.holeNumber" +
              ",ch.holeDistance" +
              ",ch.holePar" +
              ",rph1.score AS p1Score" +
              ",rph2.score AS p2Score" +
              ",rph3.score AS p3Score" +
              ",rph4.score AS p4Score" +
              ",rph1.putts AS p1Putts" +
              ",rph2.putts AS p2Putts" +
              ",rph3.putts AS p3Putts" +
              ",rph4.putts AS p4Putts" +
              ",rph1.penalties AS p1Penalties" +
              ",rph2.penalties AS p2Penalties" +
              ",rph3.penalties AS p3Penalties" +
              ",rph4.penalties AS p4Penalties" +
              ",rph1.sandShots AS p1Sand" +
              ",rph2.sandShots AS p2Sand" +
              ",rph3.sandShots AS p3Sand" +
              ",rph4.sandShots AS p4Sand" +
              ",rph1.girFlag AS p1Gir" +
              ",rph2.girFlag AS p2Gir" +
              ",rph3.girFlag AS p3Gir" +
              ",rph4.girFlag AS p4Gir" +
              ",rph1.firFlag AS p1Fir" +
              ",rph2.firFlag AS p2Fir" +
              ",rph3.firFlag AS p3Fir" +
              ",rph4.firFlag AS p4Fir" +
              ",substr(p1.firstName,1,1) || substr(p1.lastName,1,1) AS p1Initials" +
              ",substr(p2.firstName,1,1) || substr(p2.lastName,1,1) AS p2Initials" +
              ",substr(p3.firstName,1,1) || substr(p3.lastName,1,1) AS p3Initials" +
              ",substr(p4.firstName,1,1) || substr(p4.lastName,1,1) AS p4Initials " +

              "FROM rounds AS r " +
              "LEFT JOIN CourseHoles AS ch " +
              "ON ch.courseId = r.courseId " +

              "LEFT JOIN roundPlayers AS rp1 ON r._id = rp1.roundId AND rp1.playerOrder = 1 " +
              "LEFT JOIN players AS p1 ON rp1.playerId = p1._id " +
              "LEFT JOIN roundPlayers AS rp2 ON r._id = rp2.roundId AND rp2.playerOrder = 2 " +
              "LEFT JOIN players AS p2 ON rp2.playerId = p2._id " +
              "LEFT JOIN roundPlayers AS rp3 ON r._id = rp3.roundId AND rp3.playerOrder = 3 " +
              "LEFT JOIN players AS p3 ON rp3.playerId = p3._id " +
              "LEFT JOIN roundPlayers AS rp4 ON r._id = rp4.roundId AND rp4.playerOrder = 4 " +
              "LEFT JOIN players AS p4 ON rp4.playerId = p4._id " +

              "LEFT JOIN roundPlayerHoles AS rph1 " +
              "ON rph1.roundPlayerId = rp1._id " +
              "AND ch.holeNumber = rph1.holeNumber " +
              "LEFT JOIN roundPlayerHoles AS rph2 " +
              "ON rph2.roundPlayerId = rp2._id " +
              "AND ch.holeNumber = rph2.holeNumber " +
              "LEFT JOIN roundPlayerHoles AS rph3 " +
              "ON rph3.roundPlayerId = rp3._id " +
              "AND ch.holeNumber = rph3.holeNumber " +
              "LEFT JOIN roundPlayerHoles AS rph4 " +
              "ON rph4.roundPlayerId = rp4._id " +
              "AND ch.holeNumber = rph4.holeNumber;";

        final String SQL_CREATE_WIDGET_VIEW = "CREATE VIEW " +
              Contract.WidgetView.TABLE_NAME + " AS SELECT " +
              " p._id AS playerId " +
              ",p.firstName" +
              ",p.lastName" +
              ",COUNT(DISTINCT r._id) AS gameCount" +
              ",CASE " +
              "    WHEN ROUND(AVG(CASE WHEN ch.holePar IS NULL THEN NULL ELSE rph.score END) - AVG(CASE WHEN rph.score IS NULL THEN NULL ELSE ch.holePar END),0) > 0 " +
              "         THEN '+'||CAST(ROUND(AVG(CASE WHEN ch.holePar IS NULL THEN NULL ELSE rph.score END) - AVG(CASE WHEN rph.score IS NULL THEN NULL ELSE ch.holePar END),0) AS INTEGER) " +
              "    WHEN ROUND(AVG(CASE WHEN ch.holePar IS NULL THEN NULL ELSE rph.score END) - AVG(CASE WHEN rph.score IS NULL THEN NULL ELSE ch.holePar END),0) = 0 THEN 'E'" +
              "    ELSE ROUND(AVG(CASE WHEN ch.holePar IS NULL THEN NULL ELSE rph.score END) - AVG(CASE WHEN rph.score IS NULL THEN NULL ELSE ch.holePar END),0) " +
              "END AS avgScore " +
              ",MIN(roundScore) AS lowScore " +
              "FROM players AS p " +
              "LEFT JOIN roundPlayers AS rp " +
              "ON rp.playerId = p._id " +
              "LEFT JOIN rounds AS r " +
              "ON r._id = rp.roundId " +
              "AND r.roundEnabled = 1 " +
              "LEFT JOIN courseHoles AS ch " +
              "ON ch.courseId = r.courseId " +
              "LEFT JOIN roundPlayerHoles AS rph " +
              "ON rph.roundPlayerId = rp._id " +
              "AND rph.holeNumber = ch.holeNumber " +
              "LEFT JOIN (" +
              "    SELECT " +
              "        rp.playerId AS pid" +
              "        ,rp.roundId AS rid" +
              "        ,CASE WHEN COUNT(ch.holeNumber) = COUNT(rph.score) AND COUNT(ch.holeNumber) = COUNT(ch.holePar)" +
              "              THEN SUM(rph.score) - SUM(ch.holePar)" +
              "              ELSE NULL END AS roundScore" +
              "    FROM roundPlayers AS rp " +
              "    LEFT JOIN rounds AS r ON r._id = rp.roundId " +
              "    LEFT JOIN courses AS c ON c._id = r.courseId " +
              "    LEFT JOIN courseHoles AS ch ON c._id = ch.courseId " +
              "    LEFT JOIN roundPlayerHoles AS rph ON rp._id = rph.roundPlayerId AND ch.holeNumber = rph.holeNumber " +
              "    GROUP BY rp.playerId, rp.roundId" +
              ") AS rs ON p._id = rs.pid " +
              "WHERE p.playerEnabled = 1 " +
              "GROUP BY p._id;";

        sqLiteDatabase.execSQL(SQL_CREATE_COURSES_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_PLAYER_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_ROUNDS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_COURSE_HOLES_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_ROUND_PLAYERS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_ROUND_PLAYER_HOLES_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_PLAYER_LOCATION_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_MARKER_LOCATION_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_WIND_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_MATCHES_ADAPTER_VIEW );
        sqLiteDatabase.execSQL(SQL_CREATE_SCORECARD_VIEW);
        sqLiteDatabase.execSQL(SQL_CREATE_HOLE_VIEW);
        sqLiteDatabase.execSQL(SQL_CREATE_WIDGET_VIEW);
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
