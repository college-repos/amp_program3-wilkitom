package com.example.thomaswilkinson.program5;

import android.Manifest;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.nearby.connection.Strategy;

public class MainActivity extends AppCompatActivity {
    public final static String ServiceId = "com.example.thomaswilkinson.program5";
    public static final Strategy STRATEGY = Strategy.P2P_STAR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                1);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null)
        {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new TicTacDraw_Fragment()).commit();
        }
    }
}
