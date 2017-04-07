package com.sean.golfranger.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Data Base Contract. Holds naming conventions of all database schema objects (Table/field names,
 * etc.)
 */

public class Contract {
    public static final String CONTENT_AUTHORITY = "com.sean.golfranger";
    public static final Uri BASE_URI = Uri.parse("content://com.sean.golfranger");
    public static final String DB_NAME = "golf";

    public static class Courses implements BaseColumns {
        // TABLE NAME
        public static final String TABLE_NAME = "courses";

        // COLUMN NAMES
        /** Type: TEXT NOT NULL*/
        public static String CLUB_NAME = "clubName";
        /** Type: TEXT */
        public static String COURSE_NAME = "courseName";

        /** "vnd.android.cursor.dir/vnd.com.sean.golfranger.courses" */
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;
        /** "vnd.android.cursor.item/vnd.com.sean.golfranger.courses"; */
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;

        public static final String DEFAULT_SORT = _ID + " DESC";

        /** Matches: /courses/ */
        public static Uri buildDirUri() {
            return BASE_URI.buildUpon().appendPath(TABLE_NAME).build();
        }

        /** Matches: /courses/[_id]/ */
        public static Uri buildItemUri(long _id) {
            return BASE_URI.buildUpon().appendPath(TABLE_NAME).appendPath(Long.toString(_id)).build();
        }

        /** Read item ID item detail URI. */
        public static long getItemId(Uri itemUri) {
            return Long.parseLong(itemUri.getPathSegments().get(1));
        }
    }

    public static class Players implements BaseColumns {
        // TABLE NAME FOR DB
        public static final String TABLE_NAME = "players";

        // COLUMN NAMES
        /** Type: TEXT NOT NULL*/
        public static String FIRST_NAME = "firstName";
        /** Type: TEXT NOT NULL*/
        public static String LAST_NAME = "lastName";

        /** "vnd.android.cursor.dir/vnd.com.sean.golfranger.[TABLE_NAME]</>" */
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;
        /** "vnd.android.cursor.item/vnd.com.sean.golfranger.[TABLE_NAME]"; */
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;

        public static final String DEFAULT_SORT = _ID + " DESC";

        /** Matches: /[TABLE_NAME]/ */
        public static Uri buildDirUri() {
            return BASE_URI.buildUpon().appendPath(TABLE_NAME).build();
        }

        /** Matches: /[TABLE_NAME]/[_id]/ */
        public static Uri buildItemUri(long _id) {
            return BASE_URI.buildUpon().appendPath(TABLE_NAME).appendPath(Long.toString(_id)).build();
        }

        /** Read item ID item detail URI. */
        public static long getItemId(Uri itemUri) {
            return Long.parseLong(itemUri.getPathSegments().get(1));
        }
    }

    public static class Rounds implements BaseColumns {
        // TABLE NAME FOR DB
        public static final String TABLE_NAME = "rounds";

        // COLUMN NAMES
        /** Type: INT NOT NULL*/
        public static final String PLAYER1_ID = "playerOneId";
        /** Type: TEXT*/
        public static final String PLAYER1_FIRST_NAME = "playerOneFirstName";
        /** Type: TEXT*/
        public static final String PLAYER1_LAST_NAME = "playerOneLastName";
        /** Type: INT */
        public static final String PLAYER2_ID= "playerTwoId";
        /** Type: TEXT*/
        public static final String PLAYER2_FIRST_NAME = "playerTwoFirstName";
        /** Type: TEXT*/
        public static final String PLAYER2_LAST_NAME = "playerTwoLastName";
        /** Type: INT */
        public static final String PLAYER3_ID = "playerThreeId";
        /** Type: TEXT*/
        public static final String PLAYER3_FIRST_NAME = "playerThreeFirstName";
        /** Type: TEXT*/
        public static final String PLAYER3_LAST_NAME = "playerThreeLastName";
        /** Type: INT */
        public static final String PLAYER4_ID = "playerFourId";
        /** Type: TEXT*/
        public static final String PLAYER4_FIRST_NAME = "playerFourFirstName";
        /** TEXT */
        public static final String PLAYER4_LAST_NAME = "playerFourLastName";
        /** Type: INT NOT NULL */
        public static final String COURSE_ID = "courseId";
        /** TEXT */
        public static final String CLUB_NAME = "clubName";
        /** TEXT */
        public static final String COURSE_NAME = "courseName";
        /** Type: INTEGER NOT NULL*/
        public static final String DATE = "date";

        /** "vnd.android.cursor.dir/vnd.com.sean.golfranger.[TABLE_NAME]</>" */
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;
        /** "vnd.android.cursor.item/vnd.com.sean.golfranger.[TABLE_NAME]"; */
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;

        public static final String DEFAULT_SORT = DATE + " DESC";

        /** Matches: /[TABLE_NAME]/ */
        public static Uri buildDirUri() {
            return BASE_URI.buildUpon().appendPath(TABLE_NAME).build();
        }

        /** Matches: /[TABLE_NAME]/[_id]/ */
        public static Uri buildItemUri(long _id) {
            return BASE_URI.buildUpon().appendPath(TABLE_NAME).appendPath(Long.toString(_id)).build();
        }

        /** Read item ID item detail URI. */
        public static long getItemId(Uri itemUri) {
            return Long.parseLong(itemUri.getPathSegments().get(1));
        }
    }

    public static class Holes implements BaseColumns {
        // TABLE NAME FOR DB
        public static final String TABLE_NAME = "holes";

        // COLUMN NAMES
        /** Type: INT NOT NULL*/
        public static final String ROUND_ID = "roundID";
        /** Type: TEXT*/
        public static final String HOLE_NUMBER = "holeNumber";
        /** Type TEXT  */
        public static final String HOLE_DISTANCE = "holeDistance";
        /** Type: TEXT */
        public static final String HOLE_PAR = "holePar";
        /** Type: TEXT */
        public static final String HOLE_SI = "holeSI";
        /** Type: INT */
        public static final String P1_SCORE = "p1Score";
        /** Type: INT */
        public static final String P2_SCORE = "p2Score";
        /** Type: INT */
        public static final String P3_SCORE = "p3Score";
        /** Type: INT */
        public static final String P4_SCORE = "p4Score";
        /** Type: INT */
        public static final String P1_PUTTS = "p1Putts";
        /** Type: INT */
        public static final String P2_PUTTS = "p2Putts";
        /** Type: INT NOT NULL */
        public static final String P3_PUTTS = "p3Putts";
        /** Type: INT */
        public static final String P4_PUTTS = "p4Putts";
        /** Type: INT */
        public static final String P1_PENALTIES = "p1Penalties";
        /** Type: INT */
        public static final String P2_PENALTIES = "p2Penalties";
        /** Type: INT  */
        public static final String P3_PENALTIES = "p3Penalties";
        /** Type: INT */
        public static final String P4_PENALTIES = "p4Penalties";
        /** Type: BOOLEAN */
        public static final String P1_GIR = "p1Gir";
        /** Type: BOOLEAN */
        public static final String P2_GIR = "p2Gir";
        /** Type: BOOLEAN NOT NULL */
        public static final String P3_GIR = "p3Gir";
        /** Type: BOOLEAN */
        public static final String P4_GIR = "p4Gir";
        /** Type: BOOLEAN */
        public static final String P1_FIR = "p1Fir";
        /** Type: BOOLEAN */
        public static final String P2_FIR = "p2Fir";
        /** Type: BOOLEAN NOT NULL */
        public static final String P3_FIR = "p3Fir";
        /** Type: BOOLEAN */
        public static final String P4_FIR = "p4Fir";
        /** Type: BOOLEAN */
        public static final String P1_SAND = "p1Sand";
        /** Type: BOOLEAN */
        public static final String P2_SAND = "p2Sand";
        /** Type: BOOLEAN NOT NULL */
        public static final String P3_SAND = "p3Sand";
        /** Type: BOOLEAN */
        public static final String P4_SAND = "p4Sand";

        /** "vnd.android.cursor.dir/vnd.com.sean.golfranger.[TABLE_NAME]</>" */
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;
        /** "vnd.android.cursor.item/vnd.com.sean.golfranger.[TABLE_NAME]"; */
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;

        public static final String DEFAULT_SORT = _ID + " DESC";

        /** Matches: /[TABLE_NAME]/ */
        public static Uri buildDirUri() {
            return BASE_URI.buildUpon().appendPath(TABLE_NAME).build();
        }

        /** Matches: /[TABLE_NAME]/[_id]/ */
        public static Uri buildItemUri(long _id) {
            return BASE_URI.buildUpon().appendPath(TABLE_NAME).appendPath(Long.toString(_id)).build();
        }

        /** Read item ID item detail URI. */
        public static long getItemId(Uri itemUri) {
            return Long.parseLong(itemUri.getPathSegments().get(1));
        }
    }

    private Contract() {
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
        public static final int P2_FIRST_NAME = 8;
        public static final int P3_FIRST_NAME = 11;
        public static final int P4_FIRST_NAME = 14;
        public static final int DATE = 16;
    }

    public static Uri roundCoursesPlayersUri() {
         return BASE_URI.buildUpon()
               .appendPath(Rounds.TABLE_NAME)
               .appendPath(Courses.TABLE_NAME)
               .appendPath(Players.TABLE_NAME)
               .build();
    }

    public static Uri roundHolesUri() {
        return BASE_URI.buildUpon()
              .appendPath(Rounds.TABLE_NAME)
              .appendPath(Holes.TABLE_NAME)
              .build();
    }
}
