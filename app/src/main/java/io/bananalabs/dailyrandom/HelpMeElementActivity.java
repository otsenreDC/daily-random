package io.bananalabs.dailyrandom;

import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.bananalabs.dailyrandom.model.Place;

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

        private final String PLACES = "places";
        private final String DATA_NAME = "name";
        private final long DEFAULT_RADIUS = 1000;

        private EditText mRadiusText;
        private Button mSearchButton;
        private Spinner mPlaceTypeSpinner;

        private PlacesBroadcast mPlacesBroadcast;

        private ArrayList<Place> places = new ArrayList<>();
        private List<Map<String, Object>> mData;
        private SimpleAdapter mSimpleAdapter;

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
            outState.putParcelableArrayList(PLACES, places);
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

            mPlacesBroadcast = new PlacesBroadcast(this);

            mData = new ArrayList<Map<String, Object>>();

            mSimpleAdapter = new SimpleAdapter(
                    getActivity(),
                    mData,
                    android.R.layout.simple_list_item_1,
                    new String[]{DATA_NAME},
                    new int[]{android.R.id.text1});
            ListView listView = (ListView) rootView.findViewById(R.id.list_results);
            listView.setAdapter(mSimpleAdapter);
            TextView emptyText = (TextView) rootView.findViewById(android.R.id.empty);
            listView.setEmptyView(emptyText);

            return rootView;
        }

        @Override
        public void onPlacesDataReceived(String json) {
            new AsyncTask<String, Void, ArrayList<Map<String, Object>>>() {
                @Override
                protected ArrayList<Map<String, Object>> doInBackground(String... params) {
                    List<Place> places = Place.getPlacesFromPlacesResponse(params[0]);
                    ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
                    for (Place place : places) {
                        Map<String, Object> item = new HashMap<String, Object>();
                        item.put(DATA_NAME, place.getName());
                        data.add(item);
                    }
                    return data;
                }

                @Override
                protected void onPostExecute(ArrayList<Map<String, Object>> data) {
                    mData.addAll(data);
                    mSimpleAdapter.notifyDataSetChanged();
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
            mData.clear();
            PLacesService.startAactionAskPlaces(getActivity(), 18.4610001, -69.9609892, radius, placeType);

        }

    }
}
