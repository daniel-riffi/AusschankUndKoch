package com.example.ausschankundkoch;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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

    private static final int RQ_PREFERENCES = 1234;

    private SharedPreferences prefs;
    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;

    private FragmentManager fragmentManager;
    private BarFragment barFragment;
    private CookFragment cookFragment;

    private Type currentType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        Type type = (Type) bundle.getSerializable("type");

        fragmentManager = getSupportFragmentManager();
        loadUIInterface(type);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        preferenceChangeListener = this::preferenceChanged;
        prefs.registerOnSharedPreferenceChangeListener(preferenceChangeListener);
        setTypeInPreferences(type);
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("onResume");
        String ipAddress = prefs.getString("ip_address", "");
        System.out.println("ip" + ipAddress);
        if(!connectToServer(ipAddress)){
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivityForResult(intent, RQ_PREFERENCES);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id == R.id.menu_preferences){
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivityForResult(intent, RQ_PREFERENCES);
        }
        else if(id == R.id.menu_deselect){
            if(currentType == Type.BAR) barFragment.deselectNodes();
            else if(currentType == Type.COOK) cookFragment.deselectNodes();
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadUIInterface(Type type){
        if(type == Type.BAR){
            barFragment = new BarFragment();
            fragmentManager.beginTransaction()
                                .replace(R.id.fragmentViewHolder, barFragment)
                                .commitAllowingStateLoss();
        }
        else {
            cookFragment = new CookFragment();
            fragmentManager.beginTransaction()
                                .replace(R.id.fragmentViewHolder, cookFragment)
                                .commitAllowingStateLoss();
        }
        currentType = type;
    }

    private void setTypeInPreferences(Type type) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("type", type.name());
        editor.commit();
    }

    private void preferenceChanged(SharedPreferences sharedPrefs, String key) {
        String sValue = sharedPrefs.getString(key, "");
        if(key.equals("ip_address")){
            //ipAddress = sValue;
        }
        else if(key.equals("type")) {
            loadUIInterface(Type.valueOf(sValue));
        }
    }

    private boolean connectToServer(String ip) {
        return ip.equals("hallo");
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
