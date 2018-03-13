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

    public PhotoDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ON_CREATE_DATABASE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(ON_UPGRADE_DATABASE);
        onCreate(db);
    }

    public void insertFavorite(Photo p) {
        ContentValues values = new ContentValues();
        values.put("id", p.getId());
        values.put("sol", p.getSol());
        values.put("cameraName", p.getCameraName());
        values.put("imageURL", p.getImageURL());
        values.put("earthDate", p.getEarthDate().toString());

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert("photo", null, values);
        db.close();
    }

    public void deleteFavorite(int id) {
        String query = "DELETE FROM photo WHERE id = " + id;
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query);
        db.close();
    }

    public boolean isFavorite(int id) {
        boolean exists;
        String query = "SELECT * FROM photo WHERE id=" + id;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            exists = true;
        } else {
            exists = false;
        }

        db.close();
        cursor.close();

        return exists;
    }

    public ArrayList<Photo> getAllFavorites() {
        String query = "SELECT * FROM photo";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        ArrayList<Photo> photos = new ArrayList<>();

        cursor.moveToFirst();
        while (!cursor.isLast()) {
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            int sol = cursor.getInt(cursor.getColumnIndex("sol"));
            String cameraName = cursor.getString(cursor.getColumnIndex("cameraName"));
            String imageURL = cursor.getString(cursor.getColumnIndex("imageURL"));
            Date earthDate = Date.valueOf(cursor.getString(cursor.getColumnIndex("earthDate")));

            photos.add(new Photo(id, sol, cameraName, imageURL, earthDate));
            cursor.moveToNext();
        }

        db.close();
        cursor.close();

        return photos;
    }
}
