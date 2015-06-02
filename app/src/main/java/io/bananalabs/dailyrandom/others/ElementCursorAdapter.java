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

import java.text.SimpleDateFormat;
import java.util.Date;

import io.bananalabs.dailyrandom.R;
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
    public void bindView(View view, Context context, final Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();
        Element element = new Element(cursor);
        viewHolder.title.setText(element.getTitle());
        viewHolder.date.setText(formatStringDate(element.getDate()));
        viewHolder.counter.setText("" + element.getCounter());
        final int position = cursor.getPosition();
        if (element.hasLocation()) {
            viewHolder.takeMeThere.setVisibility(View.VISIBLE);
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
        else
            viewHolder.takeMeThere.setVisibility(View.GONE);

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

    private String formatStringDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy");
        String dateFormatted = sdf.format(date);

        return dateFormatted;

    }
}
