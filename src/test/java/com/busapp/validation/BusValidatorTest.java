package com.busapp.validation;

import com.busapp.model.Bus;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BusValidatorTest {

    @Test
    void shouldPassValidationForCorrectBus() {
        BusValidator validatorChain = new NullBusValidator();
        validatorChain.setNext(new BussMileageValidator())
                .setNext(new BussModelValidator())
                .setNext(new BussNumberValidator());

        Bus builder = new Bus()
                .setNumber("А123БВ")
                .setModel("МАЗ")
                .setMileage(50000);

        assertTrue(() ->
                validatorChain.validate(builder.build()).status() == BusValidator.ValidationStatus.SUCCESS);
    }

    @Test
    void shouldFailValidationForIncorrectBus() {
        BusValidator validatorChain = new NullBusValidator();
        validatorChain.setNext(new BussMileageValidator())
                .setNext(new BussModelValidator())
                .setNext(new BussNumberValidator());

        Bus invalidBus = new Bus()
                .setNumber("РТ678СО")
                .setModel("MERZ")
                .setMileage(-90);

        assertTrue(() ->
                validatorChain.validate(invalidBus).status() == BusValidator.ValidationStatus.FAIL);
    }
}
