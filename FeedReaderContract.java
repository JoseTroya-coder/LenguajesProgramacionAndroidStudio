package com.example.miprimerapp;

import android.provider.BaseColumns;

public class FeedReaderContract {

    private FeedReaderContract() {}

    public static class FeedEntry implements BaseColumns {
        public static final String nameTable = "persona";
        public static final String column1 = "nombre";
        public static final String column2 = "apellido";
        public static final String column3 = "email";
    }

    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + FeedEntry.nameTable + " (" +
                    FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    FeedEntry.column1 + " TEXT," +
                    FeedEntry.column2 + " TEXT," +
                    FeedEntry.column3 + " TEXT)";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + FeedEntry.nameTable;
}