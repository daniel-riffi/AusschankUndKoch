package com.example.ausschankundkoch;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import at.orderlibrary.Order;
import at.orderlibrary.Position;
import at.orderlibrary.UnitTestVariables;

import static org.junit.Assert.*;

public class RunningActivityTest {

    @Before
    public void setUp() throws Exception {
        UnitTestVariables.ResetVariables();
    }

    @Test
    public void T01_addOrderToList(){
        Order order = new Order(2, 6, new ArrayList<>(Arrays.asList(UnitTestVariables.position5)));
        ArrayList<Order> inProgress = new ArrayList<>(Arrays.asList(order));

        ArrayList<Order> toDo = new ArrayList<>(Arrays.asList(UnitTestVariables.order1, UnitTestVariables.order2));

        ArrayList<Position> positionsToMove = new ArrayList<>(Arrays.asList(UnitTestVariables.position2, UnitTestVariables.position4));

        RunningActivity.addOrderToList(inProgress, positionsToMove);

        int[] result_toDo = toDo.stream().flatMap(x -> x.positions.stream()).mapToInt(x -> x.product.offer.id).sorted().toArray();
        int[] expected_toDo = {1, 3};
        Assert.assertArrayEquals(expected_toDo, result_toDo);

        int[] result_inProgress = inProgress.stream().flatMap(x -> x.positions.stream()).mapToInt(x -> x.product.offer.id).sorted().toArray();
        int[] expected_inProgress = {2, 4, 5};
        Assert.assertArrayEquals(expected_inProgress, result_inProgress);
    }
}