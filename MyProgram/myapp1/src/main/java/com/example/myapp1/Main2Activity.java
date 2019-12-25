package com.example.myapp1;

import androidx.appcompat.app.AppCompatActivity;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.Calendar;

public class Main2Activity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);


        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(this);





    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this,EditRentActivity.class);
        intent.putExtra("idRent", 0);
        intent.putExtra("calendar", Calendar.getInstance());
        startActivity(intent);
    }
}
