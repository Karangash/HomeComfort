package com.example.myapp1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onClick(View v) {
        Intent intent;

        switch(v.getId()) {
            case R.id.buttonFlat:
                intent = new Intent(this, ShowFlatsActivity.class);
                startActivity(intent);
                break;
            case R.id.buttonGuest:
                intent = new Intent(this, ShowGuestActivity.class);
                startActivity(intent);
                break;
            case R.id.buttonRent:
                intent = new Intent(this, ShowRentsActivity.class);
                startActivity(intent);
                break;
            case R.id.buttonAuth:
                // intent = new Intent(this, PasswordlessActivity.class);
                //startActivity(intent);
                break;


        }
    }
}
