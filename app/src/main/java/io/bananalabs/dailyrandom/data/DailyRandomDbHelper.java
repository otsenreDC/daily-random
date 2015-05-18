package io.bananalabs.dailyrandom.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import io.bananalabs.dailyrandom.data.DailyRandomContract.*;

/**
 * Created by EDC on 3/16/15.
 */
public class DailyRandomDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "dailyrandom.db";

    private final String CREATE_TABLE = "CREATE TABLE ";
    private final String PRIMARY_KEY = " INTEGER PRIMARY KEY AUTOINCREMENT ";
    private final String TEXT_NOT_NULL = " TEXT NOT NULL ";
    private final String INTEGER_NOT_NULL = " INTEGER NOT NULL ";
    private final String REAL = " REAL ";
    private final String REAL_NOT_NULL = " REAL NOT NULL ";
    private final String FOREIGN_KEY = " FOREIGN KEY ";
    private final String REFERENCES  = " REFERENCES ";
    private final String COMMA_SEP = " , ";
    private final String OPEN_ = " (";
    private final String CLOSE_ = ")";

    private final String SQL_CREATE_ELEMENT_TABLE =
            CREATE_TABLE + ElementEntry.TABLE_NAME +
                    OPEN_ +
                    ElementEntry._ID                + PRIMARY_KEY       + COMMA_SEP +
                    ElementEntry.COLUMN_TITLE       + TEXT_NOT_NULL     + COMMA_SEP +
                    ElementEntry.COLUMN_COUNTER     + INTEGER_NOT_NULL  + COMMA_SEP +
                    ElementEntry.COLUMN_COORD_LAT   + REAL              + COMMA_SEP +
                    ElementEntry.COLUMN_COORD_LONG  + REAL              + COMMA_SEP +
                    ElementEntry.COLUMN_DATETEXT + TEXT_NOT_NULL     + COMMA_SEP +
                    ElementEntry.COLUMN_CAT_KEY     + INTEGER_NOT_NULL  + COMMA_SEP +
                    // Set up for foreign key
                    FOREIGN_KEY + OPEN_ + ElementEntry.COLUMN_CAT_KEY + CLOSE_ +
                    REFERENCES +
                    CategoryEntry.TABLE_NAME +
                    OPEN_ + CategoryEntry._ID + CLOSE_+
                    CLOSE_ + ";";

    private final String SQL_CREATE_CATEGORY_TABLE =
            CREATE_TABLE + CategoryEntry.TABLE_NAME +
                    OPEN_ +
                    CategoryEntry._ID + PRIMARY_KEY + COMMA_SEP +
                    CategoryEntry.COLUMN_TITLE + TEXT_NOT_NULL + COMMA_SEP +
                    CategoryEntry.COLUMN_COLOR + REAL_NOT_NULL +
                    CLOSE_ + ";";

    public DailyRandomDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_CATEGORY_TABLE);
        db.execSQL(SQL_CREATE_ELEMENT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + CategoryEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ElementEntry.TABLE_NAME);
        onCreate(db);
    }
}
