package com.example.shuhuihe.myapplication;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Stock_detail_news.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Stock_detail_news#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Stock_detail_news extends Fragment {

    public Stock_detail_news() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stock_detail_news, container, false);
    }
}
