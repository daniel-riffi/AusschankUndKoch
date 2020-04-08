package com.example.ausschankundkoch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import at.orderlibrary.Order;
import at.orderlibrary.Position;
import at.orderlibrary.Type;
import at.orderlibrary.UnitTestVariables;
import me.texy.treeview.TreeNode;
import me.texy.treeview.TreeView;

public class RunningActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;
    private BarFragment barFragment;
    private CookFragment cookFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running);

        fragmentManager = getSupportFragmentManager();
        loadUIInterface();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        return true;

        //TODO: PREFERENCES
    }

    private void loadUIInterface(){
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        Type type = (Type) bundle.getSerializable("type");

        if(type == Type.BAR){
            barFragment = new BarFragment();
            fragmentManager.beginTransaction()
                                .replace(R.id.fragmentViewHolder, barFragment)
                                .commit();
        }
        else {
            cookFragment = new CookFragment();
            fragmentManager.beginTransaction()
                                .replace(R.id.fragmentViewHolder, cookFragment)
                                .commit();
        }
    }

    public static void selectTreeNodes(TreeView treeView, ArrayList<Position> selectedPositions){
        treeView.getAllNodes()
                            .stream()
                            .filter(x -> x.getValue() instanceof Position)
                            .forEach(x -> x.setSelected(selectedPositions.contains(x.getValue())));
    }

    public static List<Position> getSelectedPositions(TreeView treeView){
        return treeView.getSelectedNodes()
                                .stream()
                                .filter(x -> x.getValue() instanceof Position)
                                .map(x -> (Position) x.getValue())
                                .collect(Collectors.toList());
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
                Position newPosition = current.copyPositionAndDecreaseAmount();
                orders.add(oldOrder.copyOrderWithPositions(newPosition));
            }
            else {
                Position found = newOrder.positions.stream()
                                                        .filter(x -> x.equals(current))
                                                        .findFirst()
                                                        .orElse(null);
                if(found == null){
                    Position newPosition = current.copyPositionAndDecreaseAmount();
                    newOrder.addPosition(newPosition);
                }
                else {
                    current.amount--;
                    if(current.amount == 0){
                        oldOrder.positions.remove(current);
                    }
                    found.amount++;
                }
            }
        }
    }

    public static void displayOrder(Order order){

    }
}
