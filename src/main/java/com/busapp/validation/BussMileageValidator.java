package com.busapp.validation;

import com.busapp.model.Bus;

public class BussMileageValidator extends BusValidator{
    @Override
    protected ValidationResult validateInternal(Bus bus) {
        if(bus.getMileage() < 0){
          return errorValidationResult("Пробег не может быть отрицательным (значение: %d)".formatted(bus.getMileage()));
        }
        return successValidationResult();
    }
}
