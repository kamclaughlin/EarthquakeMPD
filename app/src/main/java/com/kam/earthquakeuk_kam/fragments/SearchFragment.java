/*
  Created by Kerry-Anne McLaughlin
  kmclau208@caledonian.ac.uk, s1802675
 */
package com.kam.earthquakeuk_kam.fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.kam.earthquakeuk_kam.DateDialogueCustomListener;
import com.kam.earthquakeuk_kam.db.EqDb;
import com.kam.earthquakeuk_kam.models.Earthquake;
import com.kam.earthquakeuk_kam.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SearchFragment extends Fragment implements View.OnFocusChangeListener {

    private static final String TAG = "SearchFragment";

    private EditText mDatePickerFrom, mDatePickerTo;


    private OnSearchFragmentInteractionListener mCallback;

    public SearchFragment() {
    }


    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDatePickerFrom = (EditText) view.findViewById(R.id.datePickerFrom);
        mDatePickerTo = (EditText) view.findViewById(R.id.datePickerTo);
        Button mSubmitButton = (Button) view.findViewById(R.id.btnSearch);
        Button mClearButton = (Button) view.findViewById(R.id.btnClear);


        mDatePickerFrom.setOnFocusChangeListener(this);
        mDatePickerTo.setOnFocusChangeListener(this);
        mClearButton.setOnClickListener(v -> clearForm());
        mSubmitButton.setOnClickListener(v -> {

            String startDate = mDatePickerFrom.getText().toString();
            String endDate = mDatePickerTo.getText().toString();

            Date sDate = null;
            Date eDate = null;
            Integer mag = null;
            Integer dep = null;
            String loc = null;
            Integer sort = null;
            String sortBy = null;

            List<Earthquake> eqs;
            ArrayList<String> errors = new ArrayList<>();

            String pattern = "dd/MM/yyyy";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

            try {
                if (!startDate.equals("")) {
                    sDate = simpleDateFormat.parse(startDate);
                }

                if (!endDate.equals("")) {
                    eDate = simpleDateFormat.parse(endDate);
                }

            } catch (ParseException e) {
                errors.add("The dates you entered are invalid");

            } catch (NumberFormatException e) {
                errors.add("Please depth is a number");
            }

            if (errors.size() != 0) {
                Toast.makeText(getContext(), "Uh oh...errors", Toast.LENGTH_SHORT).show();
            } else {

                eqs = EqDb.mEqDao.searchEarthquake(sDate, eDate, mag, dep, loc, sort, sortBy);
                if (eqs.size() > 0) {
                    mCallback.onSearchResultsReturned(eqs);
                } else {
                    Toast.makeText(getContext(), "Sorry, no Earthquakes found", Toast.LENGTH_LONG).show();
                    mCallback.onSearchResultsReturned(null);
                }

            }

        });


    }

    private void clearForm() {

        mDatePickerFrom.setText("");
        mDatePickerTo.setText("");

        Toast.makeText(getContext(), "Search cleared", Toast.LENGTH_SHORT).show();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search_frament, container, false);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSearchFragmentInteractionListener) {
            mCallback = (OnSearchFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

        if (hasFocus) {

            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, -14);
            int y = calendar.get(Calendar.YEAR);
            int m = calendar.get(Calendar.MONTH);
            int d = calendar.get(Calendar.DAY_OF_MONTH);

            if (v.getId() == R.id.datePickerFrom || v.getId() == R.id.datePickerTo) {

                DatePickerDialog datePicker = new DatePickerDialog(getContext(), R.style.DialogTheme, new DateDialogueCustomListener((EditText) v), y, m, d);

                // SET MINIMUM AND MAXIMUM DATES
                long DAY_IN_MS = 1000 * 60 * 60 * 24;
                datePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePicker.getDatePicker().setMinDate(System.currentTimeMillis() - (100 * DAY_IN_MS));

                // SHOW THE DIALOGUE
                datePicker.show();
            }

        }


    }

    public interface OnSearchFragmentInteractionListener {
        // TODO: Update argument type and name
        void onSearchResultsReturned(List<Earthquake> earthquakes);
    }
}
