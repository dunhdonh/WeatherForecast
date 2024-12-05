package com.example.weather_forecast.domains;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class NominatimSearchTask extends AsyncTask<String, Void, ArrayList<String>> {

    private final OnSearchCompleteListener listener;

    public interface OnSearchCompleteListener {
        void onSearchComplete(ArrayList<String> results);
    }

    public NominatimSearchTask(OnSearchCompleteListener listener) {
        this.listener = listener;
    }

    @Override
    protected ArrayList<String> doInBackground(String... strings) {
        String query = strings[0];
        String urlString = "https://nominatim.openstreetmap.org/search?q=" + query + "&format=json";

        ArrayList<String> results = new ArrayList<>();
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            reader.close();

            // Parse JSON response
            JSONArray jsonArray = new JSONArray(result.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String displayName = jsonObject.getString("display_name");
                results.add(displayName);
            }
        } catch (Exception e) {
            Log.e("NominatimSearchTask", "Error: " + e.getMessage());
        }
        return results;
    }

    @Override
    protected void onPostExecute(ArrayList<String> results) {
        if (listener != null) {
            listener.onSearchComplete(results);
        }
    }
}
