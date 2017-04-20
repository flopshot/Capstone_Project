package com.sean.golfranger.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

import timber.log.Timber;

/**
 * Data Base Contract. Holds naming conventions of all database schema objects (Table/field names,
 * etc.)
 */

public class Contract {
    static final String CONTENT_AUTHORITY = "com.sean.golfranger";
    private static final Uri BASE_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    static final String DB_NAME = "golf";

    public static class Courses implements BaseColumns {
        //COLUMN POSITION
        static final int COURSEID_POS = 0;
        static final int CLUBNAME_POS = 1;
        static final int COURSENAME_POS = 2;
        static final int COURSEENABLED_POS = 3;

        // TABLE NAME
        static final String TABLE_NAME = "courses";

        // COLUMN NAMES
        /**
         * Type: TEXT NOT NULL
         */
        public static final String CLUB_NAME = "clubName";
        /**
         * Type: TEXT NOT NULL
         */
        public static final String COURSE_NAME = "courseName";
        /**
         * Type: BOOLEAN
         */
        public static final String COURSE_ENABLED = "courseEnabled";
        /**
         * Type: INTEGER NOT NULL
         */
        public static final String DATE = "date_created";
        /**
         * "vnd.android.cursor.dir/vnd.com.sean.golfranger.courses"
         */
        static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;
        /**
         * "vnd.android.cursor.item/vnd.com.sean.golfranger.courses";
         */
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;

        public static final String DEFAULT_SORT = _ID + " DESC";

        /**
         * Matches: /courses/
         */
        public static Uri buildDirUri() {
            return BASE_URI.buildUpon().appendPath(TABLE_NAME).build();
        }

        /**
         * Matches: /courses/[_id]/
         */
        static Uri buildItemUri(long _id) {
            return BASE_URI.buildUpon().appendPath(TABLE_NAME).appendPath(Long.toString(_id)).build();
        }

        /**
         * Read item ID item detail URI.
         */
        public static long getItemId(Uri itemUri) {
            return Long.parseLong(itemUri.getPathSegments().get(0));
        }
    }

    public static class Players implements BaseColumns {
        static final int PLAYERID_POS = 0;
        static final int PLAYERFIRST_POS = 1;
        static final int PLAYERLAST_POS = 2;
        static final int PLAYERENABLED_POS = 3;

        // TABLE NAME FOR DB
        static final String TABLE_NAME = "players";

        // COLUMN NAMES
        /**
         * Type: TEXT NOT NULL
         */
        public static final String FIRST_NAME = "firstName";
        /**
         * Type: TEXT NOT NULL
         */
        public static final String LAST_NAME = "lastName";
        /**
         *  Type: Boolean
         */
        public static final String PLAYER_ENABLED = "playerEnabled";
        /**
         * Type: INT NOT NULL
         */
        public static final String DATE = "date_created";
        /**
         * Type: INT
         */
        public static final String HANDICAP = "handicap";
        /**
         * "vnd.android.cursor.dir/vnd.com.sean.golfranger.[TABLE_NAME]</>"
         */
        static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;
        /**
         * "vnd.android.cursor.item/vnd.com.sean.golfranger.[TABLE_NAME]";
         */
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;

        public static final String DEFAULT_SORT = _ID + " DESC";
        /**
         * Matches: /[TABLE_NAME]/
         */
        public static Uri buildDirUri() {
            return BASE_URI.buildUpon().appendPath(TABLE_NAME).build();
        }
        /**
         * Matches: /[TABLE_NAME]/[_id]/
         */
        static Uri buildItemUri(long _id) {
            return BASE_URI.buildUpon().appendPath(TABLE_NAME).appendPath(Long.toString(_id)).build();
        }
        /**
         * Read item ID item detail URI.
         */
        public static long getItemId(Uri itemUri) {
            return Long.parseLong(itemUri.getPathSegments().get(0));
        }
    }

    public static class Rounds implements BaseColumns {
        // TABLE NAME FOR DB
        static final String TABLE_NAME = "rounds";

        // COLUMN NAMES
        /**
         * Type: INT NOT NULL
         */
        public static final String COURSE_ID = "courseId";
        /**
         * Type: INTEGER NOT NULL
         */
        public static final String DATE = "date_created";
        /**
         * "vnd.android.cursor.dir/vnd.com.sean.golfranger.[TABLE_NAME]</>"
         */
        static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;
        /**
         * "vnd.android.cursor.item/vnd.com.sean.golfranger.[TABLE_NAME]";
         */
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;

        public static final String DEFAULT_SORT = DATE + " DESC";

        /**
         * Matches: /[TABLE_NAME]/
         */
        public static Uri buildDirUri() {
            return BASE_URI.buildUpon().appendPath(TABLE_NAME).build();
        }

        /**
         * Matches: /[TABLE_NAME]/[_id]/
         */
        static Uri buildItemUri(long _id) {
            return BASE_URI.buildUpon().appendPath(TABLE_NAME).appendPath(Long.toString(_id)).build();
        }

        /**
         * Read item ID item detail URI.
         */
        public static long getItemId(Uri itemUri) {
            return Long.parseLong(itemUri.getPathSegments().get(0));
        }
    }

    public static class CourseHoles implements BaseColumns {
        // TABLE NAME FOR DB
        static final String TABLE_NAME = "courseHoles";

        // COLUMN NAMES
        /**
         * Type: INT NOT NULL
         */
        public static final String COURSE_ID = "courseId";
        /**
         * Type: INT
         */
        public static final String HOLE_NUMBER = "holeNumber";
        /**
         * Type: INT
         */
        public static final String HOLE_PAR = "holePar";
        /**
         * Type INT
         */
        public static final String HOLE_DISTANCE = "holeDistance";
        /**
         * Type: INT
         */
        public static final String HOLE_HANDICAP = "holeHandicap";
        /**
         * "vnd.android.cursor.dir/vnd.com.sean.golfranger.[TABLE_NAME]</>"
         */
        static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;
        /**
         * "vnd.android.cursor.item/vnd.com.sean.golfranger.[TABLE_NAME]";
         */
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;

        public static final String DEFAULT_SORT = _ID + " DESC";

        /**
         * Matches: /[TABLE_NAME]/
         */
        public static Uri buildDirUri() {
            return BASE_URI.buildUpon().appendPath(TABLE_NAME).build();
        }

        /**
         * Matches: /[TABLE_NAME]/[_id]/
         */
        public static Uri buildItemUri(long _id) {
            return BASE_URI.buildUpon().appendPath(TABLE_NAME).appendPath(Long.toString(_id)).build();
        }

        /**
         * Read item ID item detail URI.
         */
        public static long getItemId(Uri itemUri) {
            return Long.parseLong(itemUri.getPathSegments().get(0));
        }
    }

    public static class RoundPlayers implements BaseColumns {
        // TABLE NAME FOR DB
        static final String TABLE_NAME = "roundPlayers";

        // COLUMN NAMES
        /**
         * Type: INT NOT NULL
         */
        public static final String PLAYER_ID = "playerId";
        /**
         * "vnd.android.cursor.dir/vnd.com.sean.golfranger.[TABLE_NAME]</>"
         */
        static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;
        /**
         * "vnd.android.cursor.item/vnd.com.sean.golfranger.[TABLE_NAME]";
         */
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;

        public static final String DEFAULT_SORT = _ID + " DESC";

        /**
         * Matches: /[TABLE_NAME]/
         */
        public static Uri buildDirUri() {
            return BASE_URI.buildUpon().appendPath(TABLE_NAME).build();
        }

        /**
         * Matches: /[TABLE_NAME]/[_id]/
         */
        public static Uri buildItemUri(long _id) {
            return BASE_URI.buildUpon().appendPath(TABLE_NAME).appendPath(Long.toString(_id)).build();
        }

        /**
         * Read item ID item detail URI.
         */
        public static long getItemId(Uri itemUri) {
            return Long.parseLong(itemUri.getPathSegments().get(0));
        }
    }

    public static class RoundPlayerCourseHoles implements BaseColumns {
        // TABLE NAME FOR DB
        static final String TABLE_NAME = "gamePlayerCourseHoles";

        // COLUMN NAMES
        /**
         * Type: INT NOT NULL
         */
        public static final String ROUND_ID = "roundId";
        /**
         * Type: INT NOT NULL
         */
        public static final String PLAYER_ID = "playerId";
        /**
         * Type: INT NOT NULL
         */
        public static final String COURSE_HOLE_ID = "courseHoleId";
        /**
         * Type: INT
         */
        public static final String SCORE = "score";
        /**
         * Type: INT
         */
        public static final String PENALITES = "penalties";
        /**
         * Type: INT
         */
        public static final String PUTTS = "putts";
        /**
         * Type: INT
         */
        public static final String SAND_SHOTS = "sandShots";
        /**
         * Type: BOOLEAN
         */
        public static final String SAND_FLAG = "sandFlag";
        /**
         * Type: BOOLEAN
         */
        public static final String GIR_FLAG = "girFlag";
        /**
         * Type: BOOLEAN
         */
        public static final String FIR_FLAG = "firFlag";
        /**
         * "vnd.android.cursor.dir/vnd.com.sean.golfranger.[TABLE_NAME]</>"
         */
        static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;
        /**
         * "vnd.android.cursor.item/vnd.com.sean.golfranger.[TABLE_NAME]";
         */
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;

        public static final String DEFAULT_SORT = _ID + " DESC";

        /**
         * Matches: /[TABLE_NAME]/
         */
        public static Uri buildDirUri() {
            return BASE_URI.buildUpon().appendPath(TABLE_NAME).build();
        }

        /**
         * Matches: /[TABLE_NAME]/[_id]/
         */
        public static Uri buildItemUri(long _id) {
            return BASE_URI.buildUpon().appendPath(TABLE_NAME).appendPath(Long.toString(_id)).build();
        }

        /**
         * Read item ID item detail URI.
         */
        public static long getItemId(Uri itemUri) {
            return Long.parseLong(itemUri.getPathSegments().get(0));
        }
    }

    public static class CourseColumnPosition {
        public static final int ID = 0;
        public static final int CLUB_NAME = 1;
        public static final int COURSE_NAME = 2;
    }

    public static class PlayerColumnPosition {
        public static final int ID = 0;
        public static final int FIRST_NAME = 1;
        public static final int LAST_NAME = 2;
    }

    public static class RoundColumnPosition {
        public static final int ID = 0;
        public static final int CLUB_NAME = 2;
        public static final int COURSE_NAME = 3;
        public static final int P1_FIRST_NAME = 5;
        public static final int P1_LAST_NAME = 6;
        public static final int P2_FIRST_NAME = 8;
        public static final int P2_LAST_NAME = 9;
        public static final int P3_FIRST_NAME = 11;
        public static final int P3_LAST_NAME = 12;
        public static final int P4_FIRST_NAME = 14;
        public static final int P4_LAST_NAME = 15;
        public static final int DATE = 16;
    }

    public static class HoleColumnPosition {
        public static final int ID = 0;
        public static final int ROUND_ID = 1;
        public static final int HOLE_NUMBER = 2;
        public static final int HOLE_PAR = 3;
        public static final int HOLE_DISTANCE = 4;
        public static final int P1_SCORE = 5;
        public static final int P1_PUTTS = 6;
        public static final int P1_PENALTIES = 7;
        public static final int P1_FIR = 8;
        public static final int P1_GIR = 9;
        public static final int P1_SAND = 10;
        public static final int P2_SCORE = 11;
        public static final int P2_PUTTS = 12;
        public static final int P2_PENALTIES = 13;
        public static final int P2_FIR = 14;
        public static final int P2_GIR = 15;
        public static final int P2_SAND = 16;
        public static final int P3_SCORE = 17;
        public static final int P3_PUTTS = 18;
        public static final int P3_PENALTIES = 19;
        public static final int P3_FIR = 20;
        public static final int P3_GIR = 21;
        public static final int P3_SAND = 22;
        public static final int P4_SCORE = 23;
        public static final int P4_PUTTS = 24;
        public static final int P4_PENALTIES = 25;
        public static final int P4_FIR = 26;
        public static final int P4_GIR = 27;
        public static final int P4_SAND = 28;
    }

    public static class RoundCoursesPlayers {
        static final String TABLE_NAME = "roundPlayerCourse";
        public static Uri roundCoursesPlayersUri() {
            return BASE_URI.buildUpon()
                  .appendPath(Rounds.TABLE_NAME)
                  .appendPath(Courses.TABLE_NAME)
                  .appendPath(Players.TABLE_NAME)
                  .build();
        }
    }

    //TODO: Delete when ready
//    public static Uri roundHolesUri() {
//        Timber.d("Building roundHolesUri: " +
//              BASE_URI.buildUpon().appendPath(Rounds.TABLE_NAME).appendPath(Holes.TABLE_NAME).build().toString());
//        return BASE_URI.buildUpon()
//              .appendPath(Rounds.TABLE_NAME)
//              .appendPath(Holes.TABLE_NAME)
//              .build();
//    }

    public static class PlayerRoundTotals {
        static final String TABLE_NAME = "playerRoundTotals";
        public static final int ROUNDID_COL_INDEX = 0;
        public static final int P1_TOTAL_COL_INDEX = 1;
        public static final int P2_TOTAL_COL_INDEX = 2;
        public static final int P3_TOTAL_COL_INDEX = 3;
        public static final int P4_TOTAL_COL_INDEX = 4;
        public static final int P1_EXISTS_COL_INDEX = 5;
        public static final int P2_EXISTS_COL_INDEX = 6;
        public static final int P3_EXISTS_COL_INDEX = 7;
        public static final int P4_EXISTS_COL_INDEX = 8;

        public static Uri buildDirUri() {
            Timber.v("SomeOne Queried RoundTotals View");
            return BASE_URI.buildUpon()
                  .appendPath(PlayerRoundTotals.TABLE_NAME)
                  .build();
        }
    }

    public static class PlayerTotals{
        static final String TABLE_NAME = "playerTotalsView";

        public static final int PLAYER_ID = 0;
        public static final int PLAYER_FIRST = 1;
        public static final int PLAYER_LAST = 2;
        public static final int PLAYER_GAME_COUNT = 3;
        public static final int PLAYER_MEAN_SCORE = 4;
        public static final int PLAYER_MIN_SCORE = 5;

        public static Uri buildDirUri() {
            Timber.v("SomeOne Queried PlayerTotals View");
            return BASE_URI.buildUpon()
                  .appendPath(PlayerTotals.TABLE_NAME)
                  .build();
        }
    }
}