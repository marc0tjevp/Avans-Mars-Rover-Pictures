package nl.marcovp.avans.nasaroverphotos.controller;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Date;
import java.util.Objects;

import nl.marcovp.avans.nasaroverphotos.R;
import nl.marcovp.avans.nasaroverphotos.domain.Photo;

/**
 * Created by marco on 3/13/18.
 */

public class PhotoFoundTask extends AsyncTask<String, Void, String> {

    private OnPhotoAvailable listener = null;
    private Context context;
    private ProgressDialog progressDialog;
    private String TAG = this.getClass().getSimpleName();

    PhotoFoundTask(OnPhotoAvailable listener, Context context) {
        this.listener = listener;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        // Debug Log
        Log.d(TAG, "onPreExecute");

        // Start progressDialog
        progressDialog = ProgressDialog.show(context, context.getResources().getString(R.string.text_fetching_title), context.getResources().getString(R.string.text_fetching_message), true);
    }

    @Override
    protected String doInBackground(String... strings) {

        // Debug Log
        Log.d(TAG, "doInBackground");

        // Variables
        InputStream inputStream = null;
        int responseCode = -1;
        String movieUrl = strings[0];
        String response = "";

        try {

            // Open Connection
            URL url = new URL(movieUrl);
            URLConnection connection = url.openConnection();

            // Debug Log
            Log.d(TAG, "doInBackground URL: " + url);

            if (!(connection instanceof HttpURLConnection)) {
                return null;
            }

            // Connect
            HttpURLConnection httpURLConnection = (HttpURLConnection) connection;
            httpURLConnection.setAllowUserInteraction(false);
            httpURLConnection.setInstanceFollowRedirects(true);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();

            // Get Response Code
            responseCode = httpURLConnection.getResponseCode();

            // On 200, set JSON data to String
            if (responseCode == HttpURLConnection.HTTP_OK) {
                inputStream = httpURLConnection.getInputStream();
                response = getStringFromInputStream(inputStream);
            }

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "IOException: " + e.getMessage());
        }

        return response;
    }

    @Override
    protected void onPostExecute(String response) {

        // Debug Log
        Log.d(TAG, "onPostExecute");

        // Check if response is not empty
        if (response == null || Objects.equals(response, "")) {
            Toast.makeText(context, R.string.text_no_photos, Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject jsonObject;

        try {

            // Make JSON Object from string
            jsonObject = new JSONObject(response);

            // Get photos array from data
            JSONArray photos = jsonObject.getJSONArray("photos");

            for (int i = 0; i < photos.length(); i++) {

                // Get Photo object by index
                JSONObject photo = photos.getJSONObject(i);

                // Set variables
                int photoID = photo.getInt("id");
                int photoSol = photo.getInt("sol");
                JSONObject photoCamera = photo.getJSONObject("camera");
                String photoCameraName = photoCamera.getString("full_name");
                String photoURL = photo.getString("img_src");
                Date earthDate = Date.valueOf(photo.getString("earth_date"));

                // Create photo object
                Photo p = new Photo(photoID, photoSol, photoCameraName, photoURL, earthDate);

                // Debug Log
                Log.d(TAG, "New Photo: " + p.getId());

                // Return object to listener
                listener.onPhotoAvailable(p);

            }

            // Debug Log
            Log.d(TAG, "Found " + photos.length() + " Photos");

        } catch (JSONException e) {
            Log.e(TAG, "JSONEXCEPTION: " + e.getMessage());
            e.printStackTrace();
        }

        // Dismiss progressDialog
        progressDialog.dismiss();
    }

    private static String getStringFromInputStream(InputStream is) {

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        String line;

        try {

            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();
    }

    // Interface OnPhotoAvailable
    public interface OnPhotoAvailable {
        void onPhotoAvailable(Photo p);
    }
}
