package com.example.ausschankundkoch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Arrays;

import at.orderlibrary.Type;

public class MainActivity extends AppCompatActivity {

    Spinner spinner;
    ArrayAdapter<Type> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize();
    }

    private void initialize() {
        spinner = findViewById(R.id.spType);
        adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, Arrays.asList(Type.BAR, Type.COOK));
        spinner.setAdapter(adapter);
    }

    public void btnStartClicked(View view) {
        Intent intent = new Intent(this, RunningActivity.class);
        intent.putExtra("type", (Type)spinner.getSelectedItem());
        startActivity(intent);
    }
}
