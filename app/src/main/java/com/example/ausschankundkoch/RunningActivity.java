package com.example.ausschankundkoch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import java.util.List;

import at.orderlibrary.Order;
import at.orderlibrary.Position;
import at.orderlibrary.Type;

public class RunningActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running);

        TextView textView = findViewById(R.id.txtType);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        Type type = (Type) bundle.getSerializable("type");
        textView.setText(type.name());
    }

    private void loadUIInterface(Type type){

    }

    private void displayOrder(Order order){

    }

    private List<Position> addOrderToList(List<Position> positions){
        return null;
    }
}
