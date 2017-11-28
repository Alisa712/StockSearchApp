package com.example.shuhuihe.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.zxing.datamatrix.encoder.SymbolShapeHint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MainActivity extends AppCompatActivity {


    private ArrayList<String> company_list = new ArrayList<>();
    private SharedPreferences sharedpref;
    private JSONArray refreshArr;
    RequestQueue queue;
    private ArrayList<Favorite> details;
    private String URL;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final AutoCompleteTextView searchStock = findViewById(R.id.autoCompleteTextView);
        final Button getQuote = (Button) findViewById(R.id.getQuoteButton);
        final ImageView refresh = findViewById(R.id.refresh);

        AutoCompleteAdaptor autoCompleteAdaptor = new AutoCompleteAdaptor(this, R.layout.list_company, company_list);
        searchStock.setAdapter(autoCompleteAdaptor);
        searchStock.setThreshold(1);

        sharedpref = PreferenceManager.getDefaultSharedPreferences(this);
        queue = Volley.newRequestQueue(this);
//        SharedPreferences.Editor editor = sharedpref.edit();
//        editor.clear();
//        editor.commit();

        ArrayList<Favorite> favList = new ArrayList<>();
        Map<String, ?> allEntries = sharedpref.getAll();
        allEntries.remove("com.facebook.appevents.SourceApplicationInfo.callingApplicationPackage");
        allEntries.remove("com.facebook.appevents.SessionInfo.sessionStartTime");
        allEntries.remove("com.facebook.appevents.SessionInfo.interruptionCount");
        allEntries.remove("com.facebook.appevents.SourceApplicationInfo.openedByApplink");
        allEntries.remove("com.facebook.appevents.SessionInfo.sessionId");
        allEntries.remove("com.facebook.appevents.SessionInfo.sessionEndTime");
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {

            String info = entry.getValue().toString();
            JsonObject jobj = new Gson().fromJson(info, JsonObject.class);
            String symbol = jobj.get("stockFav").getAsString();
            Float price = jobj.get("priceFav").getAsFloat();
            String change = jobj.get("changeFav").getAsString();
            boolean up = jobj.get("isIncreasing").getAsBoolean();

            Favorite favItem = new Favorite(symbol, price, change, up);
            favList.add(favItem);

//            Log.d("map symbols", symbol);
//            Log.d("ps", price.toString());
//            Log.d("ch", change.toString());
//            Log.d("b", up);
        }
        ListView favListview = findViewById(R.id.favorite_list);
        FavoriteAdapter favAdapter = new FavoriteAdapter(this, R.layout.detail_fav_layout, favList);
        favListview.setAdapter(favAdapter);

        getQuote.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText companySymbol = (EditText) findViewById(R.id.autoCompleteTextView);
                String symbol = companySymbol.getText().toString();
                if (!symbol.matches(".*[a-zA-z].*")) {
                    Toast.makeText(getApplicationContext(), "Please enter a stock name or symbol", Toast.LENGTH_SHORT).show();
                } else {
                    Intent activityChangeIntent = new Intent(MainActivity.this, StockDetailActivity.class);
                    activityChangeIntent.putExtra("symbol", symbol);
                    MainActivity.this.startActivity(activityChangeIntent);
                }
            }
        });

        refresh.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                refreshAllStock();
            }
        });
    }

    private void refreshAllStock() {
        URL = "http://shuhuihe571hw8-env.us-east-2.elasticbeanstalk.com/stock/query?function=TIME_SERIES_DAILY&outputsize=full&symbol=";
        final Map<String, ?> allEntries = sharedpref.getAll();
        refreshArr = new JSONArray();
        details = new ArrayList<Favorite>();
        final boolean[] finishedAll = {false};

        int i = 0;
        //Map<String, ?> allEntries = sharedpref.getAll();
        allEntries.remove("com.facebook.appevents.SourceApplicationInfo.callingApplicationPackage");
        allEntries.remove("com.facebook.appevents.SessionInfo.sessionStartTime");
        allEntries.remove("com.facebook.appevents.SessionInfo.interruptionCount");
        allEntries.remove("com.facebook.appevents.SourceApplicationInfo.openedByApplink");
        allEntries.remove("com.facebook.appevents.SessionInfo.sessionId");
        allEntries.remove("com.facebook.appevents.SessionInfo.sessionEndTime");
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            //int i = 0;

            String info = entry.getValue().toString();
            JsonObject jobj = new Gson().fromJson(info, JsonObject.class);
            final String symbol = jobj.get("stockFav").getAsString();
            Log.d("SYMBOL", symbol);




            final Boolean finished[] = {false};

            Log.d("Before", "REQ");
            JsonObjectRequest jsonObjReq = new JsonObjectRequest
                    (Request.Method.GET, URL + symbol, null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        Log.d("INTO", "REQUEST");
                                        JSONObject tsDaily;
                                        Float lastPrice;
                                        Float previousDatePrice;
                                        Float change;
                                        Float changePercent;
                                        String changeDetail;
                                        tsDaily = response.getJSONObject("Time Series (Daily)");
                                        Iterator dates = tsDaily.keys();
                                        while (dates.hasNext()) {
                                            String date = (String) dates.next();
                                            refreshArr.put(tsDaily.get(date));
                                        }
                                        lastPrice = Float.parseFloat(refreshArr.getJSONObject(0).getString("4. close"));
                                        previousDatePrice = Float.parseFloat(refreshArr.getJSONObject(1).getString("4. close"));
                                        change = lastPrice - previousDatePrice;
                                        changePercent = change / previousDatePrice * 100;
                                        changeDetail = String.format("%.2f", change) + "(" + String.format("%.2f", changePercent) + "%)";
                                        Favorite fav_detail = new Favorite(symbol, lastPrice, changeDetail, change > 0);
                                        details.add(fav_detail);
                                        finished[0] = true;
                                        String t = "hi "+details.size();
                                        Log.d("detailsize", t);
                                        Log.d("finised", finished[0].toString());
                                        if (details.size() == allEntries.size()) {
                                            finishedAll[0] = true;
                                            resetList();

                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        finished[0] = true;
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    error.printStackTrace();
                                }
                            });

            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(jsonObjReq);
//            while (!finished[0]) {
//            }
            i++;

            Log.d("stock", "CALLED");

        }
        String testsize = "hi "+allEntries.size();

        Log.d("allentrySzie", testsize);

    }

    private void resetList() {

            Log.d("INTO", "ALL");
            ListView favListview2 = findViewById(R.id.favorite_list);
            FavoriteAdapter favAdapter2 = new FavoriteAdapter(this, R.layout.detail_fav_layout, details);
            favListview2.setAdapter(favAdapter2);

    }

}

