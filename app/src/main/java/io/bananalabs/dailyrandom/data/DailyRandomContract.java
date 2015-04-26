package io.bananalabs.dailyrandom.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by EDC on 3/16/15.
 */
public class DailyRandomContract {

    public static final String CONTENT_AUTHORITY = "io.bananalabs.dailyrandom";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_CATEGORY = "category";
    public static final String PATH_ELEMENT = "element";

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

        public static Uri buildElementUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildElementCategory(String category) {
            return CONTENT_URI.buildUpon().appendPath(category).build();
        }

        public static String getCategoryFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }
}
