/*
  Created by Kerry-Anne McLaughlin
  kmclau208@caledonian.ac.uk, s1802675
 */
package com.kam.earthquakeuk_kam.fragments;

import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.kam.earthquakeuk_kam.helpers.ColorHelper;
import com.kam.earthquakeuk_kam.models.Earthquake;
import com.kam.earthquakeuk_kam.helpers.PrettyDate;
import com.kam.earthquakeuk_kam.R;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class EarthquakeDetailFragment extends Fragment {

    private static final String EARTHQUAKE = "earthquake";

    private Earthquake mEarthquake;

    public EarthquakeDetailFragment() {
    }

    public static EarthquakeDetailFragment newInstance(Earthquake earthquake) {
        EarthquakeDetailFragment fragment = new EarthquakeDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(EARTHQUAKE, earthquake);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mEarthquake = (Earthquake) getArguments().getSerializable(EARTHQUAKE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.earthquake_detail_fragment, container, false);

        if (mEarthquake != null) {

            Resources resources = getResources();

            TextView title = (TextView) view.findViewById(R.id.location_heading);
            title.setText(mEarthquake.getLocation().getName());

            TextView subtitle = (TextView) view.findViewById(R.id.datetime_heading);
            subtitle.setText(PrettyDate.getTimeSince(mEarthquake.getDate()));

            TextView magnitudeHeading = (TextView) view.findViewById(R.id.magnitude_heading);
            magnitudeHeading.setText(String.valueOf(mEarthquake.getMagnitude()));
            Drawable d = magnitudeHeading.getBackground();
            d.setColorFilter(ColorHelper.getColor(mEarthquake.getMagnitude()), PorterDuff.Mode.MULTIPLY);

            SimpleDateFormat sdate = new SimpleDateFormat("dd MMMM yyyy", Locale.UK);
            SimpleDateFormat stime = new SimpleDateFormat("hh:mm a", Locale.UK);
            String dateString = sdate.format(mEarthquake.getDate());
            String timeString = stime.format(mEarthquake.getDate());
            TextView date = (TextView) view.findViewById(R.id.detail_date);
            date.setText(dateString);

            TextView time = (TextView) view.findViewById(R.id.detail_time);
            time.setText(timeString);

            TextView latitude = (TextView) view.findViewById(R.id.detail_lat);
            TextView longitude = (TextView) view.findViewById(R.id.detail_lon);
            latitude.setText(String.valueOf(mEarthquake.getLocation().getLatitude()));
            longitude.setText(String.valueOf(mEarthquake.getLocation().getLongitude()));


            TextView depth = (TextView) view.findViewById(R.id.detail_depth);
            depth.setText(resources.getString(R.string.earthquake_detail_depth, String.valueOf(mEarthquake.getDepth())));

        }

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
