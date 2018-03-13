package nl.marcovp.avans.nasaroverphotos.controller;

import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.IOException;

import nl.marcovp.avans.nasaroverphotos.datalayer.PhotoDBHandler;
import nl.marcovp.avans.nasaroverphotos.domain.Photo;
import nl.marcovp.avans.nasaroverphotos.R;

public class PhotoDetailActivity extends AppCompatActivity {

    private PhotoDBHandler dbHandler = null;
    private Photo p;
    private FloatingActionButton fab;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);

        dbHandler = new PhotoDBHandler(
                getApplicationContext(),
                "photo.db",
                null,
                1
        );

        Intent i = getIntent();
        p = (Photo) i.getSerializableExtra("PHOTO");

        imageView = findViewById(R.id.imageview_photo);
        TextView textView = findViewById(R.id.textview_photo);

        Picasso.with(this).load(p.getImageURL()).into(imageView);
        textView.setText(p.getCameraName());

        fab = findViewById(R.id.fab_favorite);

        if (dbHandler.isFavorite(p.getId())) {
            fab.setImageDrawable(getDrawable(R.drawable.ic_star_white_24dp));
        } else {
            fab.setImageDrawable(getDrawable(R.drawable.ic_star_border_white_24dp));
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dbHandler.isFavorite(p.getId())) {

                    // Delete Favorite
                    dbHandler.deleteFavorite(p.getId());

                    // Feedback
                    fab.setImageDrawable(getDrawable(R.drawable.ic_star_border_white_24dp));
                    Snackbar.make(view, R.string.text_favorite_removed, Snackbar.LENGTH_SHORT).show();
                } else {

                    // Insert Favorite
                    dbHandler.insertFavorite(p);

                    // Set drawable to favorite
                    fab.setImageDrawable(getDrawable(R.drawable.ic_star_white_24dp));
                    Snackbar.make(view, R.string.text_favorited_added, Snackbar.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detailmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_open:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(p.getImageURL()));
                startActivity(browserIntent);
                break;
            case R.id.action_wallpaper:
                imageView.buildDrawingCache();
                Bitmap bitmap = imageView.getDrawingCache();

                WallpaperManager wm = WallpaperManager.getInstance(this);

                try {
                    wm.setBitmap(bitmap);
                    Toast.makeText(this, "Wallpaper has been set!", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    Toast.makeText(this, "Couldn't set wallpaper :(", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                break;
        }
        return false;
    }
}
