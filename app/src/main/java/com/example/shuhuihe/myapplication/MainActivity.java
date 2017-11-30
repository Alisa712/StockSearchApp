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
    //private JSONArray refreshArr;
    RequestQueue queue;
    private ArrayList<Favorite> details;
    private String URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final AutoCompleteTextView searchStock = findViewById(R.id.autoCompleteTextView);
        final Button getQuote = (Button) findViewById(R.id.getQuoteButton);
        final Button clearQuote = (Button) findViewById(R.id.clearButton);
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
            Float changeInFloat = jobj.get("changeInFloat").getAsFloat();
            Float changePercentInFloat = jobj.get("changePercentInFloat").getAsFloat();
            long timestamp = jobj.get("timestamp").getAsLong();

            Favorite favItem = new Favorite(symbol,price,changeInFloat,changePercentInFloat,timestamp,change,up);
            favList.add(favItem);

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

        clearQuote.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText companySymbol = (EditText) findViewById(R.id.autoCompleteTextView);
                companySymbol.setText("");
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
        final SharedPreferences.Editor editor = sharedpref.edit();
        details = new ArrayList<Favorite>();
        final boolean[] finishedAll = {false};
        allEntries.remove("com.facebook.appevents.SourceApplicationInfo.callingApplicationPackage");
        allEntries.remove("com.facebook.appevents.SessionInfo.sessionStartTime");
        allEntries.remove("com.facebook.appevents.SessionInfo.interruptionCount");
        allEntries.remove("com.facebook.appevents.SourceApplicationInfo.openedByApplink");
        allEntries.remove("com.facebook.appevents.SessionInfo.sessionId");
        allEntries.remove("com.facebook.appevents.SessionInfo.sessionEndTime");
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {

            String info = entry.getValue().toString();
            JsonObject jobj = new Gson().fromJson(info, JsonObject.class);
            final String symbol = jobj.get("stockFav").getAsString();
            Log.d("SYMBOL", symbol);
            final long timestamp = jobj.get("timestamp").getAsLong();
            final Boolean finished[] = {false};
            Log.d("Before", "REQ");
            JsonObjectRequest jsonObjReq = new JsonObjectRequest
                    (Request.Method.GET, URL + symbol, null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        editor.clear();
                                        Log.d("INTO", "REQUEST");
                                        JSONArray refreshArr = new JSONArray();
                                        JSONObject tsDaily;
                                        JSONObject stockInfo = new JSONObject();
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

                                        stockInfo.put("stockFav", symbol);
                                        stockInfo.put("priceFav", lastPrice.toString());
                                        stockInfo.put("changeFav", changeDetail);
                                        stockInfo.put("isIncreasing", change>0);
                                        stockInfo.put("changeInFloat", String.format("%.2f", change));
                                        stockInfo.put("changePercentInFloat", String.format("%.2f", changePercent));
                                        stockInfo.put("timestamp", timestamp);

                                        String stockInfoStr = stockInfo.toString();
                                        Log.d("stockInfo", stockInfoStr);
                                        editor.putString(symbol, stockInfoStr);
                                        editor.apply();
                                        details.add(new Favorite(symbol,lastPrice,change,changePercent,timestamp,changeDetail,change>0));
                                        finished[0] = true;
                                        String t = "hi "+details.size();
                                        Log.d("detailsize", details.toString());
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

            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(jsonObjReq);
//            while (!finished[0]) {
//            }
            Log.d("stock", "CALLED");

        }
        String testsize = "hi " + allEntries.size();
        Log.d("allentrySzie", testsize);

    }

    private void resetList() {

        Log.d("INTO", "ALL");
        ListView favListview2 = findViewById(R.id.favorite_list);
        FavoriteAdapter favAdapter2 = new FavoriteAdapter(this, R.layout.detail_fav_layout, details);
        favListview2.setAdapter(favAdapter2);

    }

}

