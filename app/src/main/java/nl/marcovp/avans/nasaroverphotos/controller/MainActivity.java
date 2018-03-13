package nl.marcovp.avans.nasaroverphotos.controller;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
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

    // Get yesterday's date
    final Calendar c = Calendar.getInstance(TimeZone.getDefault());
    int mYear = c.get(Calendar.YEAR);
    int mMonth = (c.get(Calendar.MONTH)) + 1;
    int mDay = (c.get(Calendar.DAY_OF_MONTH)) - 1;

    // Counter to disable triggering onItemSelected
    int counter = 0;

    private String dateFormat;
    private String item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        item = "ALL";
        dateFormat = mYear + "-" + mMonth + "-" + mDay;

        if (savedInstanceState != null && savedInstanceState.getSerializable("photoArray") != null) {
            photos = (ArrayList<Photo>) savedInstanceState.getSerializable("photoArray");
            dateFormat = savedInstanceState.getString("date");
        } else {
            testByDate(item, dateFormat);
        }

        listViewPhotos = findViewById(R.id.listview_photos);
        listViewPhotos.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        listViewPhotos.setLayoutManager(layoutManager);
        photoAdapter = new PhotoAdapter(photos);
        listViewPhotos.setAdapter(photoAdapter);

        fillSpinner();

        ImageButton calendarButton = findViewById(R.id.button_calendar);
        calendarButton.setOnClickListener(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("date", dateFormat);
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

    public void testByDate(String camera, String date) {
        String url = "";
        if (camera.contains("ALL")) {
            url = "https://api.nasa.gov/mars-photos/api/v1/rovers/curiosity/photos?api_key=hkOe3Z4NdnkxYI8FlnnDMCc1o4Xuu8GRiClCnwFt&earth_date=" + date;
        } else {
            Log.d("testByDate", "Camera doesn't contain all");
            url = "https://api.nasa.gov/mars-photos/api/v1/rovers/curiosity/photos?api_key=hkOe3Z4NdnkxYI8FlnnDMCc1o4Xuu8GRiClCnwFt&earth_date=" + date + "&camera=" + camera;
        }

        // Set Date in Title
        setTitle("NASA Rover Photos - " + dateFormat);

        Log.d("MainActivity", url);

        clear();
        String[] urls = new String[]{url};
        PhotoFoundTask findPhotos = new PhotoFoundTask(this, this);
        findPhotos.execute(urls);
    }

    public void fillSpinner() {
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

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, cameras);
        cameraSpinner.setAdapter(dataAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (++counter > 1) {
            item = parent.getItemAtPosition(position).toString();
            testByDate(item, dateFormat);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    public void clear() {
        if (photoAdapter != null) {
            final int size = photos.size();
            photos.clear();
            photoAdapter.notifyItemRangeRemoved(0, size);
        }
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

    @Override
    public void onClick(View v) {
        DatePickerDialog dpd = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        dateFormat = "" + year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                        testByDate(item, dateFormat);

                        // Set Global Variables to remember selection
                        mYear = year;
                        mMonth = monthOfYear + 1;
                        mDay = dayOfMonth;

                    }
                }, mYear, (mMonth - 1), mDay);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2012);
        cal.set(Calendar.MONTH, Calendar.AUGUST);
        cal.set(Calendar.DAY_OF_MONTH, 6);

        dpd.getDatePicker().setMaxDate(new Date().getTime());
        dpd.getDatePicker().setMinDate(cal.getTimeInMillis());
        dpd.show();
    }
}
