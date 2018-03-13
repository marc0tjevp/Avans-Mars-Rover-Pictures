package nl.marcovp.avans.nasaroverphotos.controller;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import nl.marcovp.avans.nasaroverphotos.datalayer.PhotoDBHandler;
import nl.marcovp.avans.nasaroverphotos.domain.Photo;
import nl.marcovp.avans.nasaroverphotos.R;

public class PhotoDetailActivity extends AppCompatActivity {

    private PhotoDBHandler dbHandler = null;
    private Photo p;
    private FloatingActionButton fab;

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

        Toast.makeText(this, p.getImageURL(), Toast.LENGTH_SHORT).show();

        ImageView imageView = findViewById(R.id.imageview_photo);
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
                    Snackbar.make(view, R.string.text_favorite_removed, Snackbar.LENGTH_LONG).show();
                } else {

                    // Insert Favorite
                    dbHandler.insertFavorite(p);

                    // Set drawable to favorite
                    fab.setImageDrawable(getDrawable(R.drawable.ic_star_white_24dp));
                    Snackbar.make(view, R.string.text_favorited_added, Snackbar.LENGTH_LONG).show();
                }
            }
        });

    }
}
