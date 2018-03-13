package nl.marcovp.avans.nasaroverphotos.util;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
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

        ViewHolder(View v) {
            super(v);
            this.view = v;
            this.view.setOnClickListener(this);

            imageViewPhoto = v.findViewById(R.id.imageView_photo_list);
            textViewID = v.findViewById(R.id.textview_photo_list);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Photo p = (Photo) photos.get(position);

            Intent photoDetailIntent = new Intent(view.getContext().getApplicationContext(), PhotoDetailActivity.class);
            photoDetailIntent.putExtra("PHOTO", p);

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
        Photo p = (Photo) this.photos.get(position);
        Picasso.with(holder.view.getContext()).load(p.getImageURL()).into(holder.imageViewPhoto);
        holder.textViewID.setText(String.valueOf(p.getId()));

    }

    @Override
    public int getItemCount() {
        return this.photos.size();
    }
}
