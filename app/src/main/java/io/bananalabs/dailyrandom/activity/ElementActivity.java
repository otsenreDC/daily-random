package io.bananalabs.dailyrandom.activity;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.getbase.floatingactionbutton.FloatingActionButton;

import java.util.List;

import io.bananalabs.dailyrandom.R;
import io.bananalabs.dailyrandom.Utilities;
import io.bananalabs.dailyrandom.data.DailyRandomContract;
import io.bananalabs.dailyrandom.model.Element;
import io.bananalabs.dailyrandom.model.Place;
import io.bananalabs.dailyrandom.others.ElementCursorAdapter;


public class ElementActivity extends ActionBarActivity {

    public final static int REQUEST_PLACES = 1000;

    private final static String LOG_TAG = ElementActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_element);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new ElementFragment())
                    .commit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK)
            if (REQUEST_PLACES == requestCode) {
                if (data != null) {
                    String json = data.getStringExtra(Intent.EXTRA_TEXT);
                    Fragment fragment = getFragmentManager().findFragmentById(R.id.container);
                    if (fragment.getClass() == ElementFragment.class) {
                        ((ElementFragment)fragment).addPlacesFromJson(json);
                    }
                }
            }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class ElementFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, ElementCursorAdapter.OnMapButtonClickListener {

        private static final int ELEMENT_LOADER = 1;

        private ElementCursorAdapter mElementAdapter;
        private ListView mListView;
        private long categoryId = -1;

        public ElementFragment() {
        }

        // Public methods
        public void addPlacesFromJson(String json) {
            List<Place> places = Place.getPlacesFromJsonArray(json);
            for (Place place : places) {
                String title = place.getName();
                double lat = place.getLatitude();
                double lng = place.getLongitude();
                Element element = new Element(categoryId, title, 0, lat, lng);
                element.save(getActivity());
            }
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            Intent intent = getActivity().getIntent();

            if (intent != null) {
                long catKey = intent.getLongExtra(Intent.EXTRA_KEY_EVENT, -1);
                if (catKey != -1) {
                    categoryId = catKey;
                }
            }

            getLoaderManager().initLoader(ELEMENT_LOADER, null, this);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            mElementAdapter = new ElementCursorAdapter(getActivity(), null, 0);
            mElementAdapter.setMapButtonClickListener(this);

            View rootView = inflater.inflate(R.layout.fragment_element, container, false);
            mListView = (ListView) rootView.findViewById(R.id.list_elements);
            mListView.setAdapter(mElementAdapter);
            mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    Element element = Element.readElement(getActivity(), id);
                    Utilities.showDeleteDialog(getActivity(), positiveAnswerForDeletion(element), null);
                    return true;
                }
            });

            FloatingActionButton fabAddElement = (FloatingActionButton) rootView.findViewById(R.id.button_add_element);
            fabAddElement.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), NewElementActivity.class);
                    intent.putExtra(Intent.EXTRA_KEY_EVENT, categoryId);
                    v.getContext().startActivity(intent);
                }
            });

            FloatingActionButton fabRandom = (FloatingActionButton)rootView.findViewById(R.id.button_random);
            fabRandom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int) Utilities.selectRrandomlyFrom(arrayOfPositions());
                    Cursor cursor = mElementAdapter.getCursor();
                    cursor.moveToPosition(position);
                    Element element = new Element(cursor);
                    element.updateAsSeleted();
                    element.update(getActivity());
                    mListView.setItemChecked(position, true);
                }
            });

            FloatingActionButton fabSearch = (FloatingActionButton) rootView.findViewById(R.id.button_search_places);
            fabSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().startActivityForResult(new Intent(getActivity(), HelpMeElementActivity.class), REQUEST_PLACES);
                }
            });

            return rootView;
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(
                    getActivity(),
                    DailyRandomContract.ElementEntry.buildElementCategory(categoryId),
                    null,
                    null,
                    null,
                    DailyRandomContract.ElementEntry.COLUMN_DATETEXT + " DESC"
            );
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            mElementAdapter.swapCursor(data);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            mElementAdapter.swapCursor(null);
        }

        private DialogInterface.OnClickListener positiveAnswerForDeletion(final Element element) {

            return new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (element != null) {
                        element.delete(getActivity());
                    }
                }
            };
        }

        @Override
        public void onClick(Element element) {
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse(String.format("http://maps.google.com/maps?daddr=%s,%s", element.getLatitude(), element.getLongitude())));
            getActivity().startActivity(intent);
        }

        // Private Methods
        private long[] arrayOfPositions() {
            Cursor cursor = mElementAdapter.getCursor();
            long[] positions = new long[cursor.getCount()];
            for (int idx = 0; idx < cursor.getCount(); idx++) {
                positions[idx] = idx;
            }
            return positions;
        }
    }
}
