package io.bananalabs.dailyrandom;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import io.bananalabs.dailyrandom.data.DailyRandomContract;
import io.bananalabs.dailyrandom.model.Element;


public class ElementActivity extends ActionBarActivity {

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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class ElementFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

        private static final int ELEMENT_LOADER = 1;

        private SimpleCursorAdapter mElementAdapter;
        private long categoryId = -1;

        public ElementFragment() {
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            inflater.inflate(R.menu.menu_element, menu);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            int id = item.getItemId();

            //noinspection SimplifiableIfStatement
            if (id == R.id.action_settings) {

                if (categoryId != -1)
                    getActivity().startActivity(new Intent(getActivity(), NewElementActivity.class).putExtra(Intent.EXTRA_KEY_EVENT, categoryId));
                return true;
            }

            return super.onOptionsItemSelected(item);
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setHasOptionsMenu(true);
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            Intent intent = getActivity().getIntent();

            if (intent != null) {
                long catKey = intent.getLongExtra(Intent.EXTRA_KEY_EVENT, -1);
                if (catKey != -1) {
                    categoryId = catKey;
                    ArrayList<Element> elements = Element.readElementWithCategoryId(getActivity(), catKey);
                    Toast.makeText(getActivity(), "Count : " + elements.size(), Toast.LENGTH_SHORT).show();
                }
            }

            getLoaderManager().initLoader(ELEMENT_LOADER, null, this);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            mElementAdapter = new SimpleCursorAdapter(
                    getActivity(),
                    android.R.layout.simple_list_item_1,
                    null,
                    new String[]{DailyRandomContract.ElementEntry.COLUMN_TITLE},
                    new int[]{android.R.id.text1},
                    0
            );

            mElementAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
                @Override
                public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                    ((TextView) view).setText(cursor.getString(columnIndex));
                    return true;
                }
            });

            View rootView = inflater.inflate(R.layout.fragment_element, container, false);
            ListView listView = (ListView)rootView.findViewById(R.id.list_elements);
            listView.setAdapter(mElementAdapter);
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
                    DailyRandomContract.ElementEntry.COLUMN_TITLE + " ASC"
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
    }
}
