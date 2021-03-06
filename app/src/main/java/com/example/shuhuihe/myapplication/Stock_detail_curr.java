package com.example.shuhuihe.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;


import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.share.model.ShareLinkContent;
import android.content.SharedPreferences;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.share.widget.ShareDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.Key;
import java.security.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;




public class Stock_detail_curr extends Fragment {

    private View rootview;
    private TextView indiTextView;

    private ImageView fbImageView;
    private ListView listview;
    //private ListView favListview;
    private WebView mWebView;
    private ProgressBar progBar;
    private ProgressBar tableBar;
    private boolean webFinished = false;

    private String symbol;
    private final String URL = "http://shuhuihe571hw8-env.us-east-2.elasticbeanstalk.com/stock/query?function=TIME_SERIES_DAILY&outputsize=full&symbol=";
    RequestQueue queue;
    private JSONObject timeSeriesDaily;
    private JSONArray jsonArray;
    private JSONArray refreshArr;

    private JSONObject stockInfo;
    private String currentIndi;
    private String option;
    private String urlFB;
    private String key;
    private ArrayList<Favorite> details;

    //private boolean isFavorited;
    private JSONObject stockDetails;
    //public static final String MyPREFERENCES = "MyPrefs";
    SharedPreferences sharedpref;
    //private Spinner changeChartView;

    private int positionSpinner = 0;
    private ImageView emptyStarImageView;
    private ImageView filledStarImageView;

    private TextView dataFailedMsg;
    private TextView chartFailedMsg;
    private TextView hisFailedMsg;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (rootview == null) {
            rootview = inflater.inflate(R.layout.fragment_stock_detail_current, container, false);
            listview = rootview.findViewById(R.id.tableList);
            fbImageView = rootview.findViewById(R.id.facebook);
            emptyStarImageView = rootview.findViewById(R.id.star);
            filledStarImageView = rootview.findViewById(R.id.filledStar);
            dataFailedMsg = rootview.findViewById(R.id.table_failed);
            chartFailedMsg = rootview.findViewById(R.id.chart_failed);
            //hisFailedMsg = rootview.findViewById(R.id.his_failed);

            dataFailedMsg.setVisibility(View.GONE);
            chartFailedMsg.setVisibility(View.GONE);
            //hisFailedMsg.setVisibility(View.GONE);


            fbImageView.setEnabled(false);
            emptyStarImageView.setEnabled(false);
            filledStarImageView.setEnabled(false);
            emptyStarImageView.setVisibility(View.VISIBLE);
            filledStarImageView.setVisibility(View.GONE);


            //listview.setVisibility(View.GONE);
            //indiTextView = rootview.findViewById(R.id.indi);
            //indiTextView.setVisibility(View.GONE);
            progBar = rootview.findViewById(R.id.detailProgress);
            tableBar = rootview.findViewById(R.id.tableBar);
            //favListview = rootview.findViewById(R.id.favorite_list);
            sharedpref = PreferenceManager.getDefaultSharedPreferences(getContext());
            queue = Volley.newRequestQueue(getContext());
            String symbolTemp = getActivity().getIntent().getExtras().getString("symbol");
            symbol = symbolTemp.split("-")[0].trim();
            requestStockData(symbol);

            currentIndi = "Price";
            Spinner spinner = (Spinner) rootview.findViewById(R.id.change_chart);
            //spinner.setVisibility(View.GONE);



            final List<String> spinnerList = new ArrayList<>();

            spinnerList.add("Price");
            spinnerList.add("SMA");
            spinnerList.add("EMA");
            spinnerList.add("STOCH");
            spinnerList.add("RSI");
            spinnerList.add("ADX");
            spinnerList.add("CCI");
            spinnerList.add("BBANDS");
            spinnerList.add("MACD");

            final Button changeButton = rootview.findViewById(R.id.change_indicator);
            //button.setVisibility(View.GONE);
            changeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (currentIndi != null) {
                        progBar.setVisibility(View.VISIBLE);
                        loadWebView(symbol, currentIndi);
                        changeButton.setEnabled(false);
                        changeButton.setTextColor(Color.GRAY);
                    }
                }
            });

            final ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, spinnerList) {
                @Override
                public boolean isEnabled(int position) {
                    if (position == positionSpinner) {
                        //changeButton.setClickable(false);
                        // Disable the second item from Spinner
                        return false;
                    } else {
                        //changeButton.setClickable(true);
                        return true;
                    }
                }
                @Override
                public View getDropDownView(int position, View convertView,
                                            ViewGroup parent) {
                    View view = super.getDropDownView(position, convertView, parent);
                    TextView tv = (TextView) view;
                    if ( position == positionSpinner) {
                        // Set the disable item text color
                        tv.setTextColor(Color.GRAY);
                    } else {
                        tv.setTextColor(Color.BLACK);
                    }
                    return view;
                }

            };
            spinner.setAdapter(spinnerAdapter);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    String selctedIndi = adapterView.getItemAtPosition(i).toString();

                    if (!currentIndi.equals(selctedIndi)) {
                        currentIndi = selctedIndi;
                        positionSpinner=i;
                        changeButton.setEnabled(true);
                        changeButton.setTextColor(Color.BLACK);
                        spinnerAdapter.notifyDataSetChanged();
                    } else {
                        changeButton.setEnabled(false);
                        changeButton.setTextColor(Color.GRAY);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            mWebView = rootview.findViewById(R.id.webview);
            //mWebView.setVisibility(View.GONE);
            loadWebView(symbol, currentIndi);
            //ImageView facebook = rootview.findViewById(R.id.facebook);
            fbImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getChartURL(option);
                }
            });

            //ImageView star = rootview.findViewById(R.id.star);
            emptyStarImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        changeFav(symbol);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            filledStarImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        changeFav(symbol);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        return rootview;
    }

    private void changeFav(String symbol) throws JSONException {
        if (sharedpref.contains(symbol)) {
            filledStarImageView.setVisibility(View.GONE);
            emptyStarImageView.setVisibility(View.VISIBLE);
            SharedPreferences.Editor editor = sharedpref.edit();
            editor.remove(symbol);
            Log.d("REMOVE", "REMOVED!");
            editor.apply();
        } else {
            emptyStarImageView.setVisibility(View.GONE);
            filledStarImageView.setVisibility(View.VISIBLE);
            addThisStock(symbol);
        }
    }

    private void addThisStock(final String symbol) throws JSONException {
        //refreshArr = new JSONArray();
        Date date = new Date();
        final long timestamp = date.getTime();
        stockInfo.put("timestamp", timestamp);
        SharedPreferences.Editor editor = sharedpref.edit();
        String stockInfoStr = stockInfo.toString();
        Log.d("stockInfo", stockInfoStr);
        editor.putString(symbol, stockInfoStr);
        editor.apply();
        Log.d("ADDED", " IN LOCAL");
    }

    private void getChartURL(String option) {

        final String exportUrl = "http://export.highcharts.com/";
        final String optionStr = option;


        StringRequest stringRequest = new StringRequest
                (Request.Method.POST, exportUrl,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {


                                urlFB = response;
                                Log.d("fblink", exportUrl + urlFB);
                                ShareDialog shareDialog = new ShareDialog(getActivity());

                                ShareLinkContent shareLinkContent = new ShareLinkContent.Builder()
                                        .setContentUrl(Uri.parse(exportUrl + urlFB))
                                        .build();
                                shareDialog.show(shareLinkContent);

                            }

                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //mTextView.setText("That didn't work!");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> paramaters = new HashMap<>();
                paramaters.put("async", "true");
                paramaters.put("type", "image/png");
                paramaters.put("options", optionStr);

                return paramaters;
            }
        };

        queue.add(stringRequest);
    }


    @SuppressLint("SetJavaScriptEnabled")
    private void loadWebView(final String symbol, String currentIndi) {
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView.addJavascriptInterface(new JavaScriptInterfaceClass(), "shuhuiJS");

        mWebView.loadUrl("file:///android_asset/huihui.html?" + symbol.trim() + "&" + currentIndi);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });
    }



    private void requestStockData(String symbol) {
        final String stockSymbol = symbol;
        final Boolean finished[] = {false};
        jsonArray = new JSONArray();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest
                (Request.Method.GET, URL + stockSymbol, null,
                        new Response.Listener<JSONObject>() {
                            @SuppressLint("DefaultLocale")
                            public void onResponse(JSONObject response) {
                                try {
                                    //SharedPreferences.Editor editor = sharedpref.edit();
                                    stockInfo = new JSONObject();

                                    Float lastPrice;
                                    Float previousDatePrice;
                                    Float change;
                                    Float changePercent;
                                    String timestamp;
                                    String refreshedTime;
                                    String closedTime = "16:00:00";
                                    Float open;
                                    Float close;
                                    Float low;
                                    Float high;
                                    String daysRange;
                                    String volume;
                                    String changeDetail;

//                                    if (response == null) {
//                                        tableBar.setVisibility(View.GONE);
//                                        dataFailedMsg.setVisibility(View.VISIBLE);
//                                        return;
//                                    }

                                    timeSeriesDaily = response.getJSONObject("Time Series (Daily)");
                                    refreshedTime = response.getJSONObject("Meta Data").getString("3. Last Refreshed");
                                    String currentDate = refreshedTime.substring(0, 10);

                                    Iterator dates = timeSeriesDaily.keys();

                                    while (dates.hasNext()) {
                                        String date = (String) dates.next();
                                        jsonArray.put(timeSeriesDaily.get(date));
                                    }

                                    lastPrice = Float.parseFloat(jsonArray.getJSONObject(0).getString("4. close"));
                                    previousDatePrice = Float.parseFloat(jsonArray.getJSONObject(1).getString("4. close"));
                                    change = lastPrice - previousDatePrice;
                                    changePercent = change / previousDatePrice * 100;
                                    changeDetail = String.format("%.2f", change) + "(" + String.format("%.2f", changePercent) + "%)";

                                    open = Float.parseFloat(jsonArray.getJSONObject(0).getString("1. open"));

                                    if (refreshedTime.length() == 10 || refreshedTime.contains(closedTime)) {
                                        timestamp = currentDate + " " + closedTime + " EST";
                                        close = Float.parseFloat(jsonArray.getJSONObject(0).getString("4. close"));
                                    } else {
                                        timestamp = currentDate + " EST"; //not closed
                                        close = Float.parseFloat(jsonArray.getJSONObject(1).getString("4. close"));
                                    }

                                    volume = jsonArray.getJSONObject(0).getString("5. volume");
                                    low = Float.parseFloat(jsonArray.getJSONObject(0).getString("3. low"));
                                    high = Float.parseFloat(jsonArray.getJSONObject(0).getString("2. high"));

                                    daysRange = String.format("%.2f", low) + " - " + String.format("%.2f", high);

                                    stockInfo.put("stockFav", stockSymbol);
                                    stockInfo.put("priceFav", lastPrice.toString());
                                    stockInfo.put("changeFav", changeDetail);
                                    stockInfo.put("isIncreasing", change > 0);
                                    stockInfo.put("changeInFloat", String.format("%.2f", change));
                                    stockInfo.put("changePercentInFloat", String.format("%.2f", changePercent));
                                    //stockInfo.put("timestamp", timestampRealTime);

                                    ArrayList<Detail> details = new ArrayList<>();

                                    Detail symbol_detail = new Detail("Stock Symbol", stockSymbol);
                                    details.add(symbol_detail);

                                    Detail last_price = new Detail("Last Price", String.format("%.2f", lastPrice));
                                    details.add(last_price);

                                    Detail change_data = new Detail("Change", changeDetail, change > 0);
                                    details.add(change_data);

                                    Detail time_stamp = new Detail("Timestamp", timestamp);
                                    details.add(time_stamp);

                                    Detail open_data = new Detail("Open", String.format("%.2f", open));
                                    details.add(open_data);

                                    Detail close_data = new Detail("Close", String.format("%.2f", close));
                                    details.add(close_data);

                                    Detail days_range = new Detail("Day's Range", daysRange);
                                    details.add(days_range);

                                    Detail volume_data = new Detail("Volume", volume);
                                    details.add(volume_data);

                                    DetailApater detailApater = new DetailApater(getContext(), R.layout.detail_item_layout, details);
                                    listview.setAdapter(detailApater);

                                    finished[0] = true;
                                    tableBar.setVisibility(View.GONE);

                                    emptyStarImageView.setEnabled(true);
                                    filledStarImageView.setEnabled(true);

                                    //listview.setVisibility(View.VISIBLE);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    tableBar.setVisibility(View.GONE);
                                    dataFailedMsg.setVisibility(View.VISIBLE);
                                    //hisFailedMsg.setVisibility(View.VISIBLE);
                                    finished[0] = true;
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                //progBar.setVisibility(View.GONE);
                                tableBar.setVisibility(View.GONE);
                                dataFailedMsg.setVisibility(View.VISIBLE);



                                //error.printStackTrace();
                            }
                        });
        //while (!finished[0]) {}
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(1000, 5, 1));
        queue.add(jsonObjReq);

        if (sharedpref.contains(symbol)) {
            emptyStarImageView.setVisibility(View.GONE);
            filledStarImageView.setVisibility(View.VISIBLE);
        }

    }

    final class JavaScriptInterfaceClass {
        @JavascriptInterface
        public void sendToAndroid(String s) {
            option = s;
            //webFinished = finishedWebView;
            //Log.e("js", option);
        }

        @JavascriptInterface
        public void completeWeb() {
            //Toast.makeText(getContext(),"js called complete",Toast.LENGTH_SHORT).show();
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //mWebView.setVisibility(View.VISIBLE);
                    progBar.setVisibility(View.GONE);
                    fbImageView.setEnabled(true);
                    //stuff that updates ui
                }
            });
        }

        @JavascriptInterface
        public void chartError() {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progBar.setVisibility(View.GONE);
                    chartFailedMsg.setVisibility(View.VISIBLE);
                }
            });
        }

    }
}
