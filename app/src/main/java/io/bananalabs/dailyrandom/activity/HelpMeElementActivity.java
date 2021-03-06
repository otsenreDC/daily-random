package io.bananalabs.dailyrandom.activity;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;

import java.util.ArrayList;

import io.bananalabs.dailyrandom.PLacesService;
import io.bananalabs.dailyrandom.PlacesBroadcast;
import io.bananalabs.dailyrandom.R;
import io.bananalabs.dailyrandom.Utilities;
import io.bananalabs.dailyrandom.model.Place;
import io.bananalabs.dailyrandom.others.PlacesAdapter;

public class HelpMeElementActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_me_element);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new HelpMeElementFragment())
                    .commit();
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class HelpMeElementFragment extends Fragment implements PlacesBroadcast.PlacesBroadcastListener, GoogleApiClient.ConnectionCallbacks,
            GoogleApiClient.OnConnectionFailedListener {

        private final String LOG_TAG = HelpMeElementActivity.class.getSimpleName();

        private static final int REQUEST_RESOLVE_ERROR = 1001;

        private final String PLACES = "places";
        private final long DEFAULT_RADIUS = 1000;

        private EditText mRadiusText;
        private Button mSearchButton;
        private Button mMakeSelection;
        private Spinner mPlaceTypeSpinner;

        private PlacesBroadcast mPlacesBroadcast;

        private ArrayList<Place> mPlaces = new ArrayList<>();
        private PlacesAdapter mPlacesAdater;

        private GoogleApiClient mGoogleApiClient;
        private boolean mResolvingError;
        private Location mLocation;

        public HelpMeElementFragment() {
        }

        @Override
        public void onStart() {
            super.onStart();

            if (!mResolvingError) {
                this.mGoogleApiClient.connect();
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            getActivity().registerReceiver(mPlacesBroadcast, new IntentFilter(PLacesService.BROADCAST_ACTION_PLACES));
        }

        @Override
        public void onPause() {
            getActivity().unregisterReceiver(mPlacesBroadcast);
            super.onPause();
        }

        @Override
        public void onStop() {
            mGoogleApiClient.disconnect();
            super.onStop();
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            outState.putParcelableArrayList(PLACES, mPlaces);
            super.onSaveInstanceState(outState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_help_me_element, container, false);

            mRadiusText = (EditText) rootView.findViewById(R.id.edit_radius);
            mRadiusText.setText(Long.valueOf(DEFAULT_RADIUS).toString());
            mPlaceTypeSpinner = (Spinner) rootView.findViewById(R.id.spinner_type);
            mSearchButton = (Button) rootView.findViewById(R.id.button_search);
            mSearchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Utilities.isOnline(v.getContext())) {
                        performSearch();
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                                Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(mRadiusText.getWindowToken(), 0);
                    } else {
                        Toast.makeText(v.getContext(), R.string.no_network, Toast.LENGTH_SHORT).show();
                    }
                }
            });
            mMakeSelection = (Button) rootView.findViewById(R.id.button_select_items);
            mMakeSelection.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    makeSelection();
                }
            });

            mPlacesBroadcast = new PlacesBroadcast(this);

            mPlacesAdater = new PlacesAdapter(getActivity(), mPlaces);
            ListView listView = (ListView) rootView.findViewById(R.id.list_results);
            listView.setAdapter(mPlacesAdater);
            TextView emptyText = (TextView) rootView.findViewById(android.R.id.empty);
            listView.setEmptyView(emptyText);

            this.mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();

            return rootView;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            if (savedInstanceState != null) {
                ArrayList<Place> places = savedInstanceState.getParcelableArrayList(PLACES);
                mPlaces.addAll(places);
                mPlacesAdater.notifyDataSetChanged();
            }
        }

        @Override
        public void onPlacesDataReceived(String json) {
            new AsyncTask<String, Void, ArrayList<Place>>() {
                @Override
                protected ArrayList<Place> doInBackground(String... params) {
                    ArrayList<Place> places = new ArrayList<>(Place.getPlacesFromPlacesResponse(params[0]));
                    return places;
                }

                @Override
                protected void onPostExecute(ArrayList<Place> data) {
                    mPlaces.addAll(data);
                    mPlacesAdater.notifyDataSetChanged();
                }
            }.execute(json);
        }

        @Override
        public void onConnected(Bundle bundle) {
            mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }

        @Override
        public void onConnectionSuspended(int i) {

        }

        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            if (this.mResolvingError) {
                return;
            } else if (connectionResult.hasResolution()) {
                try {
                    this.mResolvingError = true;
                    connectionResult.startResolutionForResult(getActivity(), REQUEST_RESOLVE_ERROR);
                } catch (IntentSender.SendIntentException e) {
                    this.mGoogleApiClient.connect();
                }
            } else {
                this.mResolvingError = false;
            }
        }


        // Private methods
        private long getRadiusFromInput() {
            long radius = -1;
            String text = mRadiusText.getText().toString();
            if (text == null || text.isEmpty()) {
                mRadiusText.setError(getString(R.string.empty_field));
                return -1;
            }
            radius = Long.parseLong(text);
            return radius;
        }

        private String getPlaceTypeFromInput() {
            String placeType = (String) mPlaceTypeSpinner.getSelectedItem();
            return placeType;
        }

        private void performSearch() {
            long radius;
            String[] placesTypes = getActivity().getResources().getStringArray(R.array.place_types);
            int selectedPosition = mPlaceTypeSpinner.getSelectedItemPosition();
            if ((radius = getRadiusFromInput()) == -1) {
                return;
            }
            mPlaces.clear();
            PLacesService.startAactionAskPlaces(getActivity(), mLocation.getLatitude(), mLocation.getLongitude(), radius, placesTypes[selectedPosition]);
        }

        private void makeSelection() {
            SparseBooleanArray selectedPlaces = mPlacesAdater.getCheckedPositions();
            ArrayList<Place> placesSelected = new ArrayList<>();
            if (selectedPlaces != null)
                for (int idx = 0; idx < mPlaces.size(); idx++) {
                    boolean isChecked = selectedPlaces.get(idx, false);
                    if (isChecked) {
                        placesSelected.add(mPlaces.get(idx));
                    }
                }
            Log.d(LOG_TAG, new Gson().toJson(placesSelected));
            Intent intent = new Intent();
            intent.putExtra(Intent.EXTRA_TEXT, new Gson().toJson(placesSelected));
            getActivity().setResult(RESULT_OK, intent);
            getActivity().finish();
        }

        public Bundle getInstanceState() {
            return null;
        }
    }
}
