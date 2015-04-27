package io.bananalabs.dailyrandom;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import io.bananalabs.dailyrandom.data.DailyRandomContract;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new MainFragment())
                    .commit();
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

        private static final int CATEGORY_LOADER_ID = 1;

        public MainFragment() {
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();

            //noinspection SimplifiableIfStatement
            if (id == R.id.action_settings) {
                getActivity().startActivity(new Intent(getActivity(), NewCategoryActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            inflater.inflate(R.menu.menu_main, menu);
            super.onCreateOptionsMenu(menu, inflater);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Setting Option Menu
            setHasOptionsMenu(true);
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);// Setting Loader Manager

            // Setting Loader Manager
            getLoaderManager().initLoader(CATEGORY_LOADER_ID, null, this);

        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(
                    getActivity(),
                    DailyRandomContract.CategoryEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    DailyRandomContract.CategoryEntry.COLUMN_TITLE + " ASC");
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    }
}
