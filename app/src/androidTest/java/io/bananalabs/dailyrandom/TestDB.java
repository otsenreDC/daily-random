package io.bananalabs.dailyrandom;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.Map;
import java.util.Set;

import io.bananalabs.dailyrandom.data.DailyRandomContract.*;
import io.bananalabs.dailyrandom.data.DailyRandomDbHelper;

/**
 * Created by EDC on 3/16/15.
 */
public class TestDB extends AndroidTestCase {

    public void testCreateDb() throws Throwable {
        mContext.deleteDatabase(DailyRandomDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new DailyRandomDbHelper(mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());
        db.close();
    }

    public void testInsertDb() throws Throwable {
        DailyRandomDbHelper dbHelper = new DailyRandomDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues categoryValues = categoryEnrty();

        long categoryRowId;
        categoryRowId = db.insert(CategoryEntry.TABLE_NAME, null, categoryValues);

        // Verify we got a row back
        assertTrue(categoryRowId != -1);

        // Query the row back
        Cursor cursor = db.query(
                CategoryEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

        validateCursor(cursor, categoryValues);

        assertFalse(cursor.moveToNext());

        cursor.close();
        dbHelper.close();
        db.close();
    }

    public void testInsertElementDb() throws Throwable {
        DailyRandomDbHelper dbHelper = new DailyRandomDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues categoryValues = categoryEnrty();

        long categoryRowId;
        categoryRowId = db.insert(CategoryEntry.TABLE_NAME, null, categoryValues);

        ContentValues elementValues = elementEntry(categoryRowId);

        long elementRowId;
        elementRowId = db.insert(ElementEntry.TABLE_NAME, null, elementValues);

        // Verify we got a row back
        assertTrue(elementRowId != -1);

        // Query the row back
        Cursor cursor = db.query(
                ElementEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

        validateCursor(cursor, elementValues);

        assertFalse(cursor.moveToNext());

        cursor.close();
        dbHelper.close();
        db.close();

    }

    private ContentValues categoryEnrty() {
        ContentValues contentValues = new ContentValues();

        contentValues.put(CategoryEntry.COLUMN_TITLE, "Fast Food");
        contentValues.put(CategoryEntry.COLUMN_COLOR, 0);

        return contentValues;
    }

    private ContentValues elementEntry(long categoryId) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(ElementEntry.COLUMN_TITLE, "Pizza");
        contentValues.put(ElementEntry.COLUMN_COUNTER, 0);
        contentValues.put(ElementEntry.COLUMN_COORD_LAT, 0);
        contentValues.put(ElementEntry.COLUMN_COORD_LONG, 0);
        contentValues.put(ElementEntry.COLUMN_CAT_KEY, categoryId);

        return contentValues;
    }

    private void validateCursor (Cursor valueCursor, ContentValues expectedValues) {

        assertTrue(valueCursor.moveToFirst());

        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int index = valueCursor.getColumnIndex(columnName);
            assertFalse(index == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals(expectedValue, valueCursor.getString(index));
        }
        valueCursor.close();
    }
}
