package io.bananalabs.dailyrandom;

import android.database.Cursor;
import android.test.AndroidTestCase;

import io.bananalabs.dailyrandom.data.DailyRandomContract;
import io.bananalabs.dailyrandom.model.Category;

/**
 * Created by EDC on 4/26/15.
 */
public class TestCategory extends AndroidTestCase {

    public void deleteAllRecords() {
        mContext.getContentResolver().delete(
                DailyRandomContract.CategoryEntry.CONTENT_URI,
                null,
                null
        );

        mContext.getContentResolver().delete(
                DailyRandomContract.ElementEntry.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver().query(
                DailyRandomContract.CategoryEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals(0, cursor.getCount());
        cursor.close();


        cursor = mContext.getContentResolver().query(
                DailyRandomContract.ElementEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals(0, cursor.getCount());
        cursor.close();
    }

    @Override
    protected void setUp() throws Exception {
        deleteAllRecords();
    }

    public void testInsertCategory() {
        Category category = new Category("Cines", 39);
        long mCategoryRowId = category.save(mContext);
        assertTrue(mCategoryRowId != -1);

        category = new Category("Fast Food", 40);
        mCategoryRowId = category.save(mContext);
        assertTrue(mCategoryRowId != -1);
    }

    public void testReadCategory() {
        Category category = new Category("Cines", 39);
        long mCategoryRowId = category.save(mContext);
        assertTrue(mCategoryRowId != -1);

        Category readCategory = Category.readCategory(mContext, mCategoryRowId);
        assertTrue(category != null);
        assertEquals(category.getTitle(), "Cines");
    }
}
