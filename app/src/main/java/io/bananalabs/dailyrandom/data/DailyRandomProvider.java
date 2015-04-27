package io.bananalabs.dailyrandom.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Created by EDC on 3/19/15.
 */
public class DailyRandomProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private DailyRandomDbHelper mOpenHelper;

    private static final int CATEGORY = 100;
    private static final int CATEGORY_ID = 101;
    private static final int ELEMENT = 200;
    private static final int ELEMENT_ID = 201;
    private static final int ELEMENT_WITH_CATEGORY = 202;

    private static final SQLiteQueryBuilder sDailyRandomSettingQueryBuilder;

    static {
        sDailyRandomSettingQueryBuilder = new SQLiteQueryBuilder();
        sDailyRandomSettingQueryBuilder.setTables(
                DailyRandomContract.ElementEntry.TABLE_NAME + " INNER JOIN " +
                        DailyRandomContract.CategoryEntry.TABLE_NAME +
                        " ON " + DailyRandomContract.ElementEntry.TABLE_NAME +
                        "." + DailyRandomContract.ElementEntry.COLUMN_CAT_KEY +
                        " = " + DailyRandomContract.CategoryEntry.TABLE_NAME +
                        "." + DailyRandomContract.CategoryEntry._ID);
    }

    private static final String sCategorySelection =
            DailyRandomContract.CategoryEntry.TABLE_NAME +
                    "." + DailyRandomContract.CategoryEntry.COLUMN_TITLE + " = ? ";

    private Cursor getElementByCategory(Uri uri, String[] projection, String sortOrder) {
        String category = DailyRandomContract.ElementEntry.getCategoryFromUri(uri);

        String selectionArgs[] = new String[]{category};
        String selection = sCategorySelection;

        return sDailyRandomSettingQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
    }

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = DailyRandomContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, DailyRandomContract.PATH_ELEMENT, ELEMENT);
        matcher.addURI(authority, DailyRandomContract.PATH_ELEMENT + "/#", ELEMENT_ID);
        matcher.addURI(authority, DailyRandomContract.PATH_ELEMENT + "/*", ELEMENT_WITH_CATEGORY);

        matcher.addURI(authority, DailyRandomContract.PATH_CATEGORY, CATEGORY);
        matcher.addURI(authority, DailyRandomContract.PATH_CATEGORY + "/#", CATEGORY_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new DailyRandomDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        Cursor retCursor;
        int match = sUriMatcher.match(uri);
        switch (match) {
            case CATEGORY:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        DailyRandomContract.CategoryEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case CATEGORY_ID:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        DailyRandomContract.CategoryEntry.TABLE_NAME,
                        projection,
                        DailyRandomContract.CategoryEntry._ID + " = " + ContentUris.parseId(uri),
                        null,
                        null,
                        null,
                        sortOrder
                );
                break;
            case ELEMENT:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        DailyRandomContract.ElementEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case ELEMENT_ID:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        DailyRandomContract.ElementEntry.TABLE_NAME,
                        projection,
                        DailyRandomContract.ElementEntry._ID + " = " + ContentUris.parseId(uri),
                        null,
                        null,
                        null,
                        sortOrder
                );
                break;
            case ELEMENT_WITH_CATEGORY:
                retCursor = getElementByCategory(uri, projection, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);

        switch (match) {
            case CATEGORY:
                return DailyRandomContract.CategoryEntry.CONTENT_TYPE;
            case ELEMENT:
                return DailyRandomContract.ElementEntry.CONTENT_TYPE;
            case ELEMENT_WITH_CATEGORY:
                return DailyRandomContract.ElementEntry.CONTENT_TYPE;
            case ELEMENT_ID:
                return DailyRandomContract.ElementEntry.CONTENT_ITEM_TYPE;
            default:
                throw  new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case CATEGORY: {
                long _id = db.insert(DailyRandomContract.CategoryEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = DailyRandomContract.CategoryEntry.buildCategoryUri(_id);
                else
                    throw new SQLException("Failed to insert row into " + uri);
                break;
            }
            case ELEMENT: {
                long _id = db.insert(DailyRandomContract.ElementEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = DailyRandomContract.ElementEntry.buildElementUri(_id);
                else
                    throw new SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri : " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        switch (match) {
            case CATEGORY:
                rowsDeleted = db.delete(DailyRandomContract.CategoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case ELEMENT:
                rowsDeleted = db.delete(DailyRandomContract.ElementEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri : " + uri);
        }

        if (selection == null || rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case CATEGORY:
                rowsUpdated = db.update(DailyRandomContract.CategoryEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case ELEMENT:
                rowsUpdated = db.update(DailyRandomContract.ElementEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri : " + uri);
        }

        if (rowsUpdated != 0 ) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }
}
