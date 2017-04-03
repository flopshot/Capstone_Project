package com.sean.golfranger;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import timber.log.Timber;


/**
 * Home Screen Activity displayed when app is first launched
 */
public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Timber.d("Test");
    }

}
