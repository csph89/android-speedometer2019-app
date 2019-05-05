package com.unipi.gkriketos.speedinspectorapp;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private SQLiteDatabase myDB;
    private ArrayList<Double> longitudeArrayList = new ArrayList<>();
    private ArrayList<Double> latititudeArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        myDB = openOrCreateDatabase("SpeedInspectorDB", MODE_PRIVATE, null);
        Cursor cursor = myDB.rawQuery("SELECT longitude,latitude FROM SpeedInspector;", null);
        if(cursor.getCount() == 0)
            Toast.makeText(this, "No records found!", Toast.LENGTH_SHORT).show();
        else {
            while(cursor.moveToNext()) {
                String longit = cursor.getString(0);
                longitudeArrayList.add(Double.parseDouble(longit));
                String latit = cursor.getString(1);
                latititudeArrayList.add(Double.parseDouble(latit));
            }
        }
        cursor.close();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //ArrayList<Marker> myMarkers = new ArrayList<>();
        for(int i=0; i<longitudeArrayList.size(); i++) {
            LatLng myPosition = new LatLng(latititudeArrayList.get(i), longitudeArrayList.get(i));
            mMap.addMarker(new MarkerOptions().position(myPosition).title("Marker"));
            //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myPosition, 10.0f));
        }
    }
}
