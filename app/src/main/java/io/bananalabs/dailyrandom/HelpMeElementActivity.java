package io.bananalabs.dailyrandom;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;

import io.bananalabs.dailyrandom.model.Place;
import io.bananalabs.dailyrandom.others.PlacesAdapter;

public class HelpMeElementActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_me_element);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new HelpMeElementFragment())
                    .commit();
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class HelpMeElementFragment extends Fragment implements PlacesBroadcast.PlacesBroadcastListener {

        private final String LOG_TAG = HelpMeElementActivity.class.getSimpleName();

        private final String PLACES = "places";
        private final long DEFAULT_RADIUS = 1000;

        private EditText mRadiusText;
        private Button mSearchButton;
        private Button mMakeSelection;
        private Spinner mPlaceTypeSpinner;

        private PlacesBroadcast mPlacesBroadcast;

        private ArrayList<Place> mPlaces = new ArrayList<>();
        private PlacesAdapter mPlacesAdater;

        public HelpMeElementFragment() {
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
                    performSearch();
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

            return rootView;
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
            String placeType = getPlaceTypeFromInput();
            if ((radius = getRadiusFromInput()) == -1) {
                return;
            }
            mPlaces.clear();
            PLacesService.startAactionAskPlaces(getActivity(), 18.4610001, -69.9609892, radius, placeType);
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

    }
}
