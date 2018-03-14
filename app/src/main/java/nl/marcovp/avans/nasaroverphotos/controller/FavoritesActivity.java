package nl.marcovp.avans.nasaroverphotos.controller;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

import java.util.ArrayList;

import nl.marcovp.avans.nasaroverphotos.R;
import nl.marcovp.avans.nasaroverphotos.datalayer.PhotoDBHandler;
import nl.marcovp.avans.nasaroverphotos.domain.Photo;
import nl.marcovp.avans.nasaroverphotos.util.PhotoAdapter;

public class FavoritesActivity extends AppCompatActivity {

    private PhotoAdapter photoAdapter;
    private ArrayList<Photo> photos;
    private RecyclerView listViewPhotos;
    private PhotoDBHandler dbHandler = null;
    private String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d(TAG, "onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        // DB Handler
        dbHandler = new PhotoDBHandler(
                getApplicationContext(),
                "photo.db",
                null,
                1
        );

        Log.d(TAG, "DatabaseHandler: " + dbHandler.toString());

        // Set Array equal to array from dbHandler
        photos = dbHandler.getAllFavorites();

        listViewPhotos = findViewById(R.id.listview_favorites);
        listViewPhotos.setHasFixedSize(true);

        // Get Devices orientation
        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int rotation = display.getRotation();

        // Choose layout based on orientation
        if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) {
            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 1);
            listViewPhotos.setLayoutManager(layoutManager);
        } else {
            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 3);
            listViewPhotos.setLayoutManager(layoutManager);
        }

        Log.d(TAG, "Rotation: " + rotation);

        photoAdapter = new PhotoAdapter(photos);
        listViewPhotos.setAdapter(photoAdapter);
    }
}