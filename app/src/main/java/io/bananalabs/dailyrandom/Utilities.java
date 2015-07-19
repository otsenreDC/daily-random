package io.bananalabs.dailyrandom;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import io.bananalabs.dailyrandom.data.DailyRandomContract;

/**
 * Created by EDC on 4/29/15.
 */
public class Utilities {

    private static final String LOG_TAG = Utilities.class.getSimpleName();

    public static void showDeleteDialog(Context context, DialogInterface.OnClickListener positiveListener, DialogInterface.OnClickListener negativeListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.delete_message)
                .setPositiveButton(R.string.yes, positiveListener)
                .setNegativeButton(R.string.no, negativeListener);
        builder.create().show();
    }

    public static long selectRrandomlyFrom(long[] values) {
        if (values != null) {
            Random random = new Random(System.currentTimeMillis());
            int randomSelection = random.nextInt(values.length);
            return values[randomSelection];
        } else {
            return -1;
        }
    }

    public static Date parseDate(String date) {
        if (date != null) {
            SimpleDateFormat sdf = new SimpleDateFormat(DailyRandomContract.DATE_FORMAT);
            try {
                return sdf.parse(date);
            } catch (ParseException pe) {
                Log.e(LOG_TAG, pe.getLocalizedMessage());
            }
        }
        return Calendar.getInstance().getTime();
    }

    public static String formatDate(Date date) {
        if (date != null) {
            SimpleDateFormat sdf = new SimpleDateFormat(DailyRandomContract.DATE_FORMAT);
            return sdf.format(date);
        }
        return "";
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }

    public static void hideSoftKeyboard(Context context, View keyboardHolder) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(keyboardHolder.getWindowToken(), 0);
    }

}
