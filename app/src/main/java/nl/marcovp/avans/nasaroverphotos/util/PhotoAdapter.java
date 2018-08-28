package nl.marcovp.avans.nasaroverphotos.util;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import nl.marcovp.avans.nasaroverphotos.R;
import nl.marcovp.avans.nasaroverphotos.controller.PhotoDetailActivity;
import nl.marcovp.avans.nasaroverphotos.domain.Photo;

/**
 * Created by marco on 3/13/18.
 */

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder> {

    private ArrayList photos;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private View view;
        private ImageView imageViewPhoto;
        private TextView textViewID;
        private String TAG = this.getClass().getSimpleName();

        ViewHolder(View v) {
            super(v);
            this.view = v;
            this.view.setOnClickListener(this);

            // Find view elements
            imageViewPhoto = v.findViewById(R.id.imageView_photo_list);
            textViewID = v.findViewById(R.id.textview_photo_list);
        }

        @Override
        public void onClick(View v) {

            // Debug Log
            Log.d(TAG, "OnClick Row Item");

            // Get Adapter position
            int position = getAdapterPosition();

            // Get object from position
            Photo p = (Photo) photos.get(position);

            // Create new Detail Intent
            Intent photoDetailIntent = new Intent(view.getContext().getApplicationContext(), PhotoDetailActivity.class);

            // Insert photo object into intent
            photoDetailIntent.putExtra("PHOTO", p);

            // Start Activity
            view.getContext().startActivity(photoDetailIntent);
        }
    }

    public PhotoAdapter(ArrayList<Photo> photos) {
        this.photos = photos;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.photo_listview_row, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        // Get Photo from array
        Photo p = (Photo) this.photos.get(position);

        // Set Image into imageView
        Picasso.with(holder.view.getContext())
                .load(p.getImageURL())
                .resize(1000, 1000)
                .centerCrop()
//                .noFade()
                .into(holder.imageViewPhoto);

        // Set Id into textView
        holder.textViewID.setText(String.valueOf(p.getId()));

    }

    @Override
    public int getItemCount() {
        return this.photos.size();
    }
}
