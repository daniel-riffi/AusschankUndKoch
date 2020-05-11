package com.example.ausschankundkoch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import at.orderlibrary.Position;

public class DisplayPositionAdapter extends BaseAdapter {

    private List<Position> positions = new ArrayList<>();
    private int layoutId;
    private LayoutInflater inflater;

    public DisplayPositionAdapter(Context ctx, int layoutId, ArrayList<Position> positions){
     this.positions = positions;
     this.layoutId = layoutId;
     this.inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return positions.size();
    }

    @Override
    public Object getItem(int position) {
        return positions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Position position = positions.get(i);
        View listItem = (view == null) ? inflater.inflate(this.layoutId, null) : view;
        ((TextView) listItem.findViewById(R.id.txtPosition)).setText(position.product.offer.name);
        System.out.println("display position adapter: " + i + ": " + position.product.offer.name);
        return listItem;
    }
}
