package com.es3fny.ace.finder;

import android.app.Activity;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SpeechToTextClient {

    private String URL = "https://api.assemblyai.com/transcript";
    private String appid = "c34e0f8c221a4794a02627f2559114d9";

    private Activity context;
    interface IDcallback{
        void getid(int id);
    }
    interface Textcallback{
        void getText(String text);
    }

    public SpeechToTextClient(Activity context) {
        this.context = context;
    }

    void RequestTranscription(String audioUrl, final IDcallback iDcallback){
        RequestQueue mRequestQueue;

        Cache cache = new DiskBasedCache(context.getCacheDir(), 1024 * 1024);
        Network network = new BasicNetwork(new HurlStack());
        mRequestQueue = new RequestQueue(cache, network);
        mRequestQueue.start();

        JSONObject postParams = new JSONObject();
        try{
            postParams.put("audio_src_url", audioUrl);
        } catch (Exception ignore){}

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, URL, postParams, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            iDcallback.getid(response.getJSONObject("transcript").getInt("id"));
                        }catch (Exception e){}
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Toast.makeText(context,error.toString(),Toast.LENGTH_LONG).show();
                        iDcallback.getid(-1);
                    }
                }){
            @Override
            public Map getHeaders(){
                HashMap headers = new HashMap();
                headers.put("authorization", appid);
                return headers;
            }
        };
        mRequestQueue.add(jsonObjectRequest);
    }

    void getTranscription(int id, final Textcallback textcallback){
        RequestQueue mRequestQueue;

        Cache cache = new DiskBasedCache(context.getCacheDir(), 1024 * 1024);
        Network network = new BasicNetwork(new HurlStack());
        mRequestQueue = new RequestQueue(cache, network);
        mRequestQueue.start();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, URL + "/" + id, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String x = response.getJSONObject("transcript").getString("status");
                            Toast.makeText(context, x, Toast.LENGTH_SHORT).show();
                            textcallback.getText(response.getJSONObject("transcript").getString("text"));
                        }catch (Exception e){}
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        textcallback.getText(null);
                    }
                }){
            @Override
            public Map getHeaders(){
                HashMap headers = new HashMap();
                headers.put("authorization", appid);
                return headers;
            }
        };
        mRequestQueue.add(jsonObjectRequest);
    }

}
