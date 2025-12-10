package com.busapp.model;

import com.busapp.model.builder.BusCreator;
import com.busapp.model.builder.StringBusBuilder;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

class BusListTest {

    @Test
    void countTest() throws ExecutionException, InterruptedException {
        BusCreator busCreator =  new BusCreator(new StringBusBuilder());
        BusList busList = new BusList();

        for(int i=0; i<10; i++)
            busList.add(busCreator.bus("А123, Fiat, " + (i%2 == 0 ? 5000 : 6000)));

        assertEquals(5, busList.count(busCreator.bus(" А123  Fiat,  5000")));
        assertEquals(5, busList.count(busCreator.bus("А123, Fiat   6000 ")));
    }

    @Test
    void addTest(){
        BusCreator busCreator =  new BusCreator(new StringBusBuilder());
        BusList busList = new BusList();

        busList.add(busCreator.bus(",  Fiat, 5000"));
        busList.add(busCreator.bus(", Fiat, -5000"));
        busList.add(busCreator.bus(", , 5000     "));
        busList.add(busCreator.bus("  Fiat5000   "));

        assertEquals(0, busList.size());
    }

    @Test
    void sortByEvenValuesOfMileageTest(){
        BusCreator busCreator =  new BusCreator(new StringBusBuilder());
        BusList busList = new BusList();

        for(int i=4; i>=0; i--)
            busList.add(busCreator.bus("А123, Fiat, " + 100 + i));
        busList.sortByEvenValuesOfMileage();

        List<Bus> result = List.of(
                new Bus().setNumber("А123").setModel("Fiat").setMileage(1000),
                new Bus().setNumber("А123").setModel("Fiat").setMileage(1003),
                new Bus().setNumber("А123").setModel("Fiat").setMileage(1002),
                new Bus().setNumber("А123").setModel("Fiat").setMileage(1001),
                new Bus().setNumber("А123").setModel("Fiat").setMileage(1004)
        );

        assertTrue(busList.equals(result));
    }
}