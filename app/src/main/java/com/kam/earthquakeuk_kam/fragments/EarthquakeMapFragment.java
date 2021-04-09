/*
  Created by Kerry-Anne McLaughlin
  kmclau208@caledonian.ac.uk, s1802675
 */
package com.kam.earthquakeuk_kam.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.MarkerManager;
import com.google.maps.android.clustering.ClusterManager;
import com.kam.earthquakeuk_kam.MapClusterRenderer;
import com.kam.earthquakeuk_kam.R;
import com.kam.earthquakeuk_kam.activity.EarthquakeDetailActivity;
import com.kam.earthquakeuk_kam.helpers.PrettyDate;
import com.kam.earthquakeuk_kam.models.Earthquake;

import java.util.ArrayList;
import java.util.HashMap;

import static com.kam.earthquakeuk_kam.activity.BaseActivity.EARTHQUAKE_TRANSFER;

public class EarthquakeMapFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = "EarthquakeMapFragment";
    private static final String EARTHQUAKE = "earthquake";
    private static final String EARTHQUAKE_LIST = "earthquake_list";

    private Earthquake mEarthquake;
    private ArrayList<Earthquake> mEarthquakeList;
    private final HashMap<Marker, Integer> mHashMap = new HashMap<Marker, Integer>();

    private GoogleMap mMap;
    private ClusterManager<Earthquake> mEarthquakeClusterManager;

    private boolean multipleMarkers = false;


    public EarthquakeMapFragment() {
    }

    public static EarthquakeMapFragment newInstance(Earthquake earthquake) {

        Log.d(TAG, "newInstance: THIS FIRES");

        EarthquakeMapFragment fragment = new EarthquakeMapFragment();
        Bundle args = new Bundle();
        args.putSerializable(EARTHQUAKE, earthquake);
        fragment.setArguments(args);
        return fragment;
    }

    public static EarthquakeMapFragment newInstance(ArrayList<Earthquake> earthquake_list) {

        EarthquakeMapFragment fragment = new EarthquakeMapFragment();
        Bundle args = new Bundle();
        args.putSerializable(EARTHQUAKE_LIST, earthquake_list);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {

            if (getArguments().getSerializable(EARTHQUAKE) != null) {

                mEarthquake = (Earthquake) getArguments().getSerializable(EARTHQUAKE);

            } else if (getArguments().getSerializable(EARTHQUAKE_LIST) != null) {

                mEarthquakeList = (ArrayList<Earthquake>) getArguments().getSerializable(EARTHQUAKE_LIST);
                multipleMarkers = true;

            }

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: THIS FIRES ALSO");
        View view = inflater.inflate(R.layout.earthquake_map_fragment, container, false);

        SupportMapFragment mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        if (mMapFragment != null) {
            Log.d(TAG, "onCreateView: IS THE MAP NOT NULL?");

            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            mMapFragment = SupportMapFragment.newInstance();
            fragmentTransaction.replace(R.id.map, mMapFragment).commit();
        }

        try {
            mMapFragment.getMapAsync(this);
        } catch (NullPointerException e) {
            Log.e(TAG, "onCreateView: uh oh...mMapFragment is null?");
        }

        return view;
    }

    private void plotEarthquakes(GoogleMap mMap, ArrayList<Earthquake> earthquakes) {

        ArrayList<Marker> markers = new ArrayList<>();
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        if (mEarthquakeList != null && mEarthquakeList.size() > 0) {

            int i = 0;
            for (Earthquake e : mEarthquakeList) {
                builder.include(new LatLng(e.getLocation().getLatitude(), e.getLocation().getLongitude()));
                i++;
            }
            mEarthquakeClusterManager.addItems(earthquakes);

        }

        LatLngBounds bounds = builder.build();
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 300));

    }

    private void plotEarthquake(GoogleMap mMap, Earthquake earthquake) {
        LatLng location = new LatLng(earthquake.getLocation().getLatitude(), earthquake.getLocation().getLongitude());
        mMap.addMarker(createMarker(earthquake));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 8f));
    }

    private MarkerOptions createMarker(Earthquake e) {
        return new MarkerOptions()
                .position(new LatLng(e.getLocation().getLatitude(), e.getLocation().getLongitude()))
                .title(e.getLocation().getName())
                .snippet(PrettyDate.getTimeSince(e.getDate()))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker));
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mEarthquakeList != null && mEarthquakeList.size() > 0) {
            outState.putSerializable(EARTHQUAKE_LIST, mEarthquakeList);
        }

        if (mEarthquake != null) {
            outState.putSerializable(EARTHQUAKE, mEarthquake);
        }

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        setUpClusterer();

        if (!multipleMarkers) {
            plotEarthquake(mMap, mEarthquake);
        } else {
            plotEarthquakes(mMap, mEarthquakeList);
        }

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {

                Log.d(TAG, "onInfoWindowClick: " + marker.toString());

                if (mHashMap.get(marker) != null) {
                    int pos = mHashMap.get(marker);

                    Intent i = new Intent(getContext(), EarthquakeDetailActivity.class);
                    i.putExtra(EARTHQUAKE_TRANSFER, mEarthquakeList.get(pos));
                    startActivity(i);

                }


            }
        });

        mMap.setOnMarkerClickListener(marker -> {

            Log.d(TAG, "onMarkerClick: ");


            if (mHashMap.get(marker) != null) {
                int pos = mHashMap.get(marker);
                Log.d(TAG, "onMarkerClick: " + mEarthquakeList.get(pos).toString());
            }
            float markerZoom = 8.0f;

            if (mMap.getCameraPosition().zoom < 8.0f) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 8f));
            } else {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), mMap.getCameraPosition().zoom));
            }
            marker.showInfoWindow();


            return true;
        });

    }


    private void setUpClusterer() {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(51.503186, -0.126446), 10));

        MarkerManager t = new MarkerManager(mMap);

        t.newCollection();
        mEarthquakeClusterManager = new ClusterManager<>(getContext(), mMap, t);
        mEarthquakeClusterManager.setRenderer(new MapClusterRenderer(getContext(), mMap, mEarthquakeClusterManager));

        mMap.setOnCameraIdleListener(mEarthquakeClusterManager);
        mMap.setOnMarkerClickListener(mEarthquakeClusterManager);

        mEarthquakeClusterManager.cluster();

    }
}