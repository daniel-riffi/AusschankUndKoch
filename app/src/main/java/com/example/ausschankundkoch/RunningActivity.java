package com.example.ausschankundkoch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
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

    public static void loadUIInterface(Type type){

    }

    public static void displayOrder(Order order){

    }

    public static void addOrderToList(ArrayList<Order> orders, ArrayList<Position> positions){
        Order newOrder;
        for(Position current : positions){
            Order oldOrder = current.getOrder();
            newOrder = null;
            findOrder:
                for(Order order : orders){
                    if(order.orderNumber == oldOrder.orderNumber){
                        newOrder = order;
                        break findOrder;
                    }
                }
            if(newOrder == null){
                orders.add(oldOrder.copyOrderWithPositions(new ArrayList<>(Arrays.asList(current))));
            }
            else {
                newOrder.addPosition(current);
            }
        }
    }
}
