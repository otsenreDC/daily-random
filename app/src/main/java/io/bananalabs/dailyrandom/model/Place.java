package io.bananalabs.dailyrandom.model;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
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

    public static List<Place> getPlacesFromJsonArray(String jsonString) {
        Type listType = new TypeToken<ArrayList<Place>>() {
        }.getType();
        List<Place> places = new Gson().fromJson(jsonString, listType);
        return places;
    }

    class Geometry {
        public Location location;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return this.geometry.location.lat;
    }

    public double getLongitude() {
        return this.geometry.location.lng;
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

    public static ArrayList<Place> placesFromParcelableArray(ArrayList<Parcelable> places) {
        if (places != null) {
            ArrayList<Place> placeArrayList = new ArrayList<>();
            for (Parcelable parcelable : places) {

            }
            return placeArrayList;
        }
        return null;
    }
}
