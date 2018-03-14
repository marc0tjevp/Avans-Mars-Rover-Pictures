package nl.marcovp.avans.nasaroverphotos.controller;

import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

import nl.marcovp.avans.nasaroverphotos.R;

/**
 * Created by marco on 3/14/18.
 */

public class SetWallpaperTask extends AsyncTask<Void, Void, Boolean> {

    private Bitmap bitmap;
    private String TAG = this.getClass().getSimpleName();
    private boolean succes;
    private Context context;

    SetWallpaperTask(Bitmap bitmap, Context context) {
        this.bitmap = bitmap;
        this.context = context;
    }


    @Override
    protected Boolean doInBackground(Void... voids) {

        // Debug Log
        Log.d(TAG, "doInBackground");

        // Get Wallpaper Manager
        WallpaperManager wm = WallpaperManager.getInstance(context);

        // Try to set wallpaper
        try {

            // Set Wallpaper
            wm.setBitmap(bitmap);

            // Set boolean
            succes = true;

        } catch (IOException e) {

            // Set Boolean
            succes = false;

            // Print Stack
            e.printStackTrace();

            // Error Log
            Log.e(TAG, "Set as wallpaper failed: " + e.getMessage());
        }

        return succes;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);

        // Return Feedback
        if (result) {
            Toast.makeText(context, R.string.text_wallpaper_set, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, R.string.text_wallpaper_notset, Toast.LENGTH_SHORT).show();
        }
    }
}
