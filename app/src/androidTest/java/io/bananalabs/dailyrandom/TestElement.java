package io.bananalabs.dailyrandom;

import android.database.Cursor;
import android.test.AndroidTestCase;

import io.bananalabs.dailyrandom.data.DailyRandomContract;
import io.bananalabs.dailyrandom.model.Category;
import io.bananalabs.dailyrandom.model.Element;

/**
 * Created by EDC on 4/27/15.
 */
public class TestElement extends AndroidTestCase {

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

    long categoryId;
    private void insertCategory() {
        Category category = new Category("Fast Food", 9);
        categoryId = category.save(mContext);
    }

    long elementId;
    private long insertElement(long categoryId, String title) {
        Element element = new Element(0, categoryId, title, 0, 0, 0);
        elementId = element.save(mContext, categoryId);
        assertTrue(elementId != -1);
        return elementId;
    }

    public void testInsertElement() {
        insertCategory();
        insertElement(categoryId, "Mustard");
        insertElement(categoryId, "Chilis");
        insertElement(categoryId, "Pizza Hut");
        insertElement(categoryId, "Pica pollo chino");
    }

    public void testReadElements() {
        insertCategory();
        insertElement(categoryId, "Mustard");
        insertElement(categoryId, "Chilis");
        insertElement(categoryId, "Pizza Hut");
        long id = insertElement(categoryId, "Pica pollo chino");

        Element element = Element.readElement(mContext, id);
        assertEquals(element.getTitle(), "Pica pollo chino");
    }
}
