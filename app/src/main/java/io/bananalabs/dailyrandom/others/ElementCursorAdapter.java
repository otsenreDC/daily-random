package io.bananalabs.dailyrandom.others;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.bananalabs.dailyrandom.R;
import io.bananalabs.dailyrandom.data.DailyRandomContract;
import io.bananalabs.dailyrandom.model.Element;

/**
 * Created by EDC on 5/17/15.
 */
public class ElementCursorAdapter extends CursorAdapter {

    private OnMapButtonClickListener mapButtonClickListener;

    public ElementCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    public interface OnMapButtonClickListener {
        public void onClick(Element element);
    }

    public void setMapButtonClickListener(OnMapButtonClickListener mapButtonClickListener) {
        this.mapButtonClickListener = mapButtonClickListener;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View view = LayoutInflater.from(context).inflate(R.layout.list_item_element, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context,final Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.title.setText(cursor.getString(cursor.getColumnIndex(DailyRandomContract.ElementEntry.COLUMN_TITLE)));
        viewHolder.date.setText(formatStringDate(cursor.getString(cursor.getColumnIndex(DailyRandomContract.ElementEntry.COLUMN_DATETEXT))));
        viewHolder.counter.setText(cursor.getString(cursor.getColumnIndex(DailyRandomContract.ElementEntry.COLUMN_COUNTER)));
        final int position = cursor.getPosition();
        viewHolder.takeMeThere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mapButtonClickListener != null) {
                    if (cursor.moveToPosition(position)) {
                        Element element = new Element(cursor);
                        mapButtonClickListener.onClick(element);
                    }
                }
            }
        });

    }

    public class ViewHolder {
        public final TextView title;
        public final TextView date;
        public final TextView counter;
        public final CheckBox exclude;
        public final Button takeMeThere;

        public ViewHolder(View view) {
            title = (TextView) view.findViewById(R.id.text_title);
            date = (TextView) view.findViewById(R.id.text_date);
            counter = (TextView) view.findViewById(R.id.text_counter);
            exclude = (CheckBox) view.findViewById(R.id.checkbox_exclude);
            takeMeThere = (Button) view.findViewById(R.id.button_take_me_there);
        }
    }

    private String formatStringDate(String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat(DailyRandomContract.DATE_FORMAT);
        String dateFormatted = dateString;
        Date date;
        try {
            date = sdf.parse(dateString);
//            sdf.applyPattern("MMMM/dd/yyyy");
            sdf.applyPattern("EEEE, dd MMMM yyyy");
            dateFormatted = sdf.format(date);
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
        return dateFormatted;

    }
}
