package com.example.shuhuihe.myapplication;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import static java.lang.Integer.parseInt;


public class Stock_detail_curr extends Fragment {
    private View rootview;
    private ListView listview;
    private WebView mWebView;
    private String symbol;
    private final String URL = "http://shuhuihe571hw8-env.us-east-2.elasticbeanstalk.com/stock/query?function=TIME_SERIES_DAILY&outputsize=full&symbol=";
    RequestQueue queue;
    private JSONObject timeSeriesDaily;
    private JSONArray jsonArray;
    private String currentIndi = "Default";


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
            queue = Volley.newRequestQueue(getContext());
            String symbolTemp = getActivity().getIntent().getExtras().getString("symbol");
            symbol = symbolTemp.split("-")[0];
            //Log.e("stock name",symbol);
            requestStockData(symbol);

            Spinner spinner = (Spinner) rootview.findViewById(R.id.change_chart);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    String selctedIndi = adapterView.getItemAtPosition(i).toString();
                    Log.e("crrindi", currentIndi);
                    Log.e("selectindi", selctedIndi);
                    if (!currentIndi.equals(selctedIndi)) {
                        currentIndi = selctedIndi;
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            mWebView = rootview.findViewById(R.id.webview);
            Button button = rootview.findViewById(R.id.change_indicator);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (currentIndi != null) {
                        loadWebView(symbol, currentIndi);
                    }

                }
            });

            loadWebView(symbol, currentIndi);


        }
        return rootview;
    }

    private void loadWebView(final String symbol, String currentIndi) {
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);

        mWebView.loadUrl("file:///android_asset/huihui.html?"+symbol+"&"+currentIndi);
        //mWebView.addJavascriptInterface(new IJavascriptHandler(), "chartJS");
//        mWebView.setWebViewClient(new WebViewClient() {
//            @Override
//            public void onPageFinished(WebView view, String url) {
//                super.onPageFinished(view, url);
//                mWebView.evaluateJavascript("(function(){loadPrice(\""+ symbol +"\");})();", new ValueCallback<String>() {
//                    @Override
//                    public void onReceiveValue(String s) {
//                        Log.i("LoadWebView",s);
//                    }
//                });
//            }
//        });
    }

    private void requestStockData(String symbol) {
        final String stockSymbol = symbol;
        final Boolean finished[] = {false};
        jsonArray = new JSONArray();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest
                (Request.Method.GET, URL+stockSymbol, null,
                        new Response.Listener<JSONObject>() {
                            @SuppressLint("DefaultLocale")
                            public void onResponse(JSONObject response) {
                                try {
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

                                    timeSeriesDaily = response.getJSONObject("Time Series (Daily)");
                                    refreshedTime = response.getJSONObject("Meta Data").getString("3. Last Refreshed");
                                    String currentDate = refreshedTime.substring(0, 10);

                                    //Log.e("time", timeSeriesDaily.toString());
                                    Iterator dates = timeSeriesDaily.keys();
                                    while (dates.hasNext()) {
                                        String date = (String) dates.next();
                                        jsonArray.put(timeSeriesDaily.get(date));
                                    }


                                    //Log.e("jsonarr", jsonArray.toString());

                                    lastPrice = Float.parseFloat(jsonArray.getJSONObject(0).getString("4. close"));
                                    previousDatePrice = Float.parseFloat(jsonArray.getJSONObject(1).getString("4. close"));
                                    change = lastPrice - previousDatePrice;
                                    changePercent = change / previousDatePrice * 100;
                                    changeDetail = String.format("%.2f", change)+"("+String.format("%.2f", changePercent)+"%)";

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


                                    ArrayList<Detail> details = new ArrayList<>();

                                    Detail symbol_detail = new Detail("Stock Symbol",stockSymbol);
                                    details.add(symbol_detail);

                                    Detail last_price = new Detail("Last Price",String.format("%.2f", lastPrice));
                                    details.add(last_price);

                                    Detail change_data = new Detail("Change",changeDetail,change>0);
                                    details.add(change_data);

                                    Detail time_stamp = new Detail("Timestamp",timestamp);
                                    details.add(time_stamp);

                                    Detail open_data = new Detail("Open",String.format("%.2f", open));
                                    details.add(open_data);

                                    Detail close_data = new Detail("Close",String.format("%.2f", close));
                                    details.add(close_data);

                                    Detail days_range = new Detail("Day's Range",daysRange);
                                    details.add(days_range);

                                    Detail volume_data = new Detail("Volume",volume);
                                    details.add(volume_data);

                                    DetailApater detailApater = new DetailApater(getContext(),R.layout.detail_item_layout,details);
                                    listview.setAdapter(detailApater);

                                    finished[0] = true;
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

        //while (!finished[0]) {}

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(5000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonObjReq);
    }

}
