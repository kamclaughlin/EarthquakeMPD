/*
  Created by Kerry-Anne McLaughlin
  kmclau208@caledonian.ac.uk, s1802675
 */
package com.kam.earthquakeuk_kam.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Spinner;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.kam.earthquakeuk_kam.DownloadData;
import com.kam.earthquakeuk_kam.db.EqDb;
import com.kam.earthquakeuk_kam.enums.DownloadStatus;
import com.kam.earthquakeuk_kam.ParseEarthquakes;
import com.kam.earthquakeuk_kam.R;
import com.kam.earthquakeuk_kam.enums.Direction;
import com.kam.earthquakeuk_kam.fragments.EarthquakeListFragment;
import com.kam.earthquakeuk_kam.fragments.EarthquakeMapFragment;
import com.kam.earthquakeuk_kam.fragments.SearchFragment;
import com.kam.earthquakeuk_kam.models.Earthquake;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends BaseActivity implements DownloadData.OnDownloadComplete, EarthquakeListFragment.OnListFragmentInteractionListener, SearchFragment.OnSearchFragmentInteractionListener {

    private Earthquake e;
    private Intent i;

    private static final String TAG = "MainActivity";
    private static final String ACTIVE_FRAGMENT = "active_fragment";

    private Spinner spinnerFilters;
    private final FragmentManager mFragmentManager = getSupportFragmentManager();
    private EqDb mdb;
    private List<Earthquake> earthquakes;

    private BottomNavigationView mBottomNavigationView;

    private Fragment listFragment;
    private Fragment mapFragment;
    private Fragment searchFragment;
    private Fragment mFragment;

    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {

        updateFragment(item.getItemId());
        return true;
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mBottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        mBottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        mdb = new EqDb(this);
        mdb.open();
        earthquakes = EqDb.mEqDao.fetchAllEarthquakes();

        initialiseFragments();

        if (mFragment == null) {
            mFragment = listFragment;
            mFragmentManager.beginTransaction().replace(R.id.fragment_frame, mFragment).commit();
        }
    }

    private void initialiseFragments() {
        listFragment = EarthquakeListFragment.newInstance((ArrayList<Earthquake>) earthquakes);
        mapFragment = EarthquakeMapFragment.newInstance((ArrayList<Earthquake>) earthquakes);
        searchFragment = SearchFragment.newInstance();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Earthquake e;
        Intent i;

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                upDateActionBar(false);
                break;
            case R.id.menu_refresh:
                downloadData();
                break;
            case R.id.largest_mag_eq:
                e = EqDb.mEqDao.getStrongestEarthquake();
                i = new Intent(MainActivity.this, EarthquakeDetailActivity.class);
                i.putExtra(EARTHQUAKE_TRANSFER, e);
                startActivity(i);
                break;
            case R.id.deepest_eq:
                e = EqDb.mEqDao.getDeepestEarthquake();
                i = new Intent(MainActivity.this, EarthquakeDetailActivity.class);
                i.putExtra(EARTHQUAKE_TRANSFER, e);
                startActivity(i);
                break;
            case R.id.shallowest_eq:
                e = EqDb.mEqDao.getShallowestEarthquake();
                i = new Intent(MainActivity.this, EarthquakeDetailActivity.class);
                i.putExtra(EARTHQUAKE_TRANSFER, e);
                startActivity(i);
                break;
            case R.id.north_eq:
                e = EqDb.mEqDao.getFurtherstCardinalEarthquake(Direction.NORTH);
                i = new Intent(MainActivity.this, EarthquakeDetailActivity.class);
                i.putExtra(EARTHQUAKE_TRANSFER, e);
                startActivity(i);
                break;
            case R.id.south_eq:
                e = EqDb.mEqDao.getFurtherstCardinalEarthquake(Direction.SOUTH);
                i = new Intent(MainActivity.this, EarthquakeDetailActivity.class);
                i.putExtra(EARTHQUAKE_TRANSFER, e);
                startActivity(i);
                break;
            case R.id.eastern_eq:
                e = EqDb.mEqDao.getFurtherstCardinalEarthquake(Direction.EAST);
                i = new Intent(MainActivity.this, EarthquakeDetailActivity.class);
                i.putExtra(EARTHQUAKE_TRANSFER, e);
                startActivity(i);
                break;
            case R.id.western_eq:
                e = EqDb.mEqDao.getFurtherstCardinalEarthquake(Direction.WEST);
                i = new Intent(MainActivity.this, EarthquakeDetailActivity.class);
                i.putExtra(EARTHQUAKE_TRANSFER, e);
                startActivity(i);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(ACTIVE_FRAGMENT, mBottomNavigationView.getSelectedItemId());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int selectedItemId = (int) savedInstanceState.get(ACTIVE_FRAGMENT);

        updateFragment(selectedItemId);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (earthquakes == null || earthquakes.size() == 0) {
            downloadData();
            Log.d(TAG, "onResume: Downloading Data CHCKHERE");
        }
    }

    private void downloadData() {

        DownloadData downloadData = new DownloadData(this);
        String urlSource = "https://quakes.bgs.ac.uk/feeds/MhSeismology.xml";
        downloadData.execute(urlSource);

    }

    @Override
    public void onDownloadComplete(String data, DownloadStatus status) {

        if (status == DownloadStatus.OK) {
            ParseEarthquakes parseEarthquakes = new ParseEarthquakes();
            parseEarthquakes.parse(data);
            earthquakes = parseEarthquakes.getEarthquakes();

            EqDb.mEqDao.addEarthquakes(earthquakes);
            earthquakes = EqDb.mEqDao.fetchAllEarthquakes();

            initialiseFragments();

        } else {

            if (earthquakes == null || earthquakes.size() == 0) {

                AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                builder1.setTitle("Problem retreiving data");
                builder1.setMessage("There was an issue downloading the data and you currently have no saved data. Please try again shortly.");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                finish();
                            }
                        });
                AlertDialog alert11 = builder1.create();
                alert11.show();
            }

        }

    }

    @Override
    public void onListEarthquakeListItemClick(Earthquake item) {
        Intent i = new Intent(getApplicationContext(), EarthquakeDetailActivity.class);
        i.putExtra(EARTHQUAKE_TRANSFER, item);
        startActivity(i);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mFragmentManager.popBackStack();
    }

    @Override
    public void onSearchResultsReturned(List<Earthquake> earthquakes) {
        String SEARCH_RESULTS = "search_results";

        if (earthquakes == null) {
            if (mFragmentManager.findFragmentByTag(SEARCH_RESULTS) != null) {

                FragmentTransaction ft = mFragmentManager.beginTransaction();
                ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
                ft.remove(mFragmentManager.findFragmentByTag(SEARCH_RESULTS)).commit();

            }

        } else {
            Fragment searchResults = EarthquakeListFragment.newInstance((ArrayList<Earthquake>) earthquakes);
            mFragment = searchResults;
            FragmentTransaction t = mFragmentManager.beginTransaction();
            t.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_left, R.anim.slide_out_left);

            upDateActionBar(true);
            t.addToBackStack(null);

            if (findViewById(R.id.searchResultsLandscape) == null) {

                t.add(R.id.fragment_frame, mFragment, SEARCH_RESULTS).commit();
            } else {
                t.replace(R.id.searchResultsLandscape, searchResults, SEARCH_RESULTS).commit();
            }

        }


    }

    @SuppressLint("NonConstantResourceId")
    private void updateFragment(int menuId) {

        switch (menuId) {
            case R.id.navigation_map:
                mFragment = mapFragment;
                break;
            case R.id.navigation_search:
                mFragment = searchFragment;
                break;
            default:
                mFragment = listFragment;
                break;
        }
        mFragmentManager.beginTransaction().replace(R.id.fragment_frame, mFragment).commit();

    }

    private void upDateActionBar(boolean show) {

        if (show) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Search Results");
        } else {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
        }

    }

    @Override
    protected void onDestroy() {
        mdb.close();
        super.onDestroy();
    }

}