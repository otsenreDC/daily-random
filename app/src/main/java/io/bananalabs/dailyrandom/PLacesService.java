package io.bananalabs.dailyrandom;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class PLacesService extends IntentService {

    private static final String LOG_TAG = PLacesService.class.getSimpleName();

    public static final String BROADCAST_PLACES = "io.bananalabs.dailyrandom.broadcast.action.PLACES";
    public static final String BROADCAST_PROPERTY_PLACES_JSON = "places.json";

    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_ASK_PLACES = "io.bananalabs.dailyrandom.action.ASK_PLACES";

    private static final String EXTRA_LATITUDE = "io.bananalabs.dailyrandom.extra.LATITUDE";
    private static final String EXTRA_LONGITUDE = "io.bananalabs.dailyrandom.extra.LONGITUDE";
    private static final String EXTRA_TYPE = "io.bananalabs.dailyrandom.extra.TYPE";

    public static void startAactionAskPlaces(Context context, double latitude, double longitude, int type) {
        Intent intent = new Intent(context, PLacesService.class);
        intent.setAction(ACTION_ASK_PLACES);
        intent.putExtra(EXTRA_LATITUDE, latitude);
        intent.putExtra(EXTRA_LONGITUDE, longitude);
        intent.putExtra(EXTRA_TYPE, type);
        context.startService(intent);
    }

    public PLacesService() {
        super("PLacesService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_ASK_PLACES.equals(action)) {
                final Double latitude = intent.getDoubleExtra(EXTRA_LATITUDE, 0);
                final Double longitude = intent.getDoubleExtra(EXTRA_LONGITUDE, 0);
                final Integer type = intent.getIntExtra(EXTRA_TYPE, 0);
                handleActionAskPlaces(latitude, longitude, type);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionAskPlaces(Double latitude, Double longitude, Integer type) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        String placesJson = null;

        String key = "";

        try {
            final String PLACE_URL = "";
            final String KEY_PARAM = "key";
            final String LOC_PARAM = "location";
            final String TYP_PARAM = "type";

            Uri builtUri = Uri.parse(PLACE_URL).buildUpon()
                    .appendQueryParameter(KEY_PARAM, key)
                    .appendQueryParameter(LOC_PARAM, locationString(latitude, longitude))
                    .appendQueryParameter(TYP_PARAM, type.toString())
                    .build();

            URL url = new URL(builtUri.toString());

            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            InputStream inputStream = connection.getInputStream();
            if (inputStream == null) return;

            StringBuffer buffer = new StringBuffer();
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
                buffer.append("\n");
            }

            if (buffer.length() == 0) return;

            placesJson = buffer.toString();

            sendPlacesBroadcast(placesJson);

        } catch (IOException ioe) {

        }

    }

    private String locationString(Double latitude, Double longitude) {
        return String.format("%s,%s", latitude.toString(), longitude.toString());
    }

    private void sendPlacesBroadcast(String places) {
        Intent intent = new Intent(BROADCAST_PLACES);
        intent.putExtra(BROADCAST_PROPERTY_PLACES_JSON, places);
        sendBroadcast(intent);
    }
}
