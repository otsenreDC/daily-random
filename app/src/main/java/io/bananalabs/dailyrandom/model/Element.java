package io.bananalabs.dailyrandom.model;

import android.content.Context;

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

    public Element() {
        this._id = -1;
        this._categoryId = -1;
        this.title = new String();
        this.counter = 0;
        this.latitude = 0;
        this.latitude = 0;
    }

    public Element(long _categoryId) {
        this._id = -1;
        this._categoryId = _categoryId;
        this.title = new String();
        this.counter = 0;
        this.latitude = 0;
        this.latitude = 0;
    }

    public Element(long _categoryId, String title, double latitude, double longitude) {
        this._id = -1;
        this._categoryId = _categoryId;
        this.title = title;
        this.counter = 0;
        this.latitude = latitude;
        this.latitude = longitude;
    }

    public long save(Context context) {
//        Uri elementUri = context.getContentResolver().insert(DailyRandomContract.ElementEntry.buildElementCategory())
        return 0;
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
}
