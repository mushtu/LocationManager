package com.mojtaba.worktime.datasource;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.mojtaba.worktime.model.Place;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mojtaba on 6/4/15.
 */
public class DataSource {

    // Database fields
    private SQLiteDatabase database;
    private LocationSQLiteHelper dbHelper;

    private String[] placesColumns = { LocationSQLiteHelper.COLUMN_ID,
            LocationSQLiteHelper.COLUMN_NAME,LocationSQLiteHelper.COLUMN_DESCRIPTION,
            LocationSQLiteHelper.COLUMN_GOOGLE_ADDRESS,LocationSQLiteHelper.COLUMN_LATITUDE,
            LocationSQLiteHelper.COLUMN_LONGITUDE};

    public DataSource(Context context) {
        dbHelper = LocationSQLiteHelper.getInstance(context);
    }

    public synchronized void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public synchronized void close() {
        if(database != null && database.isOpen())
            database.close();
    }

    public Place createPlace(String name,String description,String gAddress,double latitude,double longitude) {
        ContentValues values = new ContentValues();
        values.put(LocationSQLiteHelper.COLUMN_NAME, name);
        values.put(LocationSQLiteHelper.COLUMN_DESCRIPTION,description);
        values.put(LocationSQLiteHelper.COLUMN_GOOGLE_ADDRESS,gAddress);
        values.put(LocationSQLiteHelper.COLUMN_LATITUDE,latitude);
        values.put(LocationSQLiteHelper.COLUMN_LONGITUDE,longitude);

        long insertId = database.insert(LocationSQLiteHelper.TABLE_PLACES, null,
                values);
        Cursor cursor = database.query(LocationSQLiteHelper.TABLE_PLACES,
                placesColumns, LocationSQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Place newPlace = cursorToPlace(cursor);
        cursor.close();
        return newPlace;
    }

    public synchronized boolean isOpen()
    {
        if(database == null) return false;
        return database.isOpen();
    }
    public Place cursorToPlace(Cursor cursor)
    {
        Place place = new Place(cursor.getString(cursor.getColumnIndex(LocationSQLiteHelper.COLUMN_NAME)),
                cursor.getLong(cursor.getColumnIndex(LocationSQLiteHelper.COLUMN_ID)));
        place.setDescription(cursor.getString(cursor.getColumnIndex(LocationSQLiteHelper.COLUMN_DESCRIPTION)));
        place.setGoogleAddress(cursor.getString(cursor.getColumnIndex(LocationSQLiteHelper.COLUMN_GOOGLE_ADDRESS)));
        place.setLatitude(cursor.getDouble(cursor.getColumnIndex(LocationSQLiteHelper.COLUMN_LATITUDE)));
        place.setLongitude(cursor.getDouble(cursor.getColumnIndex(LocationSQLiteHelper.COLUMN_LONGITUDE)));

        return place ;
    }

    public void deletePlace(Place place) {
        long id = place.getId();
        System.out.println("Place deleted with id: " + id);
        database.delete(LocationSQLiteHelper.TABLE_PLACES, LocationSQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

    public  List<Place> getAllPlaces()
    {
        ArrayList<Place> places = new ArrayList<Place>();

        Cursor cursor = database.query(LocationSQLiteHelper.TABLE_PLACES,
                placesColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Place comment = cursorToPlace(cursor);
            places.add(comment);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return places;

    }
}
