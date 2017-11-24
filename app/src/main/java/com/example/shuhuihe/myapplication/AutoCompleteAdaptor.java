package com.example.shuhuihe.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;
import android.app.Activity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
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


    public AutoCompleteAdaptor(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        this.companyList = new ArrayList<>();
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
                if (constraint != null) {

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
                                                    companyList = autoCompleteList;
                                                    filterResults.values = companyList;
                                                    filterResults.count = companyList.size();
                                                }
                                            } catch (Exception e) {
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

                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence contraint, FilterResults results) {
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

		/*
         * Recall that the variable position is sent in as an argument to this method.
		 * The variable simply refers to the position of the current object in the list. (The ArrayAdapter
		 * iterates through the list we sent it)
		 *
		 * Therefore, i refers to the current Item object.
		 */
        String company = companyList.get(position);

        if (company != null) {

            // This is how you obtain a reference to the TextViews.
            // These TextViews are created in the XML files we defined.

//            TextView symbol = (TextView) v.findViewById(R.id.companySymbol);
            TextView autoDetails = (TextView) v.findViewById(R.id.stockCompany);

            // check to see if each individual textview is null.
            // if not, assign some text!
//            if (symbol != null) {
//                symbol.setText(i.getCompanySymbol());
//            }
            if (autoDetails != null) {
                autoDetails.setText(company);
            }
        }
        // the view must be returned to our activity
        return v;
    }
}