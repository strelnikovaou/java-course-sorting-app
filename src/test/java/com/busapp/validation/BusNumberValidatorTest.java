package com.busapp.validation;

import com.busapp.model.Bus;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BusNumberValidatorTest {
    private final BusNumberValidator validator = new BusNumberValidator();

    @ParameterizedTest
    @ValueSource(strings = {"А123ВЕ", "В456КМ", "Е789НО", "К012РС"})
    void validateShouldPassForCorrectNumbers(String number) {
        Bus bus = new Bus.Builder().number(number).model("Test").mileage(10000).build();
        BusValidator.ValidationResult result = validator.validate(bus);
        assertEquals(BusValidator.ValidationStatus.SUCCESS, result.status());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "123456", "АБВГДЕ", "A123BC", "А12ВЕ", "А1234ВЕ"})
    void validateShouldFailForIncorrectNumbers(String number) {
        Bus bus = new Bus.Builder().number(number).model("Test").mileage(10000).build();
        BusValidator.ValidationResult result = validator.validate(bus);

        assertEquals(BusValidator.ValidationStatus.FAIL, result.status());
        assertTrue(result.message().toLowerCase().contains("номер"));
    }
}
