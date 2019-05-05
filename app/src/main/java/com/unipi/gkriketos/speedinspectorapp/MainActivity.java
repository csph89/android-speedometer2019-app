package com.unipi.gkriketos.speedinspectorapp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private int counter = 0;
    private boolean flag = false;
    private ArrayList<Float> speedArrayList = new ArrayList<>();
    private ArrayList<Double> longitudeArrayList = new ArrayList<>();
    private ArrayList<Double> latitudeArrayList = new ArrayList<>();
    TextView textView, textView2;

    //Kanoume xrhsh enos antikeimenou SQLiteDatabase me to opoio tha sundethoume sth vash mas.
    SQLiteDatabase myDB;

    //Kanoume xrhsh enos antikeimenou LocationManager wste na exoume prosvash
    //sta system location services.
    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);
        textView2 = findViewById(R.id.textView2);

        //Edw arxikopoiw to object tupou SQLiteDatabase.
        myDB = openOrCreateDatabase("SpeedInspectorDB", MODE_PRIVATE, null);

        //Ftiaxnoume ena table pou tha krataei tis eggrafes mas.
        myDB.execSQL("CREATE TABLE IF NOT EXISTS `SpeedInspector` (" +
                "`longitude` TEXT," +
                "`latitude` TEXT," +
                "`velocity` TEXT," +
                "`timestamp` TEXT," +
                "PRIMARY KEY(`longitude`,`latitude`,`velocity`)" +
                ");  ");

        //Edw arxikopoioume to object tupou LocationManager pou dhlwsame parapanw
        //dinontas tou to antistoixo system location service.
        locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
            getSpeed(null);
    }

    public void getSpeed(View view) {
        //Prepei upoxrewtika na zhthsoume adeia dioti to location einai dangerous permission
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 111);
        else
            //An h adeia exei hdh paraxwrhthei apo to xrhsth ths efarmoghs zhtame to location.
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0, this);
    }

    public void goMap(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    public void showMessage(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(true);
        builder.show();
    }

    public void insertData(String lon, String lat, String veloc) {
        myDB.execSQL("INSERT OR IGNORE INTO 'SpeedInspector' VALUES ('" + lon +
                "','" + lat +
                "','" + veloc +
                "',strftime('%Y-%m-%d %H:%M:%S','now','localtime'));");
    }

    public void getData(View view) {
        StringBuffer buffer = new StringBuffer();
        Cursor cursor = myDB.rawQuery("SELECT * FROM SpeedInspector;", null);
        if(cursor.getCount() == 0) //An den exei katholou eggrafes h vash mas.
            Toast.makeText(this, "No records found!", Toast.LENGTH_LONG).show();
        else {
            while (cursor.moveToNext()) {
                buffer.append("Longitude: " + cursor.getString(0) + "\n");
                buffer.append("Latitude: " + cursor.getString(1) + "\n");
                buffer.append("Speed: " + cursor.getString(2) + "\n");
                buffer.append("Time: " + cursor.getString(3) + "\n");
                buffer.append("================\n");
            }
            String s = buffer.toString();
            showMessage("Records", s);
        }
        cursor.close();
    }

    @Override
    public void onLocationChanged(Location location) {
        float speedLimit = 14;
        float currentSpeed = location.getSpeed();
        textView.setText(String.valueOf(currentSpeed));//Se allagh topothesias grafoume sth thesh tou textView thn taxuthta ths suskeuhs mas.
        if(currentSpeed <= speedLimit) {
            getWindow().getDecorView().setBackgroundColor(Color.WHITE);
            textView.setTextColor(0xFF030B72);
            textView2.setText("");
            if(flag){
                //insertData(String.valueOf(longitudeArrayList.get(0)), String.valueOf(latitudeArrayList.get(0)), String.valueOf(speedArrayList.get(0)), "");
                insertData(String.valueOf(longitudeArrayList.get(0)), String.valueOf(latitudeArrayList.get(0)), String.valueOf(speedArrayList.get(0)));
                speedArrayList.clear();
                longitudeArrayList.clear();
                latitudeArrayList.clear();
                flag = false;
            }
        } else {
            flag = true;
            counter++;
            getWindow().getDecorView().setBackgroundColor(Color.RED);
            textView2.setText("SLOW DOWN!");
            speedArrayList.add(currentSpeed);
            longitudeArrayList.add(location.getLongitude());
            latitudeArrayList.add(location.getLatitude());
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
