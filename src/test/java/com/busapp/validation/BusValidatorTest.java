package com.busapp.validation;

import com.busapp.model.Bus;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BusValidatorTest {
    @Test
    void shouldPassValidationForCorrectBuilder() {
        Bus.Builder builder = new Bus.Builder()
                .number("А123БВ")
                .model("МАЗ")
                .mileage(50000);

        assertDoesNotThrow(() -> BusValidator.validate(builder));
    }

    @Test
    void shouldThrowExceptionForEmptyNumber() {
        Bus.Builder builder = new Bus.Builder()
                .number("")
                .model("МАЗ")
                .mileage(50000);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> BusValidator.validate(builder));

        assertTrue(ex.getMessage().contains("номер автобуса"));
    }

    @Test
    void shouldThrowExceptionForNegativeMileage() {
        Bus.Builder builder = new Bus.Builder()
                .number("А123")
                .model("МАЗ")
                .mileage(-100);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> BusValidator.validate(builder));

        assertTrue(ex.getMessage().contains("пробег не может быть отрицательным"));
    }

    @Test
    void shouldPassValidationForCorrectBus() {
        Bus bus = new Bus.Builder().number("А123").model("МАЗ").mileage(100).build();
        assertDoesNotThrow(() -> BusValidator.validate(bus));
    }

    @Test
    void shouldThrowExceptionForNullBus() {
        assertThrows(IllegalArgumentException.class, () -> BusValidator.validate((Bus) null));
    }
}
