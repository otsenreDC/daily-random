package io.bananalabs.dailyrandom.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * Created by EDC on 3/16/15.
 */
public class DailyRandomContract {

    public static final String CONTENT_AUTHORITY = "io.bananalabs.dailyrandom";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_CATEGORY = "category";
    public static final String PATH_ELEMENT = "element";

    // Format used for storing dates in the database.  ALso used for converting those strings
    // back into date objects for comparison/processing.
    public static final String DATE_FORMAT = "yyyyMMddHHmmss";

    /**
     * Converts Date class to a string representation, used for easy comparison and database lookup.
     * @param date The input date
     * @return a DB-friendly representation of the date, using the format defined in DATE_FORMAT.
     */
    public static String getDbDateString(Date date){
        // Because the API returns a unix timestamp (measured in seconds),
        // it must be converted to milliseconds in order to be converted to valid date.
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        return sdf.format(date);
    }

    public static final class CategoryEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_CATEGORY).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                CONTENT_AUTHORITY + "/" + PATH_CATEGORY;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
                CONTENT_AUTHORITY + "/" + PATH_CATEGORY;

        public static final String TABLE_NAME = "category";

        // String, identify the category
        public static final String COLUMN_TITLE = "title";
        // Integer, color assigned to the categoty
        public static final String COLUMN_COLOR = "color";

        public static Uri buildCategoryUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }

    public static final class ElementEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_ELEMENT).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                CONTENT_AUTHORITY + "/" + PATH_ELEMENT;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
                CONTENT_AUTHORITY + "/" + PATH_ELEMENT;

        public static final String TABLE_NAME = "element";

        // Column with the foreign key into the category table
        public static final String COLUMN_CAT_KEY = "category_id";
        // String, identify the element
        public static final String COLUMN_TITLE = "title";
        // Integer, counts how many times the element has been chosen
        public static final String COLUMN_COUNTER = "counter";
        // Double, latitude for location (if apply)
        public static final String COLUMN_COORD_LAT = "coord_lay";
        // Double, longitude for latitude (if apply)
        public static final String COLUMN_COORD_LONG = "coord_long";
        // Date, last selection date
        public static final String COLUM_DATETEXT = "date";

        public static Uri buildElementUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildElementCategory(String category) {
            return CONTENT_URI.buildUpon().appendPath(category).build();
        }

        public static Uri buildElementCategory(long categoryId) {
            return CONTENT_URI.buildUpon().appendPath(CategoryEntry.TABLE_NAME).appendPath(Long.toString(categoryId)).build();
        }

        public static String getCategoryFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }
}
