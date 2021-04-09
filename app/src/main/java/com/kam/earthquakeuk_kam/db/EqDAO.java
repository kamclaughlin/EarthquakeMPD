/*
  Created by Kerry-Anne McLaughlin
  kmclau208@caledonian.ac.uk, s1802675
 */
package com.kam.earthquakeuk_kam.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.kam.earthquakeuk_kam.enums.Direction;
import com.kam.earthquakeuk_kam.models.Earthquake;
import com.kam.earthquakeuk_kam.models.Location;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EqDAO extends DbProvider implements IEqSchema, IEqDAO {

    private static final String TAG = "EqDAO";

    public EqDAO(SQLiteDatabase db) {
        super(db);
    }

    @Override
    protected Earthquake cursorToEntity(Cursor cursor) {

        Earthquake earthquake = new Earthquake();
        int id, location, magnitude, depth, lat, lon, link, datetime;

        if (cursor != null) {

            if (cursor.getColumnIndex(COLUMN_NAME_ID) != -1) {
                id = cursor.getColumnIndexOrThrow(COLUMN_NAME_ID);
                earthquake.setId(cursor.getString(id));
            }

            if (cursor.getColumnIndex(COLUMN_NAME_MAGNITUDE) != -1) {
                magnitude = cursor.getColumnIndexOrThrow(COLUMN_NAME_MAGNITUDE);
                earthquake.setMagnitude(cursor.getDouble(magnitude));
            }

            if (cursor.getColumnIndex(COLUMN_NAME_DEPTH) != -1) {
                depth = cursor.getColumnIndexOrThrow(COLUMN_NAME_DEPTH);
                earthquake.setDepth(cursor.getInt(depth));
            }

            if (cursor.getColumnIndex(COLUMN_NAME_LINK) != -1) {
                link = cursor.getColumnIndexOrThrow(COLUMN_NAME_LINK);
                earthquake.setLink(cursor.getString(link));
            }

            if (cursor.getColumnIndex(COLUMN_NAME_DATETIME) != -1) {
                datetime = cursor.getColumnIndexOrThrow(COLUMN_NAME_DATETIME);
                earthquake.setDate(new Date(cursor.getLong(datetime)));
            }
            Location l = new Location();

            if (cursor.getColumnIndex(COLUMN_NAME_LOCATION) != -1) {
                location = cursor.getColumnIndexOrThrow(
                        COLUMN_NAME_LOCATION);
                l.setName(cursor.getString(location));
            }

            if (cursor.getColumnIndex(COLUMN_NAME_LAT) != -1) {
                lat = cursor.getColumnIndexOrThrow(
                        COLUMN_NAME_LAT);
                l.setLatitude(cursor.getDouble(lat));
            }

            if (cursor.getColumnIndex(COLUMN_NAME_LONG) != -1) {
                lon = cursor.getColumnIndexOrThrow(
                        COLUMN_NAME_LONG);
                l.setLongitude(cursor.getDouble(lon));
            }
            earthquake.setLocation(l);

        }
        return earthquake;
    }

    public Earthquake getStrongestEarthquake() {
        return runSingleRawQuery("SELECT * FROM " + IEqSchema.TABLE_NAME + " ORDER BY " + COLUMN_NAME_MAGNITUDE + " DESC LIMIT 0,1");
    }

    public Earthquake getDeepestEarthquake() {
        return runSingleRawQuery("SELECT * FROM " + IEqSchema.TABLE_NAME + " ORDER BY " + COLUMN_NAME_DEPTH + " DESC LIMIT 0,1");
    }

    public Earthquake getShallowestEarthquake() {
        return runSingleRawQuery("SELECT * FROM " + IEqSchema.TABLE_NAME + " ORDER BY " + COLUMN_NAME_DEPTH + " ASC LIMIT 0,1");
    }

    public Earthquake getFurtherstCardinalEarthquake(Direction d) {

        String field = "";
        String sortDirection = "";

        switch (d) {
            case NORTH:
                field = COLUMN_NAME_LAT;
                sortDirection = "DESC";
                break;
            case SOUTH:
                field = COLUMN_NAME_LAT;
                sortDirection = "ASC";
                break;
            case EAST:
                field = COLUMN_NAME_LONG;
                sortDirection = "DESC";
                break;
            case WEST:
                field = COLUMN_NAME_LONG;
                sortDirection = "ASC";
                break;
        }

        return runSingleRawQuery("SELECT * FROM " + IEqSchema.TABLE_NAME + " ORDER BY " + field + " " + sortDirection + " LIMIT 0,1");
    }

    public List<Earthquake> searchEarthquake(Date pStartDate, Date pEndDate, Integer pMagnitude, Integer pDepth, String pLocation, Integer pSort, String pSortBy) {

        String query = "SELECT * FROM " + TABLE_NAME;
        ArrayList<String> conditions = new ArrayList<>();

        if (pStartDate != null) {
            conditions.add(String.format("%s >= %s", IEqSchema.COLUMN_NAME_DATETIME, pStartDate.getTime()));
        }

        if (pEndDate != null) {
            conditions.add(String.format("%s <= %s", IEqSchema.COLUMN_NAME_DATETIME, pEndDate.getTime()));
        }

        if (pMagnitude != null) {
            conditions.add(String.format("%s <= %s", IEqSchema.COLUMN_NAME_MAGNITUDE, pMagnitude));
        }

        if (pDepth != null) {
            conditions.add(String.format("%s >= %s", IEqSchema.COLUMN_NAME_DEPTH, pDepth));
        }

        if (pLocation != null && !pLocation.equals("")) {
            conditions.add(String.format("%s LIKE '%%%s%%'", IEqSchema.COLUMN_NAME_LOCATION, pLocation));
        }


        if (conditions.size() > 0) {
            query += " WHERE " + TextUtils.join(" AND ", conditions);
        }

        String sortOrder = " ORDER BY ";
        Log.d(TAG, "searchEarthquake: PSORT IN" + pSort);
        if (pSort != null) {

            switch (pSort) {
                case 0:
                    sortOrder += IEqSchema.COLUMN_NAME_DATETIME;
                    break;
                case 1:
                    sortOrder += IEqSchema.COLUMN_NAME_DEPTH;
                    break;
                case 2:
                    sortOrder += IEqSchema.COLUMN_NAME_LOCATION;
                    break;
                case 3:
                    sortOrder += IEqSchema.COLUMN_NAME_MAGNITUDE;
                    break;
            }
        }

        query += sortOrder + " " + pSortBy + ";";

        List<Earthquake> earthquakes = new ArrayList<>();

        Log.d(TAG, query);

        Cursor cursor = super.rawQuery(query, null);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                earthquakes.add(cursorToEntity(cursor));
                cursor.moveToNext();
            }
            cursor.close();
        }

        return earthquakes;

    }

    private Earthquake runSingleRawQuery(String query) {

        Earthquake e = null;

        Cursor cursor = super.rawQuery(query, null);
        if (cursor != null) {
            cursor.moveToFirst();
            e = cursorToEntity(cursor);
            cursor.close();
        }

        return e;
    }

    @Override
    public List<Earthquake> fetchAllEarthquakes() {
        List<Earthquake> earthquakes = new ArrayList<Earthquake>();
        Cursor cursor = super.query(TABLE_NAME, EARTHQUAKE_COLUMNS, null,
                null, COLUMN_NAME_ID + " DESC");

        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Earthquake earthquake = cursorToEntity(cursor);
                earthquakes.add(earthquake);
                cursor.moveToNext();
            }
            cursor.close();
        }

        return earthquakes;
    }

    @Override
    public boolean addEarthquake(Earthquake earthquake) {

        try {
            return super.insert(TABLE_NAME, getContentValues(earthquake)) > 0;
        } catch (SQLiteConstraintException ex) {
            return false;
        }
    }

    @Override
    public boolean addEarthquakes(List<Earthquake> earthquakes) {

        boolean result = true;
        for (Earthquake e : earthquakes) {

            if (!addEarthquake(e)) {
                result = false;
            }

        }
        return result;
    }

    private ContentValues getContentValues(Earthquake e) {

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME_ID, e.getId());
        cv.put(COLUMN_NAME_LOCATION, e.getLocation().getName());
        cv.put(COLUMN_NAME_MAGNITUDE, e.getMagnitude());
        cv.put(COLUMN_NAME_DEPTH, e.getDepth());
        cv.put(COLUMN_NAME_LAT, e.getLocation().getLatitude());
        cv.put(COLUMN_NAME_LONG, e.getLocation().getLongitude());
        cv.put(COLUMN_NAME_LINK, e.getLink());
        cv.put(COLUMN_NAME_DATETIME, e.getDate().getTime());

        return cv;

    }

}
