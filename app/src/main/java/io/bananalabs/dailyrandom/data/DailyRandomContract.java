package io.bananalabs.dailyrandom.data;

import android.provider.BaseColumns;

/**
 * Created by EDC on 3/16/15.
 */
public class DailyRandomContract {

    public static final class CategoryEntry implements BaseColumns {
        public static final String TABLE_NAME = "category";

        // String, identify the category
        public static final String COLUMN_TITLE = "title";
        // Integer, color assigned to the categoty
        public static final String COLUMN_COLOR = "color";

    }

    public static final class ElementEntry implements BaseColumns {
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
    }
}
