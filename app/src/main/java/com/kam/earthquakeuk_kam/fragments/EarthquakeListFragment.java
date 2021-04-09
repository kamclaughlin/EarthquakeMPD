/*
  Created by Kerry-Anne McLaughlin
  kmclau208@caledonian.ac.uk, s1802675
 */
package com.kam.earthquakeuk_kam.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kam.earthquakeuk_kam.EarthquakeListRecyclerViewAdapter;
import com.kam.earthquakeuk_kam.R;
import com.kam.earthquakeuk_kam.models.Earthquake;

import java.util.ArrayList;


public class EarthquakeListFragment extends Fragment {
    private static final String EARTHQUAKES = "earthquakes";
    private ArrayList<Earthquake> earthquakes;
    private OnListFragmentInteractionListener listener;

    public EarthquakeListFragment() {
    }

    public static EarthquakeListFragment newInstance(ArrayList<Earthquake> earthquakes) {

        EarthquakeListFragment fragment = new EarthquakeListFragment();
        Bundle args = new Bundle();
        args.putSerializable(EARTHQUAKES, earthquakes);
        fragment.setArguments(args);
        return fragment;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            earthquakes = (ArrayList<Earthquake>) getArguments().getSerializable(EARTHQUAKES);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_earthquake_list, container, false);
        RecyclerView recyclerView = (RecyclerView) view;
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(new EarthquakeListRecyclerViewAdapter(earthquakes, listener));

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            listener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface OnListFragmentInteractionListener {
        void onListEarthquakeListItemClick(Earthquake item);
    }
}
