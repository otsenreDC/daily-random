package io.bananalabs.dailyrandom;

import android.database.Cursor;
import android.test.AndroidTestCase;

import java.util.ArrayList;

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
    private long insertCategory(String title) {
        Category category = new Category(title, 9);
        categoryId = category.save(mContext);
        return categoryId;
    }

    long elementId;
    private long insertElement(long categoryId, String title) {
        Element element = new Element(0, categoryId, title, 0, 0, 0);
        elementId = element.save(mContext, categoryId);
        assertTrue(elementId != -1);
        return elementId;
    }

    public void testInsertElement() {
        insertCategory("Fast Food");
        insertElement(categoryId, "Mustard");
        insertElement(categoryId, "Chilis");
        insertElement(categoryId, "Pizza Hut");
        insertElement(categoryId, "Pica pollo chino");
    }

    public void testReadElement() {
        insertCategory("Fast Food");
        insertElement(categoryId, "Mustard");
        insertElement(categoryId, "Chilis");
        insertElement(categoryId, "Pizza Hut");
        long id = insertElement(categoryId, "Pica pollo chino");

        Element element = Element.readElement(mContext, id);
        assertEquals(element.getTitle(), "Pica pollo chino");
    }

    public void testReadElements() {
        long categoryID = insertCategory("Fast Food");
        insertElement(categoryID, "Mustard");
        insertElement(categoryID, "Chilis");
        insertElement(categoryID, "Chilis");
        insertElement(categoryID, "Pizza Hut");
        insertElement(categoryID, "Pica pollo chino");

        insertCategory("Parks");
        insertElement(categoryId, "Mustard");
        insertElement(categoryId, "Chilis");
        insertElement(categoryId, "Pizza Hut");
        insertElement(categoryId, "Pica pollo chino");

        ArrayList<Element> elements0 = Element.readElementWithCategoryId(mContext, categoryId);
        ArrayList<Element> elements1 = Element.readElementWithCategoryId(mContext, 0);
        assertTrue(elements0.size() == 4);
        assertTrue(elements1.size() == 0);

    }
}
