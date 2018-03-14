package nl.marcovp.avans.nasaroverphotos.controller;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import nl.marcovp.avans.nasaroverphotos.domain.Photo;
import nl.marcovp.avans.nasaroverphotos.util.PhotoAdapter;
import nl.marcovp.avans.nasaroverphotos.R;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, PhotoFoundTask.OnPhotoAvailable, View.OnClickListener {

    private PhotoAdapter photoAdapter;
    private ArrayList<Photo> photos = new ArrayList<>();
    private RecyclerView listViewPhotos;
    private String TAG = this.getClass().getSimpleName();

    // Get yesterday's date
    final Calendar c = Calendar.getInstance(TimeZone.getDefault());
    int mYear = c.get(Calendar.YEAR);
    int mMonth = (c.get(Calendar.MONTH)) + 1;
    int mDay = (c.get(Calendar.DAY_OF_MONTH)) - 1;

    // Counter to disable triggering onItemSelected
    int counter = 0;

    // Date and Camera
    private String dateFormat;
    private String item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Debug Log
        Log.d(TAG, "onCreate");

        // Start with all camera's and today's date
        item = "ALL";
        dateFormat = mYear + "-" + mMonth + "-" + mDay;

        // Debug Log
        Log.d(TAG, item + " - " + dateFormat);

        // Get saved instance if any, otherwise get new photos
        if (savedInstanceState != null && savedInstanceState.getSerializable("photoArray") != null) {
            photos = (ArrayList<Photo>) savedInstanceState.getSerializable("photoArray");
            dateFormat = savedInstanceState.getString("date");
        } else {
            getPhotosByCameraAndDate(item, dateFormat);
        }

        listViewPhotos = findViewById(R.id.listview_photos);
        listViewPhotos.setHasFixedSize(true);

        // Get Devices orientation
        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int rotation = display.getRotation();

        // Choose layout based on orientation
        if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) {
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
            listViewPhotos.setLayoutManager(layoutManager);
        } else {
            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 3);
            listViewPhotos.setLayoutManager(layoutManager);
        }

        // Debug Log
        Log.d(TAG, "Rotation: " + rotation);

        // Set Adapter
        photoAdapter = new PhotoAdapter(photos);
        listViewPhotos.setAdapter(photoAdapter);

        // Fill Spinner Menu with Camera's
        fillSpinner();

        // Set listener on calendar button
        ImageButton calendarButton = findViewById(R.id.button_calendar);
        calendarButton.setOnClickListener(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Debug Log
        Log.d(TAG, "onSaveInstanceState");

        // Save selected date and photo array
        outState.putString("date", dateFormat);
        outState.putSerializable("photoArray", photos);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // Debug Log
        Log.d(TAG, "onRestoreInstanceState");

        // Restore photo array
        photos = (ArrayList<Photo>) savedInstanceState.getSerializable("photoArray");
    }

    @Override
    public void onPhotoAvailable(Photo p) {

        // Debug Log
        Log.d(TAG, "onPhotoAvailable - " + p.getImageURL());

        // Add Photo to array
        photos.add(p);

        // Notify Adapter of change
        photoAdapter.notifyDataSetChanged();
    }

    public void getPhotosByCameraAndDate(String camera, String date) {

        // Make sure URL is empty
        String url = "";

        // If the camera is "ALL" don't specify a Camera.
        if (camera.contains("ALL")) {
            url = "https://api.nasa.gov/mars-photos/api/v1/rovers/curiosity/photos?api_key=hkOe3Z4NdnkxYI8FlnnDMCc1o4Xuu8GRiClCnwFt&earth_date=" + date;
        } else {
            Log.d("MainAcitivity", "Camera doesn't contain all");
            url = "https://api.nasa.gov/mars-photos/api/v1/rovers/curiosity/photos?api_key=hkOe3Z4NdnkxYI8FlnnDMCc1o4Xuu8GRiClCnwFt&earth_date=" + date + "&camera=" + camera;
        }

        // Set Date in Title
        setTitle(getString(R.string.app_name) + " - " + dateFormat);

        // Debug Log
        Log.d("MainActivity", "Constructed URL String: " + url);

        // Clear the adapter to prepare for new dataset
        clear();

        // Start Async task
        String[] urls = new String[]{url};
        PhotoFoundTask findPhotos = new PhotoFoundTask(this, this);
        findPhotos.execute(urls);
    }

    public void fillSpinner() {

        // Debug Log
        Log.d(TAG, "fillSpinner");

        // Set listener on Spinner Menu
        Spinner cameraSpinner = findViewById(R.id.spinner_cameras);
        cameraSpinner.setOnItemSelectedListener(this);

        // Hardcoded Camera Names
        ArrayList<String> cameras = new ArrayList<>();
        cameras.add("ALL");
        cameras.add("FHAZ");
        cameras.add("RHAZ");
        cameras.add("MAST");
        cameras.add("CHEMCAM");
        cameras.add("MAHLI");
        cameras.add("MARDI");
        cameras.add("NAVCAM");

        // Set standard arrayAdapter for Strings
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, cameras);
        cameraSpinner.setAdapter(dataAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // Prevent triggering onItemSelected onCreate
        if (++counter > 1) {
            // Get Camera item from Spinner Menu
            item = parent.getItemAtPosition(position).toString();

            // Get Photos by Camera and Date
            getPhotosByCameraAndDate(item, dateFormat);
        }

        // Debug Log
        Log.d(TAG, "onItemSelected got triggered");

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Do Nothing :)
    }

    public void clear() {
        // Clear photo array and notify adapter
        if (photoAdapter != null) {
            final int size = photos.size();
            photos.clear();
            photoAdapter.notifyItemRangeRemoved(0, size);
        }

        // Debug Log
        Log.d(TAG, "Clear adapter and array");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu from XML
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

    @Override
    public void onClick(View v) {

        //Debug Log
        Log.d(TAG, "onClick DatePicker: " + dateFormat);

        // DatePicker
        DatePickerDialog dpd = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // Remember selected date
                        dateFormat = "" + year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;

                        // Get photos from selected date and camera
                        getPhotosByCameraAndDate(item, dateFormat);

                        // Set Global Variables to remember selection
                        mYear = year;
                        mMonth = monthOfYear + 1;
                        mDay = dayOfMonth;

                    }
                }, mYear, (mMonth - 1), mDay);

        // Set minimal date to date of landing
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2012);
        cal.set(Calendar.MONTH, Calendar.AUGUST);
        cal.set(Calendar.DAY_OF_MONTH, 6);

        // Set maximal date to today
        dpd.getDatePicker().setMaxDate(new Date().getTime());
        dpd.getDatePicker().setMinDate(cal.getTimeInMillis());
        dpd.show();
    }
}
