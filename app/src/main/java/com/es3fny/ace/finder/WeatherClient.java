package com.es3fny.ace.finder;


import android.app.Activity;
import android.widget.Toast;

import org.json.JSONObject;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.JsonObjectRequest;

public class WeatherClient {
    private String URL = "https://api.openweathermap.org/data/2.5/";
    private String appid = "0fbf44b349ff113e3291e54c8acef773";
    interface weatherCallBack {
        void getData(String[]arr);
    }

    private Activity context;

    public WeatherClient(Activity context) {
        this.context = context;
    }

    public String[] getCurrentWeather(String cityName, final weatherCallBack weathercallback){
        String stringRequest =  URL + "weather?q=" + cityName + "&appid=" + appid + "&units=metric";
        final String[] tbr = new String[5];

        RequestQueue mRequestQueue;

        Cache cache = new DiskBasedCache(context.getCacheDir(), 1024 * 1024);
        Network network = new BasicNetwork(new HurlStack());
        mRequestQueue = new RequestQueue(cache, network);
        mRequestQueue.start();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, stringRequest, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            tbr[0] = response.getString("cod");
                            tbr[1] = response.getJSONObject("coord").getString("lon");
                            tbr[2] = response.getJSONObject("coord").getString("lat");
                            tbr[3] = response.getJSONObject("main").getString("temp");
                            tbr[4] = response.getJSONArray("weather").getJSONObject(0).getString("main");
                            weathercallback.getData(tbr);
                        } catch (Exception ignore) {
                            tbr[0] = "-1";
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        tbr[0] = "-1";
                        Toast.makeText(context,"",Toast.LENGTH_SHORT).show();
                    }
                });

        mRequestQueue.add(jsonObjectRequest);
        return tbr;
    }
}
