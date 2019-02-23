package com.es3fny.ace.finder;

import java.util.Iterator;
import org.json.JSONObject;
import java.util.ArrayList;
import org.json.JSONException;
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

public class FirebaseDatabaseHandler {
    private MainActivity context;
    final private String DBURL = "https://finder-c7792.firebaseio.com/";

    interface FirebaseCallback{
        void afterGettingData(Object data);
    }

    public FirebaseDatabaseHandler(MainActivity context) {
        this.context = context;
    }

    public void getUserPassword(String userName, final FirebaseCallback callback){
        String stringRequest =  DBURL + "Users/" + userName + ".json";

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
                            callback.afterGettingData(response.getString("password"));
                        } catch (Exception ignore) {
                            callback.afterGettingData(null);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        callback.afterGettingData(null);
                    }
                });

        mRequestQueue.add(jsonObjectRequest);
    }

    public void getUserHistory(String userName, final FirebaseCallback callback){
        String stringRequest =  DBURL + "Users/" + userName + "/history.json";

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
                            Iterator<String> history = response.keys();
                            ArrayList<String> historyArray = new ArrayList<>();

                            while (history.hasNext()) {
                                try {
                                    String next = response.getJSONObject(history.next()).getString("entry");
                                    historyArray.add(next);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            callback.afterGettingData(historyArray);
                        } catch (Exception ignore) {
                            callback.afterGettingData(null);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        callback.afterGettingData(null);
                    }
                });

        mRequestQueue.add(jsonObjectRequest);
    }

    public void clearUserHistory(String userName){
        String stringRequest =  DBURL + "Users/" + userName + "/history.json";

        RequestQueue mRequestQueue;
        Cache cache = new DiskBasedCache(context.getCacheDir(), 1024 * 1024);
        Network network = new BasicNetwork(new HurlStack());
        mRequestQueue = new RequestQueue(cache, network);
        mRequestQueue.start();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.DELETE, stringRequest, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        mRequestQueue.add(jsonObjectRequest);
    }

    public void addToUserHistory(String userName, String entry){
        String stringRequest =  DBURL + "Users/" + userName + "/history.json";

        RequestQueue mRequestQueue;
        Cache cache = new DiskBasedCache(context.getCacheDir(), 1024 * 1024);
        Network network = new BasicNetwork(new HurlStack());
        mRequestQueue = new RequestQueue(cache, network);
        mRequestQueue.start();

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("entry", entry);
        } catch (JSONException ignore) {

        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, stringRequest, requestBody, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                    }
                });

        mRequestQueue.add(jsonObjectRequest);
    }

    public void addNewUser(String userName, String password){
        String stringRequest =  DBURL + "Users/" + userName + ".json";

        RequestQueue mRequestQueue;
        Cache cache = new DiskBasedCache(context.getCacheDir(), 1024 * 1024);
        Network network = new BasicNetwork(new HurlStack());
        mRequestQueue = new RequestQueue(cache, network);
        mRequestQueue.start();

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("password", password);
        } catch (JSONException ignore) {

        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.PUT, stringRequest, requestBody, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                    }
                });

        mRequestQueue.add(jsonObjectRequest);
    }
}
