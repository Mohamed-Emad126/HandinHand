package com.example.handinhand.UI.Activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.handinhand.R;

public class ContactUsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_contact_us);
        Toolbar toolbar = findViewById(R.id.contact_us_toolbar);
        toolbar.setNavigationOnClickListener(view -> {
            finish();
        });
    }

}
