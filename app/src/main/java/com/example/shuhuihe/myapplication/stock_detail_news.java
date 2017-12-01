package com.example.shuhuihe.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

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



public class Stock_detail_news extends Fragment {


    private View rootview;
    private ListView listview;
    private TextView newsFailedMsg;
    private WebView mWebView;
    private String symbol;
    private final String URL = "http://shuhuihe571hw8-env.us-east-2.elasticbeanstalk.com/stock/news?SYMBOL=";
    RequestQueue queue;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (rootview == null) {
            rootview = inflater.inflate(R.layout.fragment_stock_detail_news, container, false);
            listview = rootview.findViewById(R.id.newsList);
            newsFailedMsg = rootview.findViewById(R.id.news_failed);



            queue = Volley.newRequestQueue(getContext());
            String symbolTemp = getActivity().getIntent().getExtras().getString("symbol");
            symbol = symbolTemp.split("-")[0].trim();

            requestNewsData(symbol);
        }
        return rootview;
    }

    private void requestNewsData(String symbol) {
        final String stockSymbol = symbol;

        JsonArrayRequest jsonArrReq = new JsonArrayRequest
                (Request.Method.GET, URL + stockSymbol, null,
                        new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                final ArrayList<News> newsArrayList = new ArrayList<>();

                                for (int i = 0; i < response.length(); i++) {
                                    try {
                                        String title;
                                        String link;
                                        String author;
                                        String date;


                                        JSONObject newsItem = response.getJSONObject(i);
                                        title = newsItem.getString("title");
                                        link = newsItem.getString("link");
                                        author = "Author: " + newsItem.getString("author_name");
                                        date = "Date: " + newsItem.getString("pubDate").substring(0, 26) + " PDT";


                                        News news_detail = new News(title, link, author, date);
                                        newsArrayList.add(news_detail);


                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                NewsAdapter newsAdapter = new NewsAdapter(getContext(), R.layout.detail_news_layout, newsArrayList);
                                listview.setAdapter(newsAdapter);
                                listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                       News detail_item = newsArrayList.get(i);
                                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(detail_item.getLink()));
                                        startActivity(intent);
                                    }
                                });
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                error.printStackTrace();
                                newsFailedMsg.setVisibility(View.VISIBLE);
                            }
                        });
        queue.add(jsonArrReq);
    }
}
