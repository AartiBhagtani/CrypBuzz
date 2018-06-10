package com.example.navin.cryptbuzz;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Alert extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alert);

    }
    public void addnewalert(View v) {
        if (v.getId() == R.id.addnewalert) {
            Intent i = new Intent(Alert.this, Addnewalert.class);
            startActivity(i);
        }
    }
}
