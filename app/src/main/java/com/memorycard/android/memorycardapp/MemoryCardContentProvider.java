package com.memorycard.android.memorycardapp;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import utilities.DataBaseManager;

public class MemoryCardContentProvider extends ContentProvider {

    static final String PROVIDER_NAME = "com.memorycard.android.memorycardapp";
    static final String URL = "content://" + PROVIDER_NAME;
    public static final Uri BASE_URI = Uri.parse(URL);
    private static final int LIST = 1;
    private static final int LISTS = 2;
    private static final int CARD = 3;
    private static final int CARDS = 4;
    private static final int LIST_MAXID = 5;
    private static final int CARD_MAXID = 6;
    public static final String TAG = "MCContentProvider";




    private static final Map<String, String> mListProjectionMap;
    private static final Map<String, String> mListsProjectionMap;
    private static final Map<String, String> mCardProjectionMap;
    private static final Map<String, String> mCardsProjectionMap;



    private static final UriMatcher uriMatcher;

    public static String tabName;
    private Context context;
    private SQLiteOpenHelper mDBHelper;
    static Uri CONTENT_URI = null;
    public MemoryCardContentProvider() {
    }

    private MemoryCardContentProvider(String tabName) {
        this.tabName = tabName;
    }

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME,DataBaseManager.LIST_TAB,LIST);
        uriMatcher.addURI(PROVIDER_NAME,DataBaseManager.LIST_TAB+"/1",LISTS);
        uriMatcher.addURI(PROVIDER_NAME,DataBaseManager.LIST_TAB+"/2",LIST_MAXID);


        mListsProjectionMap = new HashMap<>();
        mListsProjectionMap.put(DataBaseManager.COL_LIST_ID, DataBaseManager.COL_LIST_ID);
        mListsProjectionMap.put(DataBaseManager.COL_LIST_NAME, DataBaseManager.COL_LIST_NAME);
        mListsProjectionMap.put(DataBaseManager.COL_LIST_TAB_NAME, DataBaseManager.COL_LIST_TAB_NAME);
        mListsProjectionMap.put(DataBaseManager.COL_LIST_CREATE_DATE, DataBaseManager.COL_LIST_CREATE_DATE);
        mListsProjectionMap.put(DataBaseManager.COL_LIST_LAST_MODIF_DATE, DataBaseManager.COL_LIST_LAST_MODIF_DATE);
        mListsProjectionMap.put(DataBaseManager.COL_LIST_ACCURACY, DataBaseManager.COL_LIST_ACCURACY);

        mListProjectionMap = new HashMap<>();
        mListProjectionMap.put(DataBaseManager.COL_LIST_ID, "MAX("+DataBaseManager.COL_LIST_ID+")"+" as id");
        mListProjectionMap.put(DataBaseManager.COL_LIST_NAME, DataBaseManager.COL_LIST_NAME);
        mListProjectionMap.put(DataBaseManager.COL_LIST_TAB_NAME, DataBaseManager.COL_LIST_TAB_NAME);
        mListProjectionMap.put(DataBaseManager.COL_LIST_CREATE_DATE, DataBaseManager.COL_LIST_CREATE_DATE);
        mListProjectionMap.put(DataBaseManager.COL_LIST_LAST_MODIF_DATE, DataBaseManager.COL_LIST_LAST_MODIF_DATE);
        mListProjectionMap.put(DataBaseManager.COL_LIST_ACCURACY, DataBaseManager.COL_LIST_ACCURACY);

        mCardProjectionMap = new HashMap<>();
        mCardProjectionMap.put(DataBaseManager.COL_CARD_ID, "MAX("+DataBaseManager.COL_CARD_ID+")"+" as id");
        mCardProjectionMap.put(DataBaseManager.COL_CARD_GROUP_ID, DataBaseManager.COL_CARD_GROUP_ID);
        mCardProjectionMap.put(DataBaseManager.COL_CARD_TAB_NAME, DataBaseManager.COL_CARD_TAB_NAME);
        mCardProjectionMap.put(DataBaseManager.COL_CARD_DIFFICULTY, DataBaseManager.COL_CARD_DIFFICULTY);
        mCardProjectionMap.put(DataBaseManager.COL_CARD_DAY, DataBaseManager.COL_CARD_DAY);
        mCardProjectionMap.put(DataBaseManager.COL_CARD_TXT_QUESTION, DataBaseManager.COL_CARD_TXT_QUESTION);
        mCardProjectionMap.put(DataBaseManager.COL_CARD_TXT_ANSWER, DataBaseManager.COL_CARD_TXT_ANSWER);
        mCardProjectionMap.put(DataBaseManager.COL_CARD_IMG_QUESTION, DataBaseManager.COL_CARD_IMG_QUESTION);
        mCardProjectionMap.put(DataBaseManager.COL_CARD_IMG_ANSWER, DataBaseManager.COL_CARD_IMG_ANSWER);

        mCardsProjectionMap = new HashMap<>();
        mCardsProjectionMap.put(DataBaseManager.COL_CARD_ID, DataBaseManager.COL_CARD_ID);
        mCardsProjectionMap.put(DataBaseManager.COL_CARD_GROUP_ID, DataBaseManager.COL_CARD_GROUP_ID);
        mCardsProjectionMap.put(DataBaseManager.COL_CARD_TAB_NAME, DataBaseManager.COL_CARD_TAB_NAME);
        mCardsProjectionMap.put(DataBaseManager.COL_CARD_DIFFICULTY, DataBaseManager.COL_CARD_DIFFICULTY);
        mCardsProjectionMap.put(DataBaseManager.COL_CARD_DAY, DataBaseManager.COL_CARD_DAY);
        mCardsProjectionMap.put(DataBaseManager.COL_CARD_TXT_QUESTION, DataBaseManager.COL_CARD_TXT_QUESTION);
        mCardsProjectionMap.put(DataBaseManager.COL_CARD_TXT_ANSWER, DataBaseManager.COL_CARD_TXT_ANSWER);
        mCardsProjectionMap.put(DataBaseManager.COL_CARD_IMG_QUESTION, DataBaseManager.COL_CARD_IMG_QUESTION);
        mCardsProjectionMap.put(DataBaseManager.COL_CARD_IMG_ANSWER, DataBaseManager.COL_CARD_IMG_ANSWER);
    }


    public static MemoryCardContentProvider getInstance(String tableName){
        CONTENT_URI =
                Uri.withAppendedPath(
                        BASE_URI,
                        tableName);
        checkBeforeCreate(tableName);
        MemoryCardContentProvider instance =  new MemoryCardContentProvider(tableName);
        return instance;
    }

    private static void checkBeforeCreate(String tableName){
        if(!DataBaseManager.isTableExists(tableName)){
            //uri register
            DataBaseManager.ceateNewCardsGroupDataBaseTable(tableName);
            uriMatcher.addURI(PROVIDER_NAME,tableName, CARDS);
            uriMatcher.addURI(PROVIDER_NAME,tableName+"/1",CARD_MAXID);
            uriMatcher.addURI(PROVIDER_NAME,tableName+"/2",CARD);
        }
    }


    public static void checkURI(Uri uri,String tableName){
        tabName = tableName;
        if(uriMatcher.match(uri)<0){
            DataBaseManager.ceateNewCardsGroupDataBaseTable(tableName);
            uriMatcher.addURI(PROVIDER_NAME,tableName, CARDS);
            uriMatcher.addURI(PROVIDER_NAME,tableName+"/1",CARD_MAXID);
            uriMatcher.addURI(PROVIDER_NAME,tableName+"/2",CARD);
        }
    }

    @Override
    public boolean onCreate() {

        return false;
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;
        try {

            SQLiteDatabase writableDatabase = DataBaseManager.getDatabaseHelper().getWritableDatabase();

            switch (uriMatcher.match(uri)) {

                case LIST:
                case LIST_MAXID:
                case LISTS: {
                    count = writableDatabase.delete(DataBaseManager.LIST_TAB,selection, selectionArgs);
                    break;
                }

                case CARD:
                case CARD_MAXID:
                case CARDS: {
                    count = writableDatabase.delete(tabName,selection, selectionArgs);
                    break;
                }
                default: {
                    break;
                }
            }
        }catch (Exception e) {
            Log.e(TAG, "error in insert method");
            e.printStackTrace();
        }
        return count;    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long rowID = 0;
        try {
            SQLiteDatabase writableDb = DataBaseManager.getDatabaseHelper().getWritableDatabase();

            switch (uriMatcher.match(uri)) {

                case LIST:
                case LIST_MAXID:
                case LISTS: {
                    rowID = writableDb.insert(DataBaseManager.LIST_TAB, "", values);
                    break;
                }

                case CARD:
                case CARD_MAXID:
                case CARDS: {
                    rowID = writableDb.insert(tabName, "", values);
                    break;
                }
                default: {
                    break;
                }
            }
        }catch (Exception e) {
            Log.e(TAG, "error in insert method");
            e.printStackTrace();
        }
        Uri _uri = null;
/*        if (rowID > 0) {
            _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }*/
        return _uri;

    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor cursor = null;
        try {
            SQLiteDatabase readableDB = DataBaseManager.getDatabaseHelper().getReadableDatabase();

            SQLiteQueryBuilder sqlBuilder = new SQLiteQueryBuilder();

            switch (uriMatcher.match(uri)) {

                case LIST:{
                    sqlBuilder.setTables(DataBaseManager.LIST_TAB);
                    //sqlBuilder.setProjectionMap(mListsProjectionMap);
                    break;
                }
                case LIST_MAXID:{
                    sqlBuilder.setTables(DataBaseManager.LIST_TAB);
                    //sqlBuilder.setProjectionMap(mListProjectionMap);
                    break;
                }
                case LISTS:{
                    sqlBuilder.setTables(DataBaseManager.LIST_TAB);
                    //sqlBuilder.setProjectionMap(mListsProjectionMap);
                    break;
                }

                case CARD:{

                    sqlBuilder.setTables(tabName);
                    //sqlBuilder.setProjectionMap(mCardsProjectionMap);

                    break;
                }
                case CARD_MAXID:{

                    sqlBuilder.setTables(tabName);
                    //sqlBuilder.setProjectionMap(mCardProjectionMap);

                    break;
                }
                case CARDS:{

                    sqlBuilder.setTables(tabName);
                    //sqlBuilder.setProjectionMap(mCardsProjectionMap);

                    break;
                }
            }


            cursor = sqlBuilder.query(readableDB,projection, null, null, null, null, null);


        } catch (Exception e) {
            Log.e(TAG, "error query in database");
            e.printStackTrace();
        }
        //throw new UnsupportedOperationException("Not yet implemented");

        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int count = 0;

        try {
            SQLiteDatabase writableDb = DataBaseManager.getDatabaseHelper().getWritableDatabase();

            switch (uriMatcher.match(uri)) {

                case LIST:
                case LIST_MAXID:
                case LISTS: {
                    count = writableDb.update(DataBaseManager.LIST_TAB,values,selection, selectionArgs);
                    break;
                }

                case CARD:
                case CARD_MAXID:
                case CARDS: {
                    count = writableDb.update(tabName,values,selection, selectionArgs);
                    break;
                }
                default: {
                    break;
                }
            }
        }catch (Exception e) {
            Log.e(TAG, "error in insert method");
            e.printStackTrace();
        }
        return count;
  }


}
