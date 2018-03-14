package nl.marcovp.avans.nasaroverphotos.datalayer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.Date;
import java.util.ArrayList;

import nl.marcovp.avans.nasaroverphotos.domain.Photo;

/**
 * Created by marco on 3/13/18.
 */

public class PhotoDBHandler extends SQLiteOpenHelper {

    private String ON_CREATE_DATABASE = "CREATE TABLE `photo` (\n" +
            "\t`id`\tINTEGER,\n" +
            "\t`sol`\tINTEGER NOT NULL,\n" +
            "\t`cameraName`\tTEXT NOT NULL,\n" +
            "\t`imageURL`\tTEXT NOT NULL UNIQUE,\n" +
            "\t`earthDate`\tTEXT,\n" +
            "\tPRIMARY KEY(`id`)\n" +
            ");";
    private String ON_UPGRADE_DATABASE = "DROP TABLE IF EXISTS photo";
    private String TAG = this.getClass().getSimpleName();

    public PhotoDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Debug Log
        Log.d(TAG, "onCreate " + db);

        // Execute OnCreate Query
        db.execSQL(ON_CREATE_DATABASE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Debug Log
        Log.d(TAG, "onUpgrade version " + oldVersion + " -> version " + newVersion);

        // Execute OnUpgrade Query
        db.execSQL(ON_UPGRADE_DATABASE);

        // Execute onCreate
        onCreate(db);
    }

    public void insertFavorite(Photo p) {

        // Debug Log
        Log.d(TAG, "insertFavorite:" + p.getId());

        // Get values from photo object
        ContentValues values = new ContentValues();
        values.put("id", p.getId());
        values.put("sol", p.getSol());
        values.put("cameraName", p.getCameraName());
        values.put("imageURL", p.getImageURL());
        values.put("earthDate", p.getEarthDate().toString());

        // Get Writable Database
        SQLiteDatabase db = this.getWritableDatabase();

        // Insert photo into database
        db.insert("photo", null, values);

        // Close Database
        db.close();
    }

    public void deleteFavorite(int id) {

        // Debug Log
        Log.d(TAG, "deleteFavorite: " + id);

        // Delete Query
        String query = "DELETE FROM photo WHERE id = " + id;

        // Get Writable Database
        SQLiteDatabase db = this.getWritableDatabase();

        // Execute Query
        db.execSQL(query);

        // Close Database
        db.close();
    }

    public boolean isFavorite(int id) {

        // Debug Log
        Log.d(TAG, "isFavorite: " + id);

        // Boolean Exists
        boolean exists;

        // Check if favorite Query
        String query = "SELECT * FROM photo WHERE id=" + id;

        // Get Readable Database
        SQLiteDatabase db = this.getReadableDatabase();

        // Use Cursor to execute Query
        Cursor cursor = db.rawQuery(query, null);

        // Check if cursor has results
        exists = cursor.moveToFirst();

        // Close Database
        db.close();

        // Close Cursor
        cursor.close();

        // Return Boolean
        return exists;
    }

    public ArrayList<Photo> getAllFavorites() {

        // Select All Query
        String query = "SELECT * FROM photo";

        // Get Readable Database
        SQLiteDatabase db = this.getReadableDatabase();

        // Use Cursor to execute Query
        Cursor cursor = db.rawQuery(query, null);

        // Photos array
        ArrayList<Photo> photos = new ArrayList<>();

        // Loop trough Cursor
        cursor.moveToFirst();
        while (!cursor.isLast()) {

            // Set Variables
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            int sol = cursor.getInt(cursor.getColumnIndex("sol"));
            String cameraName = cursor.getString(cursor.getColumnIndex("cameraName"));
            String imageURL = cursor.getString(cursor.getColumnIndex("imageURL"));
            Date earthDate = Date.valueOf(cursor.getString(cursor.getColumnIndex("earthDate")));

            // Add new Photo object to array
            photos.add(new Photo(id, sol, cameraName, imageURL, earthDate));

            // Move to next result, if any
            cursor.moveToNext();
        }

        // Close database
        db.close();

        // Close cursor
        cursor.close();

        // Return photos array
        return photos;
    }
}
