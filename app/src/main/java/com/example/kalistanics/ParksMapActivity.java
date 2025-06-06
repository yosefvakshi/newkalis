package com.example.kalistanics;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ParksMapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private MediaPlayer player;
    private boolean musicOn = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LatLng currentLocation;
    private Button btnSearchParks;
    private final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private RequestQueue requestQueue;

    // ⬅️ הכנס כאן את מפתח ה־API שלך
    private final String API_KEY = "AIzaSyDA6Ntc2fBAZWOka9x_My0rDw4-LMZUwgA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parks_map);

        btnSearchParks = findViewById(R.id.btnSearchParks);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        requestQueue = Volley.newRequestQueue(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        btnSearchParks.setOnClickListener(v -> {
            if (currentLocation != null) {
                searchNearbyParks(currentLocation.latitude, currentLocation.longitude);
            } else {
                Toast.makeText(this, "לא הצלחנו לאתר את המיקום שלך", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        mMap.setMyLocationEnabled(true);

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 14));
                        mMap.addMarker(new MarkerOptions().position(currentLocation).title("המיקום שלך"));
                    }
                });
    }

    private void searchNearbyParks(double latitude, double longitude) {
        int radius = 50000; // טווח של 50 ק"מ (50000 מטר)
        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" +
                "location=" + latitude + "," + longitude +
                "&radius=" + radius +
                "&keyword=fitness%20equipment" +
                "&key=" + API_KEY;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray results = response.getJSONArray("results");
                        mMap.clear();
                        mMap.addMarker(new MarkerOptions().position(currentLocation).title("המיקום שלך"));

                        for (int i = 0; i < results.length(); i++) {
                            JSONObject place = results.getJSONObject(i);
                            String name = place.getString("name");
                            JSONObject location = place.getJSONObject("geometry").getJSONObject("location");
                            double lat = location.getDouble("lat");
                            double lng = location.getDouble("lng");

                            LatLng parkLocation = new LatLng(lat, lng);
                            mMap.addMarker(new MarkerOptions().position(parkLocation).title(name));
                        }

                        Toast.makeText(this, "נמצאו " + results.length() + " פארקים", Toast.LENGTH_SHORT).show();

                    } catch (JSONException e) {
                        Toast.makeText(this, "שגיאה בעיבוד התוצאה", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "שגיאה בחיבור לשרת", Toast.LENGTH_SHORT).show());

        requestQueue.add(request);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onMapReady(mMap);
            }
        }
    }

    // פונקצית מניו
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);  // טוען את הקובץ menu_main.xml
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_search_park) {
            // כאן מפעילים את ה-ParksMapActivity
            Intent intent = new Intent(this, ParksMapActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_exit) {          // יציאה
            finishAffinity();
            return true;

        } else if (id == R.id.action_main) {   // חזרה
            finish();
            return true;

        } else if (id == R.id.action_video) { // סרטון חימום
            String videoUrl = "https://www.youtube.com/watch?v=uTV-sR7_QgY";
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl)));
            return true;

        } else if (id == R.id.action_calorie) { // סרטון חימום
            Intent intent = new Intent(this, CalorieActivity.class);
            startActivity(intent);
            return true;

        } else if (id == R.id.action_music) {   // כפתור המוזיקה החדש
            toggleMusic(item);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * מפעיל/מפסיק מוזיקה ומעדכן אייקון וטקסט
     */
    private void toggleMusic(MenuItem item) {
        if (musicOn) {
            player.pause();
            item.setIcon(R.drawable.ic_music_note); // אייקון "כבוי"
            item.setTitle(R.string.music_on);       // "הפעל מוזיקה"
        } else {
            player.start();
            item.setIcon(R.drawable.ic_music_off);  // אייקון "דולק"
            item.setTitle(R.string.music_off);      // "כבה מוזיקה"
        }
        musicOn = !musicOn;
    }





}
