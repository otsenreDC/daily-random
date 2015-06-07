package io.bananalabs.dailyrandom.others;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.bananalabs.dailyrandom.R;
import io.bananalabs.dailyrandom.model.Element;

/**
 * Created by EDC on 5/17/15.
 */
public class ElementCursorAdapter extends CursorAdapter {

    private static final int VIEW_TYPE_COUNT = 2;
    private static final int VIEW_TYPE_SELECTED = 0;
    private static final int VIEW_TYPE_DEFAULT = 1;

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

        int viewType = getItemViewType(cursor.getPosition());
        int layoutId = -1;
        switch (viewType) {
            case VIEW_TYPE_SELECTED: {
                layoutId = R.layout.list_item_element_selected;
                break;
            }
            case VIEW_TYPE_DEFAULT: {
                layoutId = R.layout.list_item_element;
                break;
            }
        }

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
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

    @Override
    public int getItemViewType(int position) {
        return (position == 0) ? VIEW_TYPE_SELECTED : VIEW_TYPE_DEFAULT;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    public class ViewHolder {
        public final TextView title;
        public final TextView date;
        public final TextView counter;
        public final CheckBox exclude;
        public final ImageButton takeMeThere;

        public ViewHolder(View view) {
            title = (TextView) view.findViewById(R.id.text_title);
            date = (TextView) view.findViewById(R.id.text_date);
            counter = (TextView) view.findViewById(R.id.text_counter);
            exclude = (CheckBox) view.findViewById(R.id.checkbox_exclude);
            takeMeThere = (ImageButton) view.findViewById(R.id.button_take_me_there);
        }
    }

    private String formatStringDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy");
        String dateFormatted = sdf.format(date);

        return dateFormatted;

    }
}
