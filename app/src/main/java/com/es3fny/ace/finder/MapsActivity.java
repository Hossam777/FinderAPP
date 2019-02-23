package com.es3fny.ace.finder;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static String fileName = null;
    FirebaseDatabaseHandler firebaseDatabaseHandler;
    FirebaseHandler firebaseHandler;
    Button record,history,logout;
    EditText search;
    TextView weatherdata;
    private ProgressBar spinner;
    Handler handler;
    WeatherClient weatherClient;
    VoiceRecorder voiceRecorder;
    Activity activity;
    String recordLink;
    SpeechToTextClient speechToTextClient;

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
        spinner = (ProgressBar)findViewById(R.id.progressBar1);
        weatherdata = findViewById(R.id.weatherdata);
        handler = new Handler();
        activity = this;

        weatherClient = new WeatherClient(this);
        firebaseDatabaseHandler = new FirebaseDatabaseHandler(this);
        firebaseHandler = new FirebaseHandler();
        voiceRecorder = new VoiceRecorder();
        speechToTextClient = new SpeechToTextClient(this);

        fileName = getExternalCacheDir().getAbsolutePath();
        fileName += "/audiorecordtest.m4a";

        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
        firebaseDatabaseHandler.getUserHistory(User.getUser(), new FirebaseDatabaseHandler.FirebaseCallback() {
            @Override
            public void afterGettingData(Object data) {
                User.setHistory((ArrayList<String>)data);
            }
        });

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
                String hisory = "";
                for (int i = 0; i < User.gethistory().size(); i++) {
                    hisory += User.gethistory().get(i) + " \n ";
                 }
                 showdialoge(hisory, new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialog, int which) {
                         firebaseDatabaseHandler.clearUserHistory(User.getUser());
                     }
                 }, new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialog, int which) {

                     }
                 }, "Clear", "Close");
            }

        });
        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    if(!AppStatus.getInstance(activity).isOnline())
                    {
                        Toast.makeText(activity, "No Internet Connection", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                   get_and_show_weather();
                }
                return false;
            }
        });
        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!AppStatus.getInstance(activity).isOnline())
                {
                    Toast.makeText(activity, "No Internet Connection", Toast.LENGTH_SHORT).show();
                    return;
                }
                startRecording();
            }
        });
    }

    void get_and_show_weather(){
        weatherClient.getCurrentWeather(search.getText().toString(), new WeatherClient.weatherCallBack() {
            @Override
            public void getData(String[] arr) {
                if(arr[0].equals("200")){
                    LatLng city = new LatLng(Double.parseDouble(arr[2]), Double.parseDouble(arr[1]));
                    mMap.addMarker(new MarkerOptions().position(city).title(search.getText().toString()));
                    //mMap.moveCamera(CameraUpdateFactory.newLatLng(city));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(city, 15f));
                    weatherdata.setText(Html.fromHtml(arr[3] + " C<br>" + arr[4]));
                    User.add_to_history(search.getText().toString());
                    firebaseDatabaseHandler.addToUserHistory(User.getUser(),search.getText().toString());
                }else if(arr[0].equals("404")){
                    Toast.makeText(activity, "Wrong City Name", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    void showdialoge(String tmp, DialogInterface.OnClickListener dialogInterface1, DialogInterface.OnClickListener dialogInterface2, String text1, String text2){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(tmp)
                .setPositiveButton(text1,dialogInterface1)
                .setNegativeButton(text2, dialogInterface2);
        // Create the AlertDialog object and return it
        builder.create().show();
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
    public void startRecording() {
        record.setBackground(getResources().getDrawable(R.drawable.custom_btn_bg2));
        record.setEnabled(false);
        if(!permissionToRecordAccepted)
        {
            Toast.makeText(this, "please grant record permission", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
            return;
        }
        voiceRecorder.recording(fileName);
        Thread tmp = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                    voiceRecorder.stopRecording();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            record.setBackground(getResources().getDrawable(R.drawable.custom_btn_bg));
                            spinner.setVisibility(View.VISIBLE);
                        }
                    });
                    firebaseHandler.UploadRecord(Uri.fromFile(new File(fileName)), new FirebaseHandler.FirebaseCallback() {
                        @Override
                        public void get_download_link(String link) {
                            recordLink = link;
                            newThread().start();
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        tmp.start();
    }

    Thread newThread(){
        return new Thread(new Runnable() {
            @Override
            public void run() {
                speechToTextClient.RequestTranscription(recordLink
                        , new SpeechToTextClient.IDcallback() {
                            @Override
                            public void getid(int id) {
                                try {
                                    afterGettingId(id, 3000);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
            }
        });
    }

    void afterGettingId(final int id, int getDelay) throws Exception{
        Thread.sleep(getDelay);
        speechToTextClient.getTranscription(id, new SpeechToTextClient.Textcallback() {
            @Override
            public void getText(final String text) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(text.equals("null")){
                            try {
                                afterGettingId(id, 1000);
                                return;
                            } catch (Exception e) {

                            }
                        }
                        showdialoge("Did you mean : \n" + text, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                search.setText(text);
                                get_and_show_weather();
                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        },"Yes", "No");
                        record.setEnabled(true);
                        spinner.setVisibility(View.GONE);
                    }
                });
            }
        });
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
    }
}
