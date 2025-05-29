package com.example.kalistanics;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

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

public class ParksMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private RequestQueue requestQueue;
    private LatLng currentLocation;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private static final String API_KEY = "AIzaSyDA6Ntc2fBAZWOka9x_My0rDw4-LMZUwgA";  // הכנס כאן את מפתח ה־API שלך

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parks_map);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        requestQueue = Volley.newRequestQueue(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            Toast.makeText(this, "Error loading map fragment", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // בדיקת הרשאות
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            enableMyLocation();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 14f));
                // קורא לפונקציה לחיפוש פארקים ברדיוס מסוים
                searchNearbyParks(currentLocation.latitude, currentLocation.longitude);
            } else {
                Toast.makeText(this, "לא נמצא מיקום נוכחי", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchNearbyParks(double latitude, double longitude) {
        int radius = 1000; // רדיוס בחיפוש במטר (1000 = 1 ק"מ)

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

    // טיפול בתוצאות בקשת הרשאות



@Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            onMapReady(mMap);
        } else {
            Toast.makeText(this, "יש לאפשר גישה למיקום", Toast.LENGTH_SHORT).show();
        }
    }
}