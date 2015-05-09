package io.bananalabs.dailyrandom;

import android.test.AndroidTestCase;

/**
 * Created by EDC on 5/8/15.
 */
public class TestUtilities extends AndroidTestCase {

    public void testRandom() {
        long[] values = {1,2,3,4,5,6};
        long firstRandom = Utilities.selectRrandomlyFrom(values);
        long secondRandom = Utilities.selectRrandomlyFrom(values);
        assertFalse(firstRandom != secondRandom);
    }
}