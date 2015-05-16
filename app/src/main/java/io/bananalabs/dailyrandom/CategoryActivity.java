package io.bananalabs.dailyrandom;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import io.bananalabs.dailyrandom.data.DailyRandomContract;
import io.bananalabs.dailyrandom.model.Category;


public class CategoryActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new ActivityFragment())
                    .commit();
        }

//        PLacesService.startAactionAskPlaces(this, 18.4610001, -69.9609892, 20000, "night_club");
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class ActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

        private static final int CATEGORY_LOADER_ID = 0;

        private SimpleCursorAdapter mCategoryAdapter;

        public ActivityFragment() {
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();

            //noinspection SimplifiableIfStatement
            if (id == R.id.action_new_category) {
                getActivity().startActivity(new Intent(getActivity(), NewCategoryActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            inflater.inflate(R.menu.menu_activity, menu);
            super.onCreateOptionsMenu(menu, inflater);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            mCategoryAdapter = new SimpleCursorAdapter(
                    getActivity(),
                    android.R.layout.simple_expandable_list_item_1,
                    null,
                    new String[]{DailyRandomContract.CategoryEntry.COLUMN_TITLE},
                    new int[]{android.R.id.text1},
                    0
            );

            mCategoryAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
                @Override
                public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                    ((TextView) view).setText(cursor.getString(columnIndex));
                    return true;
                }
            });

            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            ListView listView = (ListView) rootView.findViewById(R.id.list_categories);
            listView.setAdapter(mCategoryAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getActivity(), ElementActivity.class);
                    long catKey = id;
                    intent.putExtra(Intent.EXTRA_KEY_EVENT, catKey);
                    getActivity().startActivity(intent);

                }
            });
            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    Category category = Category.readCategory(getActivity(), id);
                    Utilities.showDeleteDialog(getActivity(), positiveAnswerForDeletion(category), null);
                    return true;
                }
            });
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
                    DailyRandomContract.CategoryEntry.COLUMN_TITLE + " COLLATE NOCASE ASC");
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            mCategoryAdapter.swapCursor(data);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            mCategoryAdapter.swapCursor(null);
        }

        private DialogInterface.OnClickListener positiveAnswerForDeletion(final Category category) {

            return new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (category != null) {
                        category.delete(getActivity());
                    }
                }
            };
        }

    }
}
