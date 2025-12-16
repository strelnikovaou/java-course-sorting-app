package com.busapp.sorting;

import com.busapp.model.Bus;
import com.busapp.model.BusList;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CompositeBusSortTest {
    @Test
    void sortShouldSortByAllFields() {
        BusList buses = new BusList();
        buses.add(new Bus.Builder().number("В456КМ").model("МАЗ").mileage(50000).build());
        buses.add(new Bus.Builder().number("А123ВЕ").model("ЛиАЗ").mileage(100000).build());
        buses.add(new Bus.Builder().number("А123ВЕ").model("ЛиАЗ").mileage(80000).build());

        CompositeBusSort strategy = new CompositeBusSort();
        strategy.sort(buses);

        assertEquals("А123ВЕ", buses.get(0).getNumber());
        assertEquals(80000, buses.get(0).getMileage());
        assertEquals("А123ВЕ", buses.get(1).getNumber());
        assertEquals(100000, buses.get(1).getMileage());
        assertEquals("В456КМ", buses.get(2).getNumber());
    }

    @Test
    void sortShouldHandleIdenticalBuses() {
        BusList buses = new BusList();
        Bus bus = new Bus.Builder().number("А123ВЕ").model("ЛиАЗ").mileage(100000).build();
        buses.add(bus);
        buses.add(bus);

        CompositeBusSort strategy = new CompositeBusSort();

        assertDoesNotThrow(() -> strategy.sort(buses));
        assertEquals(2, buses.size());
    }
}
