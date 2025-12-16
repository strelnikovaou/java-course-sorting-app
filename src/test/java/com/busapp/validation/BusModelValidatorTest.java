package com.busapp.validation;

import com.busapp.model.Bus;
import com.busapp.model.BusList;
import com.busapp.sorting.BusModelSort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class BusModelValidatorTest {
    private BusModelSort strategy;
    private BusList buses;

    @BeforeEach
    void setUp() {
        strategy = new BusModelSort();
        buses = new BusList();
        buses.add(new Bus.Builder().number("А123ВЕ").model("МАЗ").mileage(100000).build());
        buses.add(new Bus.Builder().number("В456КМ").model("ЛиАЗ").mileage(50000).build());
        buses.add(new Bus.Builder().number("Е789НО").model("НЕФАЗ").mileage(75000).build());
    }


    @Test
    void sortShouldBeCaseInsensitive() {
        BusList mixedCase = new BusList();
        mixedCase.add(new Bus.Builder().number("А123ВЕ").model("маз").mileage(100000).build());
        mixedCase.add(new Bus.Builder().number("В456КМ").model("ЛиАЗ").mileage(50000).build());

        strategy.sort(mixedCase);

        assertTrue(mixedCase.get(0).getModel().equalsIgnoreCase("ЛиАЗ"));
    }

}
