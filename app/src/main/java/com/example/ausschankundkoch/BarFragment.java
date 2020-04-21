package com.example.ausschankundkoch;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import at.orderlibrary.Order;
import at.orderlibrary.Position;
import at.orderlibrary.UnitTestVariables;
import me.texy.treeview.TreeNode;
import me.texy.treeview.TreeView;

public class BarFragment extends Fragment {

    private ViewGroup viewGroupOpen;
    private ViewGroup viewGroupFinished;

    private TreeNode rootOpen;
    private TreeView treeViewOpen;

    private TreeNode rootFinished;
    private TreeView treeViewFinished;

    private ArrayList<Order> openOrders;
    private ArrayList<Order> finishedOrders;

    private Context ctx;

    public BarFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UnitTestVariables.ResetVariables();
        openOrders = new ArrayList<>(Arrays.asList(UnitTestVariables.order1, UnitTestVariables.order2));
        finishedOrders = new ArrayList<>(Arrays.asList(UnitTestVariables.order3));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bar, container, false);
        initializeViews(view);
        return view;
    }

    private void initializeViews(View view) {
        viewGroupOpen = (LinearLayout) view.findViewById(R.id.linear_layout_open_orders);
        viewGroupFinished = (LinearLayout) view.findViewById(R.id.linear_layout_finished_orders);

        view.findViewById(R.id.fragBar_btnForward).setOnClickListener(v -> moveForwardButtonClicked());
        view.findViewById(R.id.fragBar_btnBackward).setOnClickListener(v -> moveBackwardButtonClicked());

        buildTreeViews();
    }

    private void moveForwardButtonClicked() {
        ArrayList<Position> selectedPositions = new ArrayList<>(RunningActivity.getSelectedPositions(treeViewOpen));
        RunningActivity.addOrderToList(finishedOrders, selectedPositions);
        buildTreeViews();
        RunningActivity.selectTreeNodes(treeViewOpen, selectedPositions);
    }

    private void moveBackwardButtonClicked() {
        ArrayList<Position> selectedPositions = new ArrayList<>(RunningActivity.getSelectedPositions(treeViewFinished));
        RunningActivity.addOrderToList(openOrders, selectedPositions);
        buildTreeViews();
        RunningActivity.selectTreeNodes(treeViewFinished, selectedPositions);
    }

    @Override
    public void onAttach(Context context) {
        this.ctx = context;
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void buildTreeViews() {

        rootOpen = TreeNode.root();
        rootFinished = TreeNode.root();

        fillTreeView(openOrders, rootOpen);
        fillTreeView(finishedOrders, rootFinished);

        treeViewOpen = new TreeView(rootOpen, ctx, new MyNodeViewFactory());
        treeViewOpen.expandAll();
        View viewOpen = treeViewOpen.getView();
        viewOpen.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        treeViewFinished = new TreeView(rootFinished, ctx, new MyNodeViewFactory());
        treeViewFinished.expandAll();
        View viewInProgress = treeViewFinished.getView();
        viewInProgress.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        viewGroupOpen.removeAllViews();
        viewGroupFinished.removeAllViews();

        viewGroupOpen.addView(viewOpen);
        viewGroupFinished.addView(viewInProgress);
    }

    private void fillTreeView(ArrayList<Order> orders, TreeNode root){
        for(Order order : orders){
            TreeNode treeNodeOrder = new TreeNode(order);
            treeNodeOrder.setLevel(0);
            for(Position position : order.positions){
                TreeNode treeNodePosition = new TreeNode(position);
                treeNodePosition.setLevel(1);
                treeNodeOrder.addChild(treeNodePosition);
            }
            if(treeNodeOrder.hasChild()) root.addChild(treeNodeOrder);
        }
    }

    public void deselectNodes(){
        treeViewOpen.deselectAll();
        treeViewFinished.deselectAll();
    }
}
