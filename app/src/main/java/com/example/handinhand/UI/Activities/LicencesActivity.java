package com.example.handinhand.UI.Activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.handinhand.R;

public class LicencesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_licence);
        Toolbar toolbar = findViewById(R.id.licence_toolbar);
        toolbar.setNavigationOnClickListener(view -> {
            finish();
        });
    }
}