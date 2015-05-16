package io.bananalabs.dailyrandom.others;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import io.bananalabs.dailyrandom.R;
import io.bananalabs.dailyrandom.model.Place;

/**
 * Created by EDC on 5/16/15.
 */
public class PlacesAdapter extends BaseAdapter{

    private SparseBooleanArray mCheckedPlaces;
    private ArrayList<Place> mPlaces;
    private Context mContext;

    public PlacesAdapter(Context context, ArrayList<Place> places) {
        mPlaces = places;
        mContext = context;
    }

    public SparseBooleanArray getCheckedPositions() {
        return mCheckedPlaces;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        mCheckedPlaces = new SparseBooleanArray(mPlaces.size());
    }

    @Override
    public int getCount() {
        return mPlaces.size();
    }

    @Override
    public Object getItem(int position) {
        return mPlaces.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_place, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Place place = mPlaces.get(position);
        viewHolder.nameText.setText(place.getName());
        viewHolder.placeCheck.setTag(position);
        viewHolder.placeCheck.setChecked(mCheckedPlaces.get(position, false));
        viewHolder.placeCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCheckedPlaces.put((Integer) buttonView.getTag(), isChecked);
                Toast.makeText(mContext, "Count" + mCheckedPlaces.size(), Toast.LENGTH_SHORT).show();
            }
        });
        return convertView;
    }

    public static class ViewHolder {
        public final TextView nameText;
        public final CheckBox placeCheck;

        public ViewHolder(View view) {
            this.nameText = (TextView) view.findViewById(R.id.text_place_name);
            this.placeCheck = (CheckBox) view.findViewById(R.id.checkbox_place);
        }
    }
}
