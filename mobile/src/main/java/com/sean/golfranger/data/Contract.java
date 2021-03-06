package com.sean.golfranger.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

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
        public static final int COURSEID_POS = 0;
        public static final int CLUBNAME_POS = 1;
        public static final int COURSENAME_POS = 2;
        public static final int COURSEENABLED_POS = 3;
        public static final int DATECREATED_POS = 4;
        public static final int DATEUPDATED_POS = 5;
        public static final int HOLECNT_POS = 6;

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
         * Type: INT
         */
        public static final String HOLE_CNT = "holeCount";
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
        public static final int PLAYERID_POS = 0;
        public static final int PLAYERFIRST_POS = 1;
        public static final int PLAYERLAST_POS = 2;
        public static final int PLAYERENABLED_POS = 3;
        public static final int DATECREATED_POS = 4;
        public static final int DATEUPDATED_POS = 6;
        public static final int HANDICAP_POS = 5;

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
        public static final int ROUNDID_POS = 0;
        public static final int COURSEID_POS = 1;
        public static final int DATECREATED_POS = 2;
        public static final int DATEUPDATED_POS = 3;
        public static final int ROUNDENABLED_POS = 4;

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
        public static final int COURSEHOLEID_POS = 0;
        public static final int COURSEID_POS = 1;
        public static final int HOLENUMBER_POS = 2;
        public static final int HOLEPAR_POS = 3;
        public static final int HOLEDISTANCE_POS = 4;
        public static final int HOLEHANDOCAP_POS = 5;

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
        public static final int ROUNDPLAYERID_POS = 0;
        public static final int ROUNDID_POS = 1;
        public static final int PLAYERID_POS = 2;
        public static final int PLAYERORDER_POS = 3;

        // TABLE NAME FOR DB
        static final String TABLE_NAME = "roundPlayers";

        // COLUMN NAMES
        /**
        * Type: INT
        */
        public static final String ROUND_ID = "roundId";
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

    public static class RoundPlayerHoles implements BaseColumns {
        public static final int ROUNDPLAYERCOURSEHOLE_POS = 0; //Concatenation of RoundId, player position,  and hole number
        public static final int ROUNDPLAYERID_POS = 1;
        public static final int ROUNDID_POS = 2;
        public static final int SCORE_POS = 3;
        public static final int PENALTIES_POS = 4;
        public static final int PUTTS_POS = 5;
        public static final int SANDSHOTES_POS = 6;
        public static final int SANDFLAG_POS = 7;
        public static final int GIRFLAG_POS = 8;
        public static final int FIRFLAG_POS = 9;
        public static final int HOLENUM_POS = 10;


        // TABLE NAME FOR DB
        static final String TABLE_NAME = "roundPlayerHoles";

        // COLUMN NAMES
        /**
         * Type: INT NOT NULL
         */
        public static final String ROUNDPLAYER_ID = "roundPlayerId";
        /**
         * Type: INT
         */
        public static final String ROUND_ID = "roundId";
        /**
         * Type: INT
         */
        public static final String SCORE = "score";
        /**
         * Type: INT
         */
        public static final String PENALTIES = "penalties";
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
        * Type: Int
        */
        public static final String HOLE_NUM = "holeNumber";
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
        public static final int PLAYERLOCATIONID_POS = 0;
        public static final int LOCATION_POS = 1;
        public static final int DATEUPDATED_POS = 2;
        public static final int STATUS_POS = 3;

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
        public static final int MARKERLOCATIONID_POS = 0;
        public static final int LOCATION_POS = 1;
        public static final int DATEUPDATED_POS = 2;
        public static final int STATUS_POS = 3;

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
        public static final int WINDID_POS = 0;
        public static final int WINDSPEED_POS = 1;
        public static final int WINDDIRECTION_POS = 2;
        public static final int DATEUPDATED_POS = 3;
        public static final int STATUS_POS = 4;

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

    public static class MatchesView {
        static final String TABLE_NAME = "MatchesView";
        public static final String ROUND_ID = "roundId";
        public static final int ROUNDID_COL_INDEX = 0;
        public static final int COURSEID_COL_INDEX = 1;
        public static final int COURSENAME_COL_INDEX = 2;
        public static final int CLUBNAME_COL_INDEX = 3;
        public static final int P1_ID_COL_INDEX = 4;
        public static final int P1_FIRST_COL_INDEX = 5;
        public static final int P1_LAST_COL_INDEX = 6;
        public static final int P2_ID_COL_INDEX = 7;
        public static final int P2_FIRST_COL_INDEX = 8;
        public static final int P2_LAST_COL_INDEX = 9;
        public static final int P3_ID_COL_INDEX = 10;
        public static final int P3_FIRST_COL_INDEX = 11;
        public static final int P3_LAST_COL_INDEX = 12;
        public static final int P4_ID_COL_INDEX = 13;
        public static final int P4_FIRST_COL_INDEX = 14;
        public static final int P4_LAST_COL_INDEX = 15;
        public static final int START_DATE = 16;

        public static Uri buildDirUri() {
            return BASE_URI.buildUpon()
                  .appendPath(TABLE_NAME)
                  .build();
        }
    }

    public static class ScorecardView {
        static final String TABLE_NAME = "scorecardView";
        public static final String ROUND_ID = "roundId";
        public static final int ROUNDID_POS = 0;
        public static final int HOLENUMBER_POS = 1 ;
        public static final int HOLEPAR_POS = 2;
        public static final int P1SCORE_POS = 3;
        public static final int P2SCORE_POS = 4;
        public static final int P3SCORE_POS = 5;
        public static final int P4SCORE_POS = 6;
        public static final int P1INITIALS_POS = 7;
        public static final int P2INITIALS_POS = 8;
        public static final int P3INITIALS_POS = 9;
        public static final int P4INITIALS_POS = 10;
        public static final int P1TOTAL_POS = 11;
        public static final int P2TOTAL_POS = 12;
        public static final int P3TOTAL_POS = 13;
        public static final int P4TOTAL_POS = 14;
        public static Uri buildDirUri() {
            return BASE_URI.buildUpon()
                  .appendPath(TABLE_NAME)
                  .build();
        }
    }

    public static class HoleView {
        static final String TABLE_NAME = "holeView";
        public static final String ROUND_ID = "roundId";
        public static final String HOLE_NUM = "holeNumber";
        public static final int ROUNDID_POS = 0;
        public static final int HOLENUMBER_POS = 1 ;
        public static final int HOLEDISTANCE_POS = 2;
        public static final int HOLEPAR_POS = 3;
        public static final int P1SCORE_POS = 4;
        public static final int P2SCORE_POS = 5;
        public static final int P3SCORE_POS = 6;
        public static final int P4SCORE_POS = 7;
        public static final int P1PUTTS_POS = 8;
        public static final int P2PUTTS_POS = 9;
        public static final int P3PUTTS_POS = 10;
        public static final int P4PUTTS_POS = 11;
        public static final int P1PENALTIES_POS = 12;
        public static final int P2PENALTIES_POS = 13;
        public static final int P3PENALTIES_POS = 14;
        public static final int P4PENALTIES_POS = 15;
        public static final int P1SAND_POS = 16;
        public static final int P2SAND_POS = 17;
        public static final int P3SAND_POS = 18;
        public static final int P4SAND_POS = 19;
        public static final int P1GIR_POS = 20;
        public static final int P2GIR_POS = 21;
        public static final int P3GIR_POS = 22;
        public static final int P4GIR_POS = 23;
        public static final int P1FIR_POS = 24;
        public static final int P2FIR_POS = 25;
        public static final int P3FIR_POS = 26;
        public static final int P4FIR_POS = 27;
        public static final int P1INITIALS_POS = 28;
        public static final int P2INITIALS_POS = 29;
        public static final int P3INITIALS_POS = 30;
        public static final int P4INITIALS_POS = 31;
        public static final int HOLECNT_POS = 32;

        public static Uri buildDirUri() {
            return BASE_URI.buildUpon()
                  .appendPath(TABLE_NAME)
                  .build();
        }
    }

    public static class WidgetView {
        static final String TABLE_NAME = "widgetView";
        public static final int PLAYERID_POS = 0;
        public static final int PLAYERFIRST_POS = 1;
        public static final int PLAYERLAST_POS= 2;
        public static final int GAMECNT_POS = 3;
        public static final int AVGSCORE_POS= 4;
        public static final int LOWSCORE_POS = 5;

        public static Uri buildDirUri() {
            return BASE_URI.buildUpon()
                  .appendPath(TABLE_NAME)
                  .build();
        }
    }
}