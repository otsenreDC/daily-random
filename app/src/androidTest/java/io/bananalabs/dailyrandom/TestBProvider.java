package io.bananalabs.dailyrandom;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.test.AndroidTestCase;

import io.bananalabs.dailyrandom.data.DailyRandomContract;

/**
 * Created by EDC on 4/25/15.
 */
public class TestBProvider extends AndroidTestCase {

    public static String LOG_TAG = TestBProvider.class.getSimpleName();

    public void deleteAllRecords() {
        mContext.getContentResolver().delete(
                DailyRandomContract.CategoryEntry.CONTENT_URI,
                null,
                null
        );

        mContext.getContentResolver().delete(
                DailyRandomContract.ElementEntry.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver().query(
                DailyRandomContract.CategoryEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
            assertEquals(0, cursor.getCount());
        cursor.close();


        cursor = mContext.getContentResolver().query(
                DailyRandomContract.ElementEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals(0, cursor.getCount());
        cursor.close();
    }

    public void setUp() {
        deleteAllRecords();
    }

    // The target api annotation is needed for the call to keySet -- we wouldn't want
    // to use this in our app, but in a test it's fine to assume a higher target.
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    void addAllContentValues(ContentValues destination, ContentValues source) {
        for (String key : source.keySet()) {
            destination.put(key, source.getAsString(key));
        }
    }

    public void testInsertReadProvider() {
        // Testing CATEGORY
        ContentValues categoryValues = TestADB.createCategoryValues();
        Uri categoryUri = mContext.getContentResolver().insert(DailyRandomContract.CategoryEntry.CONTENT_URI, categoryValues);
        long categoryRowId = ContentUris.parseId(categoryUri);
        assertTrue(categoryRowId != -1);

        Cursor categoryCursor = mContext.getContentResolver().query(
                DailyRandomContract.CategoryEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        TestADB.validateCursor(categoryCursor, categoryValues);

        // Testing ELEMENT
        ContentValues elementValues = TestADB.createElementValues(categoryRowId);
        Uri elementUri = mContext.getContentResolver().insert(DailyRandomContract.ElementEntry.CONTENT_URI, elementValues);
        long elementRowId = ContentUris.parseId(elementUri);
        assertTrue(elementRowId != -1);

        Cursor elementCursor = mContext.getContentResolver().query(
                DailyRandomContract.ElementEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        TestADB.validateCursor(elementCursor, elementValues);

        // Testing join
        addAllContentValues(elementValues, categoryValues);

        elementCursor = mContext.getContentResolver().query(
                DailyRandomContract.ElementEntry.buildElementCategory(TestADB.TEST_CATEGORY),
                null,
                null,
                null,
                null
        );

        TestADB.validateCursor(elementCursor, elementValues);
    }

    public void testUpdate() {
        ContentValues categoryValues = TestADB.createCategoryValues();
        Uri categoryUri = mContext.getContentResolver().insert(DailyRandomContract.CategoryEntry.CONTENT_URI, categoryValues);
        long categoryRowId = ContentUris.parseId(categoryUri);

        assertTrue(categoryRowId != -1);

        ContentValues updatedValues = new ContentValues(categoryValues);
        updatedValues.put(DailyRandomContract.CategoryEntry._ID, categoryRowId);
        updatedValues.put(DailyRandomContract.CategoryEntry.COLUMN_TITLE, "Weekend food");

        int count = mContext.getContentResolver().update(
                DailyRandomContract.CategoryEntry.CONTENT_URI,
                updatedValues,
                DailyRandomContract.CategoryEntry._ID + "= ?",
                new String[]{Long.toString(categoryRowId)});

        assertEquals(count, 1);

        Cursor cursor = mContext.getContentResolver().query(
                DailyRandomContract.CategoryEntry.buildCategoryUri(categoryRowId),
                null,
                null,
                null,
                null
        );

        TestADB.validateCursor(cursor, updatedValues);

        ContentValues elementValues = TestADB.createElementValues(categoryRowId);
        Uri elementUri = mContext.getContentResolver().insert(DailyRandomContract.ElementEntry.CONTENT_URI, elementValues);
        long elementRowId = ContentUris.parseId(elementUri);

        assertTrue(elementRowId != -1);

        ContentValues updatedElementValues = new ContentValues(elementValues);
        updatedElementValues.put(DailyRandomContract.ElementEntry._ID, elementRowId);
        updatedElementValues.put(DailyRandomContract.ElementEntry.COLUMN_TITLE, "Hamburger");

        int countElement = mContext.getContentResolver().update(
                DailyRandomContract.ElementEntry.CONTENT_URI,
                updatedElementValues,
                DailyRandomContract.ElementEntry._ID + "= ?",
                new String[]{Long.toString(elementRowId)});

        assertEquals(countElement, 1);

        Cursor elementCursor = mContext.getContentResolver().query(
                DailyRandomContract.ElementEntry.buildElementUri(elementRowId),
                null,
                null,
                null,
                null
        );

        TestADB.validateCursor(elementCursor, updatedElementValues);
    }

    public void testDeleteAllRecords() {
        deleteAllRecords();
    }

    public void testDelete () {
        ContentValues categoryValues = TestADB.createCategoryValues();
        Uri categoryUri = mContext.getContentResolver().insert(DailyRandomContract.CategoryEntry.CONTENT_URI, categoryValues);
        long categoryRowId = ContentUris.parseId(categoryUri);
        assertTrue(categoryRowId != -1);

        int count = mContext.getContentResolver().delete(
                DailyRandomContract.CategoryEntry.CONTENT_URI,
                DailyRandomContract.CategoryEntry._ID + " = ?",
                new String[] {Long.toString(categoryRowId)});

        assertTrue(count == 1);
    }

    public void testGetType() {
        String type = mContext.getContentResolver().getType(DailyRandomContract.CategoryEntry.CONTENT_URI);
        assertEquals(DailyRandomContract.CategoryEntry.CONTENT_TYPE, type);
    }
}
