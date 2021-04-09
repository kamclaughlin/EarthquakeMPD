/*
  Created by Kerry-Anne McLaughlin
  kmclau208@caledonian.ac.uk, s1802675
 */
package com.kam.earthquakeuk_kam;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.kam.earthquakeuk_kam.fragments.EarthquakeListFragment;
import com.kam.earthquakeuk_kam.helpers.ColorHelper;
import com.kam.earthquakeuk_kam.helpers.PrettyDate;
import com.kam.earthquakeuk_kam.models.Earthquake;

import java.util.List;

public class EarthquakeListRecyclerViewAdapter extends RecyclerView.Adapter<EarthquakeListRecyclerViewAdapter.EarthquakeViewHolder> {

    private final List<Earthquake> mEarthquakes;
    private final EarthquakeListFragment.OnListFragmentInteractionListener mCallback;

    public EarthquakeListRecyclerViewAdapter(List<Earthquake> items, EarthquakeListFragment.OnListFragmentInteractionListener callback) {
        mEarthquakes = items;
        mCallback = callback;
    }

    @Override
    public EarthquakeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_earthquake_list_row, parent, false);
        return new EarthquakeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final EarthquakeViewHolder holder, int position) {
        holder.mItem = mEarthquakes.get(position);

        Earthquake currentEarthquake = mEarthquakes.get(position);
        holder.eLocation.setText(currentEarthquake.getLocation().getName());
        holder.eDate.setText(PrettyDate.getTimeSince(currentEarthquake.getDate()));
        holder.eMagnitude.setText(String.valueOf(currentEarthquake.getMagnitude()));

        Drawable d = holder.eMagnitude.getBackground();
        d.setColorFilter(ColorHelper.getColor(currentEarthquake.getMagnitude()), PorterDuff.Mode.MULTIPLY);


        holder.mView.setOnClickListener(v -> {
            if (null != mCallback) {
                mCallback.onListEarthquakeListItemClick(holder.mItem);
            }
        });
    }

    @Override
    public int getItemCount() {

        return ((mEarthquakes != null) && (mEarthquakes.size() != 0) ? mEarthquakes.size() : 0);
    }

    public static class EarthquakeViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public Earthquake mItem;

        final TextView eDate;
        final TextView eLocation;
        final TextView eMagnitude;

        public EarthquakeViewHolder(View view) {
            super(view);
            mView = view;
            this.eDate = view.findViewById(R.id.date_text_view);
            this.eLocation = view.findViewById(R.id.place_text_view);
            this.eMagnitude = view.findViewById(R.id.mag_text_view);

        }
    }
}
