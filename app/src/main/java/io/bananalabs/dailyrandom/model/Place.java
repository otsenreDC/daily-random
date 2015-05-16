package io.bananalabs.dailyrandom.model;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;

import java.util.List;

/**
 * Created by EDC on 5/11/15.
 */
public class Place implements Parcelable{

    private static final String KEY_LONGITUDE   = "lng";
    private static final String KEY_LATITUDE    = "lat";
    private static final String KEY_NAME        = "name";

    private Geometry geometry;
    private String name;

    public static List<Place> getPlacesFromPlacesResponse(String jsonString) {
        Gson gson = new Gson();
        JsonResponse jsonResponse = gson.fromJson(jsonString, JsonResponse.class);
        return jsonResponse.results;
    }

    class Geometry {
        Location location;
        public Geometry(){}
        public Geometry(double latitude, double longitude) {
            location = new Location(latitude, longitude);
        }
    }

    class Location {
        double lat;
        double lng;
        public Location() {}
        public Location(double latitude, double longitude) {
            this.lat = latitude;
            this.lng = longitude;
        }
    }

    class JsonResponse {
        String status;
        List<Place> results;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        Bundle bundle = new Bundle();
        bundle.putDouble(KEY_LATITUDE, geometry.location.lat);
        bundle.putDouble(KEY_LONGITUDE, geometry.location.lng);
        bundle.putString(KEY_NAME, name);
        out.writeBundle(bundle);
    }

    public static final Creator<Place> CREATOR =
            new ClassLoaderCreator<Place>() {
                @Override
                public Place createFromParcel(Parcel source, ClassLoader loader) {
                    return new Place(source);
                }

                @Override
                public Place createFromParcel(Parcel source) {
                    return null;
                }

                @Override
                public Place[] newArray(int size) {
                    return new Place[size];
                }
            };

    public Place (Parcel in) {
        Bundle bundle = in.readBundle();
        double lat = bundle.getDouble(KEY_LATITUDE, 0);
        double lng = bundle.getDouble(KEY_LONGITUDE, 0);

        this.name = bundle.getString(KEY_NAME, "");;
        this.geometry = new Geometry(lat, lng);
    }
}
