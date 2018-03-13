package nl.marcovp.avans.nasaroverphotos.controller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        dbHandler = new PhotoDBHandler(
                getApplicationContext(),
                "photo.db",
                null,
                1
        );

        photos = dbHandler.getAllFavorites();

        listViewPhotos = findViewById(R.id.listview_favorites);
        listViewPhotos.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 3);
        listViewPhotos.setLayoutManager(layoutManager);
        photoAdapter = new PhotoAdapter(photos);
        listViewPhotos.setAdapter(photoAdapter);
    }
}