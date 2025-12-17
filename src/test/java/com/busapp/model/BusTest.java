package com.busapp.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BusTest {

    @Test
    void testBuilderPattern() {
        Bus bus = new Bus.Builder()
                .number("А123ВЕ")
                .model("Mercedes-Benz")
                .mileage(50000)
                .build();

        assertEquals("А123ВЕ", bus.getNumber());
        assertEquals("Mercedes-Benz", bus.getModel());
        assertEquals(50000, bus.getMileage());
    }

    @Test
    void testCompareTo_SameNumber() {
        Bus bus1 = new Bus.Builder().number("А123ВЕ").model("Mercedes").mileage(10000).build();
        Bus bus2 = new Bus.Builder().number("А123ВЕ").model("Volvo").mileage(20000).build();

        assertTrue(bus1.compareTo(bus2) < 0);
    }

    @Test
    void testCompareTo_DifferentNumbers() {
        Bus bus1 = new Bus.Builder().number("А100ВЕ").model("Mercedes").mileage(10000).build();
        Bus bus2 = new Bus.Builder().number("В200СТ").model("Mercedes").mileage(10000).build();

        assertTrue(bus1.compareTo(bus2) < 0);
    }

    @Test
    void testCompareTo_RussianLetters() {
        Bus bus1 = new Bus.Builder()
                .number("А100ВЕ")
                .model("МАЗ")
                .mileage(10000)
                .build();
        Bus bus2 = new Bus.Builder()
                .number("Б200СТ")
                .model("МАЗ")
                .mileage(10000)
                .build();

        assertTrue(bus1.compareTo(bus2) < 0);
    }
}