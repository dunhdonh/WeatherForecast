package com.example.weather_forecast.domains;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String Database_name = "weather.db";
    private static final int Database_version = 1;
    private static final String Create_table_location =
            "CREATE TABLE location (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "city TEXT NOT NULL, " +
                    //"country TEXT NOT NULL, " +
                    //"latitude REAL NOT NULL, " +
                    "icon TEXT NOT NULL, " +
                    "temperature REAL NOT NULL); " ;
                    //"longitude REAL NOT NULL);";

    private static final String Create_table_info =
            "CREATE TABLE forecasts (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "location_id INTEGER NOT NULL, " +
                    "date TEXT NOT NULL, " +
                    "temp_min REAL NOT NULL, " +
                    "temp_max REAL NOT NULL, " +
                    "description TEXT NOT NULL, " +
                    "FOREIGN KEY (location_id) REFERENCES locations(id) ON DELETE CASCADE);";
    public DatabaseHelper(Context context) {
        super(context, Database_name, null, Database_version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Create_table_location);
        db.execSQL(Create_table_info);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS forecasts");
        db.execSQL("DROP TABLE IF EXISTS locations");
        onCreate(db);
    }

    public static interface OnItemClickListener {
        void onItemClick(int position);
    }
}
