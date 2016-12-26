package com.memorycard.android.memorycardapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by yina on 2016/12/17.
 */

public class MemoryCardDataBaseHelper extends SQLiteOpenHelper {

    private static final int VERSION = 2;
    private static final String DATABASE_NAME = "memorycardDB.db";
    private static final String TAG = "MemoryCardDBHelper";
    private static SQLiteDatabase myDB;
    private static Context mContext;

    public static SQLiteDatabase getDataBase(Context context) {

        if (myDB == null || !myDB.isOpen()) {
            myDB = new MemoryCardDataBaseHelper(context).getWritableDatabase();
        }
        return myDB;

    }




    public MemoryCardDataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
        mContext = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
