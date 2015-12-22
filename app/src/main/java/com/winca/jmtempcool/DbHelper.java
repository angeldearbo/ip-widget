package com.winca.jmtempcool;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DbHelper extends SQLiteOpenHelper {

    //The Android's default system path of your application database.
    private static String DB_PATH = "data/data/com.winca.jmtempcool/databases/";
    private static String DB_NAME = "locations.db";
    private static String TABLE_LOCATIONS = "locations";

    private final Context context;
    private SQLiteDatabase db;


    // constructor
    public DbHelper(Context context) {
        super(context, DB_NAME, null, 1);
        this.context = context;
    }

    // Creates a empty database on the system and rewrites it with your own database.
    public void create() throws IOException {

        boolean dbExist = checkDataBase();

        if (dbExist) {
            //do nothing - database already exist
        } else {
            //By calling this method and empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
            this.getReadableDatabase();

            try {
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }

    // Check if the database exist to avoid re-copy the data
    private boolean checkDataBase() {
        /*SQLiteDatabase checkDB = null;
        try{
			String path = DB_PATH + DB_NAME;
			checkDB = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
		}catch(SQLiteException e){
	      // database don't exist yet.
			e.printStackTrace();
	    }
	    if(checkDB != null){
	    	checkDB.close();
	    }
    	return checkDB != null ? true : false;*/
        File dbFile = new File(DB_PATH + DB_NAME);
        return dbFile.exists();
    }

    // copy your assets db to the new system DB
    private void copyDataBase() throws IOException {
        //Open your local db as the input stream
        InputStream myInput = context.getAssets().open(DB_NAME);
        // Path to the just created empty db
        String outFileName = DB_PATH + DB_NAME;
        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }
        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    //Open the database
    public boolean open() {
        try {
            String myPath = DB_PATH + DB_NAME;
            db = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
            return true;
        } catch (SQLException sqle) {
            db = null;
            return false;
        }
    }

    @Override
    public synchronized void close() {
        if (db != null)
            db.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public LocationData getLocationData(Location loc) {
        LocationData data = null;
        if (loc != null) {
            try {
                String query = "SELECT _id, country_code , admin2_code, name, (abs(latitude - " + loc.getLatitude() + ") + abs(longitude - " + loc.getLongitude() + ")) as distancia "
                        + "FROM " + TABLE_LOCATIONS
                        + " WHERE feature_class = 'P'"
                        + " ORDER BY distancia"
                        + " LIMIT 1";
                if (db.isOpen()) {
                    Cursor cursor = db.rawQuery(query, null);
                    if (cursor.moveToFirst()) {
                        do {
                            data = new LocationData();
                            data.set_id(Integer.parseInt(cursor.getString(0)));
                            data.set_country_code(cursor.getString(1));
                            data.set_admin2_code(cursor.getString(2));
                            data.set_name(cursor.getString(3));
                            data.set_latitude(loc.getLatitude());
                            data.set_longitude(loc.getLongitude());

                        } while (cursor.moveToNext());
                    }
                }

            } catch (Exception e) {
                // sql error
                Log.e("DbHelper", "SQL error", e);
            }
        }

        return data;
    }
}