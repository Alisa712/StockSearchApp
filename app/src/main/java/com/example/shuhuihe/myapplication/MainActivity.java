package com.example.shuhuihe.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> company_list = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final AutoCompleteTextView searchStock = findViewById(R.id.autoCompleteTextView);
        final Button getQuote = (Button) findViewById(R.id.getQuoteButton);

        AutoCompleteAdaptor autoCompleteAdaptor = new AutoCompleteAdaptor(this, R.layout.list_company, company_list);
        searchStock.setAdapter(autoCompleteAdaptor);
        searchStock.setThreshold(1);
        //searchStock.showDropDown();


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
    }
}
