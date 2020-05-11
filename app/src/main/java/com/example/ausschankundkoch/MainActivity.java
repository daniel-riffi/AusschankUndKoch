package com.example.ausschankundkoch;

import androidx.appcompat.app.AppCompatActivity;
import at.orderlibrary.Type;

import android.content.Intent;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Arrays;

import at.orderlibrary.Type;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();
    }

    public void btnCookClicked(View view) {
        Intent intent = new Intent(this, RunningActivity.class);
        intent.putExtra("type", Type.COOK);
        startActivity(intent);
    }

    public void btnBarClicked(View view) {
        Intent intent = new Intent(this, RunningActivity.class);
        intent.putExtra("type", Type.BAR);
        startActivity(intent);

    }

    @Override
    protected void onStart() {
        super.onStart();
        perms();
    }

    private void perms(){
        if(checkSelfPermission(Manifest.permission.INTERNET)!= PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.INTERNET},0);
        }
    }
}
