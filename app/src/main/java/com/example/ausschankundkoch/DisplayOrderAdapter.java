package com.example.ausschankundkoch;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import at.orderlibrary.Order;
import at.orderlibrary.Position;

public class DisplayOrderAdapter extends BaseAdapter {

    private List<Order> orders = new ArrayList<>();
    private int layoutId;
    private LayoutInflater inflater;
    private Context ctx;

    public DisplayOrderAdapter(Context ctx, int layoutId, ArrayList<Order> orders){
        this.ctx = ctx;
        this.orders = orders;
        this.layoutId = layoutId;
        this.inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return orders.size();
    }

    @Override
    public Object getItem(int position) {
        return orders.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        Order order = orders.get(position);
        View listItem = (view == null) ? inflater.inflate(this.layoutId, null) : view;
        TextView textView = ((TextView) listItem.findViewById(R.id.txtOrderId));
        textView.setText(String.valueOf(order.orderNumber));
        ListView lvPositions = listItem.findViewById(R.id.lvPositions);
        DisplayPositionAdapter adapter = new DisplayPositionAdapter(ctx, R.layout.display_position_layout, new ArrayList<>(order.positions));
        lvPositions.setAdapter(adapter);
        return listItem;
    }
}
