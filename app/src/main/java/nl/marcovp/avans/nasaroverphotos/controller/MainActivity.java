package nl.marcovp.avans.nasaroverphotos.controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;

import nl.marcovp.avans.nasaroverphotos.domain.Photo;
import nl.marcovp.avans.nasaroverphotos.util.PhotoAdapter;
import nl.marcovp.avans.nasaroverphotos.R;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, PhotoFoundTask.OnPhotoAvailable {

    private PhotoAdapter photoAdapter;
    private ArrayList<Photo> photos = new ArrayList<>();
    RecyclerView listViewPhotos;
    int check = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null && (ArrayList<Photo>) savedInstanceState.getSerializable("photoArray") != null) {
            photos = (ArrayList<Photo>) savedInstanceState.getSerializable("photoArray");
        } else {
            getAllPictures();
        }

        fillSpinner();

        listViewPhotos = findViewById(R.id.listview_photos);
        listViewPhotos.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        listViewPhotos.setLayoutManager(layoutManager);
        photoAdapter = new PhotoAdapter(photos);
        listViewPhotos.setAdapter(photoAdapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("photoArray", photos);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        photos = (ArrayList<Photo>) savedInstanceState.getSerializable("photoArray");
    }

    @Override
    public void onPhotoAvailable(Photo p) {
        photos.add(p);
        photoAdapter.notifyDataSetChanged();
    }

    public void getAllPictures() {
        String[] urls = new String[]{"https://api.nasa.gov/mars-photos/api/v1/rovers/curiosity/photos?sol=1000&page=1&api_key=hkOe3Z4NdnkxYI8FlnnDMCc1o4Xuu8GRiClCnwFt"};
        PhotoFoundTask findPhotos = new PhotoFoundTask(this, this);
        findPhotos.execute(urls);
    }

    public void getPicturesByCamera(String camera) {
        if (!camera.equals("ALL CAMERAS")) {
            String[] urls = new String[]{"https://api.nasa.gov/mars-photos/api/v1/rovers/curiosity/photos?sol=1000&page=1&api_key=hkOe3Z4NdnkxYI8FlnnDMCc1o4Xuu8GRiClCnwFt&camera=" + camera};
            System.out.println(urls[0]);
            PhotoFoundTask findPhotos = new PhotoFoundTask(this, this);
            findPhotos.execute(urls);
        } else {
            getAllPictures();
        }
    }

    public void fillSpinner() {
        Spinner cameraSpinner = findViewById(R.id.spinner_cameras);
        cameraSpinner.setOnItemSelectedListener(this);

        ArrayList<String> cameras = new ArrayList<>();
        cameras.add("ALL CAMERAS");
        cameras.add("FHAZ");
        cameras.add("RHAZ");
        cameras.add("MAST");
        cameras.add("CHEMCAM");
        cameras.add("MAHLI");
        cameras.add("MARDI");
        cameras.add("NAVCAM");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, cameras);
        cameraSpinner.setAdapter(dataAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (++check > 1) {
            clear();
            String item = parent.getItemAtPosition(position).toString();
            getPicturesByCamera(item);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    public void clear() {
        final int size = photos.size();
        photos.clear();
        photoAdapter.notifyItemRangeRemoved(0, size);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_favorites:
                Intent i = new Intent(this, FavoritesActivity.class);
                startActivity(i);
                return true;
        }
        return false;
    }
}
