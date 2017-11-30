package com.example.shuhuihe.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

//import static com.example.shuhuihe.myapplication.Favorite.defaultComp;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> company_list = new ArrayList<>();
    private SharedPreferences sharedpref;
    //private JSONArray refreshArr;
    RequestQueue queue;
    private ArrayList<Favorite> details;
    private String URL;
    private String currentSort;
    private String currentOrder;
    private int positionInSortSpinner;
    private int positionInOrderSpinner;
    private ProgressBar refreshBar;
    private ProgressBar autoBar;
    private Spinner spinnerSort;
    private Spinner spinnerOrder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final AutoCompleteTextView searchStock = findViewById(R.id.autoCompleteTextView);
        final Button getQuote = (Button) findViewById(R.id.getQuoteButton);
        final Button clearQuote = (Button) findViewById(R.id.clearButton);
        final ImageView refresh = findViewById(R.id.refresh);
        autoBar = findViewById(R.id.autoBar);
        autoBar.setVisibility(View.GONE);

        refreshBar = findViewById(R.id.refreshBar);
        refreshBar.setVisibility(View.GONE);

        AutoCompleteAdaptor autoCompleteAdaptor = new AutoCompleteAdaptor(this, R.layout.list_company, company_list);
        searchStock.setAdapter(autoCompleteAdaptor);
        //autoBar.setVisibility(View.GONE);
        searchStock.setThreshold(1);

        sharedpref = PreferenceManager.getDefaultSharedPreferences(this);
        queue = Volley.newRequestQueue(this);
        final ArrayList<Favorite> favList = new ArrayList<>();
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

            Favorite favItem = new Favorite(symbol, price, changeInFloat, changePercentInFloat, timestamp, change, up);
            favList.add(favItem);

        }
        currentSort = "Sort by";
        currentOrder = "Order";
        spinnerSort = (Spinner) findViewById(R.id.spinner1);
        spinnerOrder = (Spinner) findViewById(R.id.spinner2);

        Collections.sort(favList, Favorite.defaultComp);
        final List<String> spinnerList = new ArrayList<>();

        spinnerList.add("Sort By");
        spinnerList.add("Default");
        spinnerList.add("Symbol");
        spinnerList.add("Price");
        spinnerList.add("Change");
        spinnerList.add("Change Percent");

        final List<String> spinnerListOrder = new ArrayList<>();

        spinnerListOrder.add("Order");
        spinnerListOrder.add("Ascending");
        spinnerListOrder.add("Descending");

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, spinnerList) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 0 || position == positionInSortSpinner) {
                    // Disable the second item from Spinner
                    return false;
                } else {
                    return true;
                }
            }
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0 || position == positionInSortSpinner) {
                    // Set the disable item text color
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        ArrayAdapter<String> spinnerAdapterOrder = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, spinnerListOrder) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 0 || position == positionInOrderSpinner) {
                    // Disable the second item from Spinner
                    return false;
                } else {
                    return true;
                }
            }
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0 || position == positionInOrderSpinner) {
                    // Set the disable item text color
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        spinnerSort.setAdapter(spinnerAdapter);
        spinnerOrder.setAdapter(spinnerAdapterOrder);

        spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selctedSort = adapterView.getItemAtPosition(i).toString();
                if (!currentSort.equals(selctedSort)) {
                    currentSort = selctedSort;
                    doSort(currentSort, currentOrder, spinnerSort, spinnerOrder, favList);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        spinnerOrder.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selctedOrder = adapterView.getItemAtPosition(i).toString();
                if (!currentOrder.equals(selctedOrder)) {
                    currentOrder = selctedOrder;
                    doSort(currentSort, currentOrder, spinnerSort, spinnerOrder, favList);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

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

        favListview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                PopupMenu pop = new PopupMenu(MainActivity.this, view, Gravity.RIGHT);
                MenuInflater menuInflater = getMenuInflater();
                menuInflater.inflate(R.menu.menu_popup, pop.getMenu());

                pop.getMenu().findItem(R.id.title_item).setEnabled(false);
                pop.show();
                pop.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.no:
                                break;
                            case R.id.yes:
                                SharedPreferences.Editor editor = sharedpref.edit();
                                String symbolRemove = favList.get(i).getSymbol();
                                editor.remove(symbolRemove);
                                Log.d("REMOVE", "REMOVED!");
                                editor.apply();
                                favList.remove(i);
                                ListView favListview = findViewById(R.id.favorite_list);
                                FavoriteAdapter favAdapter = new FavoriteAdapter(MainActivity.this, R.layout.detail_fav_layout, favList);
                                favListview.setAdapter(favAdapter);
                                break;
                            default:
                                break;
                        }
                        return true;
                    }
                });
                return true;
            }
        });
    }

    private void doSort(String currentSort, String currentOrder, Spinner spinnerSort, Spinner spinnerOrder, ArrayList<Favorite> favList) {
        switch (currentSort) {
            case "Sort By":
                positionInSortSpinner = 0;
                //spinnerOrder.setEnabled(false);
                positionInOrderSpinner = 0;
                Collections.sort(favList, Favorite.defaultComp);
                break;
            case "Default":
                positionInSortSpinner = 1;
                spinnerOrder.setEnabled(false);
                Collections.sort(favList, Favorite.defaultComp);
                break;
            case "Symbol":
                positionInSortSpinner = 2;
                spinnerOrder.setEnabled(true);
                if (currentOrder.equals("Ascending") || currentOrder.equals("Order")) {
                    if (currentOrder.equals("Ascending")) {
                        positionInOrderSpinner = 1;
                    }
                    Collections.sort(favList, Favorite.symbolComp);
                } else {
                    positionInOrderSpinner = 2;
                    Collections.sort(favList, Favorite.symbolCompReverse);
                }
                break;
            case "Price":
                positionInSortSpinner = 3;
                spinnerOrder.setEnabled(true);
                if (currentOrder.equals("Ascending") || currentOrder.equals("Order")) {
                    if (currentOrder.equals("Ascending")) {
                        positionInOrderSpinner = 1;
                    }

                    Collections.sort(favList, Favorite.priceComp);
                } else {
                    positionInOrderSpinner = 2;
                    Collections.sort(favList, Favorite.priceCompReverse);
                }
                break;
            case "Change":
                positionInSortSpinner = 4;
                spinnerOrder.setEnabled(true);
                if (currentOrder.equals("Ascending") || currentOrder.equals("Order")) {
                    if (currentOrder.equals("Ascending")) {
                        positionInOrderSpinner = 1;
                    }
                    Collections.sort(favList, Favorite.changeComp);
                } else {
                    positionInOrderSpinner = 2;
                    Collections.sort(favList, Favorite.changeCompReverse);
                }
                break;

            case "Change Percent":
                positionInSortSpinner = 5;
                spinnerOrder.setEnabled(true);
                if (currentOrder.equals("Ascending") || currentOrder.equals("Order")) {
                    if (currentOrder.equals("Ascending")) {
                        positionInOrderSpinner = 1;
                    }
                    Collections.sort(favList, Favorite.changePercentComp);
                } else {
                    positionInOrderSpinner = 2;
                    Collections.sort(favList, Favorite.changePercentCompReverse);
                }
                break;
        }
        ListView favListviewSort = findViewById(R.id.favorite_list);
        FavoriteAdapter favAdapterSort = new FavoriteAdapter(this, R.layout.detail_fav_layout, favList);
        Log.d("size of fav list", " is "+favList.size());
        favListviewSort.setAdapter(favAdapterSort);
    }

    private void refreshAllStock() {
        refreshBar.setVisibility(View.VISIBLE);
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
                                        // Here the problem
                                        // editor.clear();
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
                                        stockInfo.put("priceFav", String.format("%.2f", lastPrice));
                                        stockInfo.put("changeFav", changeDetail);
                                        stockInfo.put("isIncreasing", change > 0);
                                        stockInfo.put("changeInFloat", String.format("%.2f", change));
                                        stockInfo.put("changePercentInFloat", String.format("%.2f", changePercent));
                                        stockInfo.put("timestamp", timestamp);

                                        String stockInfoStr = stockInfo.toString();
                                        Log.d("stockInfo", stockInfoStr);
                                        editor.putString(symbol, stockInfoStr);
                                        editor.apply();
                                        details.add(new Favorite(symbol, lastPrice, change, changePercent, timestamp, changeDetail, change > 0));
                                        finished[0] = true;
                                        String t = "hi " + details.size();
                                        Log.d("detailsize", details.toString());
                                        Log.d("finised", finished[0].toString());
                                        if (details.size() == allEntries.size()) {
                                            finishedAll[0] = true;
                                            doSort(currentSort, currentOrder, spinnerSort, spinnerOrder, details);
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

            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(1000, 5, 1));
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
        //doSort("Default", "Ascending");
        ListView favListview2 = findViewById(R.id.favorite_list);
        FavoriteAdapter favAdapter2 = new FavoriteAdapter(this, R.layout.detail_fav_layout, details);
        refreshBar.setVisibility(View.GONE);
        favListview2.setAdapter(favAdapter2);
    }

}

