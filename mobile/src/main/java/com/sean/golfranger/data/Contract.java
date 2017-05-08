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
        static final int DATECREATED_POS = 4;
        static final int DATEUPDATED_POS = 5;

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
        public static final String DATE_CREATED = "dateCreated";
        /**
        * Type: INTEGER NOT NULL
        */
        public static final String DATE_UPDATED = "dateUpdated";
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
        static final int DATECREATED_POS = 4;
        static final int DATEUPDATED_POS = 6;
        static final int HANDICAP_POS = 5;

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
        public static final String DATE_CREATED = "dateCreated";
        /**
        * Type: INT NOT NULL
        */
        public static final String DATE_UPDATED = "dateUpdated";
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
        static final int ROUNDID_POS = 0;
        static final int COURSEID_POS = 1;
        static final int DATECREATED_POS = 2;
        static final int DATEUPDATED_POS = 3;
        static final int ROUNDENABLED_POS = 4;

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
        public static final String DATE_CREATED = "dateCreated";
        /**
        * Type: INT NOT NULL
        */
        public static final String DATE_UPDATED = "dateUpdated";
        /**
         * Type: Bool
         */
        public static final String ROUND_ENABLED = "roundEnabled";
        /**
         * "vnd.android.cursor.dir/vnd.com.sean.golfranger.[TABLE_NAME]</>"
5         */
        static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;
        /**
         * "vnd.android.cursor.item/vnd.com.sean.golfranger.[TABLE_NAME]";
         */
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;

        public static final String DEFAULT_SORT = DATE_CREATED + " DESC";

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
        static final int COURSEHOLEID_POS = 0;
        static final int COURSEID_POS = 1;
        static final int HOLENUMBER_POS = 2;
        static final int HOLEPAR_POS = 3;
        static final int HOLEDISTANCE_POS = 4;
        static final int HOLEHANDOCAP_POS = 5;

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
        static final int ROUNDPLAYERID_POS = 0;
        static final int PLAYERID_POS = 1;
        static final int PLAYERORDER_POS = 2;

        // TABLE NAME FOR DB
        static final String TABLE_NAME = "roundPlayers";

        // COLUMN NAMES
        /**
         * Type: INT NOT NULL
         */
        public static final String PLAYER_ID = "playerId";
        /**
         * Type: INT
         */
        public static final String PLAYER_ORDER = "playerOrder";
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
        static final int ROUNDPLAYERCOURSEHOLE_POS = 0;
        static final int ROUNDPLAYERID_POS = 1;
        static final int COURSEHOLEID_POS = 2;
        static final int SCORE_POS = 3;
        static final int PENALTIES_POS = 4;
        static final int PUTTS_POS = 5;
        static final int SANDSHOTES_POS = 6;
        static final int SANDFLAG_POS = 7;
        static final int GIRFLAG_POS = 8;
        static final int FIRFLAG_POS = 9;

        // TABLE NAME FOR DB
        static final String TABLE_NAME = "gamePlayerCourseHoles";

        // COLUMN NAMES
        /**
         * Type: INT NOT NULL
         */
        public static final String ROUNDPLAYER_ID = "roundPlayerId";
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

    public static class PlayerLocation implements BaseColumns {
        static final int PLAYERLOCATIONID_POS = 0;
        static final int LOCATION_POS = 1;
        static final int DATEUPDATED_POS = 2;
        static final int STATUS_POS = 3;

        // TABLE NAME FOR DB
        static final String TABLE_NAME = "playerLocation";

        // COLUMN NAMES
        /**
         * Type: REAL
         */
        public static final String LOCATION = "location";
        /**
         * Type: INT
         */
        public static final String DATE_UPDATED = "dateUpdated";
        /**
         * Type: TEXT
         */
        public static final String STATUS = "status";
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

    public static class MarkerLocation implements BaseColumns {
        static final int MARKERLOCATIONID_POS = 0;
        static final int LOCATION_POS = 1;
        static final int DATEUPDATED_POS = 2;
        static final int STATUS_POS = 3;

        // TABLE NAME FOR DB
        static final String TABLE_NAME = "markerLocation";

        // COLUMN NAMES
        /**
         * Type: REAL
         */
        public static final String LOCATION = "location";
        /**
         * Type: INT
         */
        public static final String DATE_UPDATED = "dateUpdated";
        /**
         * Type: TEXT
         */
        public static final String STATUS = "status";
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

    public static class Wind implements BaseColumns {
        static final int WINDID_POS = 0;
        static final int WINDSPEED_POS = 1;
        static final int WINDDIRECTION_POS = 2;
        static final int DATEUPDATED_POS = 3;
        static final int STATUS_POS = 4;

        // TABLE NAME FOR DB
        static final String TABLE_NAME = "markerLocation";

        // COLUMN NAMES
        /**
         * Type: REAL
         */
        public static final String WIND_SPEED = "windSpeed";
        /**
         * Type: REAL
         */
        public static final String WIND_DIRECTION = "windDirection";
        /**
         * Type: INT
         */
        public static final String DATE_UPDATED = "dateUpdated";
        /**
         * Type: TEXT
         */
        public static final String STATUS = "status";
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