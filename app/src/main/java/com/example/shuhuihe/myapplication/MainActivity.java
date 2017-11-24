package com.example.shuhuihe.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.AutoCompleteTextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> company_list = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final AutoCompleteTextView searchStock = findViewById(R.id.autoCompleteTextView);

        AutoCompleteAdaptor adapter1 = new AutoCompleteAdaptor(this, R.layout.list_company);
        searchStock.setAdapter(adapter1);
        searchStock.setThreshold(1);
        searchStock.showDropDown();

    }
}
