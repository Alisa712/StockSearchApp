package com.example.shuhuihe.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * adapter for AutoCompleteTextView
 */

public class AutoCompleteAdaptor extends ArrayAdapter<String> {
    private List<String> companyList;
    private final String URL = "http://shuhuihe571hw8-env.us-east-2.elasticbeanstalk.com/autocomplete?input=";
    RequestQueue queue = Volley.newRequestQueue(getContext());
    //private ProgressBar autoBar;


    public AutoCompleteAdaptor(Context context, int textViewResourceId, List<String> companyList) {
        super(context, textViewResourceId);
        this.companyList = companyList;
    }

    @Override
    public int getCount() {
        return companyList.size();
    }

    @Override
    public String getItem(int index) {
        return companyList.get(index);
    }

    @Override
    public Filter getFilter() {
        Filter companyListFilter = new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults filterResults = new FilterResults();
                // Boolean to control complete
                final Boolean finished[] = {false};
                if (constraint != null) {
                    //autoBar.setVisibility(View.VISIBLE);

                    JsonArrayRequest jsArrReq = new JsonArrayRequest
                            (Request.Method.GET, URL+constraint.toString(), null,
                                    new Response.Listener<JSONArray>() {
                                        @Override
                                        public void onResponse(JSONArray response) {
                                            try {
                                                ArrayList<String> autoCompleteList = new ArrayList<>();
                                                for (int i=0; i<Math.min(response.length(), 5); i++) {
                                                    JSONObject jsObj = response.getJSONObject(i);
                                                    String symbol = jsObj.getString("Symbol");
                                                    String name = jsObj.getString("Name");
                                                    String exchange = jsObj.getString("Exchange");
                                                    autoCompleteList.add(symbol+" - "+name+" ("+exchange+")");
                                                }
                                                companyList = autoCompleteList;
                                                filterResults.values = companyList;
                                                filterResults.count = companyList.size();
                                                finished[0] = true;
                                                //autoBar.setVisibility(View.GONE);
                                            } catch (Exception e) {
                                                finished[0] = true;
                                                e.printStackTrace();
                                            }
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            error.printStackTrace();
                                        }
                                    });

                    queue.add(jsArrReq);

                } else {
                    finished[0] = true;
                }
                while (!finished[0]) {}
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return companyListFilter;
    }


    public View getView(int position, View convertView, ViewGroup parent) {

        // assign the view we are converting to a local variable
        View v = convertView;

        // first check to see if the view is null. if so, we have to inflate it.
        // to inflate it basically means to render, or show, the view.
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.list_company, null);
        }

        String company = companyList.get(position);
        //autoBar = v.findViewById(R.id.autoBar);

        if (company != null) {
            //autoBar.setVisibility(View.VISIBLE);
            TextView autoDetails = v.findViewById(R.id.stockCompany);

            if (autoDetails != null) {
                autoDetails.setText(company);
                //autoBar.setVisibility(View.GONE);
            }
        }
        return v;
    }
}