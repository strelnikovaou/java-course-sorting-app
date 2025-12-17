package com.busapp.util;

import com.busapp.model.Bus;
import com.busapp.utils.BusGenerator;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BusGeneratorTest {

    @RepeatedTest(10)
    void generateRandomBusShouldGenerateValidBuses() {
        Bus bus = BusGenerator.generateRandomBus();

        assertNotNull(bus);
        assertNotNull(bus.getNumber());
        assertNotNull(bus.getModel());
        assertTrue(bus.getNumber().matches("[АВЕКМНОРСТУХ]\\d{3}[АВЕКМНОРСТУХ]{2}")); // [буква][3 цифры][2 буквы] : A123AA
        assertFalse(bus.getModel().isBlank());
        assertTrue(bus.getMileage() >= 0);
        assertTrue(bus.getMileage() <= 1000000);
    }

    @Test
    void generateRandomBusShouldGenerateDifferentBuses() {
        Bus bus1 = BusGenerator.generateRandomBus();
        Bus bus2 = BusGenerator.generateRandomBus();

        // Автобусы должны быть разными
        boolean areDifferent = !bus1.getNumber().equals(bus2.getNumber()) ||
                !bus1.getModel().equals(bus2.getModel()) ||
                bus1.getMileage() != bus2.getMileage();

        assertTrue(areDifferent);
    }

}
