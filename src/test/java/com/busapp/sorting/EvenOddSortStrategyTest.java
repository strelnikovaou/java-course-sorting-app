package com.busapp.sorting;

import com.busapp.model.Bus;
import com.busapp.model.BusList;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EvenOddSortStrategyTest {
    @Test
    void sortShouldOnlySortEvenFieldValues() {
        BusList buses = new BusList();
        buses.add(new Bus.Builder().number("А123ВЕ").model("Test").mileage(100000).build()); // index 0 - четный
        buses.add(new Bus.Builder().number("В456КМ").model("Test").mileage(25001).build());  // index 1 - нечетный
        buses.add(new Bus.Builder().number("Е789НО").model("Test").mileage(50000).build());  // index 2 - четный
        buses.add(new Bus.Builder().number("К012РС").model("Test").mileage(75001).build());  // index 3 - нечетный
        buses.add(new Bus.Builder().number("М345ТУ").model("Test").mileage(30000).build());  // index 4 - четный

        EvenOddSortStrategy strategy = new EvenOddSortStrategy(
                new BusMileageSort(),
                Bus::getMileage
        );

        strategy.sort(buses);

        // Автобусы с четными пробегами (100000, 50000, 30000) должны быть отсортированы
        // и размещены на позициях 0, 2, 4 (где были четные)
        assertEquals(30000, buses.get(0).getMileage());  // минимальный четный
        assertEquals(50000, buses.get(2).getMileage());  // средний четный
        assertEquals(100000, buses.get(4).getMileage()); // максимальный четный

        // Автобусы с нечетными пробегами (25001, 75001) остаются на своих местах (индексы 1 и 3)
        assertEquals(25001, buses.get(1).getMileage());
        assertEquals(75001, buses.get(3).getMileage());
    }
}
