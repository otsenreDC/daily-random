package io.bananalabs.dailyrandom.model;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.Calendar;
import java.util.Date;
import java.util.ArrayList;

import io.bananalabs.dailyrandom.Utilities;
import io.bananalabs.dailyrandom.data.DailyRandomContract;

/**
 * Created by EDC on 4/26/15.
 */
public class Element {

    private long _id;
    private long _categoryId;
    private String title;
    private long counter;
    private double longitude;
    private double latitude;
    private Date date;

    public Element() {
        this._id = -1;
        this._categoryId = -1;
        this.title = new String();
        this.counter = 0;
        this.latitude = -1;
        this.latitude = -1;
        this.date = Calendar.getInstance().getTime();
    }

    public Element(long _categoryId) {
        this._id = -1;
        this._categoryId = _categoryId;
        this.title = new String();
        this.counter = 0;
        this.latitude = -1;
        this.longitude = -1;
        this.date = Calendar.getInstance().getTime();
    }

    public Element(long _categoryId, String title, long counter, double latitude, double longitude) {
        this._id = -1;
        this._categoryId = _categoryId;
        this.title = title;
        this.counter = counter;
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = Calendar.getInstance().getTime();
    }

    public Element(long _id, long _categoryId, String title, long counter, double latitude, double longitude, Date date) {
        this._id = _id;
        this._categoryId = _categoryId;
        this.title = title;
        this.counter = counter;
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
    }

    public Element(Cursor cursor) {
        Element element = Element.elementFromCursor(cursor);
        this._id = element.get_id();
        this._categoryId = element._categoryId;
        this.title = element.title;
        this.counter = element.counter;
        this.latitude = element.latitude;
        this.longitude = element.longitude;
        this.date = element.date;
    }

    public long updateAsSeleted() {
        updateDate();
        return incrementCounter();
    }

    private long incrementCounter() {
        return ++this.counter;
    }

    private void updateDate() {
        this.date = Calendar.getInstance().getTime();
    }

    public long save(Context context, long categoryId) {
        this._categoryId = categoryId;
        return this.save(context);
    }

    public long save(Context context) {
        Uri elementUri = context.getContentResolver().insert(
                DailyRandomContract.ElementEntry.CONTENT_URI,
                createContentValues());
        long elementRowId = ContentUris.parseId(elementUri);
        return elementRowId;
    }

    public long update(Context context) {
        int count = context.getContentResolver().update(
                DailyRandomContract.ElementEntry.CONTENT_URI,
                this.createContentValues(),
                DailyRandomContract.ElementEntry._ID + "= ?",
                new String[]{Long.toString(this._id)}
        );
        return count;
    }

    public long delete(Context context) {
        long deleteRows = 0;
        if (this._id != -1)
            deleteRows = context.getContentResolver().delete(
                    DailyRandomContract.ElementEntry.CONTENT_URI,
                    DailyRandomContract.ElementEntry._ID + " = ?",
                    new String[]{Long.toString(this._id)});

        return deleteRows;
    }

    public static Element readElement(Context context, long id) {
        Element element = null;

        Cursor cursor = context.getContentResolver().query(
                DailyRandomContract.ElementEntry.buildElementUri(id),
                null,
                null,
                null,
                null);

        if (cursor.moveToFirst()) {
            element = Element.elementFromCursor(cursor);
        }

        cursor.close();
        return element;
    }

    public static ArrayList<Element> readElementWithCategoryId(Context context, long categoryId) {
        ArrayList<Element> elements = new ArrayList<>();

        Cursor cursor = context.getContentResolver().query(
                DailyRandomContract.ElementEntry.buildElementCategory(categoryId),
                null,
                null,
                null,
                DailyRandomContract.ElementEntry.COLUMN_TITLE + " COLLATE NOCASE ASC");

        if (cursor.moveToFirst()) {
            do {
                elements.add(Element.elementFromCursor(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return elements;
    }

    private static Element elementFromCursor(Cursor cursor) {
        int indexTitle = cursor.getColumnIndex(DailyRandomContract.ElementEntry.COLUMN_TITLE);
        int indexCounter = cursor.getColumnIndex(DailyRandomContract.ElementEntry.COLUMN_COUNTER);
        int indexLatitude = cursor.getColumnIndex(DailyRandomContract.ElementEntry.COLUMN_COORD_LAT);
        int indexLongitude = cursor.getColumnIndex(DailyRandomContract.ElementEntry.COLUMN_COORD_LONG);
        int indexCategoryId = cursor.getColumnIndex(DailyRandomContract.ElementEntry.COLUMN_CAT_KEY);
        int indexDate = cursor.getColumnIndex(DailyRandomContract.ElementEntry.COLUMN_DATETEXT);
        int indexId = cursor.getColumnIndex(DailyRandomContract.ElementEntry._ID);

        String title = cursor.getString(indexTitle);
        long counter = cursor.getLong(indexCounter);
        double latitude = cursor.getDouble(indexLatitude);
        double longitude = cursor.getDouble(indexLongitude);
        long catId = cursor.getLong(indexCategoryId);
        String date = cursor.getString(indexDate);
        long _id = cursor.getLong(indexId);

        return new Element(_id, catId, title, counter, latitude, longitude, Utilities.parseDate(date));
    }

    public boolean hasLocation() {
        return (this.latitude > -1 && this.longitude > - 1);
    }

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public long get_categoryId() {
        return _categoryId;
    }

    public void set_categoryId(long _categoryId) {
        this._categoryId = _categoryId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getCounter() {
        return counter;
    }

    public void setCounter(long counter) {
        this.counter = counter;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    private ContentValues createContentValues() {
        ContentValues contentValues = new ContentValues();

        contentValues.put(DailyRandomContract.ElementEntry.COLUMN_TITLE, this.title);
        contentValues.put(DailyRandomContract.ElementEntry.COLUMN_COUNTER, this.counter);
        contentValues.put(DailyRandomContract.ElementEntry.COLUMN_COORD_LAT, this.latitude);
        contentValues.put(DailyRandomContract.ElementEntry.COLUMN_COORD_LONG, this.longitude);
        contentValues.put(DailyRandomContract.ElementEntry.COLUMN_DATETEXT, DailyRandomContract.getDbDateString(this.date));
        contentValues.put(DailyRandomContract.ElementEntry.COLUMN_CAT_KEY, this._categoryId);

        return contentValues;
    }

}
