package io.bananalabs.dailyrandom;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import java.util.Random;

/**
 * Created by EDC on 4/29/15.
 */
public class Utilities {

    public static void showDeleteDialog(Context context, DialogInterface.OnClickListener positiveListener, DialogInterface.OnClickListener negativeListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.delete_message)
                .setPositiveButton(R.string.yes, positiveListener)
                .setNegativeButton(R.string.no, negativeListener);
        builder.create().show();
    }

    public static long selectRrandomlyFrom(long[] values)
    {
        if (values != null) {
            Random random = new Random();

            int randomSelection = random.nextInt(values.length);
            return values[randomSelection];
        } else {
            return -1;
        }
    }

}
