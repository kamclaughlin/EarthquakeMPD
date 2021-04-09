/*
  Created by Kerry-Anne McLaughlin
  kmclau208@caledonian.ac.uk, s1802675
 */
package com.kam.earthquakeuk_kam.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public abstract class DbProvider {

    private final SQLiteDatabase mDb;

    public DbProvider(SQLiteDatabase db) {
        this.mDb = db;
    }

    public long insert(String tableName, ContentValues values) {
        return mDb.insert(tableName, null, values);
    }

    public Cursor query(String tableName, String[] columns, String selection, String[] selectionArgs, String sortOrder) {

        return mDb.query(tableName, columns, selection, selectionArgs, null, null, sortOrder);
    }

    public Cursor rawQuery(String sql, String[] selectionArgs) {
        return mDb.rawQuery(sql, selectionArgs);
    }

    protected abstract <T> T cursorToEntity(Cursor cursor);

}