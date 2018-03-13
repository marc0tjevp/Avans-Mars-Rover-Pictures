package nl.marcovp.avans.nasaroverphotos.controller;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

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

    PhotoFoundTask(OnPhotoAvailable listener, Context context) {
        this.listener = listener;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = ProgressDialog.show(context, context.getResources().getString(R.string.text_fetching_title), context.getResources().getString(R.string.text_fetching_message), true);
    }

    @Override
    protected String doInBackground(String... strings) {
        InputStream inputStream = null;
        int responseCode = -1;
        String movieUrl = strings[0];
        String response = "";

        try {

            URL url = new URL(movieUrl);
            URLConnection connection = url.openConnection();

            if (!(connection instanceof HttpURLConnection)) {
                return null;
            }

            HttpURLConnection httpURLConnection = (HttpURLConnection) connection;
            httpURLConnection.setAllowUserInteraction(false);
            httpURLConnection.setInstanceFollowRedirects(true);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();

            responseCode = httpURLConnection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                inputStream = httpURLConnection.getInputStream();
                response = getStringFromInputStream(inputStream);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }

    @Override
    protected void onPostExecute(String response) {
        if (response == null || Objects.equals(response, "")) {
            return;
        }

        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(response);
            JSONArray photos = jsonObject.getJSONArray("photos");

            for (int i = 0; i < photos.length(); i++) {
                JSONObject photo = photos.getJSONObject(i);

                int photoID = photo.getInt("id");
                int photoSol = photo.getInt("sol");
                JSONObject photoCamera = photo.getJSONObject("camera");
                String photoCameraName = photoCamera.getString("full_name");
                String photoURL = photo.getString("img_src");
                Date earthDate = Date.valueOf(photo.getString("earth_date"));

                Photo p = new Photo(photoID, photoSol, photoCameraName, photoURL, earthDate);

                listener.onPhotoAvailable(p);

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
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

    public interface OnPhotoAvailable {
        void onPhotoAvailable(Photo p);
    }
}
