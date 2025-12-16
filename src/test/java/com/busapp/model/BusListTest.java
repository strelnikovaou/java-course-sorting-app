package com.busapp.model;

import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BusListTest {
    @Test
    void testFromStream() {
        Bus bus1 = new Bus.Builder().number("А123").model("M1").mileage(10000).build();
        Bus bus2 = new Bus.Builder().number("В456").model("M2").mileage(20000).build();

        Stream<Bus> stream = Stream.of(bus1, bus2);
        BusList list = BusList.fromStream(stream);

        assertEquals(2, list.size());
        assertTrue(list.contains(bus1));
        assertTrue(list.contains(bus2));
    }
}
