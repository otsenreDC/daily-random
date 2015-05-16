package io.bananalabs.dailyrandom;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class PlacesBroadcast extends BroadcastReceiver {

    private PlacesBroadcastListener listener;

    public PlacesBroadcast() {
    }

    public PlacesBroadcast(PlacesBroadcastListener listener) {
        this.listener = listener;
    }

    public interface PlacesBroadcastListener {
        public void onPlacesDataReceived(String json);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(PLacesService.BROADCAST_ACTION_PLACES)) {
            String json = intent.getStringExtra(PLacesService.BROADCAST_PROPERTY_PLACES_JSON);
            if (listener != null) {
                listener.onPlacesDataReceived(json);
            }
        }
    }
}
