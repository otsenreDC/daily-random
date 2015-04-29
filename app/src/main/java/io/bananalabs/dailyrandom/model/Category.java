package io.bananalabs.dailyrandom.model;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.google.gson.Gson;

import io.bananalabs.dailyrandom.data.DailyRandomContract;

/**
 * Created by EDC on 4/26/15.
 */
public class Category {

    private long _id;
    private String title;
    private int color;

    public Category() {
        this.title = new String();
        this.color = 0;
    }

    public Category(long _id, String title, int color) {
        this._id = _id;
        this.title = title;
        this.color = color;
    }

    public long save(Context context) {
        Uri categoryUri = context.getContentResolver().insert(DailyRandomContract.CategoryEntry.CONTENT_URI, this.createContentValues());
        long categoryRowId = ContentUris.parseId(categoryUri);
        return categoryRowId;
    }

    public static Category readCategoryFromCursor(Cursor cursor) {
        int indexTitle = cursor.getColumnIndex(DailyRandomContract.CategoryEntry.COLUMN_TITLE);
        int indexColor = cursor.getColumnIndex(DailyRandomContract.CategoryEntry.COLUMN_COLOR);
        int indexId = cursor.getColumnIndex(DailyRandomContract.CategoryEntry._ID);
        String title = cursor.getString(indexTitle);
        int color = cursor.getInt(indexColor);
        long _id = cursor.getLong(indexId);
        return new Category(_id, title, color);
    }

    public static Category readCategory(Context context, long id) {
        Category category = null;

        Cursor cursor = context.getContentResolver().query(
                DailyRandomContract.CategoryEntry.buildCategoryUri(id),
                null,
                null,
                null,
                null);

        if (cursor.moveToFirst()) {
            category = readCategoryFromCursor(cursor);
        }
        cursor.close();
        return category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    private ContentValues createContentValues() {
        ContentValues contentValues = new ContentValues();

        contentValues.put(DailyRandomContract.CategoryEntry.COLUMN_TITLE, this.title);
        contentValues.put(DailyRandomContract.CategoryEntry.COLUMN_COLOR, this.color);

        return contentValues;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this, Category.class);
    }
}
