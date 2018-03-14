package nl.marcovp.avans.nasaroverphotos.controller;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import nl.marcovp.avans.nasaroverphotos.R;
import nl.marcovp.avans.nasaroverphotos.datalayer.PhotoDBHandler;
import nl.marcovp.avans.nasaroverphotos.domain.Photo;
import uk.co.senab.photoview.PhotoViewAttacher;

public class PhotoDetailActivity extends AppCompatActivity {

    private PhotoDBHandler dbHandler = null;
    private Photo p;
    private String TAG = this.getClass().getSimpleName();
    private FloatingActionButton fab;
    private ImageView imageView;
    private PhotoViewAttacher photoAttacher;
    private Callback imageLoadedCallback = new Callback() {
        @Override
        public void onSuccess() {
            if (photoAttacher != null) {
                photoAttacher.update();
            } else {
                photoAttacher = new PhotoViewAttacher(imageView);
            }
            Log.d(TAG, "Callback: Loaded image into view");
        }

        @Override
        public void onError() {
            Log.e("PhotoDetailActivity", "Could not load image");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);

        // Database Handler
        dbHandler = new PhotoDBHandler(
                getApplicationContext(),
                "photo.db",
                null,
                1
        );

        // Get Photo Object from Intent
        Intent i = getIntent();
        p = (Photo) i.getSerializableExtra("PHOTO");

        // Set Image
        imageView = findViewById(R.id.imageview_photo);
        Picasso.with(this).load(p.getImageURL()).into(imageView, imageLoadedCallback);

        // Set Camera Name
        TextView textView = findViewById(R.id.textview_photo);
        textView.setText(p.getCameraName());


        fab = findViewById(R.id.fab_favorite);

        if (dbHandler.isFavorite(p.getId())) {

            // Set image
            fab.setImageDrawable(getDrawable(R.drawable.ic_star_white_24dp));

            // Debug Log
            Log.d(TAG, "Image is favorite");

        } else {

            // Set image
            fab.setImageDrawable(getDrawable(R.drawable.ic_star_border_white_24dp));

            // Debug Log
            Log.d(TAG, "Image is NOT favorite");
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

                // Open in browser
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(p.getImageURL()));
                startActivity(browserIntent);

                // Debug Log
                Log.d(TAG, "Opened in browser");

                break;
            case R.id.action_wallpaper:

                // Get image from imageView
                Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

                // Start Async task
                SetWallpaperTask setWallpaperTask = new SetWallpaperTask(bitmap, this);
                setWallpaperTask.execute();

                break;
        }
        return false;
    }
}
