package com.example.shuhuihe.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> company_list = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final AutoCompleteTextView searchStock = findViewById(R.id.autoCompleteTextView);

        AutoCompleteAdaptor autoCompleteAdaptor = new AutoCompleteAdaptor(this, R.layout.list_company, company_list);
        searchStock.setAdapter(autoCompleteAdaptor);
        searchStock.setThreshold(1);
        //searchStock.showDropDown();



//        Spinner dropdown = (Spinner)findViewById(R.id.spinner1);
//        String[] items = new String[]{"1", "2", "three"};
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
//        dropdown.setAdapter(adapter);


    }

    public void getQuote() {
        
    }
}
