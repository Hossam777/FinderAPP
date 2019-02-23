package com.es3fny.ace.finder;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static String fileName = null;
    FirebaseDatabaseHandler firebaseDatabaseHandler;
    FirebaseHandler firebaseHandler;
    Button record,history,logout;
    EditText search;
    Handler handler;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        record = findViewById(R.id.record);
        history = findViewById(R.id.history);
        logout = findViewById(R.id.logout);
        search = findViewById(R.id.search);
        handler = new Handler();
        activity = this;

        firebaseDatabaseHandler = new FirebaseDatabaseHandler(this);
        //firebaseHandler = new FirebaseHandler();

        fileName = getExternalCacheDir().getAbsolutePath();
        fileName += "/audiorecordtest.m4a";

        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User.clearuser();
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                finish();
            }
        });
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(User.gethistory() == null) {
                    firebaseDatabaseHandler.getUserHistory(User.getUser(), new FirebaseDatabaseHandler.FirebaseCallback() {
                        @Override
                        public void afterGettingData(Object data) {
                            User.setHistory((ArrayList<String>)data);
                            String hisory = "";
                            for(int i = 0;i < User.gethistory().size();i++){
                                hisory +=  User.gethistory().get(i) + " \n ";
                            }
                            getdialoge(hisory).show();
                        }
                    });
                }else {
                    String hisory = "";
                    for (int i = 0; i < User.gethistory().size(); i++) {
                        hisory += User.gethistory().get(i) + " \n ";
                    }
                    getdialoge(hisory).show();
                }
            }
        });
    }

    Dialog getdialoge(String tmp){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(tmp)
                .setPositiveButton("Clear", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                        firebaseDatabaseHandler.clearUserHistory(User.getUser());
                    }
                })
                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted )
        {
            //finish();
        }

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

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
