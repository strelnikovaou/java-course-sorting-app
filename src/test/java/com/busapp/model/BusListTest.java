package com.busapp.model;

import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.concurrent.ExecutionException;
import static org.junit.jupiter.api.Assertions.*;

class BusListTest {

    @Test
    void countTest() throws ExecutionException, InterruptedException {
        BusList busList = new BusList();

        for(int i=0; i<10; i++)
            busList.add(new Bus.Builder()
                    .number("A123")
                    .model("Fiat")
                    .mileage(i%2 == 0 ? 5000 : 6000)
                    .build());

        assertEquals(5, busList.count(new Bus.Builder()
                .number("A123").model("Fiat").mileage(5000).build()));

        assertEquals(5, busList.count(new Bus.Builder()
                .number("A123").model("Fiat").mileage(6000).build()));
    }

    @Test
    void sortByEvenValuesOfMileageTest() throws ExecutionException, InterruptedException {
        BusList busList = new BusList();

        for(int i=4; i>=0; i--)
            busList.add(new Bus.Builder().number("А123").model("Fiat").mileage(1000 + i).build());
        busList.sortByEvenValuesOfMileage();

        List<Bus> result = List.of(
                new Bus.Builder().number("А123").model("Fiat").mileage(1000).build(),
                new Bus.Builder().number("А123").model("Fiat").mileage(1003).build(),
                new Bus.Builder().number("А123").model("Fiat").mileage(1002).build(),
                new Bus.Builder().number("А123").model("Fiat").mileage(1001).build(),
                new Bus.Builder().number("А123").model("Fiat").mileage(1004).build()
        );

        assertTrue(busList.equals(result));
    }
}