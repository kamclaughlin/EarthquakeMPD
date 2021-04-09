/*
  Created by Kerry-Anne McLaughlin
  kmclau208@caledonian.ac.uk, s1802675
 */
package com.kam.earthquakeuk_kam.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class EqDb {

    private final Context mContext;
    private DatabaseHelper mDatabaseHelper;
    public static EqDAO mEqDao;

    public EqDb(Context context){
        this.mContext = context;
    }

    public void open() {

        mDatabaseHelper = DatabaseHelper.getInstance(mContext);
        SQLiteDatabase mDb = mDatabaseHelper.getWritableDatabase();
        mEqDao = new EqDAO(mDb);

    }

    public void close() {
        mDatabaseHelper.close();
    }

    public static class DatabaseHelper extends SQLiteOpenHelper implements IEqSchema {

        private static final String TAG = "EqDb";
        private static DatabaseHelper instance = null;


        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            Log.d(TAG, "EqDb: CONSTRUCTOR");
        }

        public static DatabaseHelper getInstance(Context context) {

            Log.d(TAG, "getInstance: ");

            if (instance == null) {
                instance = new DatabaseHelper(context);
            }

            return instance;

        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            Log.d(TAG, "onCreate: " + USER_TABLE_CREATE);
            db.execSQL(USER_TABLE_CREATE);

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            Log.d(TAG, "onUpgrade: would upgrade the database");

        }
    }

}
