package com.sean.golfranger;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Home Screen Activity displayed when app is first launched
 */
public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    public void startNewRound(View v) {
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), RoundActivity.class);
        startActivity(intent);
    }

    public void viewOldRound(View v) {
        Intent intent = new Intent();
        //intent.setClass(getApplicationContext(), RoundsActivity.class);
        startActivity(intent);
    }

    public void viewPlayers(View v) {
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), PlayerActivity.class);
        startActivity(intent);
    }

    public void viewCourses(View v) {
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), CoursesActivity.class);
        startActivity(intent);
    }
}
