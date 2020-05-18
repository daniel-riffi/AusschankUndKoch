package com.example.ausschankundkoch;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

import at.orderlibrary.Order;
import at.orderlibrary.Position;
import at.orderlibrary.Type;
import at.orderlibrary.UnitTestVariables;
import me.texy.treeview.TreeNode;
import me.texy.treeview.TreeView;

public class CookFragment extends Fragment {

    private ViewGroup viewGroupOpen;
    private ViewGroup viewGroupInProgress;
    private ViewGroup viewGroupFinished;

    private TreeNode rootOpen;
    private TreeView treeViewOpen;

    private TreeNode rootInProgress;
    private TreeView treeViewInProgress;

    private TreeNode rootFinished;
    private TreeView treeViewFinished;

    private ArrayList<Order> openOrders;
    private ArrayList<Order> inProgressOrders;
    private ArrayList<Order> finishedOrders;

    private Context ctx;

    public CookFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UnitTestVariables.ResetVariables();
        openOrders = new ArrayList<>();//Arrays.asList(UnitTestVariables.order3, UnitTestVariables.order2));
        inProgressOrders = new ArrayList<>();//Arrays.asList(UnitTestVariables.order1));
        finishedOrders = new ArrayList<>();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cook, container, false);
        initializeViews(view);
        return view;
    }

    private void initializeViews(View view) {
        viewGroupOpen = (LinearLayout) view.findViewById(R.id.linear_layout_open_orders);
        viewGroupInProgress = (LinearLayout) view.findViewById(R.id.linear_layout_inprogress_orders);
        viewGroupFinished = (LinearLayout) view.findViewById(R.id.linear_layout_finished_orders);

        view.findViewById(R.id.fragBar_btnForwardFirst).setOnClickListener(v -> moveForwardButtonFirstClicked());
        view.findViewById(R.id.fragBar_btnBackwardFirst).setOnClickListener(v -> moveBackwardFirstButtonClicked());

        view.findViewById(R.id.fragBar_btnForwardSecond).setOnClickListener(v -> moveForwardButtonSecondClicked());
        view.findViewById(R.id.fragBar_btnBackwardSecond).setOnClickListener(v -> moveBackwardButtonSecondClicked());

        buildTreeViews();
    }

    private void moveForwardButtonFirstClicked() {
        ArrayList<Position> selectedPositions = new ArrayList<>(RunningActivity.getSelectedPositions(treeViewOpen));
        RunningActivity.addOrderToList(inProgressOrders, selectedPositions);
        RunningActivity.removeOrderWithNoPositions(openOrders);
        buildTreeViews();
        RunningActivity.selectTreeNodes(treeViewOpen, selectedPositions);
    }

    private void moveBackwardFirstButtonClicked() {
        ArrayList<Position> selectedPositions = new ArrayList<>(RunningActivity.getSelectedPositions(treeViewInProgress));
        RunningActivity.addOrderToList(openOrders, selectedPositions);
        RunningActivity.removeOrderWithNoPositions(inProgressOrders);
        buildTreeViews();
        RunningActivity.selectTreeNodes(treeViewInProgress, selectedPositions);
    }

    private void moveForwardButtonSecondClicked() {
        ArrayList<Position> selectedPositions = new ArrayList<>(RunningActivity.getSelectedPositions(treeViewInProgress));
        RunningActivity.addOrderToList(finishedOrders, selectedPositions);
        RunningActivity.removeOrderWithNoPositions(inProgressOrders);
        for(Order order : selectedPositions.stream().map(Position::getOrder).collect(Collectors.toList())){
            if(checkIfOrderIsFinished(order.orderNumber)){
                Server.getInstance().notifyServerPositionsFinished(finishedOrders.stream()
                        .filter(x -> x.orderNumber == order.orderNumber)
                        .findFirst().get()
                        .positions
                        .stream()
                        .mapToInt(x -> x.amount)
                        .sum());
                finishedOrders.remove(order);
            }
        }
        buildTreeViews();
        RunningActivity.selectTreeNodes(treeViewInProgress, selectedPositions);
    }

    private void moveBackwardButtonSecondClicked() {
        ArrayList<Position> selectedPositions = new ArrayList<>(RunningActivity.getSelectedPositions(treeViewFinished));
        RunningActivity.addOrderToList(inProgressOrders, selectedPositions);
        RunningActivity.removeOrderWithNoPositions(finishedOrders);
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

    public void buildTreeViews() {
        rootOpen = TreeNode.root();
        rootInProgress = TreeNode.root();
        rootFinished = TreeNode.root();

        fillTreeView(openOrders, rootOpen);
        fillTreeView(inProgressOrders, rootInProgress);
        fillTreeView(finishedOrders, rootFinished);

        treeViewOpen = new TreeView(rootOpen, ctx, new MyNodeViewFactory());
        treeViewOpen.expandAll();
        View viewOpen = treeViewOpen.getView();
        viewOpen.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        treeViewInProgress = new TreeView(rootInProgress, ctx, new MyNodeViewFactory());
        treeViewInProgress.expandAll();
        View viewInProgress = treeViewInProgress.getView();
        viewInProgress.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        treeViewFinished = new TreeView(rootFinished, ctx, new MyNodeViewFactory());
        treeViewFinished.expandAll();
        View viewFinished = treeViewFinished.getView();
        viewFinished.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        viewGroupOpen.removeAllViews();
        viewGroupInProgress.removeAllViews();
        viewGroupFinished.removeAllViews();

        viewGroupOpen.addView(viewOpen);
        viewGroupInProgress.addView(viewInProgress);
        viewGroupFinished.addView(viewFinished);
    }

    private void fillTreeView(ArrayList<Order> orders, TreeNode root) {
        for (Order order : orders) {
            TreeNode treeNodeOrder = new TreeNode(order);
            treeNodeOrder.setLevel(0);
            for (Position position : order.positions) {
                TreeNode treeNodePosition = new TreeNode(position);
                treeNodePosition.setLevel(1);
                treeNodeOrder.addChild(treeNodePosition);
            }
            if (treeNodeOrder.hasChild()) root.addChild(treeNodeOrder);
        }
    }

    public void deselectNodes() {
        treeViewOpen.deselectAll();
        treeViewInProgress.deselectAll();
        treeViewFinished.deselectAll();
    }

    public boolean checkIfOrderIsFinished(int orderNumber) {
        return  inProgressOrders.stream()
                            .noneMatch(x -> x.orderNumber == orderNumber)
                &&
                openOrders.stream()
                            .noneMatch(x -> x.orderNumber == orderNumber);
    }

    public void addOrder(Order order){
        openOrders.add(order);
    }
}
