package com.busapp.validation;

import com.busapp.model.Bus;
import com.busapp.model.BusList;
import com.busapp.sorting.BusMileageSort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BusMileageValidatorTest {
    private BusMileageSort strategy;
    private BusList buses;

    @BeforeEach
    void setUp() {
        strategy = new BusMileageSort();
        buses = new BusList();
        buses.add(new Bus.Builder().number("А123ВЕ").model("ЛиАЗ").mileage(100000).build());
        buses.add(new Bus.Builder().number("В456КМ").model("МАЗ").mileage(50000).build());
        buses.add(new Bus.Builder().number("Е789НО").model("НЕФАЗ").mileage(75000).build());
    }

    @Test
    void sortShouldSortBusesByMileage() {
        strategy.sort(buses);

        assertEquals(50000, buses.get(0).getMileage());
        assertEquals(75000, buses.get(1).getMileage());
        assertEquals(100000, buses.get(2).getMileage());
    }

    @Test
    void sortShouldHandleSameMileageValues() {
        BusList sameMileage = new BusList();
        sameMileage.add(new Bus.Builder().number("А123ВЕ").model("ЛиАЗ").mileage(100000).build());
        sameMileage.add(new Bus.Builder().number("В456КМ").model("МАЗ").mileage(100000).build());

        assertDoesNotThrow(() -> strategy.sort(sameMileage));
        assertEquals(2, sameMileage.size());
    }
}
