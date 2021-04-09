/*
  Created by Kerry-Anne McLaughlin
  kmclau208@caledonian.ac.uk, s1802675
 */

package com.kam.earthquakeuk_kam.activity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.kam.earthquakeuk_kam.models.Earthquake;
import com.kam.earthquakeuk_kam.fragments.EarthquakeDetailFragment;
import com.kam.earthquakeuk_kam.R;

public class EarthquakeDetailActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Earthquake e = (Earthquake) getIntent().getSerializableExtra(EARTHQUAKE_TRANSFER);

        if (savedInstanceState == null) {

            FragmentManager fragmentManager = getSupportFragmentManager();

            EarthquakeDetailFragment earthquakeDetailFragment = EarthquakeDetailFragment.newInstance(e);
            FragmentTransaction detailTransaction = fragmentManager.beginTransaction();
            detailTransaction.replace(R.id.info, earthquakeDetailFragment).commit();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
        } else {
            return super.onOptionsItemSelected(item);
        }

        return true;

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
